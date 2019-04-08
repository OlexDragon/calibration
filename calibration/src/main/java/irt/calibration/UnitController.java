package irt.calibration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.helpers.SerialPortWorker;
import irt.calibration.helpers.ThreadWorker;
import irt.calibration.tools.unit.AdcWorker;
import irt.calibration.tools.unit.DacWorker;
import irt.calibration.tools.unit.DeviceInfoWorker;
import irt.calibration.tools.unit.MeasurementWorker;
import irt.calibration.tools.unit.UnitWorker;
import irt.calibration.tools.unit.packets.PacketDac;
import irt.calibration.tools.unit.packets.PacketCalibrationMade.CalibrationModeStatus;
import irt.calibration.tools.unit.packets.PacketMuteControl.MuteStatus;
import irt.calibration.tools.unit.packets.parameters.Parameter;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.DAC;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.DAC.DacName;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

public class UnitController extends AnchorPane {

	private final static Logger logger = LogManager.getLogger();

	private final static List<TextField> textFields = new ArrayList<>();

	@FXML private ChoiceBox<String>	 chbUnitSerialPort;
	@FXML private ContextMenu		 comPortMenuUnit;
    @FXML private TextField			 tfUnitAddress;
    @FXML private GridPane			 gridPane;
    @FXML private Slider			 slider;
	@FXML private Button			 btnUnitConnect;
    @FXML private Button			 btnMute;
    @FXML private Button			 btnReload;
    @FXML private Button			 btnCalLMode;

    private static UnitWorker unitWorker;

    private DacWorker dacWorker;
	private MeasurementWorker measurementWorker;
	private DeviceInfoWorker deviceInfoWorker;
	private AdcWorker adcWorker;

	public UnitController() {

		try {

			String fxmlFile = "/fxml/UnitDetails.fxml";
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();

		} catch (IOException exc) {
			logger.catching(exc);
		}
	}

	@FXML void initialize() throws IOException {

		ControllerListener.set(tfUnitAddress, slider);

		measurementWorker= new MeasurementWorker(chbUnitSerialPort, tfUnitAddress);
		unitWorker		 = new UnitWorker		(chbUnitSerialPort, tfUnitAddress);
		dacWorker		 = new DacWorker		(chbUnitSerialPort, tfUnitAddress);
		deviceInfoWorker = new DeviceInfoWorker	(chbUnitSerialPort, tfUnitAddress);
		adcWorker		 = new AdcWorker		(chbUnitSerialPort, tfUnitAddress);

    	SerialPortWorker.addChoiceBoxs(chbUnitSerialPort);
    	comPortMenuUnit.setUserData(chbUnitSerialPort);
    }

    @FXML
    void onBaudrate() {
    	unitWorker.choiceBbaudRate();
    }

    @FXML
    void onUnitConnect() {

    	try {
    		if(unitWorker.connect(btnUnitConnect)) {

    			setButtonsDisable(false);
    			onReload();

    		}else 
    			setButtonsDisable(true);

    	}catch (Exception e) {
			setButtonsDisable(true);
			logger.catching(e);
    	}
    }

	private void setButtonsDisable(boolean disable) {
		btnMute.setDisable(disable);
		btnReload.setDisable(disable);
		btnCalLMode.setDisable(disable);
	}

    @FXML
    void onReload() {

    	textFields.clear();
		gridPane.getChildren().clear();
		gridPane.getRowConstraints().clear();

		btnMute.setUserData(null);
		ThreadWorker.runThread(toggleMute());

		btnCalLMode.setUserData(null);
		ThreadWorker.runThread(toggleCalMode());
 
      	ThreadWorker.runThread(()->dacWorker.setDac(DacName.FCM_DAC1, null).forEach(addTextField()));
      	ThreadWorker.runThread(()->dacWorker.setDac(DacName.FCM_DAC2, null).forEach(addTextField()));
      	ThreadWorker.runThread(()->dacWorker.setDac(DacName.FCM_DAC3, null).forEach(addTextField()));
      	ThreadWorker.runThread(()->dacWorker.setDac(DacName.FCM_DAC4, null).forEach(addTextField()));
      	ThreadWorker.runThread(()->deviceInfoWorker.getDeviceInfo().forEach(addRows()));
      	ThreadWorker.runThread(()->measurementWorker.getMeasurementAll().forEach(addRows()));
//       	ThreadWorker.runThread(()->measurementWorker.getPowerInput().forEach(addRows()));
//      	ThreadWorker.runThread(()->measurementWorker.getPowerOutput().forEach(addRows()));
//       	ThreadWorker.runThread(()->measurementWorker.getTemperature().forEach(addRows()));

       	if(tfUnitAddress.getText().isEmpty()) {	//Converter
       		ThreadWorker.runThread(()->adcWorker.getPowerInputAdc().forEach(addRows()));
       		ThreadWorker.runThread(()->adcWorker.getPowerOutputAdc().forEach(addRows()));
       		ThreadWorker.runThread(()->adcWorker.getTemperatureAdc().forEach(addRows()));
       	}
   }

	private BiConsumer<? super Converter<?>, ? super Parameter> addTextField() {
		return (converter, parameter)->{

			final String title = converter.getTitle();
			Node nTitle = new Text(title);

			final String value = converter.convert( parameter.getData()).toString();
			Node valueNode;

			if(converter instanceof DAC) {
				final TextField textField = new TextField(value);
				textField.setUserData(converter);
				textField.focusedProperty().addListener(ControllerListener.getFocusListener());
				textFields.add(textField);
				textField.textProperty().addListener(ControllerListener.getTextFieldListener());
				valueNode = textField;
			}else
				valueNode = new Text(value);


			addRow(gridPane, nTitle, valueNode);
		};
	}

	private Runnable toggleMute() {
		return ()->{

			Optional.ofNullable(unitWorker.setMuteStatus(Optional.ofNullable((MuteStatus)btnMute.getUserData()).map(MuteStatus::toggle).orElse(null)))
			.ifPresent(
					m->{
						m.forEach(
								(parameterID, parameter)->{

									final MuteStatus muteStatus = (MuteStatus) parameterID.convert(parameter.getData());
									btnMute.setUserData(muteStatus);

									String text = Optional.of(muteStatus).filter(ms->ms==MuteStatus.UNMUTED).map(ms->"Mute").orElse("Unmute");
									Platform.runLater(()->btnMute.setText(text));
								});
					});
		};
	}

	private Runnable toggleCallMode() {
		return ()->{

			Optional.ofNullable(unitWorker.setCalibrationMode(Optional.ofNullable((CalibrationModeStatus)btnCalLMode.getUserData()).map(CalibrationModeStatus::toggle).orElse(null)))
			.ifPresent(
					m->{
						m.forEach(
								(parameterID, parameter)->{

									final CalibrationModeStatus modeStatus = (CalibrationModeStatus) parameterID.convert(parameter.getData());
									btnCalLMode.setUserData(modeStatus);

									String text = Optional.of(modeStatus).filter(ms->ms==CalibrationModeStatus.IS_ON).map(ms->"Set Of").orElse("Set On");
									Platform.runLater(()->btnCalLMode.setText(text));
								});
					});
		};
	}

	private Runnable toggleCalMode() {
		return ()->{

			Optional.ofNullable(unitWorker.setCalibrationMode(Optional.ofNullable((CalibrationModeStatus)btnCalLMode.getUserData()).map(CalibrationModeStatus::toggle).orElse(null)))
			.ifPresent(
					m->{
						m.forEach(
								(parameterID, parameter)->{

//									logger.error(parameterID);
									final CalibrationModeStatus status = (CalibrationModeStatus) parameterID.convert(parameter.getData());
									btnCalLMode.setUserData(status);

									String text = Optional.of(status).filter(ms->ms==CalibrationModeStatus.IS_OFF).map(ms->"Set On").orElse("Set Off");
									Platform.runLater(()->btnCalLMode.setText(text));
								});
					});
		};
	}

	private BiConsumer<? super Converter<?>, ? super Parameter> addRows() {
		return
				(converter, parameter)->{

					final Object value = converter.convert( parameter.getData());
					addRow(gridPane, converter.getTitle(), value .toString());
		};
	}

	@FXML
    void onMute() {
		ThreadWorker.runThread(toggleMute());
    }

    @FXML
    void onCallMode() {
		ThreadWorker.runThread(toggleCallMode());
    }

    public static void addRow(GridPane gridPane, String title, String value) {
		addRow(gridPane, new Text(title), new Text(value));
	}

	private static void addRow(GridPane gridPane, Node titleNode, Node valueNode) {
		Platform.runLater(
				()->{
					final ObservableList<RowConstraints> rowConstraints = gridPane.getRowConstraints();
					int rowIndex = rowConstraints.size();
					rowConstraints.add(new RowConstraints());

					gridPane.add(titleNode, 0, rowIndex);

					gridPane.add(valueNode, 1, rowIndex);
				});
	}

	public static class ControllerListener {

		private static Slider slider;
		private static TextField tfUnitAddress;

		private static ChangeListener<? super Number> sliderListener = (o, ov, nv)->{
			final TextField textField = (TextField) slider.getUserData();
			textField.setText(Integer.toString(nv.intValue()));
		};

		private static final ChangeListener<? super Boolean> listener = (o, ov, nv)->{

			textFields.parallelStream().map(TextField::getStyleClass).forEach(styleClass->styleClass.remove("active"));

			final TextField bean = (TextField) ((ReadOnlyBooleanProperty)o).getBean();
			bean.getStyleClass().add("active");
			final DAC dac = (DAC)bean.getUserData();
			final Integer value = Optional.of(bean.getText().replaceAll("\\D", "")).map(Integer::parseInt).orElse(0);

			slider.setDisable(false);
			slider.setMin(dac.getMinValue());
			slider.setMax(dac.getMaxValue());
			slider.setUserData(bean);
			slider.setValue(value);

		};

		public static ChangeListener<? super Boolean> getFocusListener() {
			return listener;
		}

		private static Timer timer;
		public static ChangeListener<? super String> getTextFieldListener() {
			return (o,ov,nv)->{
				final TextField bean = (TextField) ((StringProperty)o).getBean();
				Optional.ofNullable(timer).ifPresent(Timer::cancel);
				timer = new Timer(true);
				timer.schedule(new UnitTimerTask(tfUnitAddress, bean), UnitWorker.WAIT_TIME);
			};
		}

		public static void set(TextField tfUnitAddress, Slider slider) {
			ControllerListener.slider = slider;
			ControllerListener.tfUnitAddress = tfUnitAddress;
			slider.valueProperty().addListener(sliderListener);
		}

	}
	public static class UnitTimerTask extends TimerTask {

		private final TextField tfUnitAddress;
		private final TextField tfValue;

		public UnitTimerTask(TextField tfUnitAddress, TextField tfValue) {
			this.tfUnitAddress = tfUnitAddress;
			this.tfValue = tfValue;
		}

		@Override
		public void run() {
			final Byte address = Optional.of(tfUnitAddress.getText()).map(t->t.replaceAll("\\D", "")).filter(t->!t.isEmpty()).map(Integer::parseInt).map(Integer::byteValue).orElse(null);
			Integer value = Optional.of(tfValue.getText()).map(t->t.replaceAll("\\D", "")).filter(t->!t.isEmpty()).map(Integer::parseInt).orElse(null);
			final DAC dac = (DAC) tfValue.getUserData();
			final PacketDac packet = dac.getPacket(address, value);
			final byte[] bytes = unitWorker.writeThenRead(packet, UnitWorker.WAIT_TIME);
			final PacketDac packetDac = new PacketDac(bytes);
			packetDac.parametersToMap().forEach((converter, parameter)->{
				final Integer v = (Integer) converter.convert(parameter.getData());
				tfValue.setText(v.toString());
			});
		}
	}
}
