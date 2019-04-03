package irt.calibration.data.packets.parameters;

import org.junit.Test;

public class ParameterTest {

	@Test
	public void test() {
//		final Parameter parameter = new GetAllParameters();
//		assertEquals(MeasurementFCM.ALL, parameter.getParameterID());
		System.out.println(new String(new byte[] {0x2A, 0x49, 0x44, 0x3F, 0x0A, 0x2B, 0x2B, 0x72, 0x65, 0x61, 0x64, 0x20, 0x65, 0x6F, 0x69, (byte)0x0A}));
	}
}
