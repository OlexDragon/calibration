package irt.calibration.tools.unit.packets;

import irt.calibration.tools.unit.packets.enums.GroupID;
import irt.calibration.tools.unit.packets.enums.PacketID;
import irt.calibration.tools.unit.packets.enums.PacketType;
import irt.calibration.tools.unit.packets.parameters.GetAllParameters;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.tools.unit.packets.parents.PacketImpl;

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
