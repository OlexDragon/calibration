package irt.unit.groups;

import static org.junit.Assert.assertNotNull;
import irt.converter.groups.DeviceDebugGroup;
import irt.converter.groups.DeviceDebugGroup.ADC;
import irt.serial_protocol.ComPort;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;

public class DeviceDebugGroupTest {

	private final Logger logger = (Logger) LogManager.getLogger(getClass().getName());

	@Test
	public void getADCRegister() {
		try(ComPort comPort = new ComPort("COM6")){

			DeviceDebugGroup deviceDebugGroup = new DeviceDebugGroup();
			assertNotNull(deviceDebugGroup.getADCRegister(comPort, ADC.INPUT_POWER));
			assertNotNull(deviceDebugGroup.getADCRegister(comPort, ADC.OUTPUT_POWER));

		} catch (Exception e) {
			logger.catching(e);
		}
	}
}
