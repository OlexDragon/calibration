package irt.calibration.tools.power_meter;

import java.util.Optional;

import irt.calibration.tools.CommandType;
import irt.calibration.tools.SimpleToolCommand;
import irt.calibration.tools.ToolCommand;
import irt.calibration.tools.power_meter.commands.HP437_Command;

public enum PM_Language{

	HP437(HP437_Command.values()),
	SCPI();

//	private final static Logger logger = LogManager.getLogger();

	private final ToolCommand[] powerMeterCommandClass;

	private PM_Language(ToolCommand... powerMeterCommandClass){
		this.powerMeterCommandClass = powerMeterCommandClass;
	}

	public static ToolCommand getToolCommand(PM_Language language) {

		final Optional<PM_Language> oLanguage = Optional.ofNullable(language);
		final String command = oLanguage.map(l->"SYST:LANG " + language).orElse("SYST:LANG?");

		return new SimpleToolCommand(
				command,
				oLanguage.isPresent() ? CommandType.SET : CommandType.GET,
				bytes->{
					String string = new String(bytes).trim();
					return PM_Language.valueOf(string);
				}) ;
	}

	public Optional<ToolCommand[]> getPowerMeterCommands() {
		return Optional.ofNullable(powerMeterCommandClass);
	}

	@Override
	public String toString() {
		return name();
	}
}
