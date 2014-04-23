package irt.unit.groups;

import static org.junit.Assert.assertEquals;
import irt.converter.data.UnitValue;
import irt.converter.groups.ConfigurationGroup;
import irt.converter.groups.ConfigurationGroup.Params;
import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.PacketWork.PacketId;
import irt.serial_protocol.data.value.Enums.FalseOrTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;

public class ConfigurationTest {

	private final Logger logger = (Logger) LogManager.getLogger();

	public static final String COM_PORT = "COM5";

	@Test
	public void setGetGainTest() throws Exception {
		logger.entry();
		ConfigurationGroup configuration = new ConfigurationGroup();
		UnitValue unitValue1;
		UnitValue unitValue2;

		try(ComPort comPort = new ComPort(COM_PORT)){
			comPort.openPort();
			unitValue1 = configuration.setGain(comPort, (short)( Math.random() * (Short.MAX_VALUE + 1)));
			unitValue2 = configuration.getUnitValue(comPort, Params.GAIN, PacketId.CONFIGURATION_GAIN);
		}

		logger.trace("value1={}, value2={}", unitValue1, unitValue2);
		assertEquals(unitValue1, unitValue2);
	}

	@Test
	public void setGetAttenuationTest() throws Exception {
		logger.entry();
		UnitValue unitValue1;
		UnitValue unitValue2;

		try(ComPort comPort = new ComPort(COM_PORT)){

			ConfigurationGroup configuration = new ConfigurationGroup();
			unitValue1 = configuration.setAttenuation(comPort, (short)( Math.random() * (20 + 1)));
			unitValue2 = configuration.getUnitValue(comPort, Params.ATTENUATION, PacketId.CONFIGURATION_ATTENUATION);
		}

		logger.trace("value1={}, value2={}", unitValue1, unitValue2);
		assertEquals(unitValue1, unitValue2);
	}

	@Test
	public void setGetMute() throws Exception {
		logger.entry();
		FalseOrTrue unitValue1;
		FalseOrTrue unitValue2;
		FalseOrTrue unitValue3;
		FalseOrTrue unitValue4;

		try(ComPort comPort = new ComPort(COM_PORT)){

			ConfigurationGroup configuration = new ConfigurationGroup();
			unitValue1 = configuration.setMute(comPort, FalseOrTrue.TRUE);
			unitValue2 = configuration.getMute(comPort);
			unitValue3 = configuration.setMute(comPort, FalseOrTrue.FALSE);
			unitValue4 = configuration.getMute(comPort);
		}

		logger.trace("value1={}, value2={}, value3={}, value4={}", unitValue1, unitValue2, unitValue3, unitValue4);
		assertEquals(unitValue1, unitValue2);
		assertEquals(unitValue3, unitValue4);
	}
}
