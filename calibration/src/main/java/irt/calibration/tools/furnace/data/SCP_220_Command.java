package irt.calibration.tools.furnace.data;

import java.util.Optional;
import java.util.function.Function;

import irt.calibration.tools.CommandType;
import irt.calibration.tools.CommandWithParameter;
import irt.calibration.tools.SimpleToolCommand;
import irt.calibration.tools.ToolCommand;

public enum SCP_220_Command implements CommandWithParameter {

	LAST_RESULT	(""				, CommandType.GET	, "The processing result for the last processed command.", null),
	ROM			("ROM"			, CommandType.GET	, "ROM version.", null),
	DATE		("DATE"			, CommandType.GET	, "The date of the internal calendar.", null),
	TIME		("TIME"			, CommandType.GET	, "The present time of the internal clock.", null),
	SRQ			("SRQ"			, CommandType.GET	, "SRQ status.", null),
	MASK		("MASK"			, CommandType.GET	, "The SRQ mask setting.", null),
	PRGM_USE	("PRGM USE"		, CommandType.GET	, "Management information on registered programs.", null),
	TIMER_USE	("TIMER USE"	, CommandType.GET	, "The number of currently used timers.", null),
	TIMER_LIST	("TIMER LIST"	, CommandType.GET	, "Timer setup.", null),
	TIMER_ON	("TIMER ON"		, CommandType.GET	, "The number of active timers.", null),
	ALARM		("ALARM"		, CommandType.GET	, "Alarms that have occurred.", null),
	KEYPROTECT	("KEYPROTECT"	, CommandType.GET	, "Key lock status.", null),
	TYPE		("TYPE"			, CommandType.GET	, "Chamber information.", null),
	MODE		("MODE"			, CommandType.GET	, "Chamber operating mode.", null),
	MON			("MON"			, CommandType.GET	, "Conditions inside the chamber.", null),
	TEMP		("TEMP"			, CommandType.BOTH	, "Temperature parameters for the constant mode.", null, ConstantMode.values()),
	HUMI		("HUMI"			, CommandType.BOTH	, "Humidity parameters for the constant mode.", null, ConstantMode.values()),
	SET			("SET"			, CommandType.BOTH	, "Refrigeration capacity control setup.", null, RefrigerationCapacity.values()),
	REF			("REF"			, CommandType.GET	, "Refrigeration output.", null),
	RELAY		("RELAY"		, CommandType.BOTH	, "Constant mode time signal ON/OFF setup.", null, PowerStatusFurnace.values()),
	HEATER_OUTPUT("%"			, CommandType.GET	, "Heater output.", null),
	PRGM_MON	("PRGM MON"		, CommandType.GET	, "Run status of the current program.", null),
	PRGM_SET	("PRGM SET"		, CommandType.GET	, "Program end mode of the current program.", null),
	PRGM_DATA	("PRGM DATA"	, CommandType.GET	, "Setup of the specified program.", null),
	/*The “program mode” mentioned herein refers to user Program Nos. 1 ~ 20 and ROM
		Program Nos. 21 ~ 30 which are created, edited, and run from the SCP-220 Instrumentation
		(ROM programs cannot be edited). The “remote program mode” refers to the 1-step program
		mode which enables editing, starting, and monitoring via this communication function or EBUS
		communications. For details on the remote program mode, see “4.3 To Run Programs
		from Remote”.*/
	RUN_PRGM_MON("RUN PRGM MON"	, CommandType.GET, "Run status of the current remote program.", null),
	RUN_PRGM	("RUN PRGM"		, CommandType.GET, "Program end mode of the current remote program.", null),
	PRGM_LIST	("PRGM LIST"	, CommandType.GET, "Setup of the specified program.", null),
	POWER		("POWER"		, CommandType.SET, "Turns control power ON/OFF. The chamber will start running in the constant mode.", null, PowerStatusFurnace.values());

//	private final static Logger logger = LogManager.getLogger();

	private final String command;
	private final String description;
	private final CommandParameter[] commandParameters;
	private final CommandType commandType;
	private final Function<byte[], Object> converter;

	private SCP_220_Command(String command, CommandType commandType, String description, Function<byte[], Object> converter, CommandParameter... commandParameters) {
		this.command = command;
		this.commandType = commandType;
		this.commandParameters = commandParameters;
		this.description = description;
		this.converter = converter;
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public CommandType getCommandType() {
		return commandType;
	}

	public String getDescription() {
		return description;
	}

	public CommandParameter[] getCommandParameters() {
		return commandParameters;
	}

	@Override
	public Optional<CommandParameter[]> getParameterValues(){

		return Optional.ofNullable(commandParameters);
	}

	@Override
	public Function<byte[], Object> getAnswerConverter() {
		return converter;
	}

	@Override
	public ToolCommand getCommand(CommandParameter commandParameter, String value) {

		String command;
		switch(commandType) {
		case GET:
			command = commandGet(commandParameter);
			return new SimpleToolCommand(command, commandType, commandParameter.getAnswerConverter());
		case SET:
			command = commandSet(commandParameter, value) ;
			return new SimpleToolCommand(command, commandType, commandParameter.getAnswerConverter());
		default:
			CommandType ct = commandParameter.getCommandType();
			command = Optional.of(ct).filter(type->type==CommandType.GET).map(type->commandGet(commandParameter)).orElseGet(()->commandSet(commandParameter, value));
			return new SimpleToolCommand(command, ct, commandParameter.getAnswerConverter());
	
		}
	}

	private String commandSet(CommandParameter commandParameter, String value) {
		return command + "," + commandParameter.getCommand() + value;
	}

	private String commandGet(CommandParameter commandParameter) {
		return Optional.of(commandParameter.getCommand())
				.filter(p->!p.isEmpty())
				.map(p->command + "?," + p)
				.orElseGet(()->command + "?");
	}
}
