package irt.calibration;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.PrologixController.AutoMode;
import irt.calibration.exception.PrologixTimeoutException;
import irt.calibration.tools.CommandType;
import irt.calibration.tools.SimpleToolCommand;
import irt.calibration.tools.Tool;
import irt.calibration.tools.ToolCommand;
import irt.calibration.tools.furnace.FurnaceWorker;
import irt.calibration.tools.furnace.Temperature;
import irt.calibration.tools.furnace.data.CommandParameter;
import irt.calibration.tools.furnace.data.ConstantMode;
import irt.calibration.tools.furnace.data.SCP_220_Command;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import jssc.SerialPortException;

public class FurnaceController extends AnchorPane implements Tool{
	private final static Logger logger = LogManager.getLogger();

	private static final int DEFAULT_ADDRESS = 2;
	private static final int DEFAULT_TIMEOUT = 1000;

	private Preferences prefs = Preferences.userNodeForPackage(getClass());

    @FXML private TextField tfAddress;
    @FXML private ChoiceBox<SCP_220_Command> chbCommand;
    @FXML private ChoiceBox<CommandParameter> chbCommandParameter;
    @FXML private TextField tfValue;
    @FXML private TextField tfTimeout;
    @FXML private CheckBox cbShowHelp;
    @FXML private CheckBox cbWrapText;
    @FXML private TextArea taAnswers;
    @FXML private Button btnGet;
    @FXML private Button btnSet;

	private final PrologixController prologixController;

	private Integer address;
	private Integer timeout;

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

		Tool.initializeTextField(tfAddress, "furnace_address", DEFAULT_ADDRESS, v->address=v);
		Tool.initializeTextField(tfTimeout, "furnace_timeout", DEFAULT_TIMEOUT, v->timeout=v);

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
					chbCommandParameter.setDisable(!nv.getParameterValues().isPresent());
				});
		chbCommandParameter.getSelectionModel().selectedItemProperty()
		.addListener(
				(o,ov,nv)->{
					final CommandType commandType = nv.getCommandType();
					switch(commandType) {

					case GET:
						btnGet.setDisable(false);
						btnSet.setDisable(true);
						tfValue.setDisable(true);
						break;

					case SET:
						btnGet.setDisable(true);
						btnSet.setDisable(false);
						tfValue.setDisable(false);
						break;

					default:
						btnGet.setDisable(false);
						btnSet.setDisable(false);
						tfValue.setDisable(false);
					
					}
				});

		new FurnaceWorker(chbCommand, chbCommandParameter);
	}

    @FXML void onGetTemperature() throws SerialPortException, PrologixTimeoutException {
		final Consumer<byte[]> consumer = bytes->taAnswers.setText(taAnswers.getText() + "\n" + new Temperature(bytes));
    	getTemperature(consumer);
    }

	public void getTemperature(final Consumer<byte[]> consumer) throws SerialPortException, PrologixTimeoutException {
		sendCommand(SCP_220_Command.TEMP.commandGet(),  consumer);
	}

	public void setTemperature(double target, final Consumer<byte[]> consumer) throws SerialPortException, PrologixTimeoutException {
		sendCommand(SCP_220_Command.TEMP.commandSet(ConstantMode.TARGET, Double.toString(target)),  consumer);
	}

	@FXML void onGet() throws SerialPortException, PrologixTimeoutException {

		final SCP_220_Command selectedCommand = chbCommand.getSelectionModel().getSelectedItem();
		ToolCommand commandGet = selectedCommand.commandGet();

		final CommandParameter selectedData = chbCommandParameter.getSelectionModel().getSelectedItem();
		final String data = selectedData.toString(null);

		final ToolCommand command;
		if(data!=null && !data.isEmpty())
			command = new SimpleToolCommand(commandGet.getCommand()+data, commandGet.getCommandType());
		else
			command = new SimpleToolCommand(commandGet.getCommand(), commandGet.getCommandType());

		sendCommand(command,  bytes->taAnswers.setText(taAnswers.getText() + "\n" + new Temperature(bytes)));
	}

    @FXML  void onSet() throws SerialPortException, PrologixTimeoutException {

		final SCP_220_Command selectedCommand = chbCommand.getSelectionModel().getSelectedItem();

		final CommandParameter selectedData = chbCommandParameter.getSelectionModel().getSelectedItem();
		final String value = tfValue.getText().trim();

		ToolCommand commandSet = selectedCommand.commandSet(selectedData, value);

		sendCommand(commandSet);
    }

    @FXML void onWrapText() {

    }

	private void sendCommand(ToolCommand toolCommand) throws SerialPortException, PrologixTimeoutException {
		sendCommand(toolCommand, bytes->taAnswers.setText(taAnswers.getText() + "\n" + new String(bytes)));
	}

	private void sendCommand(ToolCommand toolCommand, Consumer<byte[]> consumer) throws SerialPortException, PrologixTimeoutException {
		logger.error(toolCommand);
		synchronized (PrologixController.class) {

			prologixController.setAuto(AutoMode.ON);
			prologixController.setAddress(address);
			prologixController.sendToolCommand(toolCommand, consumer, timeout);
		}
	}
}
