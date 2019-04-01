package irt.calibration.data.packets.parents;

import irt.calibration.data.packets.enums.PacketType;
import irt.calibration.data.packets.parameters.ParameterDeviceDebug;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.ADC;

public abstract class PacketAdc extends PacketImpl {

	protected PacketAdc(Byte addr, ADC adc, Integer value) {
		super(addr, value==null ? PacketType.REQUEST : PacketType.COMMAND, adc.getGroupID(), adc.getPacketID(), new ParameterDeviceDebug(adc, value));
	}

	protected PacketAdc(byte[] array) {
		super(array);
	}
}
