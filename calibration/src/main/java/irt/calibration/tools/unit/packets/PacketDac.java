package irt.calibration.tools.unit.packets;

import irt.calibration.tools.unit.packets.enums.PacketType;
import irt.calibration.tools.unit.packets.parameters.ParameterDeviceDebug;
import irt.calibration.tools.unit.packets.parameters.ids.enums.DAC_BUC;
import irt.calibration.tools.unit.packets.parameters.ids.enums.DAC_FCM;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.DAC;
import irt.calibration.tools.unit.packets.parents.PacketImpl;

public class PacketDac extends PacketImpl {

	public PacketDac(Byte addr, DAC dac, Integer value) {
		super(addr, value==null ? PacketType.REQUEST : PacketType.COMMAND, dac.getGroupID(), dac.getPacketID(), new ParameterDeviceDebug(dac, value));
	}

	public PacketDac(byte[] array) {
		super(array);
	}

	@Override
	protected Class<? extends Converter<?>> getConverterClass() {
		return getAddress()==null ? DAC_FCM.class : DAC_BUC.class;
	}
}
