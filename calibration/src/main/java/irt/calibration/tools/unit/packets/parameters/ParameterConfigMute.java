package irt.calibration.tools.unit.packets.parameters;

import irt.calibration.tools.unit.packets.parameters.ids.ConfigurationBUC;
import irt.calibration.tools.unit.packets.parameters.ids.ConfigurationFCM;

public class ParameterConfigMute extends Parameter {

	public ParameterConfigMute(boolean converter) {
		super(converter ? ConfigurationFCM.MUTE : ConfigurationBUC.MUTE);
	}

}
