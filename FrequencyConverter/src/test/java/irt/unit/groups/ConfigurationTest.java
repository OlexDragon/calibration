package irt.unit.groups;

import static org.junit.Assert.*;
import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.PacketWork;
import irt.serial_protocol.data.value.Enums.FalseOrTrue;
import irt.unit.data.UnitValue;
import irt.unit.groups.ConfigurationGroup.Params;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;

public class ConfigurationTest {

	private final Logger logger = (Logger) LogManager.getLogger();

	@Test
	public void setGetGainTest() throws Exception {
//		Configuration configuration = new Configuration();
//		UnitValue unitValue1;
//		UnitValue unitValue2;
//
//		try(ComPort comPort = new ComPort("COM6")){
//			comPort.openPort();
//			unitValue1 = configuration.setGain(comPort, (short)( Math.random() * (Short.MAX_VALUE + 1)));
//			unitValue2 = configuration.getUnitValue(comPort, Params.GAIN, PacketWork.PACKET_ID_CONFIGURATION_FCM_GAIN);
//		}
//
//		logger.trace("value1={}, value2={}", unitValue1, unitValue2);
//		assertEquals(unitValue1, unitValue2);
	}

	@Test
	public void setGetAttenuationTest() throws Exception {
		UnitValue unitValue1;
		UnitValue unitValue2;

		try(ComPort comPort = new ComPort("COM6")){

			ConfigurationGroup configuration = new ConfigurationGroup();
			unitValue1 = configuration.setAttenuation(comPort, (short)( Math.random() * (20 + 1)));
			unitValue2 = configuration.getUnitValue(comPort, Params.ATTENUATION, PacketWork.PACKET_ID_CONFIGURATION_FCM_ATTENUATION);
		}

		logger.trace("value1={}, value2={}", unitValue1, unitValue2);
		assertEquals(unitValue1, unitValue2);
	}

	@Test
	public void setGetMute() throws Exception {
		UnitValue unitValue1;
		UnitValue unitValue2;
		UnitValue unitValue3;
		UnitValue unitValue4;

		try(ComPort comPort = new ComPort("COM6")){

			ConfigurationGroup configuration = new ConfigurationGroup();
			unitValue1 = configuration.setMute(comPort, FalseOrTrue.TRUE);
			unitValue2 = configuration.getUnitValue(comPort, Params.MUTE, PacketWork.PACKET_ID_CONFIGURATION_FCM_MUTE);
			unitValue3 = configuration.setMute(comPort, FalseOrTrue.FALSE);
			unitValue4 = configuration.getUnitValue(comPort, Params.MUTE, PacketWork.PACKET_ID_CONFIGURATION_FCM_MUTE);
		}

		logger.trace("value1={}, value2={}, value3={}, value4={}", unitValue1, unitValue2, unitValue3, unitValue4);
		assertEquals(unitValue1, unitValue2);
		assertEquals(unitValue3, unitValue4);
	}
}
