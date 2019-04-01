package irt.calibration.data.packets.parameters.ids;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import irt.calibration.data.packets.PacketMuteControl.MuteStatus;
import irt.calibration.data.packets.enums.GroupID;
import irt.calibration.data.packets.enums.PacketID;
import irt.calibration.data.packets.parameters.Parameter;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;

public enum ConfigurationBUC implements ParameterID, Converter<Object>{

	LO_SET					(1,		PacketID.LO_SET					, "LO Select"		, null),
	MUTE					(2,		PacketID.MUTE					, "Mute Status"		, bytes->MuteStatus.values()[bytes[0]&1]),
	GAIN					(3,		PacketID.GAIN					, "Gain Value"		, null),
	GAIN_RANGE				(5,		PacketID.GAIN_RANGE				, "Gain Range"		, null),
	ATTENUATION				(4,		PacketID.ATTENUATION			, "Attenuation Value",null),
	LO_FREQUENCIES			(7,		PacketID.LO_FREQUENCIES			, "LO Frequencies"	, null),
	FREQUENCY				(8,		PacketID.FREQUENCY				, "Frequency Value"	, null),
	FREQUENCY_RANGE			(9,		PacketID.FREQUENCY_RANGE		, "Frequency Range"	, null),
	REDUNDANCY_ENABLE		(10,	PacketID.REDUNDANCY_ENABLE		, "Redundancy Enable",null),
	REDUNDANCY_MODE			(11,	PacketID.REDUNDANCY_MODE		, "Redundancy Mode"	, null),
	REDUNDANCY_NAME			(12,	PacketID.REDUNDANCY_NAME		, "Redundancy Name"	, null),
	REDUNDANCY_SET_ONLINE	(14,	PacketID.REDUNDANCY_SET_ONLINE	, "Redundancy Online",null),
	REDUNDANCY_STATUS		(15,	PacketID.REDUNDANCY_STATUS		, "Redundancy Status",null),
	SPECTRUM_INVERSION		(20,	PacketID.SPECTRUM_INVERSION		, "Spectrum Inversion",null),
	OFFSET_RANGE	        (103,	PacketID.OFFSET_RANGE			, "Offset Range"	, null),    
	OFFSET_1_TO_MULTI       (104,	PacketID.OFFSET_1_TO_MULTI		, "Offser 1 to ..."	, null),    
	APC_ENABLE              (110,	PacketID.APC_ENABLE				, "ALC Enable"		, null),     /* APC enable */
	APC_LEVEL		        (111,	PacketID.APC_LEVEL				, "ALC Value"		, null),     /* APC target power level */
	APC_RANGE        		(112,	PacketID.APC_RANGE				, "ALC Range"		, null),     /* APC target power range */
	LNB_LO_SELECT           (124,	PacketID.LNB_LO_SELECT			, "LNB LO Select"	, null),
	ALL				(GeneralParameterID.ALL.getId(), PacketID.CONFIGURATION_ALL		, "Get All Configuration"		, null);

	private final byte id;
	private final PacketID packetID;
	private final Function<byte[], Object> converter;
	private final String title;

	private ConfigurationBUC(int id, PacketID packetID, String title, Function<byte[], Object> converter) {
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

	public static ConfigurationBUC valueOf(Parameter parameter) {
		return Optional.ofNullable(parameter).flatMap(p->Arrays.stream(values()).filter(v->v.id==p.getId()).findAny()).orElse(null);
	}
}
