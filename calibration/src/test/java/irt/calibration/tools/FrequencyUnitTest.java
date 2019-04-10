package irt.calibration.tools;

import static org.junit.Assert.*;

import org.junit.Test;

public class FrequencyUnitTest {

	@Test
	public void test() {
		double value = 950;
		assertEquals(0.95, FrequencyUnit.MHZ.toGHz(value), 0.000000001);
		assertEquals(950d, FrequencyUnit.MHZ.toMHz(value), 0.000000001);
		assertEquals(950000d, FrequencyUnit.MHZ.toKHz(value), 0.000000001);
		assertEquals(950000000d, FrequencyUnit.MHZ.toHz(value), 0.000000001);

		assertEquals("950 MHZ", FrequencyUnit.toString(value, FrequencyUnit.MHZ));
		assertEquals("0.95 GHZ", FrequencyUnit.toString(FrequencyUnit.MHZ.toGHz(value), FrequencyUnit.GHZ));
		assertEquals("950 MHZ", FrequencyUnit.toString(FrequencyUnit.MHZ.toMHz(value), FrequencyUnit.MHZ));
		assertEquals("950000 KHZ", FrequencyUnit.toString(FrequencyUnit.MHZ.toKHz(value), FrequencyUnit.KHZ));
		assertEquals("950000000 HZ", FrequencyUnit.toString(FrequencyUnit.MHZ.toHz(value), FrequencyUnit.HZ));
	}

}
