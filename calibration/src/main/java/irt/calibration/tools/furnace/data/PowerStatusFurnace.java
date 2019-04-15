package irt.calibration.tools.furnace.data;

import java.util.Optional;
import java.util.function.Function;

import irt.calibration.tools.CommandType;

/**
 * @author Oleksandr
 *
 */
public enum PowerStatusFurnace implements CommandParameter{
	OFF(CommandType.SET_WITH_ANSWER),
	ON(CommandType.SET_WITH_ANSWER);

	private final CommandType commandType;

	private PowerStatusFurnace(CommandType commandType) {
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

	@Override
	public Function<byte[], Object> getAnswerConverter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NeedValue getNeedValue() {
		return NeedValue.NO;
	}
}
