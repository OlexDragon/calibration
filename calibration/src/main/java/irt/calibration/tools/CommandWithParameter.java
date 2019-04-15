package irt.calibration.tools;

import java.util.Optional;

import irt.calibration.tools.furnace.data.CommandParameter;

public interface CommandWithParameter extends ToolCommand {

	Optional<CommandParameter[]> getParameterValues();
	ToolCommand getCommand(CommandParameter commandParameter, String value);
}
