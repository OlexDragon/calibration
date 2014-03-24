package irt.power_meter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import irt.power_meter.data.EPM_441A;
import irt.prologix.communication.PrologixWorker;
import irt.prologix.data.PrologixGpibUsbController.DeviceType;
import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.value.Enums.FalseOrTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;

public class PowerMeterWorlerTest {

	private final Logger logger = (Logger) LogManager.getLogger();

	private static final String COM_PORT_NAME = "COM7";

	private EPM_441A powerMeter = new EPM_441A();

	@Test
	public void constuctorTest() {
		try(ComPort comPort = new ComPort(COM_PORT_NAME)){
			PrologixWorker prologixWorker = new PrologixWorker(comPort);
			new PowerMeterWorler(prologixWorker, powerMeter);

			assertEquals(FalseOrTrue.FALSE, 	prologixWorker.getSaveConfig()	);
			assertEquals(DeviceType.CONTROLLER, prologixWorker.getMode()		);
			assertEquals(FalseOrTrue.TRUE, 		prologixWorker.isReadAfterWrite());
			assertNotNull(powerMeter.getId());

		} catch (Exception e) {
			logger.catching(e);
		};
	}

	@Test
	public void measureTest() {
		try(ComPort comPort = new ComPort(COM_PORT_NAME)){
			PrologixWorker prologixWorker = new PrologixWorker(comPort);
			PowerMeterWorler powerMeterWorler = new PowerMeterWorler(prologixWorker, powerMeter);

			assertNotNull(powerMeterWorler.measure());

		} catch (Exception e) {
			logger.catching(e);
		};
	}
}
