package irt.gui_callibration.controller;

import irt.buc.BucWorker;
import irt.buc.groups.DeviceDebugGroup.BucADC;
import irt.converter.groups.Group.UnitType;
import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.RegisterValue;
import irt.serial_protocol.data.value.Enums.FalseOrTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;

public class ControllerTest {

	private static final Logger logger = (Logger) LogManager.getLogger();

	@Test
	public void test() throws Exception {
		Controller controller = new Controller(null);
		controller.setAddress((byte) 254);
		controller.setUnitType(UnitType.BUC);

		BucWorker bucWorker = new BucWorker((byte) 254);

		try(ComPort comPort = new ComPort("COM1")){
			controller.setMute(comPort, FalseOrTrue.TRUE);
			Thread.sleep(1000);
			controller.setMute(comPort, FalseOrTrue.FALSE);
			for(int i=0; i<100; i++){
				Thread.sleep(100);
				RegisterValue outputPower = bucWorker.getDeviceDebugGroup().getADCRegister(comPort, BucADC.DEVICE_CURRENT_1);
				RegisterValue outputPowerAverage = bucWorker.getDeviceDebugGroup().getADCRegister(comPort, BucADC.DEVICE_CURRENT_1_AVERAGE);
				logger.trace("*** reg1={},  reg10={} *** - {}", outputPower, outputPowerAverage, i);
			}
		}
	}

}
