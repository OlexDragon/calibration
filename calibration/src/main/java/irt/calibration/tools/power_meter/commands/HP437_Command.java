package irt.calibration.tools.power_meter.commands;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;

import irt.calibration.tools.CommandType;
import irt.calibration.tools.ToolCommand;
import irt.calibration.tools.prologix.PrologixCommand;

public enum HP437_Command implements ToolCommand {

	ID					("*ID?",CommandType.GET	, String::new),
	ERROR				("ERR?", CommandType.GET, String::new),
	TRIGGER_HOLD		("TR0", CommandType.GET	, HP437_Command::bytesToDouble),
	TRIGGER_IMMEDIATE	("TR1", CommandType.GET	, HP437_Command::bytesToDouble),
	TRIGGER_WITH_DELAY	("TR2", CommandType.GET	, HP437_Command::bytesToDouble),
	TRIGGER_FREE_RUN	("TR3", CommandType.GET	, HP437_Command::bytesToDouble),
	DEFAULT_READ		(PrologixCommand.READ_TO_EOI.getCommand(), CommandType.GET	, HP437_Command::bytesToDouble);

	private final String command;
	private final CommandType commandType;
	private final Function<byte[], Object> bytesTo;

	private HP437_Command(String command, CommandType commandType, Function<byte[], Object> bytesTo) {
		this.command = command;
		this.commandType = commandType;
		this.bytesTo = bytesTo;
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
	public Object bytesToObject(byte[] bytes) {
		return bytesTo.apply(bytes);
	}

	public static ToolCommand getId() {
		return ID;
	}

	public static ToolCommand getValue() {
		return TRIGGER_WITH_DELAY;
	}

	public static double bytesToDouble(byte[] bytes) {

		final int index = IntStream.range(0, bytes.length).filter(b->bytes[b]==(byte)10).findAny().orElse(-1) + 1;

		byte[] b;
		if(index>0 && index<bytes.length)
			b = Arrays.copyOfRange(bytes, 0, index);
		else
			b = bytes;

		final String trim = new String(b).trim();

		if(trim.isEmpty())
			return Double.NaN;

		return Double.parseDouble(trim);
	}
}
