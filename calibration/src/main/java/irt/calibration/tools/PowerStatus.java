package irt.calibration.tools;

import java.util.Optional;
import java.util.function.Function;

import irt.calibration.tools.furnace.data.CommandParameter;

/**
 * @author Oleksandr
 *
 */
public enum PowerStatus implements CommandParameter{

	OFF	("OFF"	, CommandType.SET),
	ON	("ON"	, CommandType.SET),
	GET(""		, CommandType.GET);

	private final String command;
	private final CommandType commandType;

	private PowerStatus(String command, CommandType commandType) {
		this.command = command;
		this.commandType = commandType;
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
	public String toString(String value) {
		return Optional.of(commandType).filter(ct->CommandType.isSetCommand(ct)).map(ct->' ' + command).orElse("");
	}
//
//	@Override
//	public String toString() {
//		return name();
//	}

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
