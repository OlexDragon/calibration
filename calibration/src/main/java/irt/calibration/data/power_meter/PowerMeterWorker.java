package irt.calibration.data.power_meter;

import irt.calibration.data.power_meter.commands.PowerMeterCommand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

public class PowerMeterWorker {

	public PowerMeterWorker(ChoiceBox<PM_Language> chbPMLanguage, ChoiceBox<PowerMeterCommand> chbPMCommand) {
		ObservableList<PM_Language> value = FXCollections.observableArrayList(PM_Language.values());
		chbPMLanguage.setItems(value);

		chbPMLanguage.getSelectionModel().selectedItemProperty()
		.addListener(
				(o,ov,nv)->nv.getPowerMeterCommands()
				.ifPresent(
						comands->{
							ObservableList<PowerMeterCommand> v = FXCollections.observableArrayList(comands);
							chbPMCommand.setItems(v);
						})
				);
	}

	public void send(PowerMeterCommand command) {
		// TODO Auto-generated method stub
		
	}

}
