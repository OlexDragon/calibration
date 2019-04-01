package irt.calibration.data.packets.parameters.ids;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import irt.calibration.data.packets.enums.GroupID;
import irt.calibration.data.packets.enums.PacketID;
import irt.calibration.data.packets.parameters.Parameter;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.data.packets.data.Power;

public enum MeasurementBUC implements ParameterIDMeasurement, Converter<Object> {

	POWER_INPUT					((byte)1, PacketID.POWER_INPUT		, "Input Power", Power::new),
	POWER_OUTPUT				((byte)2, PacketID.POWER_OUTPUT		, "Output Power", Power::new),
	POWER_REFLECTED				((byte)5, PacketID.POWER_REFLECTED	, "Reflected Power", Power::new),
	TEMPERATURE					((byte)3, PacketID.TEMPERATURE		, "Temperature", Power::new),
	STATUS						((byte)4, PacketID.MEASUREMENT_STATUS,"Status", bytes->Arrays.toString(bytes)),
//	STATUS_LNB1					((byte)5, PacketID.STATUS_LNB1, null),
//	STATUS_LNB2					((byte)6, PacketID.STATUS_LNB2, null),
//	DOWNLINK_WAVEGUIDE_SWITCH	((byte)8, PacketID.DOWNLINK_WAVEGUIDE_SWITCH, null),
//	DOWNLINK_STATUS				((byte)9, PacketID.DOWNLINK_STATUS, null),// Downlink status is deprecated
	ALL				(GeneralParameterID.ALL.getId(), PacketID.MEASUREMENT_ALL, "Get All Measurement", null);

	private final byte id;
	private final GroupID groupID = GroupID.MEASUREMENT;
	private final PacketID packetID;
	private final String title;
	private final Function<byte[], Object> converter;

	private MeasurementBUC(byte id, PacketID packetID, String title, Function<byte[], Object> converter) {
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
		return groupID;
	}

	@Override
	public PacketID getPacketID() {
		return packetID;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public byte toByte() {
		return id;
	}

	@Override
	public Object convert(byte[] bytes) {
		return converter.apply(bytes);
	}

	public static MeasurementBUC valueOf(Parameter parameter) {
		return Optional.ofNullable(parameter).flatMap(p->Arrays.stream(values()).filter(v->v.id==p.getId()).findAny()).orElse(null);
	}
}
