package irt.converter.groups;

import irt.converter.data.UnitValue;
import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.PacketWork.PacketId;
import irt.serial_protocol.data.RegisterValue;
import irt.serial_protocol.data.StringData;
import irt.serial_protocol.data.packet.LinkHeader;
import irt.serial_protocol.data.packet.LinkedPacket;
import irt.serial_protocol.data.packet.Packet;
import irt.serial_protocol.data.packet.PacketHeader;
import irt.serial_protocol.data.packet.PacketHeader.Type;
import irt.serial_protocol.data.packet.Payload;
import irt.serial_protocol.data.value.Value;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public abstract class Group {

	protected final Logger logger = (Logger) LogManager.getLogger(getClass().getName());

	public short PARAMETER_ALL = 0xFF;

	public interface Parameter{ public byte getId(); }

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
	private UnitType unitType;
	private Byte address;

	public abstract irt.serial_protocol.data.packet.PacketHeader.Group getGroup();

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		logger.trace("\nthis.packet = {},\npacket to set = {}", this.packet, packet);
		if(packet!=null && packet.getHeader().getGroupId()==getGroup().getId())
			this.packet = packet;
	}

	public Packet getPacket(ComPort comPort, Parameter parameter, PacketId packetId) {
		logger.entry(comPort, parameter, packetId);

		if(comPort!=null && comPort.isOpened()){
			Packet p = createPacket(parameter, packetId);
			logger.trace(p);

			setPacket(comPort.send(p));
		}else
			packet = null;

		return logger.exit(packet);
	}

	public Packet getPacket(ComPort comPort, Parameter parameter, PacketId packetId, Value value) {
		logger.entry(comPort, parameter, packetId, value);

		if(comPort!=null && comPort.isOpened()){
			Packet p = createPacket(parameter, packetId, value);
			logger.trace(p);

			setPacket(comPort.send(p));
		}else
			packet = null;

		return logger.exit(packet);
	}

	public Packet getAll(ComPort comPort){
		return getPacket(comPort, Params.ALL, PacketId.ALL);
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

	public UnitValue getUnitValue(ComPort comPort, Parameter parameter, PacketId packetId){
		logger.entry(parameter, packetId);

		UnitValue unitValue = null;

		getPacket(comPort, parameter, packetId);
		unitValue = getUnitValue(parameter);

		return logger.exit(unitValue);
	}

	public <T> UnitValue setUnitValue(ComPort comPort, Parameter parameter, PacketId packetId, T value) {
		logger.entry(comPort, parameter, packetId, value);

		UnitValue unitValue = null;

		if (comPort != null && comPort.isOpened()) {

			Packet p = createPacket(parameter, packetId, value);

			setPacket(comPort.send(p));
			unitValue = getUnitValue(parameter);
		}

		return logger.exit(unitValue);
	}

	public UnitType getUnitType() {
		return unitType;
	}

	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
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

	public Byte getAddress() {
		return address;
	}

	public void setAddress(Byte address) {
		this.address = address;
	}

	private Packet createPacket(Parameter parameter, PacketId packetId) {
		logger.entry(parameter, packetId);
		PacketHeader packetHeader = new PacketHeader(Type.REQUEST, getGroup(), packetId);
		Payload payload = new Payload(parameter.getId(), null);

		Packet p = new Packet(packetHeader, payload);

		if(address!=null)
			p = new LinkedPacket(new LinkHeader(address, (byte)0, (short)0), p);

		return logger.exit(p);
	}

	private <T> Packet createPacket(Parameter parameter, PacketId packetId, T value) {
		logger.entry(parameter, packetId, value);
		PacketHeader packetHeader = new PacketHeader(value!=null ? Type.COMMAND : Type.REQUEST, getGroup(), packetId);
		Payload payload = new Payload(parameter.getId(), Packet.toBytes(value));

		Packet p = new Packet(packetHeader, payload);

		if(address!=null)
			p = new LinkedPacket(new LinkHeader(address, (byte)0, (short)0), p);

		return logger.exit(p);
	}

	@Override
	public String toString() {
		return "Group [packet=" + packet + "]";
	}
}
