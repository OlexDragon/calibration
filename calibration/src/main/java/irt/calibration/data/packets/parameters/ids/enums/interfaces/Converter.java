package irt.calibration.data.packets.parameters.ids.enums.interfaces;

public interface Converter <V> {

	String getTitle();
	V convert(byte[] bytes);
}
