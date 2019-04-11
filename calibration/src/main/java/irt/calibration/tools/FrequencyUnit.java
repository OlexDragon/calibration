package irt.calibration.tools;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.function.Function;

import irt.calibration.tools.furnace.data.CommandParameter;

/**
 * @author Oleksandr
 *
 */
public enum FrequencyUnit implements CommandParameter{

	GHZ	("GHZ"	, CommandType.SET, v->v				, v->v*1000		, v->v*1000000	, v->v*1000000000),
	MHZ	("MHZ"	, CommandType.SET, v->v/1000		, v->v			, v->v*1000		, v->v*1000000),
	KHZ	("KHZ"	, CommandType.SET, v->v/1000000		, v->v/1000		, v->v			, v->v*1000),
	HZ	("HZ"	, CommandType.SET, v->v/1000000000	, v->v/1000000	, v->v/1000		, v->v),
	GET(""		, CommandType.GET, null, null, null, null);

	private final String command;
	private final CommandType commandType;
	private final Function<Double, Double> toGHz;
	private final Function<Double, Double> toMHz;
	private final Function<Double, Double> toKHz;
	private final Function<Double, Double> toHz;

	private FrequencyUnit(
			String command,
			CommandType commandType,
			Function<Double, Double> toGHz,
			Function<Double, Double> toMHz,
			Function<Double, Double> toKHz,
			Function<Double, Double> toHz) {

		this.command = command;
		this.commandType = commandType;
		this.toGHz = toGHz;
		this.toMHz = toMHz;
		this.toKHz = toKHz;
		this.toHz = toHz;
	}

	public double toGHz(double value) {
		return toGHz.apply(value);
	}

	public double toMHz(double value) {
		return toMHz.apply(value);
	}

	public double toKHz(double value) {
		return toKHz.apply(value);
	}

	public double toHz(double value) {
		return toHz.apply(value);
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

	private final static NumberFormat formatter = new DecimalFormat("#0.#########");     
	public static String toString(double value, FrequencyUnit frequencyUnit) {
		return formatter.format(value) + " " + frequencyUnit;
	}
}
