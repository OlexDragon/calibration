package irt.calibration.tools.unit.packets;

import irt.calibration.tools.unit.packets.parameters.ids.MeasurementBUC;
import irt.calibration.tools.unit.packets.parameters.ids.MeasurementFCM;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.tools.unit.packets.parents.PacketMeasurement;

public class PacketPowerInput extends PacketMeasurement {

	public PacketPowerInput(Byte addr) {
		super(addr, addr==null ? MeasurementFCM.POWER_INPUT : MeasurementBUC.POWER_INPUT );
	}

	public PacketPowerInput(byte[] array) {
		super(array);
	}

	@Override
	protected Class<? extends Converter<?>> getConverterClass() {
		return getAddress()==null ? MeasurementFCM.class : MeasurementBUC.class;
	}
}
