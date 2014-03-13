package irt.unit.groups;

import irt.serial_protocol.data.PacketWork;
import irt.serial_protocol.data.packet.Packet;
import irt.serial_protocol.data.packet.PacketHeader;
import irt.serial_protocol.data.packet.PacketHeader.Group;
import irt.serial_protocol.data.packet.PacketHeader.Type;
import irt.serial_protocol.data.packet.Payload;
import irt.unit.data.UnitValue;

public class Measurement extends irt.unit.groups.Group{

	public static final byte MEASUREMENT = Packet.IRT_SLCP_PACKET_ID_MEASUREMENT;

	public enum Params implements Parameter{
		NONE			(Packet.IRT_SLCP_PARAMETER_MEASUREMENT_FCM_NONE			),
		SUMMARY_ALARM	(Packet.IRT_SLCP_PARAMETER_MEASUREMENT_FCM_SUMMARY_ALARM),
		STATUS			(Packet.IRT_SLCP_PARAMETER_MEASUREMENT_FCM_STATUS		),
		TEMPERATURE		(Packet.IRT_SLCP_PARAMETER_MEASUREMENT_FCM_TEMPERATURE	),
		INPUT_POWER		(Packet.IRT_SLCP_PARAMETER_MEASUREMENT_FCM_INPUT_POWER	),
		OUTPUT_POWER	(Packet.IRT_SLCP_PARAMETER_MEASUREMENT_FCM_OUTPUT_POWER	),
		MON_5V5			(Packet.IRT_SLCP_PARAMETER_MEASUREMENT_FCM_MON_5V5		),
		MON_13V2_POS	(Packet.IRT_SLCP_PARAMETER_MEASUREMENT_FCM_MON_13V2_POS	),
		MON_13V2_NEG	(Packet.IRT_SLCP_PARAMETER_MEASUREMENT_FCM_MON_13V2_NEG	),
		CURRENT			(Packet.IRT_SLCP_PARAMETER_MEASUREMENT_FCM_CURRENT		),
		TEMPERATURE_CPU	(Packet.IRT_SLCP_PARAMETER_MEASUREMENT_FCM_TEMPERATURE_CPU),
		ALL				(Packet.IRT_SLCP_PARAMETER_MEASUREMENT_FCM_ALL			);

		private byte parameter;

		private Params(byte parameter){
			this.parameter = parameter;
		}

		public byte getId() {
			return parameter;
		}
	}

	public static final Packet GET_ALL = new Packet(
												new PacketHeader(
														Type.REQUEST,
														Group.MEASUREMENT,
														PacketWork.PACKET_ID_MEASUREMENT_ALL),
												new Payload(
														Params.ALL.getId(),
														null));

//***************************************************************************************************

	public UnitValue getInputPower(){
		return getUnitValue(Params.INPUT_POWER);
		
	}

	public UnitValue getOutputPower(){
		return getUnitValue(Params.OUTPUT_POWER);
		
	}

	@Override
	public Group getGroup() {
		return Group.MEASUREMENT;
	}
}
