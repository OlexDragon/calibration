package irt.calibration.data.packets;

import java.util.Optional;

import irt.calibration.data.packets.parameters.ids.DeviceDebug;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.data.packets.parents.PacketDeviceDebug;

public class PacketCalibrationMade extends PacketDeviceDebug {

	public PacketCalibrationMade(Byte addr, CalibrationModeStatus value) {
		super(addr, DeviceDebug.CALIBRATION_MODE, Optional.ofNullable(value).map(Enum::ordinal).orElse(null));
	}

	public PacketCalibrationMade(byte[] array) {
		super(array);
	}

	@Override
	protected Class<? extends Converter<?>> getConverterClass() {
		return DeviceDebug.class;
	}

	public enum CalibrationModeStatus{
		IS_OFF,
		IS_ON;

		public CalibrationModeStatus toggle() {
			return values()[(ordinal()+1)%2];
		}
	}
}
