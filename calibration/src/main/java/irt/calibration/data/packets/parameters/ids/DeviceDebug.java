package irt.calibration.data.packets.parameters.ids;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import irt.calibration.data.packets.PacketCalibrationMade.CalibrationModeStatus;
import irt.calibration.data.packets.enums.GroupID;
import irt.calibration.data.packets.enums.PacketID;
import irt.calibration.data.packets.parameters.Parameter;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;

public enum DeviceDebug implements ParameterID, Converter<Object>{

	INFO			(1, null, "Device Information"	, null),	/* device information: parts, firmware and etc. */
	DUMP			(2, null, "Dump of Registers"		, null),	/* dump of registers for specified device index */
	READ_WRITE		(3, null, "Registers Read/Write"	, null),	/* registers read/write operations */
	DEBUG_INDEX		(4, null, "index information"		, null),	/* device index information print */
	CALIBRATION_MODE(5, PacketID.CALIBRATION_MODE, "Calibration Mode"		, bytes->CalibrationModeStatus.values()[ByteBuffer.wrap(bytes).getInt()%2]),	/* calibration mode */
	ENVIRONMENT_IO	(10, null, "Environment Variables", null),	/* operations with environment variables */
	DEVICES			(30, null, "Devices"				, null);

	private final byte id;
	private final String title;
	private final Function<byte[], Object> converter;
	private final PacketID packetID;

	private DeviceDebug(int id, PacketID packetID, String title, Function<byte[], Object> converter) {
		this.id = (byte)id;
		this.packetID = packetID;
		this.title = title;
		this.converter = converter;
	}

	@Override
	public byte getId() {
		return id;
	}

	@Override
	public GroupID getGroupID() {
		return GroupID.DEVICE_DEBAG;
	}

	@Override
	public PacketID getPacketID() {
		return packetID;
	}

	@Override
	public byte toByte() {
		return id;
	}

	public boolean match(Parameter parameter) {
		return parameter.getId() == (byte)ordinal();
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public Object convert(byte[] bytes) {
		return converter.apply(bytes);
	}

	public static DeviceDebug valueOf(Parameter parameter) {
		return Optional.ofNullable(parameter).flatMap(p->Arrays.stream(values()).filter(v->v.id==p.getId()).findAny()).orElse(null);
	}
}
