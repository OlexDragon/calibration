package irt.calibration.tools.furnace.data;

import java.util.Optional;

import irt.calibration.tools.CommandType;

public enum ConstantMode implements CommandParameter {

	GET("", CommandType.GET),
	TARGET("S", CommandType.SET),
	HIGH_LIMIT("H", CommandType.SET),
	LOW_LIMIT("l", CommandType.SET);

	private final String command;
	private final CommandType commandType;

	private ConstantMode(String command, CommandType commandType) {
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

	@Override
	public String toString(String value) {
		return Optional.ofNullable(value).map(v->command + v.trim()).orElse(command);
	}

	@Override
	public Object bytesToObject(byte[] bytes) {
		return new String(bytes);
	}
}
