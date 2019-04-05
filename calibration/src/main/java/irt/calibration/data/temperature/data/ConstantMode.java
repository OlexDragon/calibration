package irt.calibration.data.temperature.data;

import java.security.InvalidParameterException;
import java.util.Optional;

public enum ConstantMode implements SettingData {

	TARGET("S"),
	HIGH_LIMIT("H"),
	LOW_LIMIT("l");

	private final String command;

	private ConstantMode(String command) {
		this.command = command;
	}

	@Override
	public String toString(String value) {
		Optional.ofNullable(value).orElseThrow(()->new InvalidParameterException("The Value can not be null"));
		return command + value;
	}
}
