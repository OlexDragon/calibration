package irt.calibration;

import static irt.calibration.exception.ExceptionWrapper.catchConsumerException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.beans.Average;
import irt.calibration.exception.PrologixTimeoutException;
import irt.calibration.helpers.ThreadWorker;
import irt.calibration.tools.CommandType;
import irt.calibration.tools.Tool;
import irt.calibration.tools.ToolCommand;
import irt.calibration.tools.power_meter.PM_Language;
import irt.calibration.tools.power_meter.PM_Model;
import irt.calibration.tools.power_meter.PowerMeterWorker;
import irt.calibration.tools.power_meter.commands.HP437_Command;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import jssc.SerialPortException;

public class PowerMeterController extends AnchorPane implements Tool{
	private final static Logger logger = LogManager.getLogger();

	public static final int DEFAULT_ADDRESS = 13;
	private static final int DEFAULT_TIMEOUT = 1000;

	@FXML private TextField					 tfPMAddress;
    @FXML private ChoiceBox<PM_Model>		 chbPMModel;
    @FXML private ChoiceBox<PM_Language>	 chbPMLanguage;
    @FXML private ChoiceBox<ToolCommand>	 chbPMCommand;
    @FXML private TextField					 tfPMValue;
    @FXML private TextField					 tfTimeout;
    @FXML private TextArea					 taPMAnswers;
    @FXML private CheckBox					 cbShowHelp;
    @FXML private CheckBox					 cbWrapText;
    @FXML private Button					 btnSend;
    @FXML private Button					 btbAverage;

	private final PrologixController prologixController;
	private final PowerMeterWorker powerMeterWorker;

	private Integer address;
	private Integer timeout;

	private boolean powerMeterConnected = false;

	public PowerMeterController(PrologixController prologixController) {

		this.prologixController = prologixController;

		powerMeterWorker = new PowerMeterWorker(prologixController);

		try {

			String fxmlFile = "/fxml/PowerMeter.fxml";
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();

		} catch (IOException exc) {
			logger.catching(exc);
		}
	}

	@FXML void initialize() throws IOException {
		Tool.initializeTextField(tfPMAddress, "power_meter_address", DEFAULT_ADDRESS, v->address=v);
		Tool.initializeTextField(tfTimeout, "power_meter_timeout", DEFAULT_TIMEOUT, v->timeout=v);
		initializeChoiceBoxes();
	}

	@FXML void onWrapTextPrplogix(ActionEvent event) {
    	CheckBox cb = (CheckBox) event.getSource();
    	taPMAnswers.setWrapText(cb.isSelected());
    }

    @FXML void onSend() {
    	ThreadWorker.runThread(()->{
    		try {
    			synchronized (ToolCommand.class) {
    				sendCommand();
    			}
    		} catch (Exception e) {
    			logger.catching(e);
    		}
    	});
    }

    @FXML void onEnableFontPanel() {
    	prologixController.enableFontPanel();
    }

    @FXML
    void onAverage() {
    	Platform.runLater(()->taPMAnswers.setText(""));
    	ThreadWorker.runThread(()->{
    		Platform.runLater(()->btbAverage.setDisable(true));
			final Average average = new Average();
			synchronized (ToolCommand.class) {
				for(int i=0; i<100; i++) {
					try {
						powerMeterWorker.getValue(getTimeout(),
								bytes->{
									logger.error("{} : {}", new String(bytes).trim(), bytes);
									Optional.ofNullable(bytes).filter(b->b.length>0)
									.ifPresent(
											b->{
												synchronized (average) {
													average.addValue(bytesToDouble(bytes));
												}
												Platform.runLater(()->{
													final String text = taPMAnswers.getText();
													synchronized (average) {
														final double averageValue = average.getAverageValue();
														taPMAnswers.setText(text + "\naverage=" + averageValue + "; " + average);
													}
												});
											});
								});
					} catch (SerialPortException | PrologixTimeoutException e) {
						logger.catching(e);
					}
//					try { Thread.sleep(1000); } catch (InterruptedException e) { }
				}
			}
			Platform.runLater(()->btbAverage.setDisable(false));
    	});
    }

    @FXML
    void onLanguage() throws SerialPortException, PrologixTimeoutException {
		synchronized (ToolCommand.class) {
			powerMeterWorker.get(PM_Language.getToolCommand(null), getTimeout(), bytes->{
				final String value = new String(bytes).trim();
				taPMAnswers.setText(value);
				PM_Language language = PM_Language.valueOf(value);
				chbPMLanguage.getSelectionModel().select(language);
			});
		}
    }

    private void initializeChoiceBoxes() {
		chbPMCommand.getSelectionModel().selectedItemProperty()
		.addListener(
				(o,ov,nv)->{
					btnSend.setDisable(false);
					tfPMValue.setDisable(Optional.ofNullable(nv).map(v->v.getCommandType()==CommandType.GET).orElse(true));
				});

		ObservableList<PM_Language> value = FXCollections.observableArrayList(PM_Language.values()).sorted((a,b)->a.toString().compareTo(b.toString()));
		chbPMLanguage.setItems(value);

		final SingleSelectionModel<PM_Language> selectionModel = chbPMLanguage.getSelectionModel();
		selectionModel.selectedItemProperty()
		.addListener(
				(o,ov,nv)->{
					nv.getPowerMeterCommands()
					.ifPresent(
							commands->{

								// Disable Buttons
								btnSend.setDisable(true);
								tfPMValue.setDisable(true);
								chbPMCommand.setDisable(!nv.getPowerMeterCommands().isPresent());

								ObservableList<ToolCommand> items = FXCollections.observableArrayList(commands);
								chbPMCommand.setItems(items);
								chbPMCommand.getSelectionModel().select(HP437_Command.DEFAULT_READ);
							});
					synchronized (ToolCommand.class) {
						try {
							setAddress();
							prologixController.sendToolCommand(PM_Language.getToolCommand(nv).getCommand(), null, 1000);
						} catch (SerialPortException | PrologixTimeoutException e) {
							logger.catching(e);
						}
					}
				});
		selectionModel.select(0);
		
		try {
			prologixController.sendToolCommand(PM_Language.getToolCommand(null).getCommand(),
							bytes->{
								try {

									String name = new String(bytes).trim();
									logger.error(name);
									final PM_Language language = PM_Language.valueOf(name);
									selectionModel.select(language);

								} catch (Exception e) {
									logger.catching(Level.ERROR, e);
								}
							}, getTimeout());
			powerMeterConnected = true;
		} catch (Exception e) {

			PrologixTimeoutException ex = CalibrationApp.getException(PrologixTimeoutException.class, e);
			if(ex!=null) {
				logger.catching(Level.DEBUG, e);
				CalibrationApp.showAlert("Timeout.", "Unable to read Power Meter settings.", AlertType.ERROR);
				powerMeterConnected = false;
				return;
			}

			logger.catching(e);
		}
	}

    private int getTimeout() {
		return Optional.ofNullable(timeout).orElse(DEFAULT_TIMEOUT);
	}

	private double bytesToDouble(byte[] bytes) {

		final int index = IntStream.range(0, bytes.length).filter(b->bytes[b]==(byte)10).findAny().orElse(-1) + 1;

		byte[] b;
		if(index>0 && index<bytes.length)
			b = Arrays.copyOfRange(bytes, 0, index);
		else
			b = bytes;

		final String trim = new String(b).trim();

		if(trim.isEmpty())
			return Double.NaN;

		return Double.parseDouble(trim);
	}

    private void sendCommand() {
		get(bytes->Platform.runLater(()->taPMAnswers.setText(taPMAnswers.getText() + "\n" + bytesToDouble(bytes))));
	}

	public void get(Consumer<byte[]> consumer) {
		Optional.ofNullable(chbPMCommand.getSelectionModel().getSelectedItem())
		.ifPresent(
				catchConsumerException(
				command->{

					synchronized (ToolCommand.class) {
						setAddress();
						powerMeterWorker.get(command, getTimeout(), consumer);
					}
				}));
	}

	@Override
	public void setAddress() {
		prologixController.setAddress(address);
	}

	public boolean isPowerMeterConnected() {
		return powerMeterConnected;
	}
}
