package irt.calibration.tools.unit.packets;

import irt.calibration.tools.unit.packets.enums.GroupID;
import irt.calibration.tools.unit.packets.enums.PacketID;
import irt.calibration.tools.unit.packets.enums.PacketType;
import irt.calibration.tools.unit.packets.parameters.ParameterConfigMute;
import irt.calibration.tools.unit.packets.parameters.ids.ConfigurationBUC;
import irt.calibration.tools.unit.packets.parameters.ids.ConfigurationFCM;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.tools.unit.packets.parents.PacketImpl;

public class PacketMuteControl extends PacketImpl {

	public PacketMuteControl(Byte addr, MuteStatus muteStatus) {
		super(
				addr,
				muteStatus==null ? PacketType.REQUEST : PacketType.COMMAND,
						GroupID.CONFIGURATION,
						PacketID.MUTE,
						new ParameterConfigMute(addr==null).putData(muteStatus==null ? null : new byte[] {(byte) muteStatus.ordinal()}));
	}

	public PacketMuteControl(byte[] array) {
		super(array);
	}

	public enum MuteStatus{
		UNMUTED,
		MUTED;

		public MuteStatus toggle() {
			return values()[(ordinal()+1)%2];
		}
	}

	public enum MuteCommand{
		UNMUTE,
		MUTE;
	}

	@Override
	protected Class<? extends Converter<?>> getConverterClass() {
		return getAddress()==null ? ConfigurationFCM.class : ConfigurationBUC.class;
	}
}
