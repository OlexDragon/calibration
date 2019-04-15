package irt.calibration.tools;

import java.util.function.Function;

public interface ToolCommand {

	String getCommand();
	CommandType getCommandType();
	Function<byte[], Object> getAnswerConverter();
}
