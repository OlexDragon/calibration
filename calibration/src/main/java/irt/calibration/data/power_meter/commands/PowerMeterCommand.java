package irt.calibration.data.power_meter.commands;

import irt.calibration.data.CommandType;

public interface PowerMeterCommand {

	String getCommand();
	CommandType getCommandType();
}
