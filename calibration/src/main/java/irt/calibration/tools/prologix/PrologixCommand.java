
package irt.calibration.tools.prologix;

import irt.calibration.tools.CommandType;

public enum PrologixCommand {

	SEND_TO_INSTRUMENT(""		, CommandType.BOTH, PrologixDeviceType.CONTROLLER	, PrologixCommandDescription.SEND_TO_INSTRUMENT),
	INSTRUMENT_GET(""			, CommandType.GET, PrologixDeviceType.CONTROLLER	, PrologixCommandDescription.SEND_TO_INSTRUMENT),
	INSTRUMENT_SET(""			, CommandType.SET, PrologixDeviceType.CONTROLLER	, PrologixCommandDescription.SEND_TO_INSTRUMENT),
	/**
	 * Prologix GPIB-USB controller can be configured to automatically address instruments to talk after sending them a command in order to read their response.
	 */
	AUTO		("++auto"		, CommandType.BOTH, PrologixDeviceType.FOR_BOTH	, PrologixCommandDescription.AUTO),
	/**
	 * The addr command is used to configure, or query the GPIB address
	 */
	ADDR		("++addr"		, CommandType.BOTH, PrologixDeviceType.FOR_BOTH	, PrologixCommandDescription.ADDR),
	/**
	 * This command enables or disables the assertion of the EOI signal with the last character of any command sent over GPIB port.
	 */
	EOI			("++eoi"		, CommandType.BOTH, PrologixDeviceType.FOR_BOTH	, PrologixCommandDescription.EOI),
	/**
	 * This command specifies GPIB termination characters.(0 � CR+LF, 1 � CR, 2 � LF, 3 � None)
	 */
	EOS			("++eos"		, CommandType.BOTH, PrologixDeviceType.FOR_BOTH	, PrologixCommandDescription.EOS),
	/**
	 * This command enables or disables the appending of a user specified character (see eot_char) to USB output whenever EOI is detected while reading a character from the GPIB port.
	 */
	EOT_ENABLE	("++eot_enable"	, CommandType.BOTH, PrologixDeviceType.FOR_BOTH	, PrologixCommandDescription.EOT_ENABLE),
	/**
	 * This command specifies the character to be appended to USB output when eot_enable is set to 1 and EOI is detected.
	 */
	EOT_CHAR	("++eot_char"	, CommandType.BOTH, PrologixDeviceType.FOR_BOTH	, PrologixCommandDescription.EOT_CHAR),
	/**
	 * This command configures the Prologix GPIB-USB controller to be a CONTROLLER or DEVICE.
	 */
	MODE		("++mode"		, CommandType.BOTH, PrologixDeviceType.FOR_BOTH	, PrologixCommandDescription.MODE),
	/*
	 * This command performs a power-on reset of the controller. The process takes about 5 seconds. All input received over USB during this time are ignored.
	 */
	RST			("++rst"		, CommandType.SET, PrologixDeviceType.FOR_BOTH	, PrologixCommandDescription.RST),
	/**
	 * This command enables, or disables, automatic saving of configuration parameters in EPROM. If enabled, the following configuration parameters are saved whenever they are updated � mode, addr, auto, eoi, eos, eot_enable, eot_char and read_tmo_ms.
	 */
	SAVECFG		("++savecfg"	, CommandType.BOTH, PrologixDeviceType.FOR_BOTH	, PrologixCommandDescription.SAVECFG),
	/**
	 * This command returns the version string of the Prologix GPIB-USB controller.
	 */
	VER			("++ver"		, CommandType.BOTH, PrologixDeviceType.FOR_BOTH	, PrologixCommandDescription.VER),
	/**
	 * This command prints a brief summary of all available commands.
	 */
	HELP		("++help"		, CommandType.BOTH, PrologixDeviceType.FOR_BOTH	, PrologixCommandDescription.HELP),
	/**
	 * This command sends the Selected Device Clear (SDC) message to the currently specified GPIB address.
	 */
	CLR			("++clr"		, CommandType.SET, PrologixDeviceType.CONTROLLER	, PrologixCommandDescription.CLR),
	/**
	 * This command asserts GPIB IFC signal for 150 microseconds making Prologix GPIB-USB controller the Controller-In-Charge on the GPIB bus.
	 */
	IFC			("++ifc"		, CommandType.BOTH, PrologixDeviceType.CONTROLLER	, PrologixCommandDescription.IFC),
	/**
	 * This command enables front panel operation of the currently addressed instrument.
	 */
	LOC			("++loc"		, CommandType.SET, PrologixDeviceType.CONTROLLER	, PrologixCommandDescription.LOC),
	/**
	 * This command disables front panel operation of the currently addressed instrument.
	 */
	LLO			("++llo"		, CommandType.SET, PrologixDeviceType.CONTROLLER	, PrologixCommandDescription.LLO),
	/**
	 * This command can be used to read data from an instrument until:
	 * <ul>
	 * 	<li> EOI is detected or timeout expires, or </li>
	 * 	<li> A specified character is read or timeout expires, or </li>
	 * 	<li> Timeout expires </li>
	 * </ul>
	 */
	READ		("++read"		, CommandType.BOTH, PrologixDeviceType.CONTROLLER	, PrologixCommandDescription.READ),
	/**
	 * Read until EOI detected or timeout
	 */
	READ_TO_EOI	("++read eoi"	, CommandType.BOTH, PrologixDeviceType.CONTROLLER	, PrologixCommandDescription.READ_TO_EOI),
	/**
	 * This command specifies the timeout value, in milliseconds, to be used in the read command and spoll command.
	 */
	READ_TMO_MS	("++read_tmo_ms", CommandType.BOTH, PrologixDeviceType.CONTROLLER	, PrologixCommandDescription.READ_TMO_MS),
	/**
	 * This command performs a serial poll of the instrument at the specified address.
	 */
	SPOLL		("++spoll"		, CommandType.BOTH, PrologixDeviceType.CONTROLLER	, PrologixCommandDescription.SPOLL),
	/**
	 * This command returns the current state of the GPIB SRQ signal.
	 */
	SRQ			("++srq"		, CommandType.BOTH, PrologixDeviceType.CONTROLLER	, PrologixCommandDescription.SRQ),
	/**
	 * This command issues Group Execute Trigger GPIB command to devices at the specified addresses.
	 */
	TRG			("++trg"		, CommandType.BOTH, PrologixDeviceType.CONTROLLER	, PrologixCommandDescription.TRG),

	/**
	 * This command configures the GPIB-USB controller to listen to all traffic on the GPIB bus, irrespective of the currently specified address.
	 */
	LON			("++lon"		, CommandType.BOTH, PrologixDeviceType.DEVICE		, PrologixCommandDescription.LON),
	/**
	 * The status command is used to specify the device status byte to be returned when serial polled by a GPIB controller.
	 */
	STATUS		("++status"		, CommandType.BOTH, PrologixDeviceType.DEVICE		, PrologixCommandDescription.STATUS);

	private final String command;
	private final PrologixDeviceType deviceType;
	private final String description;
	private final CommandType commandType;

	private PrologixCommand(String command, CommandType commandType, PrologixDeviceType deviceType, PrologixCommandDescription commandsHelp){
		this.command = command;
		this.deviceType = deviceType;
		description = commandsHelp.toString();
		this.commandType = commandType;
	}

	public String getCommand() {
		return command;
	}

	public CommandType getCommandType() {
		return commandType;
	}

	public PrologixDeviceType getDeviceType() {
		return deviceType;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return name();
	}
}
