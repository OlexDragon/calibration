package irt.unit.groups;

import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.RegisterValue;
import irt.serial_protocol.data.StringData;
import irt.serial_protocol.data.packet.Packet;
import irt.serial_protocol.data.packet.PacketHeader;
import irt.serial_protocol.data.packet.PacketHeader.Type;
import irt.serial_protocol.data.packet.Payload;
import irt.unit.data.UnitValue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public abstract class Group {

	@Override
	public String toString() {
		return "Group [packet=" + packet + "]";
	}

	protected final Logger logger = (Logger) LogManager.getLogger(getClass().getName());

	public short PARAMETER_ALL = 0xFF;

	interface Parameter{ public byte getId(); }

	public enum Params implements Parameter{
		NONE				(Packet.IRT_SLCP_PARAMETER_NONE	),
		ALL					(Packet.IRT_SLCP_PARAMETER_ALL	);

		private byte parameter;

		private Params(byte parameter){
			this.parameter = parameter;
		}

		public byte getId() {
			return parameter;
		}
	}

	public enum UnitType{
		CONVERTER,
		BUC
	}
//***************************************************************************************************
	private Packet packet;
	private UnitType UnitType;

	public abstract irt.serial_protocol.data.packet.PacketHeader.Group getGroup();

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		logger.entry(packet);
		if(packet!=null && packet.getHeader().getGroupId()==getGroup().getId())
			this.packet = packet;
	}

	public Packet getPacket(ComPort comPort, Parameter parameter, short packetId) {
		logger.entry(comPort, parameter, packetId);
		if(comPort!=null && comPort.isOpened()){
			PacketHeader packetHeader = new PacketHeader(Type.REQUEST, getGroup(), packetId);
			Payload payload = new Payload(parameter.getId(), null);
			Packet p = new Packet(packetHeader, payload);
			logger.trace(p);

			setPacket(comPort.send(p));
		}else
			packet = null;

		return logger.exit(packet);
	}

	public Packet getAll(ComPort comPort){
		return getPacket(comPort, Params.ALL, PARAMETER_ALL);
	}

	public Integer getInt(Parameter parameter) {
		logger.entry(parameter);
		Integer integer = null;

		if(packet != null){
			Payload payload = packet.getPayload(parameter.getId());
			logger.trace(payload);

			if(payload != null) integer = payload.getInt(0);
		}

		return logger.exit(integer);
	}

	public UnitValue getUnitValue(Parameter parameter) {
		logger.entry(parameter);
		UnitValue unitValue = null;

		if(packet != null){
			Payload payload = packet.getPayload(parameter.getId());
			logger.trace(payload);

			if(payload != null){

				unitValue = new UnitValue();

				switch(payload.getParameterHeader().getSize()){
				case 1:
					unitValue.setFlags((byte) 1);
					unitValue.setValue(payload.getByte());
					break;
				case 2:
					unitValue.setFlags((byte) 1);
					unitValue.setValue(payload.getShort(0));
					break;
				case 3:
					unitValue.setFlags(payload.getByte());
					unitValue.setValue(payload.getShort((byte)1));
					break;
				default:
					unitValue = null;
				}
			}
		}
		return logger.exit(unitValue);
	}

	public StringData getStringData(Parameter parameter) {
		logger.entry(parameter);
		StringData stringData = null;

		if(packet != null){
			Payload payload = packet.getPayload(parameter.getId());
			logger.trace(payload);

			if(payload != null)
				stringData = payload.getStringData();
		}

		return logger.exit(stringData);
	}

	public UnitValue getUnitValue(ComPort comPort, Parameter parameter, short packetId){
		logger.entry(parameter, packetId);

		UnitValue unitValue = null;


			getPacket(comPort, parameter, packetId);
			unitValue = getUnitValue(parameter);

		return logger.exit(unitValue);
	}

	public <T> UnitValue setUnitValue(ComPort comPort, Parameter parameter, short packetId, T value) {
		logger.entry(comPort, parameter, packetId, value);

		UnitValue unitValue = null;

		if(comPort!=null && comPort.isOpened()){

			PacketHeader packetHeader = new PacketHeader(value!=null ? Type.COMMAND : Type.REQUEST, getGroup(), packetId);
			Payload payload = new Payload(parameter.getId(), Packet.toBytes(value));
			Packet p = new Packet(packetHeader, payload);
			logger.trace(p);

			setPacket(comPort.send(p));
			unitValue = getUnitValue(parameter);
		}

		return logger.exit(unitValue);
	}

	public UnitType getUnitType() {
		return UnitType;
	}

	public void setUnitType(UnitType unitType) {
		UnitType = unitType;
	}

	protected RegisterValue getRegisterValue() {
		RegisterValue registerValue = null;
		if(packet!=null){
			Payload pl = packet.getPayload(0);
			if(pl!=null)
				registerValue = pl.getRegisterValue();
		}
		return registerValue;
	}

	public boolean hasAnswer(){
		return packet!=null && packet.getHeader().getType()==Type.RESPONSE.getType();
	}
}
