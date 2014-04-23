package irt.converter.groups;

import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.PacketWork.PacketId;
import irt.serial_protocol.data.RegisterValue;
import irt.serial_protocol.data.packet.Packet;
import irt.serial_protocol.data.packet.PacketHeader;
import irt.serial_protocol.data.packet.PacketHeader.Group;
import irt.serial_protocol.data.packet.PacketHeader.Type;
import irt.serial_protocol.data.packet.Payload;


public class DeviceDebugGroup extends irt.converter.groups.Group {

	public enum Params implements Parameter{
		INFO			(Packet.IRT_SLCP_PARAMETER_DEVICE_DEBUG_INFO			),
		DUMP			(Packet.IRT_SLCP_PARAMETER_DEVICE_DEBUG_DUMP			),
		READ_WRITE		(Packet.IRT_SLCP_PARAMETER_DEVICE_DEBUG_READ_WRITE		),
		INDEX			(Packet.IRT_SLCP_PARAMETER_DEVICE_DEBUG_INDEX			),
		CALIBRATION_MODE(Packet.IRT_SLCP_PARAMETER_DEVICE_DEBUG_CALIBRATION_MODE),
		ENVIRONMENT_IO	(Packet.IRT_SLCP_PARAMETER_DEVICE_DEBUG_ENVIRONMENT_IO	);

		private byte parameter;

		private Params(byte parameter){
			this.parameter = parameter;
		}

		@Override
		public byte getId() {
			return parameter;
		}
	}

	public interface ADCInterface{
		public Params getParameter();
		public PacketId getPacketId();
		public RegisterValue getRegisterValue();
	}

	public enum ConverterADC implements ADCInterface{

		INPUT_POWER(new RegisterValue(10, 0, null),	 PacketId.DEVICE_DEBAG_INPUT_POWER),
		OUTPUT_POWER(new RegisterValue(10, 1, null), PacketId.DEVICE_DEBUG_OUTPUT_POWER);

		private RegisterValue registerValue;
		private PacketId packetId;


		private ConverterADC(RegisterValue registerValue, PacketId packetId){
			this.registerValue = registerValue;
			this.packetId = packetId;
		}
		public PacketId getPacketId() {
			return packetId;
		}

		public RegisterValue getRegisterValue() {
			return registerValue;
		}

		public Params getParameter(){
			return Params.READ_WRITE;
		}

		@Override
		public String toString() {
			return registerValue+"; packetId="+packetId;
		}
	}

	@Override
	public Group getGroup() {
		return Group.DEVICE_DEBAG;
	}

	public RegisterValue getADCRegister(ComPort comPort, ADCInterface adc) {
		logger.entry(comPort, adc);

		if(comPort!=null && comPort.isOpened())
			setPacket(comPort.send(createPacket(adc)));

		return logger.exit(getRegisterValue());
	}

	public Packet createPacket(ADCInterface adc) {
		PacketHeader packetHeader = new PacketHeader(Type.REQUEST, getGroup(), adc.getPacketId());
		Payload payload = new Payload(adc.getParameter().getId(), Packet.toBytes(adc.getRegisterValue()));
		Packet p = new Packet(packetHeader, payload);
		return p;
	}

	public RegisterValue getADCRegister(ComPort comPort, ADCInterface adc, int times) {
		logger.entry(comPort, adc, times);
		RegisterValue adcRegister = null;
		long sum = 0;

		for(int i=0; i<times; i++){
			try { Thread.sleep(adc instanceof ConverterADC ? 1100 : 110); } catch (InterruptedException e) { logger.catching(e); }
			adcRegister = getADCRegister(comPort, adc);
			sum += adcRegister.getValue().getValue();
			logger.debug("adcRegister={}, sum={}",adcRegister, sum);
		}

		logger.debug("sum = {}", sum);

		adcRegister.getValue().setValue(sum/times);

		return logger.exit(adcRegister);
	}

	public RegisterValue getInputPower(ComPort comPort) {
		return getADCRegister(comPort, ConverterADC.INPUT_POWER);
	}

	public RegisterValue getInputPower(ComPort comPort, int times) {
		return getADCRegister(comPort, ConverterADC.INPUT_POWER, times);
	}

	public RegisterValue getOutputPower(ComPort comPort, int times) {
		return getADCRegister(comPort, ConverterADC.OUTPUT_POWER, times);
	}

	public RegisterValue getOutputPower(ComPort comPort) {
		return getADCRegister(comPort, ConverterADC.OUTPUT_POWER);
	}
}
