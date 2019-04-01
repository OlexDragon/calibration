package irt.calibration.data.packets.parameters.ids;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import irt.calibration.data.packets.data.Power;
import irt.calibration.data.packets.enums.GroupID;
import irt.calibration.data.packets.enums.PacketID;
import irt.calibration.data.packets.parameters.Parameter;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;

public enum MeasurementFCM implements ParameterIDMeasurement, Converter<Object>{

	NONE			((byte)	 0, PacketID.NONE				, "None", null),
	SUMMARY_ALARM	((byte)	 1, PacketID.SUMMARY_ALARM		, "Summary Alarm", Arrays::toString),
	STATUS			((byte)	 2, PacketID.MEASUREMENT_STATUS				, "Status", Arrays::toString),
	POWER_INPUT		((byte)	 4, PacketID.POWER_INPUT		, "Input Power", Power::new),
	POWER_OUTPUT	((byte)	 5, PacketID.POWER_OUTPUT		, "Output Power", Power::new),
	TEMPERATURE_UNIT((byte)	 3, PacketID.TEMPERATURE		, "Temperature", Power::new),
	V5_5			((byte)	 6, PacketID.V5_5				, "+5.5V", Power::new),
	V13_2			((byte)	 7, PacketID.V13_2				, "+13,2V", Power::new),
	V13_2_NEG		((byte)	 8, PacketID.V13_2_NEG			, "-13.2V", Power::new),
	CURRENT			((byte)	 9, PacketID.CURRENT			, "Current", Power::new),
	TEMPERATURE_CPU	((byte)	 10, PacketID.TEMPERATURE_CPU	, "CPU Temperature", Power::new),
	ATTENUATION		((byte)	 20, PacketID.ATTENUATION		, "Attenuation", Power::new),
	REFERENCE_SOURCE((byte)	 21, PacketID.REFERENCE_SOURCE	, "Reference Source", Arrays::toString),
	ALL	(GeneralParameterID.ALL.getId(), PacketID.MEASUREMENT_ALL, "Get All Measurement", null);

	private final byte id;
	private final PacketID packetID;
	private final String title;
	private final Function<byte[], Object> converter;

	private MeasurementFCM(byte id, PacketID packetID, String title, Function<byte[], Object> converter) {
		this.id = id;
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
		return GroupID.MEASUREMENT;
	}

	@Override
	public PacketID getPacketID() {
		return packetID;
	}

	@Override
	public byte toByte() {
		return id;
	}

	@Override
	public Object convert(byte[] bytes) {
		return converter.apply(bytes);
	}

	public static MeasurementFCM valueOf(Parameter parameter) {
		return Optional.ofNullable(parameter).flatMap(p->Arrays.stream(values()).filter(v->v.id==p.getId()).findAny()).orElse(null);
	}

	@Override
	public String getTitle() {
		return title;
	}
}
