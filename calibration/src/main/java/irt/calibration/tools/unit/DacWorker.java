package irt.calibration.tools.unit;

import java.util.Map;

import irt.calibration.tools.unit.packets.PacketDac;
import irt.calibration.tools.unit.packets.parameters.Parameter;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.DAC;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.DAC.DacName;
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
