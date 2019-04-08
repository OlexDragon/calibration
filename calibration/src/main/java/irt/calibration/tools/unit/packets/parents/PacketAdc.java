package irt.calibration.tools.unit.packets.parents;

import irt.calibration.tools.unit.packets.enums.PacketType;
import irt.calibration.tools.unit.packets.parameters.ParameterDeviceDebug;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.ADC;

public abstract class PacketAdc extends PacketImpl {

	protected PacketAdc(Byte addr, ADC adc, Integer value) {
		super(addr, value==null ? PacketType.REQUEST : PacketType.COMMAND, adc.getGroupID(), adc.getPacketID(), new ParameterDeviceDebug(adc, value));
	}

	protected PacketAdc(byte[] array) {
		super(array);
	}
}
