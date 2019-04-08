package irt.calibration.tools.unit.packets.parameters.ids.enums;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

import irt.calibration.tools.unit.packets.enums.GroupID;
import irt.calibration.tools.unit.packets.enums.PacketID;
import irt.calibration.tools.unit.packets.parameters.Parameter;
import irt.calibration.tools.unit.packets.parameters.ids.DeviceDebug;
import irt.calibration.tools.unit.packets.parameters.ids.ParameterID;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.ADC;

/* TODO
 * Bias Board ADC: index = 5
 * Addresses:
   BOARD_SENSOR_ADDR_CFG_REG = 0,
    BOARD_SENSOR_ADDR_HSI1_REG = 1,
    BOARD_SENSOR_ADDR_HSI2_REG = 2,
    BOARD_SENSOR_ADDR_OPOWER_REG = 3,
    BOARD_SENSOR_ADDR_TEMP_REG = 4,
    BOARD_SENSOR_ADDR_WGS_REG = 5,
    BOARD_SENSOR_ADDR_HSSx_AV_REG = 10,	 */

public enum ADC_FCM implements ADC{

	POWER_INPUT	(10, 0, PacketID.POWER_INPUT_ADC	, "ADC Input Power"),
	POWER_OUTPUT(10, 1, PacketID.POWER_OUTPUT_ADC	, "ADC Output Power"),
	TEMPERATURE	(10, 2, PacketID.TEMPERATURE_ADC	, "ADC Temperature"),
	CURRENT		(10, 4, PacketID.CURRENT_ADC		, "ADC Current"),
	_5V5		(10, 6, PacketID.V5_5				, "ADC +5.5V"),
	_13v2		(10, 7, PacketID.V13_2				, "ADC +13.2V"),
	_13V2_NEG	(10, 8, PacketID.V13_2_NEG			, "ADC -13.2V");

	private final int index;
	private final int addr;
	private final PacketID packetID;
	private final String title;

	private ADC_FCM(int index, int addr, PacketID packetID, String title) {
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

	public static ADC_FCM valueOf(Parameter parameter) {
		return Optional.ofNullable(parameter)
				.filter(p->p.getId()==DeviceDebug.READ_WRITE.getId())
				.map(Parameter::getData)
				.map(ByteBuffer::wrap)
				.flatMap(bb->Arrays.stream(values()).filter(v->bb.get()==v.index).filter(v->bb.get()==v.addr).findAny())
				.orElse(null);
	}
}
