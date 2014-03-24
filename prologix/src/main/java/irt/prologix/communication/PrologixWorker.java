package irt.prologix.communication;

import irt.prologix.data.PrologixGpibUsbController.Commands;
import irt.prologix.data.PrologixGpibUsbController.CommandsInterface;
import irt.prologix.data.PrologixGpibUsbController.DeviceType;
import irt.prologix.data.PrologixGpibUsbController.Eos;
import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.value.Enums.FalseOrTrue;
import jssc.SerialPortException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class PrologixWorker {

	private Logger logger = (Logger) LogManager.getLogger();

	private ComPort comPort;

	public ComPort getComPort() {
		return comPort;
	}

	public PrologixWorker(ComPort comPort) throws Exception {
		logger.entry(comPort);

		this.comPort = comPort;

		if(getSaveConfig()!=FalseOrTrue.FALSE)
			setSaveConfig(FalseOrTrue.FALSE);

		logger.exit();
	}

	public DeviceType getMode() throws Exception{
		logger.entry(comPort);
		byte[] read = sendCommand(Commands.MODE);

		DeviceType mode = null;

		Byte index = readToByte(read);
		DeviceType[] values = DeviceType.values();

		if(index!=null && index<values.length)
			mode = values[index];

		return logger.exit(mode);
	}

	public void setMode( DeviceType deviceType) throws Exception{
		logger.entry(deviceType);
		sendCommand(Commands.MODE.setValue(deviceType.ordinal()));
	}

	public Byte getAddr() throws Exception{
		logger.entry();
		return logger.exit(readToByte(sendCommand(Commands.ADDR)));
	}

	public void setAddr(int addr) throws Exception{
		logger.entry(addr);
		sendCommand(Commands.ADDR.setValue(addr));
	}

	public FalseOrTrue isReadAfterWrite() throws Exception{
		Byte bytesToInteger = readToByte(sendCommand(Commands.READ_AFTER_WRITE));
		return logger.exit(bytesToInteger!=null ? FalseOrTrue.values()[bytesToInteger] : null);
	}

	public void setReadAfterWrite(FalseOrTrue falseOrTrue) throws Exception{
		logger.entry(falseOrTrue);
		sendCommand(Commands.READ_AFTER_WRITE.setValue(falseOrTrue.ordinal()));
	}

	public void clearToolSettings() throws Exception{
		logger.entry();
		sendCommand(Commands.CLR);
	}

	public FalseOrTrue getSaveConfig() throws Exception{
		Byte bytesToInteger = readToByte(sendCommand(Commands.SAVECFG));
		return logger.exit(bytesToInteger!=null ? FalseOrTrue.values()[bytesToInteger] : null);
	}

	public void setSaveConfig(FalseOrTrue falseOrTrue) throws Exception{
		logger.entry(falseOrTrue);
		sendCommand(Commands.SAVECFG.setValue(falseOrTrue.ordinal()));
	}

	public byte[] sendCommand(CommandsInterface command, boolean waitForAnswer, Eos waitFor, int waitTime) throws Exception {
		logger.entry(command, waitForAnswer, waitFor, waitTime);
		clearComPort();
		return logger.exit(comPort.send(command.getCommand(), waitTime, waitForAnswer, waitFor.toString()));
	}

	//----------------------------------------------------------------------------------------------
	private byte[] sendCommand(CommandsInterface command) throws Exception {
		logger.entry(command);
		return logger.exit(comPort.send(command.getCommand(), 2000, command.getValue()==null, Eos.LF.toString()));
	}

	public Byte readToByte(byte[] read) {
		Byte index = null;

		if(read!=null){

			String str = new String(read);
			logger.trace("new String({}) = {}", read, str);

			str = str.replaceAll("\\D", "");
			if(!str.isEmpty() && str.length()<=3)
				index = Byte.parseByte(str);
		}
		return logger.exit(index);
	}

	private void clearComPort() throws SerialPortException {
		byte[] clear = comPort.clear();
		if(clear!=null)
			logger.warn("Clear={}, clear as string = {}", clear, new String(clear));
	}
}
