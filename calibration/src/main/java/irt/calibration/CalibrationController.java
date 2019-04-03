package irt.calibration;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class CalibrationController{

	private PowerMeterController powerMeterController;
	private UnitController		 unitDetailsController;
	private PrologixController	 prologixController;

    @FXML private BorderPane borderPaneUnit;
    @FXML private TextField tfUnitAddress;

    @FXML private TabPane tabPaneCalibration;
    @FXML private Tab tabPowerMeter;
    @FXML private Tab tabUnit;
    @FXML private Tab tabPrologix;

    @FXML void initialize() throws IOException {

    	powerMeterController = new PowerMeterController();
    	tabPowerMeter.setContent(powerMeterController);

    	unitDetailsController = new UnitController();
    	tabUnit.setContent(unitDetailsController);

		prologixController = new PrologixController();
		tabPrologix.setContent(prologixController);
   }
}
