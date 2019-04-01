package irt.calibration.data.packets;

import irt.calibration.data.packets.enums.PacketType;
import irt.calibration.data.packets.parameters.GetAllParameters;
import irt.calibration.data.packets.parameters.ids.DeviceInfo;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.data.packets.parents.PacketImpl;

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
