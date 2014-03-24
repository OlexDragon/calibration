package irt.unit.groups;

import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.PacketWork;
import irt.serial_protocol.data.RegisterValue;
import irt.serial_protocol.data.packet.Packet;
import irt.serial_protocol.data.packet.PacketHeader;
import irt.serial_protocol.data.packet.PacketHeader.Group;
import irt.serial_protocol.data.packet.PacketHeader.Type;
import irt.serial_protocol.data.packet.Payload;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;


public class DeviceDebugGroup extends irt.unit.groups.Group {

	private final Logger logger = (Logger) LogManager.getLogger();

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

	public enum ADC{

		INPUT_POWER(new RegisterValue(10, 0, null),	 PacketWork.PACKET_ID_FCM_ADC_INPUT_POWER),
		OUTPUT_POWER(new RegisterValue(10, 1, null), PacketWork.PACKET_ID_FCM_ADC_OUTPUT_POWER);

		private RegisterValue registerValue;
		private short packetId;


		private ADC(RegisterValue registerValue, short packetId){
			this.registerValue = registerValue;
			this.packetId = packetId;
		}
		public short getPacketId() {
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

	public RegisterValue getADCRegister(ComPort comPort, ADC value) {
		logger.entry(comPort, value);

		if(comPort!=null && comPort.isOpened()){

			PacketHeader packetHeader = new PacketHeader(Type.REQUEST, getGroup(), value.getPacketId());
			Payload payload = new Payload(value.getParameter().getId(), Packet.toBytes(value.getRegisterValue()));
			Packet p = new Packet(packetHeader, payload);

			setPacket(comPort.send(p));

		}

		return logger.exit(getRegisterValue());
	}

	public RegisterValue getADCRegister(ComPort comPort, ADC value, int times) {
		logger.entry(comPort, value, times);
		RegisterValue adcRegister = null;
		long sum = 0;

		for(int i=0; i<times; i++){
			try { Thread.sleep(1000); } catch (InterruptedException e) { logger.catching(e); }
			adcRegister = getADCRegister(comPort, value);
			sum += adcRegister.getValue().getValue();
			logger.debug("adcRegister={}, sum={}",adcRegister, sum);
		}

		logger.trace("sum = {}", sum);

		adcRegister.getValue().setValue(sum/times);

		return logger.exit(adcRegister);
	}
}
