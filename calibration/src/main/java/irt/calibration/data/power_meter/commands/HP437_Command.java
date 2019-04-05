package irt.calibration.data.power_meter.commands;

import irt.calibration.data.CommandType;
import irt.calibration.data.prologix.PrologixCommand;

public enum HP437_Command implements PowerMeterCommand{

	ID					("*ID?",CommandType.GET),
	TRIGGER_HOLD		("TR0", CommandType.GET),
	TRIGGER_IMMEDIATE	("TR1", CommandType.GET),
	TRIGGER_WITH_DELAY	("TR2", CommandType.GET),
	TRIGGER_FREE_RUN	("TR3", CommandType.GET),
	DEFAULT_READ		(PrologixCommand.READ_TO_EOI.getCommand(), CommandType.GET);

	private final String command;
	private final CommandType commandType;

	private HP437_Command(String command, CommandType commandType) {
		this.command = command;
		this.commandType = commandType;
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandType getCommandType() {
		return commandType;
	}

	public static PowerMeterCommand getId() {
		return ID;
	}

	public static PowerMeterCommand getValue() {
		return TRIGGER_WITH_DELAY;
	}
}
