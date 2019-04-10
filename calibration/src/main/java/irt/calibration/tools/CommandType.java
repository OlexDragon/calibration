package irt.calibration.tools;

public enum CommandType {
	GET,
	SET,
	BOTH;

	public boolean match(CommandType commandType) {

		if(commandType==BOTH)
			return true;

		return this==commandType;
	}
}
