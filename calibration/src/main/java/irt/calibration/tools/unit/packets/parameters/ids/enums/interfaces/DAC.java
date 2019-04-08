package irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces;

import irt.calibration.tools.unit.packets.PacketDac;
import irt.calibration.tools.unit.packets.parameters.ids.enums.DAC_BUC;
import irt.calibration.tools.unit.packets.parameters.ids.enums.DAC_FCM;

public interface DAC extends Register {

	Double getMinValue();
	Double getMaxValue();
	PacketDac getPacket(Byte address, Integer value) ;

	public static PacketDac getPacket(Byte address, DacName dacName, Integer value) {
		final String name = dacName.name();
		return new PacketDac(address, address==null ? DAC_FCM.valueOf(name) : DAC_BUC.valueOf(name), value);
	}

	public enum DacName{
		FCM_DAC1,
		FCM_DAC2,
		FCM_DAC3,
		FCM_DAC4;
	}
}
