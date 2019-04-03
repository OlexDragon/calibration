package irt.calibration.data.power_meter.commands;

public enum HP437b_Command implements PowerMeterCommand{

	ID("*ID?"),
	TRIGGER_HOLD		("TR0"),
	TRIGGER_IMMEDIATE	("TR1"),
	TRIGGER_WITH_DELAY	("TR2"),
	TRIGGER_FREE_RUN	("TR3");

	private final String command;

	private HP437b_Command(String command) {
		this.command = command;
	}

	@Override
	public String getCommand() {
		return command;
	}

	public static PowerMeterCommand getId() {
		return ID;
	}

	public static PowerMeterCommand getValue() {
		return TRIGGER_WITH_DELAY;
	}
}
