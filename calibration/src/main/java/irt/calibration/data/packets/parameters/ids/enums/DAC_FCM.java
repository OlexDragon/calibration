package irt.calibration.data.packets.parameters.ids.enums;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

import irt.calibration.data.packets.PacketDac;
import irt.calibration.data.packets.enums.GroupID;
import irt.calibration.data.packets.enums.PacketID;
import irt.calibration.data.packets.parameters.Parameter;
import irt.calibration.data.packets.parameters.ids.DeviceDebug;
import irt.calibration.data.packets.parameters.ids.ParameterID;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.DAC;
import javafx.util.Pair;

public enum DAC_FCM implements DAC{

	FCM_DAC1	(1, 0, PacketID.CONVERTER_DAC1	, "Converter DAC #1"),
	FCM_DAC2	(2, 0, PacketID.CONVERTER_DAC2	, "Converter DAC #2"),
	FCM_DAC3	(3, 0, PacketID.CONVERTER_DAC3	, "Converter DAC #3"),
	FCM_DAC4	(4, 0, PacketID.CONVERTER_DAC4	, "Converter DAC #4");

	private final int index;
	private final int addr;
	private final PacketID packetID;
	private final String title;

	private DAC_FCM(int index, int addr, PacketID packetID, String title) {
		this.index = index;
		this.addr = addr;
		this.packetID = packetID;
		this.title = title;
	}

	public int getIndex() {
		return index;
	}

	public int getAddr() {
		return addr;
	}

	public PacketID getPacketID() {
		return packetID;
	}

	public GroupID getGroupID() {
		return GroupID.DEVICE_DEBAG;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public ParameterID getParameterID() {
		return DeviceDebug.READ_WRITE;
	}

	@Override
	public Integer convert(byte[] bytes) {
		return Optional.of(ByteBuffer.wrap(bytes)).filter(bb->bb.limit()==12).map(bb->bb.getInt(8)).orElse(null);
	}

	@Override
	public Double getMaxValue() {
		return 4095.0;
	}

	@Override
	public Double getMinValue() {
		return 0.0;
	}

	@Override
	public PacketDac getPacket(Byte address, Integer value) {
		return new PacketDac(null, this, value);
	}

	public static DAC_FCM valueOf(Parameter parameter) {
		return Optional.ofNullable(parameter)
				.filter(p->p.getId()==DeviceDebug.READ_WRITE.getId())
				.map(Parameter::getData)
				.map(ByteBuffer::wrap)
				.map(bb->new Pair<>(bb.getInt(), bb.getInt()))
				.flatMap(
						pair->
						Arrays.stream(values())
						.filter(v->pair.getKey()==v.index)
						.filter(v->pair.getValue()==v.addr)
						.findAny())
				.orElse(null);
	}
}
