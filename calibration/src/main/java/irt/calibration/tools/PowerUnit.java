package irt.calibration.tools;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Optional;

import irt.calibration.tools.furnace.data.CommandParameter;

/**
 * @author Oleksandr
 *
 */
public enum PowerUnit implements CommandParameter{

	DBM	("DBM"	, CommandType.SET),
	DB	("DB"	, CommandType.SET),
	GET(""		, CommandType.GET);

	private final String command;
	private final CommandType commandType;

	private PowerUnit(String command, CommandType commandType) {

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
		return Optional.of(commandType).filter(ct->ct.match(CommandType.SET)).map(ct->' ' + value + ' ' + command).orElse("");
	}

	private final static NumberFormat formatter = new DecimalFormat("#0.#");     
	public static String toString(double value, PowerUnit frequencyUnit) {
		return formatter.format(value) + " " + frequencyUnit;
	}
}
