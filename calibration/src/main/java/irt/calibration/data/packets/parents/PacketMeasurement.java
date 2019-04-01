package irt.calibration.data.packets.parents;

import irt.calibration.data.packets.enums.PacketType;
import irt.calibration.data.packets.parameters.ParameterMeasurement;
import irt.calibration.data.packets.parameters.ids.ParameterIDMeasurement;

public abstract class PacketMeasurement extends PacketImpl {

	protected PacketMeasurement(Byte addr, ParameterIDMeasurement measurement) {
		super(addr, PacketType.REQUEST, measurement.getGroupID(), measurement.getPacketID(), new ParameterMeasurement(measurement));
	}

	protected PacketMeasurement(byte[] array) {
		super(array);
	}
}
