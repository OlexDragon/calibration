package irt.calibration.tools;

public interface ToolCommand {

	String getCommand();
	CommandType getCommandType();
	Object bytesToObject(byte[] bytes);
}
