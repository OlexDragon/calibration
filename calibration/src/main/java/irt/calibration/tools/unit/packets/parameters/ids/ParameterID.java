package irt.calibration.tools.unit.packets.parameters.ids;

import irt.calibration.tools.unit.packets.enums.GroupID;
import irt.calibration.tools.unit.packets.enums.PacketID;

public interface ParameterID {

	byte getId();
	GroupID getGroupID();
	PacketID getPacketID();
	byte toByte();
}
