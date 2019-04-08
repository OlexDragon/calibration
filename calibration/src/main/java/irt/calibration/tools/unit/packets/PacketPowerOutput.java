package irt.calibration.tools.unit.packets;

import irt.calibration.tools.unit.packets.parameters.ids.MeasurementBUC;
import irt.calibration.tools.unit.packets.parameters.ids.MeasurementFCM;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.tools.unit.packets.parents.PacketMeasurement;

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
