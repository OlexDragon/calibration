package irt.calibration;

import java.io.IOException;

import irt.calibration.tools.Tool;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class MainController {

    @FXML private BorderPane borderPaneUnit;
    @FXML private TextField tfUnitAddress;

    @FXML private TabPane tabPaneCalibration;
    @FXML private Tab tabFurnace;
    @FXML private Tab tabSignalGenerator;
    @FXML private Tab tabPowerMeter;
    @FXML private Tab tabUnit;
    @FXML private Tab tabPrologix;

    @FXML void initialize() throws IOException {

    	//Prologix
    	final PrologixController prologixController = new PrologixController();
		tabPrologix.setContent(prologixController);

		final boolean prologixConnected = prologixController.isPrologixConnected();
		final SingleSelectionModel<Tab> selectionModel = tabPaneCalibration.getSelectionModel();
		if(!prologixConnected)
			selectionModel.select(tabPrologix);

		// Power Meter
		final PowerMeterController powerMeterController = new PowerMeterController(prologixController);
    	tabPowerMeter.setContent(powerMeterController);
    	tabPowerMeter.setOnSelectionChanged(onTabSelected(powerMeterController));

    	final boolean powerMeterConnected = powerMeterController.isPowerMeterConnected();
    	if(!powerMeterConnected && selectionModel.getSelectedIndex()==0)
			selectionModel.select(tabPowerMeter);

    	// Signal Generator
		final SignalGeneratorController signalGeneratorController = new SignalGeneratorController(prologixController);
		tabSignalGenerator.setContent(signalGeneratorController);
		tabSignalGenerator.setOnSelectionChanged(onTabSelected(signalGeneratorController));

		// Furnace
		final FurnaceController furnaceController = new FurnaceController(prologixController);
		tabFurnace.setContent(furnaceController);
		tabFurnace.setOnSelectionChanged(onTabSelected(furnaceController));

		// Unit (BUC, Converter, ...
		final UnitController unitDetailsController = new UnitController();
    	tabUnit.setContent(unitDetailsController);
    }

	public EventHandler<Event> onTabSelected(final Tool controller) {
		return e->{
//			if(((Tab)e.getSource()).isSelected())
//				controller.setAddress();
		};
	}
}
