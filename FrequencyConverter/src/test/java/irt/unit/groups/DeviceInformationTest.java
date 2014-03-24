package irt.unit.groups;

import static org.junit.Assert.assertNotNull;
import irt.serial_protocol.ComPort;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;

public class DeviceInformationTest {

	private final Logger logger = (Logger) LogManager.getLogger(getClass().getName());

	@Test
	public void test() {
		try(ComPort comPort = new ComPort("COM6")){

			DeviceInformationGroup deviceInformation = new DeviceInformationGroup();
			deviceInformation.getAll(comPort);

			assertNotNull(deviceInformation.getFirmwareBuildDate());
			assertNotNull(deviceInformation.getFirmwareVersion());
			assertNotNull(deviceInformation.getPartNumber());
			assertNotNull(deviceInformation.getSerialNumber());
			assertNotNull(deviceInformation.getUnitName());
			assertNotNull(deviceInformation.getUptimeCounter());
		} catch (Exception e) {
			logger.catching(e);
		}
	}

}
