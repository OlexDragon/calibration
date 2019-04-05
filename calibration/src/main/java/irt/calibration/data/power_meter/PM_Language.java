package irt.calibration.data.power_meter;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.data.power_meter.commands.HP437_Command;
import irt.calibration.data.power_meter.commands.PowerMeterCommand;

public enum PM_Language {

	HP437(HP437_Command.class),
	SCPI(null);

	private final static Logger logger = LogManager.getLogger();

	private final Class<? extends PowerMeterCommand> powerMeterCommandClass;

	private PM_Language(Class<? extends PowerMeterCommand> powerMeterCommandClass){
		this.powerMeterCommandClass = powerMeterCommandClass;
	}

	public static String getLanguage() {
		return "SYST:LANG?";
	}

	public Optional<PowerMeterCommand[]> getPowerMeterCommands() {
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
				.map(PowerMeterCommand[].class::cast);
	}
}
