package irt.calibration;

import java.io.IOException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.data.prologix.PrologixCommand;
import irt.calibration.helpers.PrologixWorker;
import irt.calibration.helpers.SerialPortWorker;
import irt.calibration.helpers.ThreadWorker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import jssc.SerialPort;
import jssc.SerialPortException;

public class PrologixController extends AnchorPane {

	private static final int DEFAULT_TIMEOUT = 20;

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

	public PrologixController() {

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

	@FXML void initialize() throws IOException, SerialPortException {
		SerialPortWorker.addChoiceBoxs(chbPrologixSerialPort);
		prologixWorker = new PrologixWorker(chbPrologixSerialPort, chbPrologixCommand, cbShowHelp, taPrologixAnswers);
		onConnectPrologix();

		chbPrologixCommand.getSelectionModel().selectedItemProperty().addListener((o,ov,nv)->tfPrologixValue.setText(""));
	}

    @FXML void onConnectPrologix() throws SerialPortException {

    		if(SerialPortWorker.connect(chbPrologixSerialPort, btnPrologixConnect))
    			onPreset();
    }

    @FXML void onPreset() throws SerialPortException {
		prologixWorker.preset();
    }

    @FXML void onSendPrologix() {
    	try {

    		String text = tfPrologixValue.getText();
			String txtTimeout = tfTimeout.getText();
			int timeout = Optional.ofNullable(txtTimeout)

					.map(to->to.replaceAll("\\D", ""))
					.filter(to->!to.isEmpty())
					.map(Integer::valueOf)
					.filter(to->to!=0)
					.orElse(DEFAULT_TIMEOUT);

			tfTimeout.setText(Integer.toString(timeout));

			prologixWorker.send(text, timeout);

    	} catch (Exception e) {
			logger.catching(e);
		}
    }

    @FXML void onWrapTextPrplogix(ActionEvent event) {
    	CheckBox cb = (CheckBox) event.getSource();
    	taPrologixAnswers.setWrapText(cb.isSelected());
    }

	public void getUnitDetails(SerialPort serialPort) {
		ThreadWorker.runThread(()->{
			
		});
	}

}
