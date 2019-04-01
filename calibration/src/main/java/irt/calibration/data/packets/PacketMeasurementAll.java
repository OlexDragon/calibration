package irt.calibration.data.packets;

import irt.calibration.data.packets.parameters.ids.MeasurementBUC;
import irt.calibration.data.packets.parameters.ids.MeasurementFCM;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.data.packets.parents.PacketMeasurement;

public class PacketMeasurementAll extends PacketMeasurement {

	public PacketMeasurementAll(Byte addr) {
		super(addr, addr==null ? MeasurementFCM.ALL : MeasurementBUC.ALL	);
	}

	public PacketMeasurementAll(byte[] array) {
		super(array);
	}

	@Override
	protected Class<? extends Converter<?>> getConverterClass() {
		return getAddress()==null ? MeasurementFCM.class : MeasurementBUC.class;
	}
}
