package irt.calibration.tools.unit.packets.enums;

import java.util.Arrays;
import java.util.Optional;

public enum Error {
	NO_ERROR						((byte) 0, "No error (positive acknowledge). Indicates successful completion of command or request."),
	INTERNAL_ERROR					((byte) 1, "System internal error during operation."),
	WRITE_ERROR						((byte) 2, "Non-volatile memory write error."),
	FUNCTION_NOT_IMPLEMENTED		((byte) 3, "Function not implemented."),
	VALUE_OUT_OF_RANGE				((byte) 4, "Value outside of valid range."),
	INFORMATION_CANNOT_BE_GENERATED	((byte) 5, "Requested information can�t be generated."),
	COMMAND_CANNOT_BE_EXECUTED		((byte) 6, "Command can�t be executed."),
	INVALID_DATA_FORMAT				((byte) 7, "Invalid data format."),
	INVALID_VALUE					((byte) 8, "Invalid value, same as �Value out of range� error, but more generic."),
	NO_MEMORY						((byte) 9, "Not enough memory for operation."),
	REQUESTED_ELEMENT_NOT_FOUND		((byte) 10, "Requested element not found."),
	TIMED_OUT						((byte) 11, "Timed out.");

	private byte packetError;
	private String description;

	private Error(byte packetError, String description) {
		this.packetError = packetError;
		this.description = description;
	}

	public byte toByte() {
		return packetError;
	}

	@Override
	public String toString() {
		return description;
	}

	public static Optional<Error> valueOf(byte error) {
		return Arrays.stream(values()).filter(v->v.packetError==error).findAny();
	}
}
