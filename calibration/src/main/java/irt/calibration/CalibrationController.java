package irt.calibration;

import static irt.calibration.exception.ExceptionWrapper.catchConsumerException;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.helpers.CalibrationWorker;
import irt.calibration.helpers.ThreadWorker;
import irt.calibration.tools.Tool;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;

public class CalibrationController extends AnchorPane implements Tool{
	private final static Logger logger = LogManager.getLogger();

	public CalibrationController() {

		try {

			String fxmlFile = "/fxml/Calibration.fxml";
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();

		} catch (IOException exc) {
			logger.catching(exc);
		}
	}

    @FXML private ChoiceBox<String> chbCalibrationType;
    @FXML private ChoiceBox<Path> chbCalibrationSequence;
    @FXML private TextArea taAnswers;


	@FXML void initialize() throws IOException {
		fillCalibrationTypes();
	}

	private void fillCalibrationTypes() throws IOException {
		ObservableList<String> items = FXCollections.observableArrayList(CalibrationWorker.getCalibrationTypes());
		chbCalibrationType.setItems(items );
	}

    @FXML void onCreateNewSquence() {
    }

    @FXML void onCreateNewType() {

    	TextInputDialog dialog = new TextInputDialog();
    	dialog.initOwner(chbCalibrationType.getScene().getWindow());
    	dialog.setTitle("Calibration Type");
    	dialog.setHeaderText("Enter the calibration type name in the text field.");
    	dialog.setContentText("Type Name:");
    	addListener(dialog);

    	dialog.showAndWait()
    	.ifPresent(
    			catchConsumerException(
    					newType->{
    						ThreadWorker.runThread(()->CalibrationWorker.creatNewCalibrationType(newType));
    						fillCalibrationTypes();
    					}));
    }

	private void addListener(TextInputDialog dialog) {

		Node button = dialog.getDialogPane().lookupButton(ButtonType.OK);
		button.setDisable(true);

		((TextField) dialog.getDialogPane().lookup(".text-field")).textProperty()
		.addListener(
				(o,ov,nv)->{
					if(nv.trim().isEmpty()) {

						button.setDisable(true);
						return;
					}
					boolean typeContainsThisName = chbCalibrationType.getItems().parallelStream().filter(name->name.equalsIgnoreCase(nv)).findAny().isPresent();
					button.setDisable(typeContainsThisName);
				});
	}
}
