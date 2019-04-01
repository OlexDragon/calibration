package irt.calibration.data.packets.parameters.ids;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import irt.calibration.data.packets.PacketMuteControl.MuteStatus;
import irt.calibration.data.packets.enums.GroupID;
import irt.calibration.data.packets.enums.PacketID;
import irt.calibration.data.packets.parameters.Parameter;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;

public enum ConfigurationFCM implements ParameterID, Converter<Object>{

	MUTE				(7, PacketID.MUTE				, "Mute Status"			, bytes->MuteStatus.values()[bytes[0]&1]),
	GAIN				(1, PacketID.GAIN				, "Gain Value"			, null),
	ATTENUATION			(2, PacketID.ATTENUATION		, "Attenuation Value"	, null),
	ATTENUATION_RANGE	(6, PacketID.ATTENUATION_RANGE	, "Attenuation Range"	, null),
	FREQUENCY			(3, PacketID.FREQUENCY			, "Frequency Value"		, null),
	FREQUENCY_RANGE		(4, PacketID.FREQUENCY_RANGE	, "Frequency Range"		, null),
	LNB_POWER			(8, PacketID.LNB_POWER			, "LNB Power"			, null),
	FLAGS				(9, PacketID.FLAGS				, "Flags"				, null),
	OFFSET				(10,PacketID.OFFSET				, "Offset"				, null),
	ALC_ENABLE			(11,PacketID.ALC_ENABLE			, "ALC Status"			, null),
	ALC_LEVEL			(12,PacketID.ALC_LEVEL			, "ALC Value"			, null),
	ALC_RANGE			(14,PacketID.ALC_RANGE			, "ALC Range"			, null),		//		= 14
//	PARAMETER_CONFIG_DLRS_WGS_SWITCHOVER							= 14,

	ALC_ENABLED			(15,PacketID.ALC_ENABLED		, "ALC Statuc"	, null),
	ALC_THRESHOLD		(16,PacketID.ALC_THRESHOLD		,"ALC Threshold", null),
	ALC_THRESHOLD_RANGE	(17,PacketID.ALC_THRESHOLD_RANGE, "ALC Range", null),

	LNB_REFERENCE_CONTROL(21, PacketID.LNB_REFERENCE_CONTROL, "LNB Reference", null),

	ALL	(GeneralParameterID.ALL.getId(), PacketID.CONFIGURATION_ALL, "Get All Configuration", null);

	private final byte id;
	private final PacketID packetID;
	private final Function<byte[], Object> converter;
	private final String title;

	private ConfigurationFCM(int id, PacketID packetID, String title, Function<byte[], Object> converter) {
		this.id = (byte) id;
		this.packetID = packetID;
		this.converter = converter;
		this.title = title;
	}

	@Override
	public byte getId() {
		return id;
	}

	@Override
	public GroupID getGroupID() {
		return GroupID.CONFIGURATION;
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

	@Override
	public String getTitle() {
		return title;
	}

	public static ConfigurationFCM valueOf(Parameter parameter) {
		return Optional.ofNullable(parameter).flatMap(p->Arrays.stream(values()).filter(v->v.id==p.getId()).findAny()).orElse(null);
	}
}
