package irt.calibration.tools;

import java.util.function.Function;

public class SimpleToolCommand implements ToolCommand {

	private final String command;
	private final CommandType commandType;
	private final Function<byte[], Object> converter;

	public SimpleToolCommand(String command, CommandType commandType, Function<byte[], Object> converter) {
		this.command = command;
		this.commandType = commandType;
		this.converter = converter;
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandType getCommandType() {
		return commandType;
	}

	@Override
	public String toString() {
		return "SimpleToolCommand [command=" + command + ", commandType=" + commandType + "]";
	}

	@Override
	public Function<byte[], Object> getAnswerConverter() {
		return converter;
	}

}
