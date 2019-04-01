
package irt.calibration.data.prologix;

public enum PrologixCommands {

	/**
	 * The addr command is used to configure, or query the GPIB address
	 */
	ADDR		("++addr"		,PrologixDeviceType.FOR_BOTH	),
	/**
	 * This command enables or disables the assertion of the EOI signal with the last character of any command sent over GPIB port.
	 */
	EOI			("++eoi"		,PrologixDeviceType.FOR_BOTH	),
	/**
	 * This command specifies GPIB termination characters.(0 � CR+LF, 1 � CR, 2 � LF, 3 � None)
	 */
	EOS			("++eos"		,PrologixDeviceType.FOR_BOTH	),
	/**
	 * This command enables or disables the appending of a user specified character (see eot_char) to USB output whenever EOI is detected while reading a character from the GPIB port.
	 */
	EOT_ENABLE	("++eot_enable"	,PrologixDeviceType.FOR_BOTH	),
	/**
	 * This command specifies the character to be appended to USB output when eot_enable is set to 1 and EOI is detected.
	 */
	EOT_CHAR	("++eot_char"	,PrologixDeviceType.FOR_BOTH	),
	/**
	 * This command configures the Prologix GPIB-USB controller to be a CONTROLLER or DEVICE.
	 */
	MODE		("++mode"		,PrologixDeviceType.FOR_BOTH	),
	/*
	 * This command performs a power-on reset of the controller. The process takes about 5 seconds. All input received over USB during this time are ignored.
	 */
	RST			("++rst"		,PrologixDeviceType.FOR_BOTH	),
	/**
	 * This command enables, or disables, automatic saving of configuration parameters in EPROM. If enabled, the following configuration parameters are saved whenever they are updated � mode, addr, auto, eoi, eos, eot_enable, eot_char and read_tmo_ms.
	 */
	SAVECFG		("++savecfg"	,PrologixDeviceType.FOR_BOTH	),
	/**
	 * This command returns the version string of the Prologix GPIB-USB controller.
	 */
	VER			("++ver"		,PrologixDeviceType.FOR_BOTH	),
	/**
	 * This command prints a brief summary of all available commands.
	 */
	HELP		("++help"		,PrologixDeviceType.FOR_BOTH	),

	/**
	 * Prologix GPIB-USB controller can be configured to automatically address instruments to talk after sending them a command in order to read their response.
	 */
	READ_AFTER_WRITE("++auto"	,PrologixDeviceType.CONTROLLER	),
	/**
	 * This command sends the Selected Device Clear (SDC) message to the currently specified GPIB address.
	 */
	CLR			("++clr"		,PrologixDeviceType.CONTROLLER	),
	/**
	 * This command asserts GPIB IFC signal for 150 microseconds making Prologix GPIB-USB controller the Controller-In-Charge on the GPIB bus.
	 */
	IFC			("++ifc"		,PrologixDeviceType.CONTROLLER	),
	/**
	 * This command enables front panel operation of the currently addressed instrument.
	 */
	LOC			("++loc"		,PrologixDeviceType.CONTROLLER	),
	/**
	 * This command disables front panel operation of the currently addressed instrument.
	 */
	LLO			("++llo"		,PrologixDeviceType.CONTROLLER	),
	/**
	 * This command can be used to read data from an instrument until:
	 * <ul>
	 * 	<li> EOI is detected or timeout expires, or </li>
	 * 	<li> A specified character is read or timeout expires, or </li>
	 * 	<li> Timeout expires </li>
	 * </ul>
	 */
	READ		("++read"		,PrologixDeviceType.CONTROLLER	),
	/**
	 * Read until EOI detected or timeout
	 */
	READ_TO_EOI	("++read eoi"	,PrologixDeviceType.CONTROLLER	),
	/**
	 * This command specifies the timeout value, in milliseconds, to be used in the read command and spoll command.
	 */
	READ_TMO_MS	("++read_tmo_ms",PrologixDeviceType.CONTROLLER	),
	/**
	 * This command performs a serial poll of the instrument at the specified address.
	 */
	SPOLL		("++spoll"		,PrologixDeviceType.CONTROLLER	),
	/**
	 * This command returns the current state of the GPIB SRQ signal.
	 */
	SRQ			("++srq"		,PrologixDeviceType.CONTROLLER	),
	/**
	 * This command issues Group Execute Trigger GPIB command to devices at the specified addresses.
	 */
	TRG			("++trg"		,PrologixDeviceType.CONTROLLER	),

	/**
	 * This command configures the GPIB-USB controller to listen to all traffic on the GPIB bus, irrespective of the currently specified address.
	 */
	LON			("++lon"		,PrologixDeviceType.DEVICE		),
	/**
	 * The status command is used to specify the device status byte to be returned when serial polled by a GPIB controller.
	 */
	STATUS		("++status"		,PrologixDeviceType.DEVICE		);

	private final String command;
	private final PrologixDeviceType deviceType;

	private PrologixCommands(String command, PrologixDeviceType deviceType){
		this.command = command;
		this.deviceType = deviceType;
	}

	public String getCommand() {
		return command;
	}

	public PrologixDeviceType getDeviceType() {
		return deviceType;
	}

	@Override
	public String toString() {
		return name();
	}


}
