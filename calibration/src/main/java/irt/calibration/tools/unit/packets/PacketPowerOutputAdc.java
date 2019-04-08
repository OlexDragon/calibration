package irt.calibration.tools.unit.packets;

import irt.calibration.tools.unit.packets.parameters.ids.enums.ADC_FCM;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.tools.unit.packets.parents.PacketAdc;

public class PacketPowerOutputAdc extends PacketAdc {

	public PacketPowerOutputAdc(Byte addr) {
		super(addr, ADC_FCM.POWER_OUTPUT, null);
	}

	public PacketPowerOutputAdc(byte[] array) {
		super(array);
	}

	@Override
	protected Class<? extends Converter<?>> getConverterClass() {
		return ADC_FCM.class;
	}
}
