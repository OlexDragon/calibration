package irt.calibration;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.data.furnace.FurnaceWorker;
import irt.calibration.data.furnace.data.SCP_220_Command;
import irt.calibration.data.furnace.data.SettingData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class FurnaceController extends AnchorPane{
	private final static Logger logger = LogManager.getLogger();

	private Preferences prefs = Preferences.userNodeForPackage(getClass());

    @FXML private TextField tfAddress;
    @FXML private ChoiceBox<SCP_220_Command> chbCommand;
    @FXML private ChoiceBox<SettingData> chbCommandParameter;
    @FXML private TextField tfValue;
    @FXML private TextField tfTimeout;
    @FXML private CheckBox cbShowHelp;
    @FXML private CheckBox cbWrapText;
    @FXML private TextArea taAnswers;
    @FXML private Button btnGet;
    @FXML private Button btnSet;

	private final PrologixController prologixController;

	public FurnaceController(PrologixController prologixController) {

		this.prologixController = prologixController;

		try {

			String fxmlFile = "/fxml/Furnace.fxml";
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();

		} catch (IOException exc) {
			logger.catching(exc);
		}
	}

	@FXML void initialize() throws IOException {

		Optional.ofNullable(prefs.getInt("furnace_address", -1)).filter(addr->addr>0).ifPresent(addr->tfAddress.setText(Integer.toString(addr)));
		tfAddress.focusedProperty()
		.addListener(
				(o, ov, nv)->
				Optional.of(nv)
				.filter(v->!v)
				.map(v->tfAddress.getText().replaceAll("\\D", ""))
				.filter(v->!v.isEmpty()).map(v->Integer.parseInt(v))
				.ifPresent(v->prefs.putInt("power_meter_address", v)));

		chbCommand.getSelectionModel().selectedItemProperty()
		.addListener(
				(o,ov,nv)->{
					btnSet.setDisable(true);
					btnGet.setDisable(true);
					tfValue.setDisable(true);
					chbCommandParameter.setDisable(!nv.getDataClassValues().isPresent());
				});
		chbCommandParameter.getSelectionModel().selectedItemProperty()
		.addListener(
				(o,ov,nv)->{
					btnGet.setDisable(false);
					btnSet.setDisable(false);
					tfValue.setDisable(false);
				});

		new FurnaceWorker(chbCommand, chbCommandParameter);
	}

	@FXML void onGet() {

		final SCP_220_Command selectedCommand = chbCommand.getSelectionModel().getSelectedItem();
		String commandGet = selectedCommand.commandGet();

		final SettingData selectedData = chbCommandParameter.getSelectionModel().getSelectedItem();
		final String data = selectedData.toString(null);
		if(data!=null && !data.isEmpty())
			commandGet += data;

		sendCommand(commandGet);
	}

    @FXML  void onSet() {

		final SCP_220_Command selectedCommand = chbCommand.getSelectionModel().getSelectedItem();

		final SettingData selectedData = chbCommandParameter.getSelectionModel().getSelectedItem();
		final String value = tfValue.getText().trim();

		String commandSet = selectedCommand.commandSet(selectedData, value);

		sendCommand(commandSet);
	

    }

    @FXML void onWrapText() {

    }

	private void sendCommand(String command) {
		sendCommand(command, bytes->taAnswers.setText(taAnswers.getText() + "\n" + new String(bytes)));
	}

	private void sendCommand(String command, Consumer<byte[]> consumer) {
		logger.error(command);
		synchronized (PrologixController.class) {

			final Integer addr = Optional.of(tfAddress.getText()).map(t->t.replaceAll("\\D", "")).filter(t->!t.isEmpty()).map(Integer::parseInt).orElse(2);
			prologixController.setAddress(addr);

			final Integer timeout = Optional.of(tfTimeout.getText()).map(t -> t.replaceAll("\\S", "")).filter(t -> !t.isEmpty()).map(Integer::parseInt).orElse(2000);
			prologixController.sendToolCommand(command, consumer, timeout);
		}
	}
}
