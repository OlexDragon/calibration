package irt.calibration;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class CalibrationController{

	private UnitController unitDetailsController;
	private PrologixController prologixController;

    @FXML private BorderPane borderPaneUnit;
    @FXML private TextField tfUnitAddress;


    @FXML private TabPane tabPaneCalibration;
    @FXML private Tab tabUnit;
    @FXML private Tab tabPrologix;

    @FXML void initialize() throws IOException {

    	unitDetailsController = new UnitController();
    	tabUnit.setContent(unitDetailsController);

		prologixController = new PrologixController();
		tabPrologix.setContent(prologixController);
   }
}
