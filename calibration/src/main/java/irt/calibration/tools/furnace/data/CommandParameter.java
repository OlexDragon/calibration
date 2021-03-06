package irt.calibration.tools.furnace.data;

import irt.calibration.tools.ToolCommand;

public interface CommandParameter extends ToolCommand {

	String toString(String value);
	NeedValue getNeedValue();

	public enum NeedValue{
		YES,
		NO,
		BITH
	}
}
