package irt.unit.groups;

import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.packet.Packet;
import junit.framework.TestCase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;

public class MonitorTest extends TestCase {

	private final Logger logger = (Logger) LogManager.getLogger();

	private MeasurementGroup monitorConverter;

	public MonitorTest() throws Exception {
		Packet packet = MeasurementGroup.GET_ALL;
		logger.trace(packet);
		try(ComPort comPort = new ComPort("COM6")){
			packet = comPort.send(packet);
		}
		monitorConverter = new MeasurementGroup();
		monitorConverter.setPacket(packet);
		logger.trace(packet);
	}

	@Test
	public void testGetInputPower() {
		assertNotNull(monitorConverter.getInputPower());
	}

	@Test
	public void testGetOutputPower() {
		assertNotNull(monitorConverter.getOutputPower());
	}
}
