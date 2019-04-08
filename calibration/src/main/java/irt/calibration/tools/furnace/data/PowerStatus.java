package irt.calibration.tools.furnace.data;

import java.util.Optional;

import irt.calibration.tools.CommandType;

/**
 * @author Oleksandr
 *
 */
public enum PowerStatus implements SettingData{
	OFF(CommandType.SET),
	ON(CommandType.SET);

	private final CommandType commandType;

	private PowerStatus(CommandType commandType) {
		this.commandType = commandType;
	}

	@Override
	public String toString(String value) {
		return Optional.ofNullable(value).map(v->name() + ',' + v ).orElse(name());
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
