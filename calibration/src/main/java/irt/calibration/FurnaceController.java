package irt.calibration;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.PrologixController.AutoMode;
import irt.calibration.anotations.CalibrationTool;
import irt.calibration.anotations.ToolAction;
import irt.calibration.exception.PrologixTimeoutException;
import irt.calibration.tools.CommandType;
import irt.calibration.tools.CommandWithParameter;
import irt.calibration.tools.Tool;
import irt.calibration.tools.ToolCommand;
import irt.calibration.tools.furnace.data.CommandParameter;
import irt.calibration.tools.furnace.data.CommandParameter.NeedValue;
import irt.calibration.tools.furnace.data.ConstantMode;
import irt.calibration.tools.furnace.data.SCP_220_Command;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import jssc.SerialPortException;

@CalibrationTool("Temperature Chamber")
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
					tfValue.setText("");
					Optional.ofNullable(nv)
					.ifPresent(this::disableNodesd);
				});
		tfValue.textProperty().addListener((o,ov,nv)->btnSet.setDisable(nv.isEmpty()));

		ObservableList<SCP_220_Command> value = FXCollections.observableArrayList(SCP_220_Command.values()).sorted((a,b)->a.toString().compareTo(b.toString()));
		chbCommand.setItems(value);

		final SingleSelectionModel<SCP_220_Command> selectionModel = chbCommand.getSelectionModel();
		selectionModel.selectedItemProperty()
		.addListener(
				(o,ov,nv)->nv.getParameterValues()
						.ifPresent(
								commands->{
									ObservableList<CommandParameter> v = FXCollections.observableArrayList(commands);
									chbCommandParameter.setItems(v);
									chbCommandParameter.getSelectionModel().select(0);
								})
				);
		selectionModel.select(0);
	}

	private void disableNodesd(CommandParameter commandParameter) {

		final CommandType commandType = commandParameter.getCommandType();

		btnGet.setDisable(commandType==CommandType.SET_WITH_ANSWER);
		tfValue.setText("");

		switch(commandType) {

		case GET:
			btnSet.setDisable(true);
			tfValue.setDisable(true);
			break;

		default:
			NeedValue needValue = commandParameter.getNeedValue();
			tfValue.setDisable(needValue==NeedValue.NO);
			btnSet.setDisable(needValue!=NeedValue.NO);
		}
	}

    @FXML void onGetTemperature() throws SerialPortException, PrologixTimeoutException {
    	getTemperature(null);
    }

	public void getTemperature(final Consumer<byte[]> consumer) throws SerialPortException, PrologixTimeoutException {
		sendCommand(SCP_220_Command.TEMP,  ConstantMode.GET, null, consumer);
	}

	public void setTemperature(double target, final Consumer<byte[]> consumer) throws SerialPortException, PrologixTimeoutException {
		sendCommand(SCP_220_Command.TEMP,  ConstantMode.TARGET, tfValue.getText(), consumer);
	}

	@FXML void onGet() throws SerialPortException, PrologixTimeoutException {

		final SCP_220_Command selectedCommand = chbCommand.getSelectionModel().getSelectedItem();
		final CommandParameter selectedParameter = chbCommandParameter.getSelectionModel().getSelectedItem();
		final String value = tfValue.getText();

		sendCommand(selectedCommand,  selectedParameter, value, null);
	}

    @FXML void onSet() throws SerialPortException, PrologixTimeoutException {

		final SCP_220_Command selectedCommand = chbCommand.getSelectionModel().getSelectedItem();
		final CommandParameter selectedParameter = chbCommandParameter.getSelectionModel().getSelectedItem();
		final String value = tfValue.getText().trim();

		sendCommand(selectedCommand, selectedParameter, value, null);
    }

    @FXML void onWrapText() {

    }

	private void sendCommand(CommandWithParameter toolCommand, CommandParameter commandParameter, String value, Consumer<byte[]> consumer) throws SerialPortException, PrologixTimeoutException {
		synchronized (PrologixController.class) {

			ToolCommand command = toolCommand.getCommand(commandParameter, value);
			Date date = new Date();
			PrologixController.DATE_FORMAT.format(date);
			
			String text = PrologixController.DATE_FORMAT.format(date) +command.getCommand();
			taAnswers.appendText(text);
			prologixController.setAuto(AutoMode.ON);
			prologixController.setAddress(address);
			prologixController.sendToolCommand(command, getConsumer(command.getAnswerConverter(), consumer), timeout);
		}
	}

	private Consumer<byte[]> getConsumer(Function<byte[], Object> converter, Consumer<byte[]> consumer) {
		return bytes->{

			Optional.ofNullable(consumer).ifPresent(c->c.accept(bytes));
			taAnswers.appendText(" = " + converter.apply(bytes).toString());
		};
	}

	@ToolAction("Set Chamber Temperature")
	public void setTemperature(String value) throws SerialPortException, PrologixTimeoutException {
		sendCommand(SCP_220_Command.TEMP, ConstantMode.GET, null, null);
	}
}
