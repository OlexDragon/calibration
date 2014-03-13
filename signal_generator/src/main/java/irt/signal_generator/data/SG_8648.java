package irt.signal_generator.data;

import irt.prologix.communication.Tools;
import irt.serial_protocol.data.value.ValueDouble;
import irt.serial_protocol.data.value.ValueFrequency;


public class SG_8648 extends Tools{

	public enum OnOrOff{
		OFF,
		ON;

		public boolean isOn(){
			return name().equals("ON");
		}
	}

	private byte addr = 19;
	private ValueFrequency 	valueFrequency;
	private ValueDouble 	valuePower;

	public void setId(String id){
		logger.entry(id);

		super.setId(id);

		if(id!=null)
			if(id.contains("8648B"))
						valueFrequency = new ValueFrequency( "1GHz", "9 kHz", "2 GHz");
					else//8648C
						valueFrequency = new ValueFrequency( "1GHz", "9 kHz", "3.2 GHz");
		valueFrequency.setDoToStringFofrat(false);

		valuePower = new ValueDouble(-100, -129.3, 20.7, 1);

		logger.trace(valueFrequency);
	}

	public byte getAddr() {
		return logger.exit(addr);
	}

	public void setAddr(byte addr) {
		logger.entry(addr);
		this.addr = addr;
	}

	public String getFrequency() {
		return logger.exit(valueFrequency.toString());
	}

	public ValueFrequency getValueFrequency() {
		return valueFrequency;
	}

	public ValueDouble getValuePower() {
		return valuePower;
	}

	public String getPower() {
		return logger.exit(valuePower.toString());
	}

	public void setFrequency(String value) {
		logger.entry(value);
		valueFrequency.setValue(value);
		logger.exit(valueFrequency);
	}

	public void setFrequency(long value) {
		logger.entry(value);
		valueFrequency.setValue(value);
		logger.exit(valueFrequency);
	}

	public void setPower(String value) {
		logger.entry(value);
		valuePower.setValue(value);
		logger.exit(valuePower);
	}

	public void setPower(long value) {
		logger.entry(value);
		valuePower.setValue(value);
		logger.exit(valuePower);
	}

	public byte[] getFrequencySetCommand(long value) {
		logger.entry(value);
		setFrequency(value);
		return logger.exit(Commands.FREQUENCY.getCommand(valueFrequency.toString()));
	}

	public byte[] getPowerSetCommand(long value) {
		logger.entry(value);
		setPower(value);
		return logger.exit(Commands.AMPLITUDE.getCommand(valuePower.toString()));
	}

	public byte[] getRFOnSetCommand(OnOrOff onOrOff) {
		return Commands.RF_ON.getCommand(onOrOff.name());
	}

	/**
	 * false - off, true - on
	 */
//	private boolean powerReference;
//	private boolean isPowerOn;
//		switch(valueChangeEvent.getID()){
//		case PrologixGpibUsbController.ID_TOOLS_SIGNAL_GENERATOP:
//
//			IdValue idValue = (IdValue)valueChangeEvent.getSource();
//			String value = (String) idValue.getValue();
//
//			if(value!=null && !value.isEmpty())
//				switch(idValue.getID()){
//				case FREQUENCY:
//					if(value.contains("8648B"))
//						valueFrequency = new ValueFrequency( value, "9 kHz", "2 GHz", -1);
//					else//8648C
//						valueFrequency = new ValueFrequency( value, "9 kHz", "3.2 GHz", -1);
//
//					firePacketListener(new ValueChangeEvent(valueFrequency, FREQUENCY));
//					break;
//				case POWER_REF:
//					firePacketListener(new ValueChangeEvent(powerReference = Integer.parseInt(value)==1, POWER_REF));
//					break;
//				case POWER:
//					valuePower = new ValueDouble(Double.parseDouble(value), -129.3, 20.7, 1);
//					valuePower.setPrefix(powerReference ? " dB" : " dBm");
//					firePacketListener(new ValueChangeEvent(valuePower, POWER));
//					break;
//				case POWER_ON_OFF:
//					isPowerOn = Integer.parseInt(value)>0;
//					firePacketListener(new ValueChangeEvent(isPowerOn, POWER_ON_OFF));
//					break;
//				case ID:
//					firePacketListener(new ValueChangeEvent(id=value, ID));
//			}
//		}

//	public void getAll() {
//		if(gpibController.isSet()){
//			try {
//				if(gpibController.getEos()!=GPIBController.EOS_LF)
//					gpibController.setEos(GPIBController.EOS_LF);
//
//				gpibController.sendCommand(PrologixGpibUsbController.ID_TOOLS_SIGNAL_GENERATOP, ID,			addr,	"*IDN?",			true, (short) 500);
//				gpibController.sendCommand(PrologixGpibUsbController.ID_TOOLS_SIGNAL_GENERATOP, POWER_REF,	addr,	"POW:REF:STAT?",	true, (short) 500);
//				gpibController.sendCommand(PrologixGpibUsbController.ID_TOOLS_SIGNAL_GENERATOP, POWER,		addr,	"POW:AMPL?",		true, (short) 500);
//				gpibController.sendCommand(PrologixGpibUsbController.ID_TOOLS_SIGNAL_GENERATOP, POWER_ON_OFF,addr,	"OUTP:STAT?",		true, (short) 500);
//				gpibController.sendCommand(PrologixGpibUsbController.ID_TOOLS_SIGNAL_GENERATOP, FREQUENCY,	addr,	"FREQ:CW?",			true, (short) 500);
//
//			} catch (SerialPortException e) { e.printStackTrace(); }
//		}
//	}
//
//	public ValueFrequency getValueFrequency() {
//		return valueFrequency;
//	}
//
//	public ValueDouble getValuePower() {
//		return valuePower;
//	}
//
//	public byte getAddr() {
//		return addr;
//	}
//
//	public void setPower(String powerStr) throws SerialPortException {
//		gpibController.sendCommand(PrologixGpibUsbController.ID_TOOLS_SIGNAL_GENERATOP, POWER, addr, "POW:AMPL "+powerStr, false, (short) 0);
//	}
//
//	public void setFrequency(String freqStr) throws SerialPortException {
//		gpibController.sendCommand(PrologixGpibUsbController.ID_TOOLS_SIGNAL_GENERATOP, FREQUENCY, addr, "FREQ:CW "+freqStr, false, (short) 0);
//	}
//
//	public void setPowerOnOff() throws SerialPortException {
//		isPowerOn = !isPowerOn;
//		gpibController.sendCommand(PrologixGpibUsbController.ID_TOOLS_SIGNAL_GENERATOP, POWER_ON_OFF, addr, "OUTP:STAT "+(isPowerOn ? "ON" : "OFF"), false, (short) 0);
//	}
//
//	public boolean isPowerOn() {
//		return isPowerOn;
//	}
//
//	public String getId() {
//		return id;
//	}
//
//
}
