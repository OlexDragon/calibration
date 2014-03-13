package irt.prologix.communication;

import irt.prologix.data.PrologixGpibUsbController.Commands;
import irt.prologix.data.PrologixGpibUsbController.DeviceType;
import irt.prologix.data.PrologixGpibUsbController.Eos;
import irt.prologix.data.PrologixGpibUsbController.FalseOrTrue;
import irt.serial_protocol.ComPort;
import jssc.SerialPortException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class PrologixWorker {

	private Logger logger = (Logger) LogManager.getLogger();

	private ComPort comPort;

	public PrologixWorker(ComPort comPort) throws Exception {
		logger.entry(comPort);
		this.comPort = comPort;

		if(getSaveConfig()!=FalseOrTrue.FALSE)
			setSaveConfig(FalseOrTrue.FALSE);
	}

	public DeviceType getMode() throws Exception{
		logger.entry(comPort);
		byte[] read = sendCommand(Commands.MODE, null);

		DeviceType mode = null;

		Byte index = readToByte(read);
		DeviceType[] values = DeviceType.values();

		if(index!=null && index<values.length)
			mode = values[index];

		return logger.exit(mode);
	}

	public void setMode( DeviceType deviceType) throws Exception{
		logger.entry(deviceType);
		sendCommand(Commands.MODE, deviceType.ordinal());
	}

	public Byte getAddr() throws Exception{
		logger.entry();
		return logger.exit(readToByte(sendCommand(Commands.ADDR, null)));
	}

	public void setAddr(int addr) throws Exception{
		logger.entry(addr);
		sendCommand(Commands.ADDR, addr);
	}

	public FalseOrTrue isReadAfterWrite() throws Exception{
		Byte bytesToInteger = readToByte(sendCommand(Commands.READ_AFTER_WRITE, null));
		return logger.exit(bytesToInteger!=null ? FalseOrTrue.values()[bytesToInteger] : null);
	}

	public void setReadAfterWrite(FalseOrTrue falseOrTrue) throws Exception{
		logger.entry(falseOrTrue);
		sendCommand(Commands.READ_AFTER_WRITE, falseOrTrue.ordinal());
	}

	public void clearToolSettings() throws Exception{
		logger.entry();
		sendCommand(Commands.CLR, null);
	}

	public FalseOrTrue getSaveConfig() throws Exception{
		Byte bytesToInteger = readToByte(sendCommand(Commands.SAVECFG, null));
		return logger.exit(bytesToInteger!=null ? FalseOrTrue.values()[bytesToInteger] : null);
	}

	public void setSaveConfig(FalseOrTrue falseOrTrue) throws Exception{
		logger.entry(falseOrTrue);
		sendCommand(Commands.SAVECFG, falseOrTrue.ordinal());
	}

	public byte[] sendCommand( byte[] value, boolean waitForAnswer, Eos waitFor, int waitTime) throws Exception {
		logger.entry(value, waitFor);
		clearComPort();
		return logger.exit(comPort.send(value, waitTime, waitForAnswer, waitFor.toString()));
	}

	//----------------------------------------------------------------------------------------------
	private byte[] sendCommand(Commands command, Integer value) throws Exception {
		logger.entry(command, value);
		clearComPort();
		boolean waitForAnswer = value==null;
		byte[] c = waitForAnswer ? command.getCommand() : command.getCommand(value);
		logger.trace("command={}", c);
		return logger.exit(comPort.send(c, 1000, waitForAnswer, Eos.LF.toString()));
	}

	public Byte readToByte(byte[] read) {
		Byte index = null;

		if(read!=null){
			String str = new String(read).replaceAll("\\D", "");
			logger.trace(str);
			if(!str.isEmpty())
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
