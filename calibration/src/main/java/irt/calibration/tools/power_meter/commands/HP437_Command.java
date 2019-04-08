package irt.calibration.tools.power_meter.commands;

import irt.calibration.tools.CommandType;
import irt.calibration.tools.ToolCommand;
import irt.calibration.tools.prologix.PrologixCommand;

public enum HP437_Command implements ToolCommand {

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

	public static ToolCommand getId() {
		return ID;
	}

	public static ToolCommand getValue() {
		return TRIGGER_WITH_DELAY;
	}
}
