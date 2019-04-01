package irt.calibration.data.packets;

import irt.calibration.data.packets.enums.GroupID;
import irt.calibration.data.packets.enums.PacketID;
import irt.calibration.data.packets.enums.PacketType;
import irt.calibration.data.packets.parameters.GetAllParameters;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.data.packets.parents.PacketImpl;

public class PacketConfigurationAll extends PacketImpl {

	public PacketConfigurationAll(Byte addr) {
		super(addr, PacketType.REQUEST, GroupID.CONFIGURATION, PacketID.CONFIGURATION_ALL, new GetAllParameters());
	}

	public PacketConfigurationAll(byte[] array) {
		super(array);
	}

	@Override
	protected Class<? extends Converter<?>> getConverterClass() {
		// TODO Auto-generated method stub
		return null;
	}
}
