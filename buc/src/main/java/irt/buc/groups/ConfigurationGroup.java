package irt.buc.groups;

import irt.converter.data.UnitValue;
import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.PacketWork.PacketId;
import irt.serial_protocol.data.Range;
import irt.serial_protocol.data.packet.Packet;
import irt.serial_protocol.data.packet.PacketHeader.Type;
import irt.serial_protocol.data.packet.Payload;

public class ConfigurationGroup extends irt.converter.groups.ConfigurationGroup{

	public static final byte CONFIGURATION = Packet.IRT_SLCP_PACKET_ID_CONFIGURATION;

	public enum Params implements Parameter{
		NONE				(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_PICOBUC_NONE				),
		GAIN				(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_PICOBUC_GAIN				),
		ATTENUATION			(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_PICOBUC_ATTENUATION		),
		FREQUENCY			(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_PICOBUC_USER_FREQUENCY		),
		FREQUENCY_RANGE		(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_PICOBUC_USER_FREQUENCY_RANGE),
		GAIN_RANGE			(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_PICOBUC_GAIN_RANGE			),
		ATTENUATION_RANGE	(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_PICOBUC_ATTENUATION_RANGE	),
		MUTE				(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_PICOBUC_MUTE_CONTROL		),
		BUC_ENABLE			(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_FCM_BUC_ENABLE				),
		ALL					(Packet.IRT_SLCP_PARAMETER_CONFIGURATION_PICOBUC_ALL				);

		private byte parameter;

		private Params(byte parameter){
			this.parameter = parameter;
		}

		public byte getId() {
			return parameter;
		}
	}

	public ConfigurationGroup(Byte address) {
		setAddress(address);
	}

	@Override
	public Range getFrequencyRange(ComPort comPort) {	
		logger.entry(comPort);
		Range range = null;

		Packet packet = getPacket(comPort, Params.FREQUENCY_RANGE, PacketId.CONFIGURATION_FREQUENCY_RANGE);
		if(packet!=null){
			Payload payload = packet.getPayload(Params.FREQUENCY_RANGE.getId());
			if(payload!=null){
				range = new Range(payload);
			}
		}

		return logger.exit(range);
	}

	@Override
	public Long getFrequency(ComPort comPort) {
		logger.entry(comPort);
		Long value = null;

		Packet packet = getPacket(comPort, Params.FREQUENCY, PacketId.CONFIGURATION_FREQUENCY);
		if(packet!=null && packet.getHeader().getType()==Type.RESPONSE.getType()){
			Payload payload = packet.getPayload(Params.FREQUENCY.getId());
			logger.trace(payload);
			if(payload!=null)
				value = payload.getLong();
		}

		return logger.exit(value);
	}

	@Override
	public MuteStatus getMute(ComPort comPort) {
		MuteStatus muteStatus = null;

		UnitValue unitValue = setUnitValue(comPort, Params.MUTE, PacketId.CONFIGURATION_MUTE,  null);
		if(unitValue!=null)
			muteStatus = MuteStatus.values()[unitValue.getValue()];

		return muteStatus;
	}

	@Override
	public MuteStatus setMute(ComPort comPort, MuteStatus muteStatus) {
		logger.entry(comPort, muteStatus);

		UnitValue setUnitValue = setUnitValue(comPort, Params.MUTE, PacketId.CONFIGURATION_MUTE,  (byte)muteStatus.ordinal());

		MuteStatus mute = null;
		if(setUnitValue!=null)
			mute = MuteStatus.values()[setUnitValue.getValue()];

		return mute;
	}
}
