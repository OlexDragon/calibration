package irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces;

public interface Converter <V> {

	String getTitle();
	V convert(byte[] bytes);
}
