package irt.converter.groups;

import static org.junit.Assert.assertNotNull;
import irt.converter.groups.DeviceDebugGroup;
import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.RegisterValue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;

public class DeviceDebugGroupTest {

	private final Logger logger = (Logger) LogManager.getLogger(getClass().getName());

	@Test
	public void getADCRegister() {
		logger.entry();
		try(ComPort comPort = new ComPort(ConfigurationGroupTest.COM_PORT)){

			DeviceDebugGroup deviceDebugGroup = new DeviceDebugGroup();
			RegisterValue inputPower = deviceDebugGroup.getInputPower(comPort);
			RegisterValue outputPower = deviceDebugGroup.getOutputPower(comPort);

			logger.trace("inputPower={}, ={}", inputPower, outputPower);
			assertNotNull(inputPower);
			assertNotNull(outputPower);

		} catch (Exception e) {
			logger.catching(e);
		}
	}
}
