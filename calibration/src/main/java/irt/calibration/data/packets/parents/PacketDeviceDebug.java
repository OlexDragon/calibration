package irt.calibration.data.packets.parents;

import irt.calibration.data.packets.enums.PacketType;
import irt.calibration.data.packets.parameters.ParameterDeviceDebug;
import irt.calibration.data.packets.parameters.ids.DeviceDebug;

public abstract class PacketDeviceDebug extends PacketImpl {

	protected PacketDeviceDebug(Byte addr, DeviceDebug deviceDebug, Integer value) {
		super(addr, value==null ? PacketType.REQUEST : PacketType.COMMAND, deviceDebug.getGroupID(), deviceDebug.getPacketID(), new ParameterDeviceDebug(deviceDebug, value));
	}

	protected PacketDeviceDebug(byte[] array) {
		super(array);
	}
}
