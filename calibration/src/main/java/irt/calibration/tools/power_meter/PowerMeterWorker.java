package irt.calibration.tools.power_meter;

import java.util.function.Consumer;

import irt.calibration.PrologixController;
import irt.calibration.exception.PrologixTimeoutException;
import irt.calibration.tools.ToolCommand;
import irt.calibration.tools.power_meter.commands.HP437_Command;
import jssc.SerialPortException;

public class PowerMeterWorker {

	private final PrologixController prologixController;

	public PowerMeterWorker(PrologixController prologixController) {

		this.prologixController = prologixController;
	}

	public void get(ToolCommand command, int timeout, Consumer<byte[]> consumer) throws SerialPortException, PrologixTimeoutException {
		synchronized (PrologixController.class) {
			prologixController.sendToolCommand(command.getCommand(), consumer, timeout);
		}
	}

	public void getValue(int timeout, Consumer<byte[]> consumer) throws SerialPortException, PrologixTimeoutException {
		get(HP437_Command.DEFAULT_READ, timeout, consumer);
	}
}
