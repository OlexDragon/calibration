package irt.calibration.tools.unit.packets;

import irt.calibration.tools.unit.packets.parameters.ids.MeasurementBUC;
import irt.calibration.tools.unit.packets.parameters.ids.MeasurementFCM;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.tools.unit.packets.parents.PacketMeasurement;

public class PacketTemperature extends PacketMeasurement {

	public PacketTemperature(Byte addr) {
		super(addr, addr==null ? MeasurementFCM.TEMPERATURE_UNIT : MeasurementBUC.TEMPERATURE );
	}

	public PacketTemperature(byte[] array) {
		super(array);
	}

	@Override
	protected Class<? extends Converter<?>> getConverterClass() {
		 return getAddress()==null ? MeasurementFCM.class : MeasurementBUC.class;
	}
}
