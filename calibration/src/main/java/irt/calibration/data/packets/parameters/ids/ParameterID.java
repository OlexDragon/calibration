package irt.calibration.data.packets.parameters.ids;

import irt.calibration.data.packets.enums.GroupID;
import irt.calibration.data.packets.enums.PacketID;

public interface ParameterID {

	byte getId();
	GroupID getGroupID();
	PacketID getPacketID();
	byte toByte();
}
