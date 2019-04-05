package irt.calibration.data.furnace.data;

import java.util.Optional;

public enum ConstantMode implements SettingData {

	NONE(""),
	TARGET(",S"),
	HIGH_LIMIT(",H"),
	LOW_LIMIT(",l");

	private final String command;

	private ConstantMode(String command) {
		this.command = command;
	}

	@Override
	public String toString(String value) {
		return Optional.ofNullable(value).map(v->command + v.trim()).orElse(command);
	}
}
