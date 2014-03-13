package irt.unit.groups;

import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.PacketWork;
import irt.serial_protocol.data.packet.Packet;
import irt.serial_protocol.data.packet.PacketHeader.Group;
import irt.unit.data.UnitValue;

public class Configuration extends irt.unit.groups.Group{

	public static final byte CONFIGURATION = Packet.IRT_SLCP_PACKET_ID_CONFIGURATION;

	public enum Params implements Parameter{
		NONE				(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_NONE			),
		GAIN				(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_GAIN			),
		ATTENUATION			(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_ATTENUATION	),
		FREQUENCY			(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_FREQUENCY		),
		FREQUENCY_RANGE		(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_FREQUENCY_RANGE),
		GAIN_RANGE			(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_GAIN_RANGE		),
		ATTENUATION_RANGE	(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_ATTENUATION_RANGE),
		MUTE_CONTROL		(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_MUTE_CONTROL	),
		BUC_ENABLE			(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_BUC_ENABLE		),
		FLAGS				(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_FLAGS			),
		GAIN_OFFSET			(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_GAIN_OFFSET	),
		ALL					(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_ALL			);

		private byte parameter;

		private Params(byte parameter){
			this.parameter = parameter;
		}

		public byte getId() {
			return parameter;
		}
	}

//***************************************************************************************************

	@Override
	public Group getGroup() {
		return Group.CONFIGURATION;
	}

	public UnitValue getGain(){
		return getUnitValue(Params.GAIN);
	}

	public UnitValue setGain(ComPort comPort, short value){
		return setUnitValue(comPort, Params.GAIN, PacketWork.PACKET_ID_CONFIGURATION_FCM_GAIN,  value);
	}

	public UnitValue setAttenuation(ComPort comPort, short value){
		return setUnitValue(comPort, Params.ATTENUATION, PacketWork.PACKET_ID_CONFIGURATION_FCM_ATTENUATION,  value);
	}
}
