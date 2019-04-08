package irt.calibration.tools.unit.packets.parameters.ids;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import irt.calibration.tools.unit.packets.converters.UnitsConverter;
import irt.calibration.tools.unit.packets.enums.GroupID;
import irt.calibration.tools.unit.packets.enums.PacketID;
import irt.calibration.tools.unit.packets.parameters.Parameter;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.Converter;

public enum DeviceInfo implements ParameterID, Converter<String>{


	NONE				((byte)0, PacketID.NONE, "Not used"			, Arrays::toString),

	DEVICE_TYPE			((byte)1, PacketID.DEVICE_TYPE, "Type"				, bytes->Optional.ofNullable(bytes)
															.filter(b->b.length==12)
															.map(ByteBuffer::wrap)
															.map(ByteBuffer::asIntBuffer)
															.map(ib->{
																int[] ints = new int[ib.limit()];
																ib.get(ints);
																return ints;
															})
															.map(Arrays::stream)
															.map(
																	stream->
																	stream.mapToObj(Integer::toString)
																	.collect(Collectors.joining(".")))
															.orElseGet(()->Arrays.toString(bytes))),

	FIRMWARE_VERSION	((byte)2, PacketID.FIRMWARE_VERSION, "Firmware Version"	, String::new),
	FIRMWARE_BUILD_DATE	((byte)3, PacketID.FIRMWARE_BUILD_DATE, "Firmware Build"	, String::new),

	UNIT_UPTIME_COUNTER	((byte)4, PacketID.UNIT_UPTIME_COUNTER, "Counter"			, bytes->Optional.ofNullable(bytes)
													.filter(b->b.length==4)
													.map(ByteBuffer::wrap)
													.map(ByteBuffer::getInt)
													.map(UnitsConverter::secondsToString)
													.orElseGet(()->Arrays.toString(bytes))),

	DEVICE_SN			((byte)5, PacketID.DEVICE_SN, "Serial Number"	, String::new),
	UNIT_NAME			((byte)6, PacketID.UNIT_NAME, "Name"				, String::new),
	UNIT_PART_NUMBER	((byte)7, PacketID.UNIT_PART_NUMBER, "Part Number", String::new),

	ALL					(GeneralParameterID.ALL.getId(), PacketID.INFO, "Use only for request", Arrays::toString);

	private final byte id;
	private final Function<byte[], String> converter;
	private String title;
	private PacketID packetID;

	private DeviceInfo(byte id, PacketID packetID, String title, Function<byte[], String> valueToString) {
		this.id = id;
		this.packetID = packetID;
		this.title = title;
		this.converter = valueToString;
	}

	@Override
	public byte getId() {
		return id;
	}

	@Override
	public GroupID getGroupID() {
		return GroupID.DEVICE_INFO;
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
	public String toString() {
		return title;
	}

	public String convert(byte[] bytes) {
		return converter.apply(bytes);
	}

	@Override
	public String getTitle() {
		return title;
	}

	public static DeviceInfo valueOf(Parameter parameter) {
		return Optional.ofNullable(parameter).flatMap(p->Arrays.stream(values()).filter(v->v.id==p.getId()).findAny()).orElse(null);
	}
}
