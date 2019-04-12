package irt.calibration;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.exception.PrologixTimeoutException;
import irt.calibration.helpers.SerialPortWorker;
import irt.calibration.tools.SimpleToolCommand;
import irt.calibration.tools.Tool;
import irt.calibration.tools.ToolCommand;
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

	private static final int DEFAULT_TIMEOUT = 100;

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

		Tool.initializeTextField( tfTimeout, "prologix_timeout", DEFAULT_TIMEOUT, v->timeout=v);
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

    @FXML void onClear(ActionEvent event) {
    	taPrologixAnswers.setText("");
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
    	send(PrologixCommand.VER, null, timeout, getConsumer(null));
    	send(PrologixCommand.SAVECFG, "0", timeout, getConsumer(null));
    	send(PrologixCommand.MODE, "1", timeout, getConsumer(null));
    }

	@FXML void onSendPrologix() {
    	try {

    		String value = tfPrologixValue.getText();
			send(getSelectedCommand(), value, timeout, getConsumer(null));

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

	private Integer address;
	public void setAddress(Integer addr) {

		if(address!=null && address.equals(addr))
			return;

		this.address = addr;
		send(PrologixCommand.ADDR, Optional.ofNullable(addr).map(i->i.toString()).orElse(null), timeout, getConsumer(null));
	}

	private AutoMode autoMode;
	public void setAuto(AutoMode autoMode) {

		if(this.autoMode!=null && this.autoMode.equals(autoMode))
			return;

		this.autoMode = autoMode;
		send(PrologixCommand.AUTO ,Integer.toString(autoMode.ordinal()), timeout, getConsumer(null));
	}

	private final static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yy HH:mm:SS -> ");
	private void writeToTextArea(ToolCommand command, String value) {
		Date date = new Date();
		taPrologixAnswers.appendText( '\n' + DATE_FORMAT.format(date) + command +  (value==null ? " " : ": " + value));
	}

	private Consumer<byte[]> getConsumer(Consumer<byte[]> consumer) {
		return bytes->{

			Optional.ofNullable(consumer).ifPresent(c->c.accept(bytes));

			String answer = new String(bytes);
			logger.debug("{} : {}", answer, bytes);
			taPrologixAnswers.appendText(" = " + answer +'\n');
		};
	}

	public void enableFontPanel() {
		send(PrologixCommand.LOC, null, timeout, getConsumer(null));
	}

	public void sendToolCommand(ToolCommand command, Consumer<byte[]> consumer, int timeout) throws SerialPortException, PrologixTimeoutException {
		send(new SimpleToolCommand(command.getCommand(), command.getCommandType()), null, timeout, getConsumer(consumer));
	}

	public boolean isPrologixConnected() {
		return Optional.ofNullable(prlogixConnected).orElse(false);
	}

    private void send(ToolCommand command, String value, Integer timeout, Consumer<byte[]> consumer) {
		writeToTextArea(command, value);
    	prologixWorker.send(command, value, timeout, consumer);
	}

	private PrologixCommand getSelectedCommand() {
		return chbPrologixCommand.getSelectionModel().getSelectedItem();
	}

	public enum AutoMode {
		OFF,
		ON
	}
}
