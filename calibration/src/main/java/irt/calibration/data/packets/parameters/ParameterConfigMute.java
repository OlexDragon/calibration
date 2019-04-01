package irt.calibration.data.packets.parameters;

import irt.calibration.data.packets.parameters.ids.ConfigurationBUC;
import irt.calibration.data.packets.parameters.ids.ConfigurationFCM;

public class ParameterConfigMute extends Parameter {

	public ParameterConfigMute(boolean converter) {
		super(converter ? ConfigurationFCM.MUTE : ConfigurationBUC.MUTE);
	}

}
