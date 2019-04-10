package irt.calibration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.exception.PrologixTimeoutException;
import irt.calibration.helpers.SerialPortWorker;
import irt.calibration.tools.Tool;
import irt.calibration.tools.prologix.PrologixCommand;
import irt.calibration.tools.prologix.PrologixWorker;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import jssc.SerialPort;
import jssc.SerialPortException;

public class PrologixController extends AnchorPane {

	private static final int DEFAULT_TIMEOUT = 20;

	private final static Logger logger = LogManager.getLogger();

	@FXML private BorderPane					 borderPanePrologix;
    @FXML private ChoiceBox<String>				 chbPrologixSerialPort;
    @FXML private ChoiceBox<PrologixCommand>	 chbPrologixCommand;
    @FXML private Button						 btnPrologixConnect;
    @FXML private TextField						 tfPrologixValue;
    @FXML private TextField						 tfTimeout;
    @FXML private TextArea						 taPrologixAnswers;
    @FXML private CheckBox						 cbShowHelp;

	private PrologixWorker prologixWorker;

	private Boolean prlogixConnected;

	private final ChangeListener<? super PrologixCommand> commandListener = (o, ov, nv)->{

		prologixWorker.setPrologixCommand(nv);
		taPrologixAnswers.setText(taPrologixAnswers.getText() + "\n" + prologixWorker.getPrologixCommand() + " : ");

		Optional.ofNullable(cbShowHelp).filter(CheckBox::isSelected).ifPresent(cb->showHelp(nv));
	};

	private Integer timeout;

	public PrologixController() {

		prologixWorker = new PrologixWorker();

		try {

			String fxmlFile = "/fxml/Prologix.fxml";
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();

		} catch (IOException exc) {
			logger.catching(exc);
		}
	}

	@FXML void initialize() throws IOException, SerialPortException, PrologixTimeoutException {

		Tool.initializeTextField( tfTimeout, "prologix_timeout", 100, v->timeout=v);
		// Show Help Check Box
		final SingleSelectionModel<PrologixCommand> selectionModel = chbPrologixCommand.getSelectionModel();
		cbShowHelp.setOnAction(e->Optional.ofNullable(selectionModel.getSelectedItem()).filter(sc->cbShowHelp.isSelected()).ifPresent(sc->showHelp(sc)));

		fillPrologixCommandsChoiceBox();

		selectionModel.selectedItemProperty().addListener(commandListener);
		selectionModel.select(0);

		SerialPortWorker.addChoiceBoxs(chbPrologixSerialPort);

		try {
			onConnectPrologix();
		}catch (Exception e) {
			prlogixConnected = false;
			logger.catching(Level.DEBUG, e);
			CalibrationApp.showAlert("Connection Error.", "Unable to connect to Proligix USB to GPIB adapter.", AlertType.ERROR);
		}

		// Clear text field 'Value'
		chbPrologixCommand.getSelectionModel().selectedItemProperty().addListener((o,ov,nv)->tfPrologixValue.setText(""));
	}

	@FXML void onConnectPrologix(){

    		if(SerialPortWorker.connect(chbPrologixSerialPort, btnPrologixConnect))
				try {
					prologixWorker.setSerialPort((SerialPort) chbPrologixSerialPort.getUserData());
					onPreset();
					prlogixConnected = true;
				}catch (Exception e) {
					prlogixConnected = false;
					logger.catching(Level.DEBUG, e);
					CalibrationApp.showAlert("Connection Error.", "Unable to connect to Proligix USB to GPIB adapter.", AlertType.ERROR);
				}
    }

    @FXML void onPreset() throws SerialPortException, PrologixTimeoutException {
		prologixWorker
		.preset(
				bytes->{
					String string = new String(bytes);
					taPrologixAnswers.setText(string);
				}, timeout);
    }

    @FXML void onSendPrologix() {
    	try {

    		String text = tfPrologixValue.getText();
			String txtTimeout = tfTimeout.getText();
			int timeout = Optional.ofNullable(txtTimeout)

					.map(to->to.replaceAll("\\D", ""))
					.filter(to->!to.isEmpty())
					.map(Integer::valueOf)
					.filter(to->to!=0)
					.orElse(DEFAULT_TIMEOUT);

			tfTimeout.setText(Integer.toString(timeout));

			final Consumer<byte[]> consumer = bytes->{
				logger.debug("{}", bytes);
				taPrologixAnswers.setText(taPrologixAnswers.getText() + new String(bytes));
			};
			prologixWorker.send(text, timeout,
					consumer);

		}catch (Exception e) {
			prlogixConnected = false;
			logger.catching(Level.DEBUG, e);
			CalibrationApp.showAlert("Connection Error.", "Unable to connect to Proligix USB to GPIB adapter.", AlertType.ERROR);
		}
    }

    @FXML void onWrapTextPrplogix(ActionEvent event) {
    	CheckBox cb = (CheckBox) event.getSource();
    	taPrologixAnswers.setWrapText(cb.isSelected());
    }

    private void fillPrologixCommandsChoiceBox() {

		final PrologixCommand[] values = PrologixCommand.values();
		Arrays.sort(values, (a,b)->a.name().compareTo(b.name()));
		final ObservableList<PrologixCommand> observableArrayList = FXCollections.observableArrayList(values);
		chbPrologixCommand.setItems(observableArrayList);
	}

	private void showHelp(PrologixCommand prologixCommand) {
		taPrologixAnswers.setText(prologixCommand.getDescription());
	}

	public void setAddress(Integer addr) {
		prologixWorker.setAddress(addr);
	}

	public void enableFontPanel() {
		prologixWorker.enableFontPanel();
	}

	public void sendToolCommand(String command, Consumer<byte[]> consumer, int timeout) throws SerialPortException, PrologixTimeoutException {
		prologixWorker.get(command, consumer, timeout);
	}

	public boolean isPrologixConnected() {
		return Optional.ofNullable(prlogixConnected).orElse(false);
	}
}
