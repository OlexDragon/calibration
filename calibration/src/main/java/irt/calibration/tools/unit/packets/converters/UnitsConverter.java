package irt.calibration.tools.unit.packets.converters;

import java.util.concurrent.TimeUnit;

public class UnitsConverter{

	public static String timeUnitsToString(TimeUnit greatestUnit, long sourceDuration, TimeUnit sourceUnit) {

		int ordinal = greatestUnit.ordinal();
		if(ordinal<=sourceUnit.ordinal())
			return String.format("%02d", sourceDuration);

		final long greatestDuration = greatestUnit.convert(sourceDuration, sourceUnit);
		final long rest = sourceDuration - sourceUnit.convert(greatestDuration, greatestUnit);

		return String.format("%02d:", greatestDuration) + timeUnitsToString(TimeUnit.values()[--ordinal], rest, sourceUnit);
	}

	public static String secondsToString(long sourceDuration) {

		final StringBuffer sb = new StringBuffer();
		TimeUnit greatestUnit = TimeUnit.DAYS;
		int ordinal = greatestUnit.ordinal();

		while(true){
			if(ordinal<=TimeUnit.SECONDS.ordinal()) {
				sb.append(String.format("%02d", sourceDuration));
				break;
			}

			final long greatestDuration = greatestUnit.convert(sourceDuration, TimeUnit.SECONDS);

			if(greatestDuration>0 || sb.length()>0) {
				sb.append(String.format("%02d:", greatestDuration));
				sourceDuration -= TimeUnit.SECONDS.convert(greatestDuration, greatestUnit);
			}

			greatestUnit = TimeUnit.values()[--ordinal];
		};

		return sb.toString();
	}
}
