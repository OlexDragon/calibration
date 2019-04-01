
package irt.calibration.data.prologix;

public enum PrologixCommandsHelp {

	/**
	 * The addr command is used to configure, or query the GPIB address
	 */
	ADDR		("The addr command is used to configure, or query the GPIB address. Meaning of the GPIB address depends on the operating mode of the controller. In CONTROLLER mode, it refers to the GPIB address of the instrument being controlled. In DEVICE mode, it is the address of the GPIB peripheral that Prologix GPIB-USB controller is emulating.\r\n" + 
			"An optional secondary address may also be specified. Secondary address must be separated from the primary address by a space character. Valid secondary address values are 96 to 126 (decimal). Secondary address value of 96 corresponds to secondary GPIB address of 0, 97 corresponds to 1, and so on. Specifying secondary address has no effect in DEVICE mode.\r\n" + 
			"If the command is issued with no parameters, the currently configured address (primary, and secondary, if specified) is returned.\r\n" + 
			"SYNTAX: ++addr [<PAD> [<SAD>]]\r\n" + 
			"PAD (Primary Address) is a decimal value between 0 and 30.\r\n" + 
			"SAD (Secondary Address) is a decimal value between 96 and 126. SAD is optional.\r\n" + 
			"MODES AVAILABLE: CONTROLLER, DEVICE\r\n" + 
			"EXAMPLES:\r\n" + 
			"++addr 5 – Set primary address to 5\r\n" + 
			"++addr – Query current address\r\n" + 
			"++addr 9 96 – Set primary address to 9 and secondary address to 0\r\n" + 
			"NOTE:\r\n" + 
			"Default GPIB address of many HP-GL/2 plotters is 5."),
	/**
	 * This command enables or disables the assertion of the EOI signal with the last character of any command sent over GPIB port.
	 */
	EOI			("This command enables or disables the assertion of the EOI signal with the last character of any command sent over GPIB port. Some instruments require EOI signal to be asserted in order to properly detect the end of a command.\r\n" + 
			"SYNTAX: ++eoi [0|1]\r\n" + 
			"Prologix GPIB-USB Controller User Manual\r\n" + 
			"5/14/2013 10\r\n" + 
			"MODES AVAILABLE: CONTROLLER, DEVICE\r\n" + 
			"EXAMPLES:\r\n" + 
			"++eoi 1 Enable EOI assertion with last character\r\n" + 
			"++eoi 0 Disable EOI assertion\r\n" + 
			"++eoi Query if EOI assertion is enabled or disabled"),
	/**
	 * This command specifies GPIB termination characters.(0 – CR+LF, 1 – CR, 2 – LF, 3 – None)
	 */
	EOS			("This command specifies GPIB termination characters. When data from host is received over USB, all non-escaped LF, CR and ESC characters are removed and GPIB terminators, as specified by this command, are appended before sending the data to instruments. This command does not affect data from instruments received over GPIB port.\r\n" + 
			"If the command is issued with no arguments then the current configuration is returned.\r\n" + 
			"SYNTAX: ++eos [0|1|2|3] where: 0 – CR+LF, 1 – CR, 2 – LF, 3 – None\r\n" + 
			"MODES AVAILABLE: CONTROLLER, DEVICE\r\n" + 
			"EXAMPLES:\r\n" + 
			"++eos 0 Append CR+LF to instrument commands\r\n" + 
			"++eos 1 Append CR to instrument commands\r\n" + 
			"++eos 2 Append LF to instrument commands\r\n" + 
			"++eos 3 Do not append anything to instrument commands\r\n" + 
			"++eos Query current EOS state"),
	/**
	 * This command enables or disables the appending of a user specified character (see eot_char) to USB output whenever EOI is detected while reading a character from the GPIB port.
	 */
	EOT_ENABLE	("This command enables or disables the appending of a user specified character (see eot_char) to USB output whenever EOI is detected while reading a character from the GPIB port.\r\n" + 
			"If the command is issued without any argument, the current state of eot_enable is returned.\r\n" + 
			"SYNTAX: eot_enable [0|1]\r\n" + 
			"MODES AVAILABLE: CONTROLLER, DEVICE\r\n" + 
			"EXAMPLES:\r\n" + 
			"++eot_enable 1 Append user defined character when EOI detected\r\n" + 
			"++eot_enable 0 Do not append character when EOI detected\r\n" + 
			"++eot_enable Query current eot_enable state"),
	/**
	 * This command specifies the character to be appended to USB output when eot_enable is set to 1 and EOI is detected.
	 */
	EOT_CHAR	("This command specifies the character to be appended to USB output when eot_enable is set to 1 and EOI is detected.\r\n" + 
			"If the command is issued without any argument, the currently specified character is returned.\r\n" + 
			"SYNTAX: eot_char [<char>] where <char> is a decimal value less than 256\r\n" + 
			"MODES AVAILABLE: CONTROLLER, DEVICE\r\n" + 
			"EXAMPLES:\r\n" + 
			"++eot_char 42 Append * (ASCII 42) when EOI is detected\r\n" + 
			"++eot_char Query currently configured eot_char"),
	/**
	 * This command configures the Prologix GPIB-USB controller to be a CONTROLLER or DEVICE.
	 */
	MODE		("This command configures the Prologix GPIB-USB controller to be a CONTROLLER or DEVICE.\r\n" + 
			"If the command is issued without any arguments, the current mode is returned.\r\n" + 
			"SYNTAX: ++mode [0|1] where 1 – CONTROLLER, 0 – DEVICE\r\n" + 
			"MODES AVAILABLE: CONTROLLER, DEVICE\r\n" + 
			"EXAMPLES:\r\n" + 
			"++mode 1 Switch to CONTROLLER mode\r\n" + 
			"++mode 0 Switch to DEVICE mode\r\n" + 
			"++mode Query current mode"),
	/*
	 * This command performs a power-on reset of the controller. The process takes about 5 seconds. All input received over USB during this time are ignored.
	 */
	RST			("This command performs a power-on reset of the controller. The process takes about 5 seconds. All input received over USB during this time are ignored.\r\n" + 
			"SYNTAX: ++rst\r\n" + 
			"MODES AVAILABLE: CONTROLLER, DEVICE"),
	/**
	 * This command enables, or disables, automatic saving of configuration parameters in EPROM. If enabled, the following configuration parameters are saved whenever they are updated – mode, addr, auto, eoi, eos, eot_enable, eot_char and read_tmo_ms.
	 */
	SAVECFG		("This command enables, or disables, automatic saving of configuration parameters in EPROM. If enabled, the following configuration parameters are saved whenever they are updated – mode, addr, auto, eoi, eos, eot_enable, eot_char and read_tmo_ms.\r\n" + 
			"However, frequent updates may eventually wear out the EPROM. This command may be used to temporarily disable automatic saving of configuration parameters to reduce EEPROM wear.\r\n" + 
			"The savecfg setting itself is not saved in EPROM. It is always enabled on startup (after power up, or reset).\r\n" + 
			"SYNTAX: ++savecfg [0|1]\r\n" + 
			"MODES AVAILABLE: CONTROLLER, DEVICE\r\n" + 
			"EXAMPLE:\r\n" + 
			"++savecfg 1 Enable saving of configuration parameters in EPROM\r\n" + 
			"++savecfg 0 Disable saving of configuration parameters in EPROM\r\n" + 
			"++savecfg Query current setting\r\n" + 
			"NOTE:\r\n" + 
			"Prologix GPIB-USB Controller User Manual\r\n" + 
			"5/14/2013 14\r\n" + 
			"“++savecfg 1” command will immediately save the current values of all configuration parameters, in addition to enabling the automatic saving of parameters."),
	/**
	 * This command returns the version string of the Prologix GPIB-USB controller.
	 */
	VER			("This command returns the version string of the Prologix GPIB-USB controller.\r\n" + 
			"SYNTAX: ++ver\r\n" + 
			"MODES AVAILABLE: CONTROLLER, DEVICE"),
	/**
	 * This command prints a brief summary of all available commands.
	 */
	HELP		("This command prints a brief summary of all available commands.\r\n" + 
			"SYNTAX: ++help\r\n" + 
			"MODES AVAILABLE: CONTROLLER, DEVICE"),

	/**
	 * Prologix GPIB-USB controller can be configured to automatically address instruments to talk after sending them a command in order to read their response.
	 */
	READ_AFTER_WRITE("Prologix GPIB-USB controller can be configured to automatically address instruments to talk after sending them a command in order to read their response. The feature called,\r\n" + 
			"Prologix GPIB-USB Controller User Manual\r\n" + 
			"5/14/2013 9\r\n" + 
			"Read-After-Write, saves the user from having to issue read commands repeatedly. This command enabled or disabled the Read-After-Write feature.\r\n" + 
			"In addition, auto command also addresses the instrument at the currently specified address to TALK or LISTEN. ++auto 0 addresses the instrument to LISTEN and ++auto 1 addresses the instrument to TALK.\r\n" + 
			"If the command is issued without any arguments it returns the current state of the read-after-write feature.\r\n" + 
			"SYNTAX: ++auto [0|1]\r\n" + 
			"MODES AVAILABLE: CONTROLLER\r\n" + 
			"NOTE:\r\n" + 
			"Some instruments generate “Query Unterminated” or “-420” error if they are addressed to talk after sending a command that does not generate a response (often called non-query commands). In effect the instrument is saying, I have been asked to talk but I have nothing to say. The error is often benign and may be ignored. Otherwise, use the ++read command to read the instrument response. For example:\r\n" + 
			"++auto 0 — Turn off read-after-write and address instrument to listen\r\n" + 
			"SET VOLT 1.0 — Non-query command\r\n" + 
			"*idn? — Query command\r\n" + 
			"++read eoi — Read until EOI asserted by instrument\r\n" + 
			"\"HP54201A\" — Response from instrument"),
	/**
	 * This command sends the Selected Device Clear (SDC) message to the currently specified GPIB address.
	 */
	CLR			("This command sends the Selected Device Clear (SDC) message to the currently specified GPIB address. Please consult the programming manual for details on how a particular instrument responds to this message.\r\n" + 
			"SYNTAX: ++clr\r\n" + 
			"MODES AVAILABLE: CONTROLLER"),
	/**
	 * This command asserts GPIB IFC signal for 150 microseconds making Prologix GPIB-USB controller the Controller-In-Charge on the GPIB bus.
	 */
	IFC			("This command asserts GPIB IFC signal for 150 microseconds making Prologix GPIB-USB controller the Controller-In-Charge on the GPIB bus.\r\n" + 
			"SYNTAX: ++ifc\r\n" + 
			"MODES AVAILABLE: CONTROLER"),
	/**
	 * This command enables front panel operation of the currently addressed instrument.
	 */
	LOC			("This command enables front panel operation of the currently addressed instrument.\r\n" + 
			"SYNTAX: ++loc\r\n" + 
			"MODES AVAILABLE: CONTROLLER"),
	/**
	 * This command disables front panel operation of the currently addressed instrument.
	 */
	LLO			("This command disables front panel operation of the currently addressed instrument.\r\n" + 
			"SYNTAX: ++llo\r\n" + 
			"MODES AVAILABLE: CONTROLLER"),
	/**
	 * This command can be used to read data from an instrument until:
	 * <ul>
	 * 	<li> EOI is detected or timeout expires, or </li>
	 * 	<li> A specified character is read or timeout expires, or </li>
	 * 	<li> Timeout expires </li>
	 * </ul>
	 */
	READ		("This command can be used to read data from an instrument until:\r\n" + 
			" EOI is detected or timeout expires, or\r\n" + 
			" A specified character is read or timeout expires, or\r\n" + 
			" Timeout expires\r\n" + 
			"Timeout is set using the read_tmo_ms command and applies to inter-character delay, i.e., the delay since the last character was read. Timeout is not be confused with the total time for which data is read.\r\n" + 
			"SYNTAX: ++read [eoi|<char>] where <char> is a decimal value less than 256\r\n" + 
			"MODES AVAILABLE: CONTROLLER\r\n" + 
			"EXAMPLES:\r\n" + 
			"++read Read until timeout\r\n" + 
			"++read eoi Read until EOI detected or timeout\r\n" + 
			"Prologix GPIB-USB Controller User Manual\r\n" + 
			"5/14/2013 13\r\n" + 
			"++read 10 Read until LF (ASCII 10) is received or timeout"),
	/**
	 * Read until EOI detected or timeout
	 */
	READ_TO_EOI	("This command can be used to read data from an instrument until:\r\n" + 
			" EOI is detected or timeout expires, or\r\n" + 
			" A specified character is read or timeout expires, or\r\n" + 
			" Timeout expires\r\n" + 
			"Timeout is set using the read_tmo_ms command and applies to inter-character delay, i.e., the delay since the last character was read. Timeout is not be confused with the total time for which data is read.\r\n" + 
			"SYNTAX: ++read [eoi|<char>] where <char> is a decimal value less than 256\r\n" + 
			"MODES AVAILABLE: CONTROLLER\r\n" + 
			"EXAMPLES:\r\n" + 
			"++read Read until timeout\r\n" + 
			"++read eoi Read until EOI detected or timeout\r\n" + 
			"Prologix GPIB-USB Controller User Manual\r\n" + 
			"5/14/2013 13\r\n" + 
			"++read 10 Read until LF (ASCII 10) is received or timeout"),
	/**
	 * This command specifies the timeout value, in milliseconds, to be used in the read command and spoll command.
	 */
	READ_TMO_MS	("This command specifies the timeout value, in milliseconds, to be used in the read command and spoll command. Timeout may be set to any value between 1 and 3000 milliseconds.\r\n" + 
			"SYNTAX: ++read_tmo_ms <time> where <time> is decimal value between 1 and 3000\r\n" + 
			"MODES AVAILABLE: CONTROLLER"),
	/**
	 * This command performs a serial poll of the instrument at the specified address.
	 */
	SPOLL		("This command performs a serial poll of the instrument at the specified address. If no address is specified then this command serial polls the currently addressed instrument (as set by a previous ++addr command). This command uses the time-out value specified by the read_tmo_ms command.\r\n" + 
			"SYNTAX: ++spoll [<PAD> [<SAD>]]\r\n" + 
			"PAD (Primary Address) is a decimal value between 0 and 30.\r\n" + 
			"SAD (Secondary Address) is a decimal value between 96 and 126. SAD is optional.\r\n" + 
			"MODES AVAILABLE: CONTROLLER\r\n" + 
			"EXAMPLE:\r\n" + 
			"++spoll 5 Serial poll instrument at primary address 5\r\n" + 
			"++spoll 9 96 Serial poll instrument at primary address 9, seconday address 0\r\n" + 
			"++spoll Serial poll currently addressed instrument"),
	/**
	 * This command returns the current state of the GPIB SRQ signal.
	 */
	SRQ			("This command returns the current state of the GPIB SRQ signal. The command returns ‘1’ is SRQ signal is asserted (low) and ‘0’ if the signal is not asserted (high).\r\n" + 
			"SYNTAX: ++srq\r\n" + 
			"MODES AVAILABLE: CONTROLLER"),
	/**
	 * This command issues Group Execute Trigger GPIB command to devices at the specified addresses.
	 */
	TRG			("This command issues Group Execute Trigger GPIB command to devices at the specified addresses. Up to 15 addresses maybe specified. Addresses must be separated by spaces. If no address is specified then Group Execute Trigger command is issued to the currently addressed instrument (as set by a previous ++addr command). Refer to the programming manual for a specific instrument’s response to Group Execute Trigger command.\r\n" + 
			"SYNTAX: ++trg [<PAD1> [<SAD1>] <PAD2> [SAD2] … <PAD15> [<SAD15>]]\r\n" + 
			"MODES AVAILABLE: CONTROLLER"),

	/**
	 * This command configures the GPIB-USB controller to listen to all traffic on the GPIB bus, irrespective of the currently specified address.
	 */
	LON			("This command configures the GPIB-USB controller to listen to all traffic on the GPIB bus, irrespective of the currently specified address. This configuration is also known as “listen-only” mode. In this mode, the controller can only receive, but cannot send any data.\r\n" + 
			"Prologix GPIB-USB Controller User Manual\r\n" + 
			"5/14/2013 12\r\n" + 
			"SYNTAX: lon [0|1]\r\n" + 
			"MODES AVAILABLE: DEVICE\r\n" + 
			"EXAMPLES:\r\n" + 
			"++lon 1 Enable “listen-only” mode\r\n" + 
			"++lon 0 Disable “listen-only” mode\r\n" + 
			"++lon Query “listen-only” mode"),
	/**
	 * The status command is used to specify the device status byte to be returned when serial polled by a GPIB controller.
	 */
	STATUS		("The status command is used to specify the device status byte to be returned when serial polled by a GPIB controller. If the RQS bit (bit #6) of the status byte is set then the SRQ signal is asserted (low). After a serial poll, SRQ line is de-asserted and status byte is set to 0. Status byte is initialized to 0 on power up.\r\n" + 
			"SRQ is also de-asserted and status byte is cleared if DEVICE CLEAR (DCL) message, or SELECTED DEVICE CLEAR (SDC) message, is received from the GPIB controller.\r\n" + 
			"If the command is issued without any arguments it returns the currently specified status byte.\r\n" + 
			"SYNTAX: ++status [0-255]\r\n" + 
			"Prologix GPIB-USB Controller User Manual\r\n" + 
			"5/14/2013 15\r\n" + 
			"MODES AVAILABLE: DEVICE\r\n" + 
			"EXAMPLE:\r\n" + 
			"++status 48 Specify serial poll status byte as 48. Since bit #6 is set, this\r\n" + 
			"command will assert SRQ.\r\n" + 
			"++status Query current serial poll status byte.");

	private String help;

	private PrologixCommandsHelp(String help){
		this.help = help;
	}

	@Override
	public String toString() {
		return help;
	}


}
