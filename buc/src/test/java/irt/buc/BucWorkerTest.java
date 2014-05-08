package irt.buc;

import static org.junit.Assert.*;
import irt.converter.data.UnitValue;
import irt.converter.groups.DeviceInformationGroup;
import irt.serial_protocol.ComPort;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;

public class BucWorkerTest {

	protected final Logger logger = (Logger) LogManager.getLogger();

	private static final String COM_PORT = "COM1";

	@Test
	public void muteTest() throws Exception {
		BucWorker bucWorker = new BucWorker();
		try(ComPort comPort = new ComPort(COM_PORT)){

			//Mute
			bucWorker.setMute(comPort, true);
			boolean mute = bucWorker.isMute(comPort);
			logger.trace("\nMute1 = {}", mute);
			assertTrue(mute);

			//Unmute
			bucWorker.setMute(comPort, false);
			mute = bucWorker.isMute(comPort);
			logger.trace("Mute2 = {}", mute);
			assertFalse(mute);
		}
	}

	@Test
	public void attenuationTest() throws Exception {
		BucWorker bucWorker = new BucWorker();
		try(ComPort comPort = new ComPort(COM_PORT)){

			UnitValue setAttenuation = bucWorker.setAttenuation(comPort, (short)( Math.random() * (20 + 1)));
			UnitValue getAttenuation = bucWorker.getAttenuation(comPort);
			logger.trace("\nsetAttenuation = {}, getAttenuation={}", setAttenuation, getAttenuation);
			assertEquals(setAttenuation, getAttenuation);
		}
	}

	@Test
	public void deviceInfoTest() throws Exception {
		BucWorker bucWorker = new BucWorker();
		try(ComPort comPort = new ComPort(COM_PORT)){

			DeviceInformationGroup unitInfo = bucWorker.getUnitInfo(comPort);
			logger.trace("\n{}", unitInfo);

			assertNotNull(unitInfo.getGroup());
			assertNotNull(unitInfo.getSerialNumber());
			assertNotNull(unitInfo.getUnitName());
			assertNotNull(unitInfo.getPartNumber());
			assertNotNull(unitInfo.getFirmwareVersion());
			assertNotNull(unitInfo.getFirmwareBuildDate());
			assertNotNull(unitInfo.getUptimeCounter());
			assertNotNull(unitInfo.getAddress());
		}
	}
}
