package irt.calibration.data.packets.parameters.ids;

import java.util.Arrays;

import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;

public class ParameterIDUnknown implements Converter<String> {

	private String title = "Unknown";

	@Override
	public String convert(byte[] bytes) {
		return Arrays.toString(bytes);
	}

	@Override
	public String getTitle() {
		return title;
	}

	public Converter<?> setTitle(String title) {
		this.title = title;
		return this;
	}
}
