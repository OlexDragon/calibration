package irt.calibration.tools.power_meter;

import java.util.function.Consumer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.PrologixController;
import irt.calibration.tools.ToolCommand;
import irt.calibration.tools.power_meter.commands.HP437_Command;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;

public class PowerMeterWorker {
	private static final Logger logger = LogManager.getLogger();

	private final PrologixController prologixController;

	public PowerMeterWorker(PrologixController prologixController, ChoiceBox<PM_Language> chbPMLanguage, ChoiceBox<ToolCommand> chbPMCommand) {

		this.prologixController = prologixController;

		ObservableList<PM_Language> value = FXCollections.observableArrayList(PM_Language.values()).sorted((a,b)->a.toString().compareTo(b.toString()));
		chbPMLanguage.setItems(value);

		final SingleSelectionModel<PM_Language> selectionModel = chbPMLanguage.getSelectionModel();
		selectionModel.selectedItemProperty()
		.addListener(
				(o,ov,nv)->{
					nv.getPowerMeterCommands()
					.ifPresent(
							commands->{
								ObservableList<ToolCommand> v = FXCollections.observableArrayList(commands);
								chbPMCommand.setItems(v);
								chbPMCommand.getSelectionModel().select(5);
							});
					synchronized (PrologixController.class) {
						prologixController.sendToolCommand(PM_Language.getToolCommand(nv).getCommand(), null, 1000);
					}
				});
		selectionModel.select(0);
		synchronized (PrologixController.class) {
			prologixController.sendToolCommand(PM_Language.getToolCommand(null).getCommand(),
					bytes->{
						try {

							String name = new String(bytes).trim();
							final PM_Language language = PM_Language.valueOf(name);
							logger.error(language.toString());
							selectionModel.select(language);

						} catch (Exception e) {
							logger.catching(Level.ERROR, e);
						}
					}, 1000);
		}
	}

	public void get(ToolCommand command, int addr, int timeout, Consumer<byte[]> consumer) {
		synchronized (PrologixController.class) {

			prologixController.setAddress(addr);
			prologixController.sendToolCommand(command.getCommand(), consumer, timeout);
		}
	}

	public void getValue(int addr, int timeout, Consumer<byte[]> consumer) {
		get(HP437_Command.DEFAULT_READ, addr, timeout, consumer);
	}
}
