package irt.prologix.communication;

import static org.junit.Assert.assertEquals;
import irt.prologix.data.PrologixGpibUsbController.DeviceType;
import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.value.Enums.FalseOrTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;

public class PrologixWorkerTest {

	private static final String COM_PORT_NAME = "COM7";

	private final Logger logger = (Logger) LogManager.getLogger();

	@Test
	public void modeTest() throws Exception {

		try(ComPort comPort = new ComPort(COM_PORT_NAME)){
			PrologixWorker prologixWorker = new PrologixWorker(comPort);

			DeviceType deviceType = DeviceType.values()[(int) (Math.random()*2)];
			prologixWorker.setMode(deviceType);

			DeviceType actual = prologixWorker.getMode();
			logger.trace("deviceType={}, actual={}", deviceType, actual);
			assertEquals(deviceType, actual);

		}catch(Exception ex){
			logger.catching(ex);
		}
	}

	@Test
	public void addrTest(){
		logger.entry();

		try(ComPort comPort = new ComPort(COM_PORT_NAME)){
			PrologixWorker prologixWorker = new PrologixWorker(comPort);

			int addrToSend = (byte) (Math.random()*30);
			prologixWorker.setAddr(addrToSend);

			Byte getAddr = prologixWorker.getAddr();

			logger.trace("addrToSend={}, getAddr={}", addrToSend, getAddr);
			assertEquals(addrToSend, getAddr!=null ? (int)getAddr : addrToSend+1);

			logger.exit();
		}catch(Exception ex){
			logger.catching(ex);
		}
	}

	@Test
	public void autoTest(){

		try(ComPort comPort = new ComPort(COM_PORT_NAME)){
			PrologixWorker prologixWorker = new PrologixWorker(comPort);

			if(prologixWorker.getMode()!=DeviceType.CONTROLLER)
				prologixWorker.setMode(DeviceType.CONTROLLER);

			FalseOrTrue isAuto = FalseOrTrue.values()[(int) (Math.random()*2)];
			prologixWorker.setReadAfterWrite(isAuto);

			FalseOrTrue falseOrTrue = prologixWorker.isReadAfterWrite();

			logger.trace("isAuto={}, getAddr={}", isAuto, falseOrTrue);
			assertEquals(isAuto, falseOrTrue);

		}catch(Exception ex){
			logger.catching(ex);
		}
	}
}
