package irt.calibration;

import java.io.IOException;
import java.util.Optional;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.data.Action;
import irt.calibration.data.CommandType;
import irt.calibration.data.power_meter.PM_Language;
import irt.calibration.data.power_meter.PM_Model;
import irt.calibration.data.power_meter.PowerMeterWorker;
import irt.calibration.data.power_meter.commands.PowerMeterCommand;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class PowerMeterController extends AnchorPane implements Action {

	private final static Logger logger = LogManager.getLogger();

	@FXML private TextField					 tfPMAddress;
    @FXML private ChoiceBox<PM_Model>		 chbPMModel;
    @FXML private ChoiceBox<PM_Language>	 chbPMLanguage;
    @FXML private ChoiceBox<PowerMeterCommand> chbPMCommand;
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

		powerMeterWorker = new PowerMeterWorker(chbPMLanguage, chbPMCommand);

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
	}

    @FXML void onWrapTextPrplogix(ActionEvent event) {
    	CheckBox cb = (CheckBox) event.getSource();
    	taPMAnswers.setWrapText(cb.isSelected());
    }

    @FXML void onSendPowerMeter() {
    	try {
    		Optional.ofNullable(chbPMCommand.getSelectionModel().getSelectedItem()).ifPresent(this::sendCommand);
 
    	} catch (Exception e) {
			logger.catching(e);
		}
    }

    @FXML void onEnableFontPanel() {
    	prologixController.enableFontPanel();
    }

	@Override
	public void takeTction() {
		Optional.of(tfPMAddress.getText()).map(t->t.replaceAll("\\D", "")).filter(t->!t.isEmpty()).map(Integer::parseInt).ifPresent(prologixController::setAddress);
	}

	private void sendCommand(PowerMeterCommand command) {
		logger.error(command);
		prologixController.sendToolCommand(
				command.getCommand(),
				bytes->taPMAnswers.setText(
						taPMAnswers.getText() + "\n" + Double.parseDouble(new String(bytes))),
				Optional.of(tfTimeout.getText()).map(t->t.replaceAll("\\S", "")).filter(t->!t.isEmpty()).map(Integer::parseInt).orElse(2000));
	}
}
