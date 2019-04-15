package irt.calibration;

import static irt.calibration.exception.ExceptionWrapper.catchConsumerException;
import static irt.calibration.helpers.OptionalIfElse.of;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.PrologixController.AutoMode;
import irt.calibration.backgroumd.anotations.CalibrationValue;
import irt.calibration.beans.Average;
import irt.calibration.exception.PrologixTimeoutException;
import irt.calibration.helpers.ThreadWorker;
import irt.calibration.tools.CommandType;
import irt.calibration.tools.Tool;
import irt.calibration.tools.ToolCommand;
import irt.calibration.tools.power_meter.PM_Language;
import irt.calibration.tools.power_meter.PM_Model;
import irt.calibration.tools.power_meter.commands.HP437_Command;
import javafx.application.Platform;
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

	@CalibrationValue("Yes #2")
	private Integer address;
	private Integer timeout;

	private boolean powerMeterConnected = false;

	public PowerMeterController(PrologixController prologixController) {

		this.prologixController = prologixController;

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
    				sendCommand(null);
    			}
    		} catch (Exception e) {

				if(prologixTimeoutException(e))
						return;

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
						getValue(timeout,
								bytes->{

									Optional.ofNullable(bytes).filter(b->b.length>0)
									.ifPresent(
											b->{
												ThreadWorker.runThread(()->{
													synchronized (average) {
														average.addValue((Number) HP437_Command.DEFAULT_READ.getAnswerConverter().apply(bytes));
													}
													Platform.runLater(()->{
														synchronized (average) {
															final double averageValue = average.getAverageValue();
															taPMAnswers.appendText( "\naverage=" + averageValue + "; " + average);
														}
													});
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

			final ToolCommand toolCommand = PM_Language.getToolCommand(null);

			final Consumer<byte[]> consumer =
					bytes->{
						final String value = toolCommand.getAnswerConverter().apply(bytes).toString();
						taPMAnswers.setText(value);
						PM_Language language = PM_Language.valueOf(value);
						chbPMLanguage.getSelectionModel().select(language);
					};

			sendCommand(toolCommand, timeout, consumer);
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
					of(nv.getPowerMeterCommands().filter(cs->cs.length>0))
					.ifPresent(
							commands->{
								// Disable Button and text field
								btnSend.setDisable(true);
								tfPMValue.setDisable(true);
								chbPMCommand.setDisable(false);

								ObservableList<ToolCommand> items = FXCollections.observableArrayList(commands);
								chbPMCommand.setItems(items);
								chbPMCommand.getSelectionModel().select(HP437_Command.DEFAULT_READ);
							})
					.ifNotPresent(()->{
						btnSend.setDisable(true);
						tfPMValue.setDisable(true);
						chbPMCommand.setDisable(true);
					});
					if(powerMeterConnected)
						try {
							final ToolCommand toolCommand = PM_Language.getToolCommand(nv);
							logger.debug(toolCommand);
							sendCommand(toolCommand, timeout, null);
						} catch (SerialPortException | PrologixTimeoutException e) {
							logger.catching(e);
						}
				});
		selectionModel.select(0);

		ObservableList<PM_Model> models = FXCollections.observableArrayList(PM_Model.values());
		chbPMModel.setItems(models);
		int index = prefs.getInt("power_meter_model", 0);

		SingleSelectionModel<PM_Model> sm = chbPMModel.getSelectionModel();
		sm.select(index);
		sm.selectedIndexProperty().addListener((o, ov, nv)->Optional.of(nv.intValue()).filter(i->i>=0).ifPresent(i->prefs.putInt("power_meter_model", i)));

		Optional.ofNullable(sm.getSelectedItem()).filter(model->model.getLanguages()!=null)
		.ifPresent(languages->{

			try {
				final Consumer<byte[]> consumer = bytes->{
					try {

						String name = new String(bytes).trim();
						final PM_Language language = PM_Language.valueOf(name);
						selectionModel.select(language);

					} catch (Exception e) {
						logger.catching(Level.ERROR, e);
					}
				};
				sendCommand(PM_Language.getToolCommand(null), timeout, consumer);
				powerMeterConnected = true;
			} catch (Exception e) {

				if(prologixTimeoutException(e))
						return;

				logger.catching(e);
			}
		});
	}

	public boolean prologixTimeoutException(Exception e) {
		PrologixTimeoutException ex = CalibrationApp.getException(PrologixTimeoutException.class, e);

		if(ex==null) 
			return false;

		logger.catching(Level.DEBUG, e);
		CalibrationApp.showAlert("Timeout.", "Unable to read Power Meter data.", AlertType.ERROR);
		powerMeterConnected = false;
		return true;
	}

	public void sendCommand(Consumer<byte[]> consumer) {
		Optional.ofNullable(chbPMCommand.getSelectionModel().getSelectedItem())
		.ifPresent(
				catchConsumerException(
				command->{
					synchronized (ToolCommand.class) {
						taPMAnswers.appendText( PrologixController.DATE_FORMAT.format(new Date()) + command + " : ");
						sendCommand(command, timeout, getConsumer(command, consumer));
					}
				}));
	}

private Consumer<byte[]> getConsumer(ToolCommand command, Consumer<byte[]> consumer) {
		return bytes->{
			Optional.ofNullable(consumer).ifPresent(c->c.accept(bytes));
			final Object object = command.getAnswerConverter().apply(bytes);
			taPMAnswers.appendText(object.toString());
		};
	}

//	@Override
//	public void setAddress() {
//	}

	public boolean isPowerMeterConnected() {
		return powerMeterConnected;
	}

	public void sendCommand(ToolCommand command, int timeout, Consumer<byte[]> consumer) throws SerialPortException, PrologixTimeoutException {
		synchronized (PrologixController.class) {
			prologixController.setAddress(address);
			prologixController.setAuto(command.getCommandType()!=CommandType.SET ? AutoMode.ON : AutoMode.OFF);
			prologixController.sendToolCommand(command, consumer, timeout);
		}
	}

	public void getValue(int timeout, Consumer<byte[]> consumer) throws SerialPortException, PrologixTimeoutException {
		sendCommand(HP437_Command.DEFAULT_READ, timeout, consumer);
	}
}
