package irt.calibration.data.power_meter;

public enum PM_Language {

	HP_437B,
	SCPI;

	public static String getLanguage() {
		return "SYST:LANG?";
	}
}
