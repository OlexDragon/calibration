package irt.calibration.data.packets;

import irt.calibration.data.packets.parameters.ids.MeasurementBUC;
import irt.calibration.data.packets.parameters.ids.MeasurementFCM;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.data.packets.parents.PacketMeasurement;

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
