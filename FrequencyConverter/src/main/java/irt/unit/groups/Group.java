package irt.unit.groups;

import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.packet.Packet;
import irt.serial_protocol.data.packet.PacketHeader;
import irt.serial_protocol.data.packet.PacketHeader.Type;
import irt.serial_protocol.data.packet.Payload;
import irt.unit.data.UnitValue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public abstract class Group {

	private final Logger logger = (Logger) LogManager.getLogger(getClass().getName());

	interface Parameter{ public byte getId(); }

//***************************************************************************************************
	private Packet packet;

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		logger.entry(packet);
		if(packet!=null && packet.getHeader().getGroupId()==getGroup().getId())
			this.packet = packet;
	}

	public abstract irt.serial_protocol.data.packet.PacketHeader.Group getGroup();

	public UnitValue getUnitValue(Parameter parameter) {
		logger.entry(parameter);
		UnitValue unitValue = null;

		if(packet != null){
			Payload payload = packet.getPayload(parameter.getId());
			logger.trace(payload);

			if(payload != null){

				unitValue = new UnitValue();

				switch(payload.getParameterHeader().getSize()){
				case 2:
					unitValue.setFlags((byte) 1);
					unitValue.setValue(payload.getShort(0));
					break;
				case 3:
					unitValue.setFlags(payload.getByte());
					unitValue.setValue(payload.getShort((byte)1));
				}
			}
		}
		return logger.exit(unitValue);
	}

	public UnitValue getUnitValue(ComPort comPort, Parameter parameter, short packetId){
		logger.entry(parameter, packetId);

		UnitValue unitValue = null;

		if(comPort!=null && comPort.isOpened()){

			PacketHeader packetHeader = new PacketHeader(Type.REQUEST, getGroup(), packetId);
			Payload payload = new Payload(parameter.getId(), null);
			Packet p = new Packet(packetHeader, payload);

			packet = comPort.send(p);
			unitValue = getUnitValue(parameter);
		}

		return logger.exit(unitValue);
	}

	public <T> UnitValue setUnitValue(ComPort comPort, Parameter parameter, short packetId, T value) {
		logger.entry(parameter, packetId);

		UnitValue unitValue = null;

		if(comPort!=null && comPort.isOpened()){

			PacketHeader packetHeader = new PacketHeader(Type.COMMAND, getGroup(), packetId);
			Payload payload = new Payload(parameter.getId(), Packet.toBytes(value));
			Packet p = new Packet(packetHeader, payload);

			packet = comPort.send(p);
			unitValue = getUnitValue(parameter);
		}

		return logger.exit(unitValue);
	}
}
