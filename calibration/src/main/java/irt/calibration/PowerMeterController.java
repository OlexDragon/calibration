package irt.calibration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.prefs.Preferences;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.beans.Average;
import irt.calibration.helpers.ThreadWorker;
import irt.calibration.tools.CommandType;
import irt.calibration.tools.ToolCommand;
import irt.calibration.tools.power_meter.PM_Language;
import irt.calibration.tools.power_meter.PM_Model;
import irt.calibration.tools.power_meter.PowerMeterWorker;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class PowerMeterController extends AnchorPane {
	private final static Logger logger = LogManager.getLogger();

	@FXML private TextField					 tfPMAddress;
    @FXML private ChoiceBox<PM_Model>		 chbPMModel;
    @FXML private ChoiceBox<PM_Language>	 chbPMLanguage;
    @FXML private ChoiceBox<ToolCommand> chbPMCommand;
    @FXML private TextField					 tfPMValue;
    @FXML private TextField					 tfTimeout;
    @FXML private TextArea					 taPMAnswers;
    @FXML private CheckBox					 cbShowHelp;
    @FXML private CheckBox					 cbWrapText;
    @FXML private Button					 btnSend;

	private Preferences prefs = Preferences.userNodeForPackage(getClass());

	private final PrologixController prologixController;
private PowerMeterWorker powerMeterWorker;

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

		Optional.ofNullable(prefs.getInt("power_meter_address", -1)).filter(addr->addr>0).ifPresent(addr->tfPMAddress.setText(Integer.toString(addr)));
		tfPMAddress.focusedProperty()
		.addListener(
				(o, ov, nv)->
				Optional.of(nv)
				.filter(v->!v)
				.map(v->tfPMAddress.getText().replaceAll("\\D", ""))
				.filter(v->!v.isEmpty()).map(v->Integer.parseInt(v))
				.ifPresent(v->prefs.putInt("power_meter_address", v)));

		chbPMLanguage.getSelectionModel().selectedItemProperty()
		.addListener(
				(o,ov,nv)->{
					btnSend.setDisable(true);
					tfPMValue.setDisable(true);
					chbPMCommand.setDisable(!nv.getPowerMeterCommands().isPresent());
				});
		chbPMCommand.getSelectionModel().selectedItemProperty()
		.addListener(
				(o,ov,nv)->{
					btnSend.setDisable(false);
					tfPMValue.setDisable(Optional.ofNullable(nv).map(v->v.getCommandType()==CommandType.GET).orElse(true));
				});

		powerMeterWorker = new PowerMeterWorker(prologixController, chbPMLanguage, chbPMCommand);
	}

    @FXML void onWrapTextPrplogix(ActionEvent event) {
    	CheckBox cb = (CheckBox) event.getSource();
    	taPMAnswers.setWrapText(cb.isSelected());
    }

    @FXML void onSendPowerMeter() {
    	try {

    		sendCommand();
 
    	} catch (Exception e) {
			logger.catching(e);
		}
    }

    @FXML void onEnableFontPanel() {
    	prologixController.enableFontPanel();
    }

    @FXML
    void onAverage() {
    	taPMAnswers.setText("");
    	ThreadWorker.runThread(()->{
			final Integer addr = getAddress();
			final Integer timeout = getTimeout();
			final Average average = new Average();
			for(int i=0; i<100; i++) {
				synchronized (average) {
					powerMeterWorker.getValue(addr, timeout, bytes->average.addValue(bytesToDouble(bytes)));
				}
				Platform.runLater(()->{
					final String text = taPMAnswers.getText();
					synchronized (average) {
						final double averageValue = average.getAverageValue();
						taPMAnswers.setText(text + "\naverage=" + averageValue + "; " + average);
					}
				});
				try { Thread.sleep(100); } catch (InterruptedException e) { }
			}
    	});
    }

    @FXML
    void onLanguage() {
    	ThreadWorker.runThread(()->{
			final Integer addr = getAddress();
			final Integer timeout = getTimeout();
			powerMeterWorker.get(PM_Language.getToolCommand(null), addr, timeout, bytes->taPMAnswers.setText(new String(bytes)));
    	});
    }
	private double bytesToDouble(byte[] bytes) {
		final int index = IntStream.range(0, bytes.length).filter(b->b==10).findAny().orElse(-1);
		if(index>=0)
			bytes = Arrays.copyOfRange(bytes, 0, index);
		return Double.parseDouble(new String(bytes).trim());
	}

    private void sendCommand() {
		get(bytes->taPMAnswers.setText(taPMAnswers.getText() + "\n" + bytesToDouble(bytes)));
	}

	public void get(Consumer<byte[]> consumer) {
		Optional.ofNullable(chbPMCommand.getSelectionModel().getSelectedItem())
		.ifPresent(
				command->{

					logger.error(command);

					final Integer addr = getAddress();
					final Integer timeout = getTimeout();
					powerMeterWorker.get(command, addr, timeout, consumer);
				});
	}

	private Integer getTimeout() {
		return Optional.of(tfTimeout.getText())

				.map(t -> t.replaceAll("\\S", ""))
				.filter(t -> !t.isEmpty())
				.map(Integer::parseInt)
				.orElse(2000);
	}

	private Integer getAddress() {
		return Optional.of(tfPMAddress.getText())

				.map(t->t.replaceAll("\\D", ""))
				.filter(t->!t.isEmpty())
				.map(Integer::parseInt)
				.orElse(13);
	}
}
