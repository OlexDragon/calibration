package irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces;

import irt.calibration.tools.unit.packets.enums.GroupID;
import irt.calibration.tools.unit.packets.enums.PacketID;
import irt.calibration.tools.unit.packets.parameters.ids.ParameterID;

public interface Register extends Converter<Integer> {

	int getIndex();
	int getAddr();
	GroupID getGroupID();
	PacketID getPacketID();
	ParameterID getParameterID();
}
