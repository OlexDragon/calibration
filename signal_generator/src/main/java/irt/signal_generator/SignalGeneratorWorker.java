package irt.signal_generator;

import irt.prologix.communication.PrologixWorker;
import irt.prologix.communication.Tools.Commands;
import irt.prologix.communication.Worker;
import irt.prologix.data.PrologixGpibUsbController.DeviceType;
import irt.prologix.data.PrologixGpibUsbController.Eos;
import irt.signal_generator.data.SG_8648;
import irt.signal_generator.data.SG_8648.OnOrOff;

public class SignalGeneratorWorker extends Worker{

	private SG_8648 signalGenerator;

	public SignalGeneratorWorker(PrologixWorker prologixWorker, SG_8648 signalGenerator ) throws Exception {
		super(prologixWorker);
		this.signalGenerator = signalGenerator;
		logger.trace("prologixWorker={}, signalGenerator={}", prologixWorker, signalGenerator);

		if (prologixWorker != null) {

			logger.trace("prologixWorker.getMode()!=DeviceType.CONTROLLER");
			if(prologixWorker.getMode()!=DeviceType.CONTROLLER)
				prologixWorker.setMode(DeviceType.CONTROLLER);

			String str = getId();
			signalGenerator.setId(str);

			str = getFrequency();
			signalGenerator.setFrequency(str);

			str = getPower();
			signalGenerator.setPower(str);
		}
		logger.exit();
	}

	public String getPower() throws Exception {
		logger.entry();
		checkAddress();
		byte[] sendCommand = getPrologixWorker().sendCommand(Commands.AMPLITUDE, true, Eos.LF, 1000);

		if(sendCommand!=null)
			signalGenerator.setPower(new String(sendCommand));

		return logger.exit(sendCommand!=null ? signalGenerator.getPower() : null);
	}

	public String getFrequency() throws Exception {
		logger.entry();
		checkAddress();
		byte[] sendCommand = getPrologixWorker().sendCommand(Commands.FREQUENCY, true, Eos.LF, 1000);

		if(sendCommand!=null)
			signalGenerator.setFrequency(new String(sendCommand));

		return logger.exit(sendCommand!=null ? signalGenerator.getFrequency() : null);
	}

	public SG_8648 getSignalGenerator() {
		return signalGenerator;
	}

	public void setSignalGenerator(SG_8648 signalGenerator) {
		this.signalGenerator = signalGenerator;
	}

	public void setFrequency(long value) throws Exception {
		logger.entry(value);
		checkAddress();
		getPrologixWorker().sendCommand(signalGenerator.getFrequencySetCommand(value), false, Eos.LF, 1000);
		logger.exit();
	}

	public String setFrequency(String value) throws Exception {
		logger.entry(value);
		checkAddress();
		getPrologixWorker().sendCommand(signalGenerator.getFrequencySetCommand(value), false, Eos.LF, 1000);
		logger.exit();
		return signalGenerator.getFrequency();
	}

	public String setPower(long value) throws Exception {
		logger.entry(value);
		checkAddress();
		getPrologixWorker().sendCommand(signalGenerator.getPowerSetCommand(value), false, Eos.LF, 1000);
		logger.exit();
		return signalGenerator.getPower();
	}

	public String setPower(String value) throws Exception {
		logger.entry(value);
		checkAddress();
		getPrologixWorker().sendCommand(signalGenerator.getPowerSetCommand(value), false, Eos.LF, 1000);
		logger.exit();
		return signalGenerator.getPower();
	}

	public OnOrOff isRFOn() throws Exception {
		logger.entry();
		checkAddress();
		PrologixWorker prologixWorker = getPrologixWorker();

		byte[] read = prologixWorker.sendCommand(Commands.RF_ON, true, Eos.LF, 1000);
		Byte readToByte = prologixWorker.readToByte(read);
		return logger.exit(readToByte!=null ? OnOrOff.values()[readToByte] : null);
	}

	public void setRFOn(OnOrOff onOrOff) throws Exception {
		logger.entry(onOrOff);
		checkAddress();
		getPrologixWorker().sendCommand(signalGenerator.getRFOnSetCommand(onOrOff), false, Eos.LF, 1000);
		logger.exit();
	}

	@Override
	public byte getAddr() {
		return signalGenerator.getAddr();
	}
}
