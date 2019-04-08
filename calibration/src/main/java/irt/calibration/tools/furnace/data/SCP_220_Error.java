package irt.calibration.tools.furnace.data;

import java.util.Arrays;

public enum SCP_220_Error {

	CMD_ERR			("NA:CMD ERR"			, "Chamber did not recognize the command data."	, "When “TENMP?” is sent as the command. The correct command is “TEMP?”."),
	ADDR_ERR		("NA:ADDR ERR"			, "Address error"								, "When an address is attached to a command which cannot be expressed with an address."),
	CONT_NOT_READY_1("NA:CONT NOT READY-1"	, "The chamber could not execute the command."	, "When a humidity command is sent to a chamber which does not support humidity control."),
	CONT_NOT_READY_2("NA:CONT NOT READY-2"	, "The chamber could not execute the command."	, "When a program related command is sent to a chamber that does not have a program initiated."),
	CONT_NOT_READY_3("NA:CONT NOT READY-3"	, "The chamber could not execute the command."	, "When an SCP-220 Instrumentation command is sent to a chamber with a different controller."),
	CONT_NOT_READY_4("NA:CONT NOT READY-4"	, "The chamber could not execute the command."	, "When the user attempts to lock keys while power is OFF."),
	CONT_NOT_READY_5("NA:CONT NOT READY-5"	, "The chamber could not execute the command."	, "When the user attempts to change a time signal setting which cannot be changed."),
	DATA_NOT_READY	("NA:DATA NOT READY"	, "The requested data cannot be found."			, "When the user attempts to run a program which is not set up."),
	PARA_ERR		("NA:PARA ERR"			, "Parameter error"								, "The parameter is missing in the command.OR When the attached parameter can not be recognized because of typographical error, etc."),
	DATA_OUT_OF_RANGE("NA:DATA OUT OF RANGE", "Data is out of the specified range."			, "When the user attempts to set the target temperature beyond either the temperature high or low alarm."),
	PROTECT_ON		("NA:PROTECT ON"		, "Settings are protected against change by the protect setting.", "When the user attempts to change the target temperature when the protection is ON."),
	PRGM_WRITE_ERR_1("NA:PRGM WRITE ERR-1"	, "Program editing error"						, "When the user attempts to write data into a ROM pattern."),
	PRGM_WRITE_ERR_2("NA:PRGM WRITE ERR-2"	, "Program editing error"						, "When the user attempts to write data without specifying the new program/overwrite mode."),
	PRGM_WRITE_ERR_3("NA:PRGM WRITE ERR-3"	, "Program editing error"						, "When an edit request is sent while not in the new program mode."),
	PRGM_WRITE_ERR_4("NA:PRGM WRITE ERR-4"	, "Program editing error"						, "When reception is OFF because a new program is being created."),
	PRGM_WRITE_ERR_5("NA:PRGM WRITE ERR-5"	, "Program editing error"						, "When reception is OFF because overwriting is in process."),
	PRGM_WRITE_ERR_6("NA:PRGM WRITE ERR-6"	, "Program editing error"						, "When an overwrite request is sent while not in the overwrite mode."),
	PRGM_WRITE_ERR_7("NA:PRGM WRITE ERR-7"	, "Program editing error"						, "When the program number specified on an individual line differs from the program being written to."),
	PRGM_WRITE_ERR_8("NA:PRGM WRITE ERR-8"	, "Program editing error"						, "When non-continuous step numbers are specified."),
	PRGM_WRITE_ERR_9("NA:PRGM WRITE ERR-9"	, "Program editing error"						, "When counter setup is wrong."),
	PRGM_WRITE_ERR_10("NA:PRGM WRITE ERR-10", "Program editing error"						, "When the user attempts to change program data while the program is running."),
	PRGM_WRITE_ERR_11("NA:PRGM WRITE ERR-11", "Program editing error"						, "When the user attempts to set a counter or the end mode without the necessary data."),
	PRGM_WRITE_ERR_12("NA:PRGM WRITE ERR-12", "Program editing error"						, "When the user attempts to change a program created on the SCP-220 instrumentation."),
	PRGM_WRITE_ERR_13("NA:PRGM WRITE ERR-13", "Program editing error"						, "When step data is invalid."),
	PRGM_WRITE_ERR_14("NA:PRGM WRITE ERR-14", "Program editing error"						, "When the user attempts to set exposure time when ramp control is ON."),
	PRGM_WRITE_ERR_15("NA:PRGM WRITE ERR-15", "Program editing error"						, "When the user attempts to turn humidity ramp control ON when humidity control is OFF.");

	private final String errorMessage;
	private final String meaning;
	private final String example;

	private SCP_220_Error(String errorMessage, String meaning, String example) {
		this.errorMessage = errorMessage;
		this.meaning = meaning;
		this.example = example;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getMeaning() {
		return meaning;
	}

	public String getExample() {
		return example;
	}

	public static SCP_220_Error responseToError(String response) {
		return Arrays.stream(values()).filter(v->v.errorMessage.equals(response)).findAny().orElse(null);
	}
}
