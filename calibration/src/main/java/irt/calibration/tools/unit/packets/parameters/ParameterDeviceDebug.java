package irt.calibration.tools.unit.packets.parameters;

import java.nio.ByteBuffer;
import java.util.Optional;

import irt.calibration.tools.unit.packets.parameters.ids.DeviceDebug;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.Register;

public class ParameterDeviceDebug extends Parameter {

	public ParameterDeviceDebug(Register register, Integer value) {
		super(register.getParameterID());

		final ByteBuffer bb = ByteBuffer.allocate(value==null ? 8 : 12).putInt(register.getIndex()).putInt(register.getAddr());

		Optional.ofNullable(value).ifPresent(bb::putInt);

		putData(bb.array());
	}

	public ParameterDeviceDebug(DeviceDebug deviceDebug, Integer value) {
		super(deviceDebug);

		Optional.ofNullable(value).map(v->ByteBuffer.allocate(4).putInt(v).array()).ifPresent(this::putData);
	}
}
