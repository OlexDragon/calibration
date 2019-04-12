package irt.calibration.tools;

public class SimpleToolCommand implements ToolCommand {

	private final String command;
	private final CommandType commandType;

	public SimpleToolCommand(String command, CommandType commandType) {
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
	public String toString() {
		return "SimpleToolCommand [command=" + command + ", commandType=" + commandType + "]";
	}

	@Override
	public Object bytesToObject(byte[] bytes) {
		return new String(bytes);
	}

}
