package irt.calibration.data.power_meter;

import irt.calibration.data.power_meter.commands.PowerMeterCommand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;

public class PowerMeterWorker {

	public PowerMeterWorker(ChoiceBox<PM_Language> chbPMLanguage, ChoiceBox<PowerMeterCommand> chbPMCommand) {
		ObservableList<PM_Language> value = FXCollections.observableArrayList(PM_Language.values());
		chbPMLanguage.setItems(value);

		final SingleSelectionModel<PM_Language> selectionModel = chbPMLanguage.getSelectionModel();
		selectionModel.selectedItemProperty()
		.addListener(
				(o,ov,nv)->nv.getPowerMeterCommands()
				.ifPresent(
						commands->{
							ObservableList<PowerMeterCommand> v = FXCollections.observableArrayList(commands);
							chbPMCommand.setItems(v);
							chbPMCommand.getSelectionModel().select(5);
						})
				);
		selectionModel.select(0);
	}
}
