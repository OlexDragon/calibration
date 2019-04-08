package irt.calibration.tools.unit.packets.parents;

import irt.calibration.tools.unit.packets.enums.PacketType;
import irt.calibration.tools.unit.packets.parameters.ParameterDeviceDebug;
import irt.calibration.tools.unit.packets.parameters.ids.DeviceDebug;

public abstract class PacketDeviceDebug extends PacketImpl {

	protected PacketDeviceDebug(Byte addr, DeviceDebug deviceDebug, Integer value) {
		super(addr, value==null ? PacketType.REQUEST : PacketType.COMMAND, deviceDebug.getGroupID(), deviceDebug.getPacketID(), new ParameterDeviceDebug(deviceDebug, value));
	}

	protected PacketDeviceDebug(byte[] array) {
		super(array);
	}
}
