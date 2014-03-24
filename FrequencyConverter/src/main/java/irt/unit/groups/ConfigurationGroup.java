package irt.unit.groups;

import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.PacketWork;
import irt.serial_protocol.data.Range;
import irt.serial_protocol.data.packet.Packet;
import irt.serial_protocol.data.packet.PacketHeader.Group;
import irt.serial_protocol.data.packet.PacketHeader.Type;
import irt.serial_protocol.data.packet.Payload;
import irt.serial_protocol.data.value.Enums.FalseOrTrue;
import irt.unit.data.UnitValue;

public class ConfigurationGroup extends irt.unit.groups.Group{

	public static final byte CONFIGURATION = Packet.IRT_SLCP_PACKET_ID_CONFIGURATION;

	public enum Params implements Parameter{
		NONE				(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_NONE			),
		GAIN				(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_GAIN			),
		ATTENUATION			(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_ATTENUATION	),
		FREQUENCY			(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_FREQUENCY		),
		FREQUENCY_RANGE		(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_FREQUENCY_RANGE),
		GAIN_RANGE			(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_GAIN_RANGE		),
		ATTENUATION_RANGE	(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_ATTENUATION_RANGE),
		MUTE		(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_MUTE_CONTROL	),
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

	public UnitValue getAttenuation(){
		return getUnitValue(Params.ATTENUATION);
	}

	public UnitValue getMute(){
		return getUnitValue(Params.MUTE);
	}

	public FalseOrTrue getMute(ComPort comPort) {
		FalseOrTrue falseOrTrue = null;

		UnitValue unitValue = setUnitValue(comPort, Params.MUTE, PacketWork.PACKET_ID_CONFIGURATION_FCM_MUTE,  null);
		if(unitValue!=null)
			falseOrTrue = FalseOrTrue.values()[unitValue.getValue()];

		return falseOrTrue;
	}

	public UnitValue setGain(ComPort comPort, short value){
		return setUnitValue(comPort, Params.GAIN, PacketWork.PACKET_ID_CONFIGURATION_FCM_GAIN,  value);
	}

	public UnitValue setAttenuation(ComPort comPort, short value){
		return setUnitValue(comPort, Params.ATTENUATION, PacketWork.PACKET_ID_CONFIGURATION_FCM_ATTENUATION,  value);
	}

	public UnitValue setMute(ComPort comPort, FalseOrTrue falseOrTrue){
		logger.entry(comPort, falseOrTrue);
		return setUnitValue(comPort, Params.MUTE, PacketWork.PACKET_ID_CONFIGURATION_FCM_MUTE,  (byte)falseOrTrue.ordinal());
	}

	public Range getFrequencyRange(ComPort comPort) {	
		logger.entry(comPort);
		Range range = null;

		Packet packet = getPacket(comPort, Params.FREQUENCY_RANGE, PacketWork.PACKET_ID_CONFIGURATION_FCM_FREQUENCY_RANGE);
		if(packet!=null){
			Payload payload = packet.getPayload(Params.FREQUENCY_RANGE.getId());
			if(payload!=null){
				range = new Range(payload);
			}
		}

		return logger.exit(range);
	}

	public Long getFrequency(ComPort comPort) {
		logger.entry(comPort);
		Long value = null;

		Packet packet = getPacket(comPort, Params.FREQUENCY, PacketWork.PACKET_ID_CONFIGURATION_FCM_FREQUENCY);
		if(packet!=null && packet.getHeader().getType()==Type.RESPONSE.getType()){
			Payload payload = packet.getPayload(Params.FREQUENCY.getId());
			logger.trace(payload);
			if(payload!=null)
				value = payload.getLong();
		}

		return logger.exit(value);
	}
}
