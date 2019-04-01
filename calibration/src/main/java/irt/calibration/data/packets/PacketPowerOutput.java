package irt.calibration.data.packets;

import irt.calibration.data.packets.parameters.ids.MeasurementBUC;
import irt.calibration.data.packets.parameters.ids.MeasurementFCM;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.data.packets.parents.PacketMeasurement;

public class PacketPowerOutput extends PacketMeasurement {

	public PacketPowerOutput(Byte addr) {
		super(addr, addr==null ? MeasurementFCM.POWER_OUTPUT : MeasurementBUC.POWER_OUTPUT );
	}

	public PacketPowerOutput(byte[] array) {
		super(array);
	}

	@Override
	protected Class<? extends Converter<?>> getConverterClass() {
		return getAddress()==null ? MeasurementFCM.class : MeasurementBUC.class;
	}
}
