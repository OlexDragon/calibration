package irt.power_meter;

import irt.power_meter.data.EPM_441A;
import irt.prologix.communication.PrologixWorker;
import irt.prologix.communication.Worker;
import irt.prologix.communication.Tools.Commands;
import irt.prologix.data.PrologixGpibUsbController.Eos;

public class PowerMeterWorker extends Worker {

	private EPM_441A powerMeter;

	public PowerMeterWorker(PrologixWorker prologixWorker, EPM_441A powerMeter) throws Exception {
		super(prologixWorker);
		this.powerMeter = powerMeter;

		powerMeter.setId(getId());
	}

	@Override
	public byte getAddr() {
		return powerMeter.getAddr();
	}

	public String measure() throws Exception {
		logger.entry();
		checkAddress();
		byte[] read = getPrologixWorker().sendCommand(Commands.MEASURE, true, Eos.LF, 10000);
		logger.trace("read={}", read);

		if(read!=null)
			powerMeter.setPower(new String(read));

		return logger.exit(read!=null ? powerMeter.getPower().toString() : null);
	}
}
