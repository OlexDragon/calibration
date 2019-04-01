package irt.calibration.data.packets;

import irt.calibration.data.packets.parameters.ids.enums.ADC_FCM;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.data.packets.parents.PacketAdc;

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
