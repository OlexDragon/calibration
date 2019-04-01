package irt.calibration.helpers;

import java.util.Map;

import irt.calibration.data.packets.PacketPowerInputAdc;
import irt.calibration.data.packets.PacketPowerOutputAdc;
import irt.calibration.data.packets.PacketTemperaturAdc;
import irt.calibration.data.packets.parameters.Parameter;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class AdcWorker extends UnitWorker{

	public AdcWorker(ChoiceBox<String> chbUnitSerialPort, TextField tfUnitAddress) {
		super(chbUnitSerialPort, tfUnitAddress);
	}

	public Map<Converter<?>, Parameter> getPowerInputAdc() {

		final Byte unitAddress = getUnitAddress();
		PacketPowerInputAdc packet = new PacketPowerInputAdc(unitAddress);
		final byte[] array = writeThenRead(packet, WAIT_TIME);
		packet = new PacketPowerInputAdc(array);

		errorCheck(packet);

//		logger.error(packet);
		return packet.parametersToMap();
	}

	public Map<Converter<?>, Parameter> getPowerOutputAdc() {

		final Byte unitAddress = getUnitAddress();
		PacketPowerOutputAdc packet = new PacketPowerOutputAdc(unitAddress);
		final byte[] array = writeThenRead(packet, WAIT_TIME);

		packet = new PacketPowerOutputAdc(array);

		errorCheck(packet);

//		logger.error(packet);
		return packet.parametersToMap();
	}

	public Map<Converter<?>, Parameter> getTemperatureAdc() {

		final Byte unitAddress = getUnitAddress();
		PacketTemperaturAdc packet = new PacketTemperaturAdc(unitAddress);
		final byte[] array = writeThenRead(packet, WAIT_TIME);

		packet = new PacketTemperaturAdc(array);

		errorCheck(packet);

//		logger.error(packet);
		return packet.parametersToMap();
	}
}
