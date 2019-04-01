package irt.calibration.exception;

import java.util.Arrays;

public class PacketException extends IllegalArgumentException{
	private static final long serialVersionUID = -3211581231686617310L;

	public PacketException(String string, byte[] array) {
		super(string + " - " + Arrays.toString(array));
	}
}
