package irt.calibration.tools.power_meter;

public enum PM_Model {

	HP_438A	(null),
	E4418B	(PM_Language.values()),
	EPM_441A(PM_Language.values());

	private final PM_Language[] languages;

	private PM_Model(PM_Language[] languages) {
		this.languages = languages;
	}

	public PM_Language[] getLanguages() {
		return languages;
	}
}
