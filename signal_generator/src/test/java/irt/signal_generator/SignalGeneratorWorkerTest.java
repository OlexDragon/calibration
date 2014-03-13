package irt.signal_generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import irt.prologix.communication.PrologixWorker;
import irt.prologix.data.PrologixGpibUsbController.DeviceType;
import irt.prologix.data.PrologixGpibUsbController.FalseOrTrue;
import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.value.ValueDouble;
import irt.serial_protocol.data.value.ValueFrequency;
import irt.signal_generator.data.SG_8648;
import irt.signal_generator.data.SG_8648.OnOrOff;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;

public class SignalGeneratorWorkerTest {

	private static final String COM_PORT = "COM7";

	private Logger logger = (Logger) LogManager.getLogger();

	private SG_8648 signalGenerator = new SG_8648();

	@Test
	public void constuctorTest() {
		try(ComPort comPort = new ComPort(COM_PORT)){
			PrologixWorker prologixWorker = new PrologixWorker(comPort);
			new SignalGeneratorWorker(prologixWorker, signalGenerator);

			assertEquals(FalseOrTrue.FALSE, 	prologixWorker.getSaveConfig()	);
			assertEquals(DeviceType.CONTROLLER, prologixWorker.getMode()		);
			assertEquals(FalseOrTrue.TRUE, 		prologixWorker.isReadAfterWrite());
			assertNotNull(signalGenerator.getId());

		} catch (Exception e) {
			logger.catching(e);
		};
	}

	@Test
	public void setFrequencyTest() {
		try(ComPort comPort = new ComPort(COM_PORT)){
			PrologixWorker prologixWorker = new PrologixWorker(comPort);
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
	}

	@Test
	public void setPowerTest() {
		try(ComPort comPort = new ComPort(COM_PORT)){
			PrologixWorker prologixWorker = new PrologixWorker(comPort);
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
	}

	@Test
	public void setRfOnTest() {
		try(ComPort comPort = new ComPort(COM_PORT)){
			PrologixWorker prologixWorker = new PrologixWorker(comPort);
			SignalGeneratorWorker signalGeneratorWorker = new SignalGeneratorWorker(prologixWorker, signalGenerator);

			signalGeneratorWorker.setRFOn(OnOrOff.ON);
			Thread.sleep(500);
			assertEquals(OnOrOff.ON, signalGeneratorWorker.isRFOn());

			signalGeneratorWorker.setRFOn(OnOrOff.OFF);

		} catch (Exception e) {
			logger.catching(e);
		};
	}
}
