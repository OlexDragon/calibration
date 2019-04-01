package irt.calibration.helpers;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import irt.calibration.data.packets.converters.UnitsConverter;

public class UnitsConverterTest {

//	private final static Logger logger = LogManager.getLogger();

	@Test
	public void test() {
		for(int i=0; i<100000; i++) {
			final String str1 = UnitsConverter.timeUnitsToString(TimeUnit.DAYS, i, TimeUnit.SECONDS);
			final String str2 = UnitsConverter.secondsToString(i);

//			logger.error(str2);
			assertEquals(str2, str1.substring(str1.length()-str2.length()));
		}
	}
}
