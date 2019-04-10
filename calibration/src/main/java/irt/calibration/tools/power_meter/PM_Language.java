package irt.calibration.tools.power_meter;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.tools.CommandType;
import irt.calibration.tools.ToolCommand;
import irt.calibration.tools.power_meter.commands.HP437_Command;

public enum PM_Language{

	HP437(HP437_Command.class),
	SCPI(null);

	private final static Logger logger = LogManager.getLogger();

	private final Class<? extends ToolCommand> powerMeterCommandClass;

	private PM_Language(Class<? extends ToolCommand> powerMeterCommandClass){
		this.powerMeterCommandClass = powerMeterCommandClass;
	}

	public static ToolCommand getToolCommand(PM_Language language) {

		final String command = Optional.ofNullable(language).map(l->"SYST:LANG " + language).orElse("SYST:LANG?");
		return new ToolCommand() {
			
			@Override
			public CommandType getCommandType() {
				return CommandType.BOTH;
			}
			
			@Override
			public String getCommand() {
				return command;
			}
		};
	}

	public Optional<ToolCommand[]> getPowerMeterCommands() {
		return Optional.ofNullable(powerMeterCommandClass)
				.map(
						clazz->{
							try {
								return clazz.getMethod("values");
							} catch (NoSuchMethodException | SecurityException e) {
								logger.catching(e);
							}
							return null;
						})
				.map(
						method->{
							try {
								return method.invoke(null);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								logger.catching(e);
							}
							return null;
						})
				.map(ToolCommand[].class::cast);
	}

	@Override
	public String toString() {
		return name();
	}
}
