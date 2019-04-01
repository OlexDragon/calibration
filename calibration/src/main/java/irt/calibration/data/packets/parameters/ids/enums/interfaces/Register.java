package irt.calibration.data.packets.parameters.ids.enums.interfaces;

import irt.calibration.data.packets.enums.GroupID;
import irt.calibration.data.packets.enums.PacketID;
import irt.calibration.data.packets.parameters.ids.ParameterID;

public interface Register extends Converter<Integer> {

	int getIndex();
	int getAddr();
	GroupID getGroupID();
	PacketID getPacketID();
	ParameterID getParameterID();
}
