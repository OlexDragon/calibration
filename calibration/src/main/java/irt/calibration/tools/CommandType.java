package irt.calibration.tools;

public enum CommandType {
	GET,
	SET,
	BOTH,
	SET_WITH_ANSWER;

	public static boolean isSetCommand(CommandType commandType) {

		switch(commandType) {
		case GET:
			return false;
		default:
			return true;
		}
	}

	public static boolean waitForAnswer(CommandType commandType) {

		switch(commandType) {
		case SET:
			return false;
		default:
			return true;
		}
	}
}
