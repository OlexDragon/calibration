package irt.calibration.tools.furnace.data;

import irt.calibration.tools.CommandType;

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
	REF0(CommandType.SET),
	REF1(CommandType.SET),
	REF2(CommandType.SET),
	REF3(CommandType.SET),
	REF4(CommandType.SET),
	REF5(CommandType.SET),
	REF6(CommandType.SET),
	REF7(CommandType.SET),
	REF8(CommandType.SET),
	REF9(CommandType.SET);

	private final CommandType commandType;

	private RefrigerationCapacity(CommandType commandType) {
		this.commandType = commandType;
	}

	@Override
	public String toString(String value) {
		return name();
	}

	@Override
	public String getCommand() {
		return toString();
	}

	@Override
	public CommandType getCommandType() {
		return commandType;
	}
}
