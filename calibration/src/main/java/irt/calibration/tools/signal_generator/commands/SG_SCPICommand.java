package irt.calibration.tools.signal_generator.commands;

import java.util.Optional;
import java.util.function.Function;

import irt.calibration.tools.CommandType;
import irt.calibration.tools.CommandWithParameter;
import irt.calibration.tools.FrequencyUnit;
import irt.calibration.tools.PowerStatus;
import irt.calibration.tools.PowerUnit;
import irt.calibration.tools.ToolCommand;
import irt.calibration.tools.furnace.data.CommandParameter;

public enum SG_SCPICommand implements CommandWithParameter {

	OUTPUT	("OUTP:STAT", CommandType.BOTH, PowerStatus.values()),
	POWER	("POW:AMPL", CommandType.BOTH, PowerUnit.values()),
	FREQUENCY("FREQ:CW", CommandType.BOTH, FrequencyUnit.values());

	private final String command;
	private final CommandType commandType;
	private final CommandParameter[] parameters;

	private SG_SCPICommand(String command, CommandType commandType, CommandParameter... parameters) {
		this.command = command;
		this.parameters = parameters;
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
	public Optional<CommandParameter[]> getParameterValues() {
		return Optional.of(parameters).filter(ps->ps.length>0);
	}

	@Override
	public Function<byte[], Object> getAnswerConverter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ToolCommand getCommand(CommandParameter commandParameter, String value) {
		// TODO Auto-generated method stub
		return null;
	}
}
