package irt.calibration.tools.furnace.data;

import java.util.Optional;
import java.util.function.Function;

import irt.calibration.tools.CommandType;
import irt.calibration.tools.furnace.Temperature;

public enum ConstantMode implements CommandParameter {

	GET			(""	, CommandType.GET				, NeedValue.NO	, Temperature::new),
	TARGET		("S", CommandType.SET_WITH_ANSWER	, NeedValue.YES	, bytes->new String(bytes).trim()),
	HIGH_LIMIT	("H", CommandType.SET_WITH_ANSWER	, NeedValue.YES	, bytes->new String(bytes).trim()),
	LOW_LIMIT	("l", CommandType.SET_WITH_ANSWER	, NeedValue.YES	, bytes->new String(bytes).trim());

	private final String parameter;
	private final CommandType commandType;
	private final NeedValue needValue;
	private final Function<byte[], Object> converter;

	private ConstantMode(String command, CommandType commandType, NeedValue needValue, Function<byte[], Object> converter) {
		this.parameter = command;
		this.commandType = commandType;
		this.needValue = needValue;
		this.converter = converter;
	}

	@Override
	public CommandType getCommandType() {
		return commandType;
	}

	@Override
	public String toString(String value) {
		return Optional.ofNullable(value).map(v->parameter + v.trim()).orElse(parameter);
	}

	@Override
	public Function<byte[], Object> getAnswerConverter() {
		return converter;
	}

	@Override
	public String getCommand() {
		return parameter;
	}

	@Override
	public NeedValue getNeedValue() {
		return needValue;
	}
}
