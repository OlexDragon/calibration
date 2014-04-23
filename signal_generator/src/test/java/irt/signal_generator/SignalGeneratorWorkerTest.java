package irt.signal_generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import irt.prologix.communication.PrologixWorker;
import irt.prologix.communication.PrologixWorkerTest;
import irt.prologix.data.PrologixGpibUsbController.DeviceType;
import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.value.Enums.FalseOrTrue;
import irt.serial_protocol.data.value.ValueDouble;
import irt.serial_protocol.data.value.ValueFrequency;
import irt.signal_generator.data.SG_8648;
import irt.signal_generator.data.SG_8648.OnOrOff;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;

public class SignalGeneratorWorkerTest {

	private static final String COM_PORT_NAME = PrologixWorkerTest.COM_PORT_NAME;

	private Logger logger = (Logger) LogManager.getLogger();

	@Test
	public void constuctorTest() {
		logger.entry();
		try(ComPort comPort = new ComPort(COM_PORT_NAME)){
			PrologixWorker prologixWorker = new PrologixWorker(comPort);
			SG_8648 signalGenerator = new SG_8648();
			new SignalGeneratorWorker(prologixWorker, signalGenerator);

			assertEquals(FalseOrTrue.FALSE, 	prologixWorker.getSaveConfig()	);
			assertEquals(DeviceType.CONTROLLER, prologixWorker.getMode()		);
			assertEquals(FalseOrTrue.TRUE, 		prologixWorker.isReadAfterWrite());
			assertNotNull(signalGenerator.getId());

		} catch (Exception e) {
			logger.catching(e);
		};
		logger.exit();
	}

	@Test
	public void setFrequencyTest() {
		logger.entry();
		try(ComPort comPort = new ComPort(COM_PORT_NAME)){
			PrologixWorker prologixWorker = new PrologixWorker(comPort);
			SG_8648 signalGenerator = new SG_8648();
			SignalGeneratorWorker signalGeneratorWorker = new SignalGeneratorWorker(prologixWorker, signalGenerator);

			ValueFrequency valueFrequency = signalGenerator.getValueFrequency();

			long minValue = valueFrequency.getMinValue();

			long rundom = (long) (minValue+Math.random()*(valueFrequency.getMaxValue()-minValue+1));
			signalGeneratorWorker.setFrequency(rundom);

			String string = valueFrequency.toString();
			assertEquals(string, signalGeneratorWorker.getFrequency());

		} catch (Exception e) {
			logger.catching(e);
		};
		logger.exit();
	}

	@Test
	public void setPowerTest() {
		logger.entry();
		try(ComPort comPort = new ComPort(COM_PORT_NAME)){
			PrologixWorker prologixWorker = new PrologixWorker(comPort);
			SG_8648 signalGenerator = new SG_8648();
			SignalGeneratorWorker signalGeneratorWorker = new SignalGeneratorWorker(prologixWorker, signalGenerator);

			ValueDouble valuePower = signalGenerator.getValuePower();

			long minValue = valuePower.getMinValue();

			long rundom = (long) (minValue+Math.random()*(valuePower.getMaxValue()-minValue+1));
			signalGeneratorWorker.setPower(rundom);

			String string = valuePower.toString();
			assertEquals(string, signalGeneratorWorker.getPower());

		} catch (Exception e) {
			logger.catching(e);
		};
		logger.exit();
	}

	@Test
	public void setRfOnTest(){
		logger.entry();
		try(ComPort comPort = new ComPort(COM_PORT_NAME)){
			PrologixWorker prologixWorker = new PrologixWorker(comPort);
			logger.trace("new SG_8648()");
			SG_8648 signalGenerator = new SG_8648();
			logger.trace("new SignalGeneratorWorker()");
			SignalGeneratorWorker signalGeneratorWorker = new SignalGeneratorWorker(prologixWorker, signalGenerator);

			logger.trace("signalGeneratorWorker.setRFOn(OnOrOff.ON)");
			signalGeneratorWorker.setRFOn(OnOrOff.ON);
			Thread.sleep(500);

			OnOrOff rfOn = signalGeneratorWorker.isRFOn();
			signalGeneratorWorker.setRFOn(OnOrOff.OFF);

			logger.trace("OnOrOff.{}", rfOn);
			assertEquals(OnOrOff.ON, rfOn);

		} catch (Exception e) {
			logger.catching(e);
		};
		logger.exit();
	}
}
