package irt.calibration.tools.unit.packets.parents;

import irt.calibration.tools.unit.packets.enums.PacketType;
import irt.calibration.tools.unit.packets.parameters.ParameterMeasurement;
import irt.calibration.tools.unit.packets.parameters.ids.ParameterIDMeasurement;

public abstract class PacketMeasurement extends PacketImpl {

	protected PacketMeasurement(Byte addr, ParameterIDMeasurement measurement) {
		super(addr, PacketType.REQUEST, measurement.getGroupID(), measurement.getPacketID(), new ParameterMeasurement(measurement));
	}

	protected PacketMeasurement(byte[] array) {
		super(array);
	}
}
