package irt.calibration.data.furnace.data;

import java.util.Optional;

/**
 * @author Oleksandr
 *
 */
public enum PowerStatus implements SettingData{
	OFF,
	ON;

	@Override
	public String toString(String value) {
		return Optional.ofNullable(value).map(v->name() + ',' + v ).orElse(name());
	}
}
