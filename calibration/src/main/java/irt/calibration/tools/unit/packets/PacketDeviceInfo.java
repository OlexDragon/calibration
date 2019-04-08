package irt.calibration.tools.unit.packets;

import irt.calibration.tools.unit.packets.enums.PacketType;
import irt.calibration.tools.unit.packets.parameters.GetAllParameters;
import irt.calibration.tools.unit.packets.parameters.ids.DeviceInfo;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.tools.unit.packets.parents.PacketImpl;

public class PacketDeviceInfo extends PacketImpl {

	public PacketDeviceInfo(Byte addr) {
		super(addr, PacketType.REQUEST, DeviceInfo.ALL.getGroupID(), DeviceInfo.ALL.getPacketID(), new GetAllParameters());
	}

	public PacketDeviceInfo(byte[] array) {
		super(array);
	}

	@Override
	protected Class<? extends Converter<?>> getConverterClass() {
		return DeviceInfo.class;
	}
}
