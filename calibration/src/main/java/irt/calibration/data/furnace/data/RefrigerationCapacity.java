package irt.calibration.data.furnace.data;

/**
 * @author Oleksandr
 *
 */
public enum RefrigerationCapacity implements SettingData{

	/*
	 * 	REF0 : Refrigeration OFF
		REF1 ~ REF2 : 20% refrigeration capacity
		REF3 ~ REF5 : 50% refrigeration capacity
		REF6 ~ REF8 : 100% refrigeration capacity
		REF9 : Auto refrigeration capacity control
	 */
	REF0,
	REF1,
	REF2,
	REF3,
	REF4,
	REF5,
	REF6,
	REF7,
	REF8,
	REF9;

	@Override
	public String toString(String value) {
		return name();
	}
}
