package irt.calibration;

import java.io.IOException;
import java.util.Optional;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.data.power_meter.PM_Language;
import irt.calibration.data.power_meter.PM_Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class PowerMeterController extends AnchorPane {

	private final static Logger logger = LogManager.getLogger();

	@FXML private TextField					 tfPMAddress;
    @FXML private ChoiceBox<PM_Model>		 chbPMModel;
    @FXML private ChoiceBox<PM_Language>		 chbPMLanguage;
    @FXML private ChoiceBox<String>			 chbPMCommand;
    @FXML private TextField					 tfPowerMeterValue;
    @FXML private TextField					 tfTimeout;
    @FXML private TextArea					 taPrologixAnswers;
    @FXML private CheckBox					 cbShowHelp;
    @FXML private CheckBox					 cbWrapText;

	private Preferences prefs = Preferences.userNodeForPackage(getClass());

	public PowerMeterController() {

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
	}

    @FXML void onWrapTextPrplogix(ActionEvent event) {
    	CheckBox cb = (CheckBox) event.getSource();
    	taPrologixAnswers.setWrapText(cb.isSelected());
    }

    @FXML void onSendPowerMeter() {
    	try {

 
    	} catch (Exception e) {
			logger.catching(e);
		}
    }
}
