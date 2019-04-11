package irt.calibration;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.PrologixController.AoutoMode;
import irt.calibration.exception.PrologixTimeoutException;
import irt.calibration.tools.CommandType;
import irt.calibration.tools.Tool;
import irt.calibration.tools.ToolCommand;
import irt.calibration.tools.furnace.data.CommandParameter;
import irt.calibration.tools.signal_generator.commands.SG_SCPICommand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import jssc.SerialPortException;

public class SignalGeneratorController extends AnchorPane implements Tool {
	private final static Logger logger = LogManager.getLogger();

	public static final int DEFAULT_ADDRESS = 19;
	private static final int DEFAULT_TIMEOUT = 1000;

	private final PrologixController prologixController;

	private Integer address;
	private Integer timeout;

	public SignalGeneratorController(PrologixController prologixController) {

		this.prologixController = prologixController;

		try {

			String fxmlFile = "/fxml/SignalGenerator.fxml";
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();

		} catch (IOException exc) {
			logger.catching(exc);
		}
	}

	@FXML private TextField tfAddress;
    @FXML private ChoiceBox<?> chbModel;
    @FXML private ChoiceBox<SG_SCPICommand> chbCommand;
    @FXML private ChoiceBox<CommandParameter> chbCommandParameter;
    @FXML private TextField tfValue;
    @FXML private Button btnGet;
    @FXML private Button btnSet;
    @FXML private TextField tfTimeout;
    @FXML private CheckBox cbShowHelp;
    @FXML private CheckBox cbWrapText;
    @FXML private TextArea taAnswers;

	@FXML void initialize() throws IOException {
		Tool.initializeTextField(tfAddress, "signal_generator_address", DEFAULT_ADDRESS, v->address=v);
		Tool.initializeTextField(tfTimeout, "signal_generator_timeout", DEFAULT_TIMEOUT, v->timeout=v);
		ObservableList<SG_SCPICommand> value = FXCollections.observableArrayList(SG_SCPICommand.values());
		chbCommand.setItems(value);
		chbCommand.getSelectionModel().selectedItemProperty()
		.addListener(
				(o,ov,nv)->{
					logger.error("{} : {}", nv.getParameterClass(), nv.getParameterValues());
					nv.getParameterValues()
					.map(pv->FXCollections.observableArrayList(pv))
					.ifPresent(chbCommandParameter::setItems);
				});

		chbCommandParameter.getSelectionModel().selectedItemProperty()
		.addListener(
				(o,ov,nv)->{
					CommandType commandType = Optional.ofNullable(nv).map(CommandParameter::getCommandType).orElse(null);
					enable(commandType);
				});
	}

    @FXML void onGet() {
    	final SG_SCPICommand command = chbCommand.getSelectionModel().getSelectedItem();
    	try {
    		get(command, bytes->taAnswers.setText(taAnswers.getText() + "\n" + new String(bytes)));
		} catch (SerialPortException | PrologixTimeoutException e) {
			logger.catching(e);
		}
    }

	@FXML void onSet() {
    	final SG_SCPICommand command = chbCommand.getSelectionModel().getSelectedItem();
    	final CommandParameter parameter = chbCommandParameter.getSelectionModel().getSelectedItem();
    	final String value = tfValue.getText();

		try {
			set(command, parameter, value);
		} catch (SerialPortException | PrologixTimeoutException e) {
			logger.catching(e);
		}
    }

	@FXML void onWrapTextPrplogix() {

    }

	@Override
	public void setAddress() {
		prologixController.setAddress(address);
	}

	private void enable(CommandType commandType) {

		if(commandType==null) {
			btnGet.setDisable(true);
			btnSet.setDisable(true);
			tfValue.setDisable(true);
			return;
		}

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
	}

    private void get(SG_SCPICommand scpiCommand, Consumer<byte[]> consumer) throws SerialPortException, PrologixTimeoutException {
    	

		synchronized (ToolCommand.class) {
			prologixController.setAuto(AoutoMode.ON);
			String command = scpiCommand.getCommand() + "?";
			prologixController.sendToolCommand(command , consumer, timeout);
		}
	}

    public void set(SG_SCPICommand scpiCommand, CommandParameter parameter, String value) throws SerialPortException, PrologixTimeoutException {

		synchronized (ToolCommand.class) {
			prologixController.setAuto(AoutoMode.OFF);
			setAddress();
			String command = scpiCommand.getCommand() + parameter.toString(value);
			prologixController.sendToolCommand(command , null, timeout);
		}
	}
}
