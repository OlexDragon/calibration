package irt.calibration.data.packets.parameters.ids;

import irt.calibration.data.packets.enums.GroupID;
import irt.calibration.data.packets.enums.PacketID;
import irt.calibration.data.packets.parameters.Parameter;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;

public enum GeneralParameterID implements ParameterID, Converter<Object>{

	ALL				((byte)	 255);

	private final byte id;

	private GeneralParameterID(byte id) {
		this.id = id;
	}

	@Override
	public byte getId() {
		return id;
	}

	@Override
	public GroupID getGroupID() {
		throw new RuntimeException("The enum GeneralParameterID does not know which GroupID to use.");
	}

	@Override
	public PacketID getPacketID() {
		throw new RuntimeException("The enum GeneralParameterID does not know which PacketID to use.");
	}

	@Override
	public byte toByte() {
		return id;
	}

	@Override
	public Object convert(byte[] bytes) {
		// TODO Auto-generated method stub
		return null;
	}

	public static GeneralParameterID valueOf(Parameter parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}
}
