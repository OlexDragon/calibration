package irt.calibration.tools.furnace;

import irt.calibration.tools.furnace.data.SCP_220_Command;
import irt.calibration.tools.furnace.data.SettingData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;

public class FurnaceWorker {
//	private final static Logger logger = LogManager.getLogger();

	public FurnaceWorker(ChoiceBox<SCP_220_Command> chbCommand, ChoiceBox<SettingData> chbCommandParameter) {
		ObservableList<SCP_220_Command> value = FXCollections.observableArrayList(SCP_220_Command.values()).sorted((a,b)->a.toString().compareTo(b.toString()));
		chbCommand.setItems(value);

		final SingleSelectionModel<SCP_220_Command> selectionModel = chbCommand.getSelectionModel();
		selectionModel.selectedItemProperty()
		.addListener(
				(o,ov,nv)->nv.getDataClassValues()
						.ifPresent(
								commands->{
									ObservableList<SettingData> v = FXCollections.observableArrayList(commands);
									chbCommandParameter.setItems(v);
									chbCommandParameter.getSelectionModel().select(0);
								})
				);
		selectionModel.select(0);
	}
}
