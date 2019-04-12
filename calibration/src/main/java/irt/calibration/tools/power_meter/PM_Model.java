package irt.calibration.tools.power_meter;

import irt.calibration.PrologixController.AutoMode;

public enum PM_Model {

	HP_438A	(null, AutoMode.OFF),
	E4418B	(PM_Language.values(), AutoMode.ON),
	EPM_441A(PM_Language.values(), AutoMode.ON);

	private final PM_Language[] languages;
	private final AutoMode autoMode;

	private PM_Model(PM_Language[] languages, AutoMode autoMode) {
		this.languages = languages;
		this.autoMode = autoMode;
	}

	public PM_Language[] getLanguages() {
		return languages;
	}

	public AutoMode getAoutoMode() {
		return autoMode;
	}
}
