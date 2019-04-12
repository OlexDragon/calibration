package irt.calibration.tools.signal_generator.commands;

import static irt.calibration.tools.CommandWithParameter.getValuesOf;

import java.util.Optional;

import irt.calibration.tools.CommandType;
import irt.calibration.tools.CommandWithParameter;
import irt.calibration.tools.FrequencyUnit;
import irt.calibration.tools.PowerStatus;
import irt.calibration.tools.PowerUnit;
import irt.calibration.tools.furnace.data.CommandParameter;

public enum SG_SCPICommand implements CommandWithParameter {

	OUTPUT	("OUTP:STAT", PowerStatus.class, CommandType.BOTH),
	POWER	("POW:AMPL", PowerUnit.class, CommandType.BOTH),
	FREQUENCY("FREQ:CW", FrequencyUnit.class, CommandType.BOTH);

	private final String command;
	private final CommandType commandType;
	private final Class<? extends CommandParameter> parameterClass;

	private SG_SCPICommand(String command, Class<? extends CommandParameter> parameterClass, CommandType commandType) {
		this.command = command;
		this.parameterClass = parameterClass;
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

	public Class<? extends CommandParameter> getParameterClass() {
		return parameterClass;
	}

	@Override
	public Optional<CommandParameter[]> getParameterValues() {
		return getValuesOf(parameterClass);
	}

	@Override
	public Object bytesToObject(byte[] bytes) {
		return new String(bytes);
	}
}
