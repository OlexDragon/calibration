package irt.calibration.helpers;

import java.util.Map;

import irt.calibration.data.packets.PacketDac;
import irt.calibration.data.packets.parameters.Parameter;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.DAC;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.DAC.DacName;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class DacWorker extends UnitWorker {

	public DacWorker(ChoiceBox<String> chbUnitSerialPort, TextField tfUnitAddress) {
		super(chbUnitSerialPort, tfUnitAddress);
	}

	public Map<Converter<?>, Parameter> setDac(DacName dacName, Integer value) {

		final Byte unitAddress = getUnitAddress();
		PacketDac packet = DAC.getPacket(unitAddress, dacName, value);
		final byte[] array = writeThenRead(packet, WAIT_TIME);

		packet = new PacketDac(array);

		errorCheck(packet);

//		logger.error(packet);
		return packet.parametersToMap();
	}
}
