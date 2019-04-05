package irt.calibration.data.furnace.data;

import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.data.CommandType;

public enum SCP_220_Command {

	LAST_RESULT	(""				, CommandType.GET, null, "The processing result for the last processed command."),
	ROM			("ROM"			, CommandType.GET, null, "ROM version."),
	DATE		("DATE"			, CommandType.GET, null, "The date of the internal calendar."),
	TIME		("TIME"			, CommandType.GET, null, "The present time of the internal clock."),
	SRQ			("SRQ"			, CommandType.GET, null, "SRQ status."),
	MASK		("MASK"			, CommandType.GET, null, "The SRQ mask setting."),
	PRGM_USE	("PRGM USE"		, CommandType.GET, null, "Management information on registered programs."),
	TIMER_USE	("TIMER USE"	, CommandType.GET, null, "The number of currently used timers."),
	TIMER_LIST	("TIMER LIST"	, CommandType.GET, null, "Timer setup."),
	TIMER_ON	("TIMER ON"		, CommandType.GET, null, "The number of active timers."),
	ALARM		("ALARM"		, CommandType.GET, null, "Alarms that have occurred."),
	KEYPROTECT	("KEYPROTECT"	, CommandType.GET, null, "Key lock status."),
	TYPE		("TYPE"			, CommandType.GET, null, "Chamber information."),
	MODE		("MODE"			, CommandType.GET, null, "Chamber operating mode."),
	MON			("MON"			, CommandType.GET, null, "Conditions inside the chamber."),
	TEMP		("TEMP"			, CommandType.BOTH, ConstantMode.class			, "Temperature parameters for the constant mode."),
	HUMI		("HUMI"			, CommandType.BOTH, ConstantMode.class			, "Humidity parameters for the constant mode."),
	SET			("SET"			, CommandType.BOTH, RefrigerationCapacity.class	, "Refrigeration capacity control setup."),
	REF			("REF"			, CommandType.GET, null, "Refrigeration output."),
	RELAY		("RELAY"		, CommandType.BOTH, PowerStatus.class, "Constant mode time signal ON/OFF setup."),
	HEATER_OUTPUT("%"			, CommandType.GET, null, "Heater output."),
	PRGM_MON	("PRGM MON"		, CommandType.GET, null, "Run status of the current program."),
	PRGM_SET	("PRGM SET"		, CommandType.GET, null, "Program end mode of the current program."),
	PRGM_DATA	("PRGM DATA"	, CommandType.GET, null, "Setup of the specified program."),
	/*The “program mode” mentioned herein refers to user Program Nos. 1 ~ 20 and ROM
		Program Nos. 21 ~ 30 which are created, edited, and run from the SCP-220 Instrumentation
		(ROM programs cannot be edited). The “remote program mode” refers to the 1-step program
		mode which enables editing, starting, and monitoring via this communication function or EBUS
		communications. For details on the remote program mode, see “4.3 To Run Programs
		from Remote”.*/
	RUN_PRGM_MON("RUN PRGM MON"	, CommandType.GET, null, "Run status of the current remote program."),
	RUN_PRGM	("RUN PRGM"		, CommandType.GET, null, "Program end mode of the current remote program."),
	PRGM_LIST	("PRGM LIST"	, CommandType.GET, null, "Setup of the specified program."),
	POWER		("POWER"		, CommandType.SET, PowerStatus.class, "Turns control power ON/OFF. The chamber will start running in the constant mode.");

	private final static Logger logger = LogManager.getLogger();

	private final String command;
	private final String description;
	private final Class<? extends SettingData> dataClass;
	private final CommandType commandType;

	private SCP_220_Command(String command, CommandType commandType, Class<? extends SettingData> dataClass, String description) {
		this.command = command;
		this.commandType = commandType;
		this.dataClass = dataClass;
		this.description = description;
	}

	public String getCommand() {
		return command;
	}

	public CommandType getCommandType() {
		return commandType;
	}

	public String getDescription() {
		return description;
	}

	public Class<? extends SettingData> getDataClass() {
		return dataClass;
	}

	public Optional<SettingData[]> getDataClassValues(){

		return Optional.ofNullable(dataClass)
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
								return (SettingData[]) method.invoke(null);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								logger.catching(e);
							}
							return null;
						});
	}

	public String commandGet() {
		return command + "?";
	}

	public String commandSet(SettingData settingData, String value) {

		Optional.ofNullable(settingData)
		.filter(sd->settingData!=null)
		.map(Object::getClass)
		.filter(c->c.equals(dataClass))
		.orElseThrow(()->new InvalidParameterException());

		return command + "," + settingData.toString(value);
	}
}
