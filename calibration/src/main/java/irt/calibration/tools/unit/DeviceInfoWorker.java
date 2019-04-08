package irt.calibration.tools.unit;

import java.util.Map;

import irt.calibration.tools.unit.packets.PacketDeviceInfo;
import irt.calibration.tools.unit.packets.parameters.Parameter;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.Converter;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class DeviceInfoWorker extends UnitWorker{

	public DeviceInfoWorker(ChoiceBox<String> chbUnitSerialPort, TextField tfUnitAddress) {
		super(chbUnitSerialPort, tfUnitAddress);
	}

	public Map<Converter<?>, Parameter> getDeviceInfo() {

		final Byte unitAddress = getUnitAddress();
		PacketDeviceInfo packet = new PacketDeviceInfo(unitAddress);
		final byte[] array = writeThenRead(packet, WAIT_TIME);
		packet = new PacketDeviceInfo(array);

		errorCheck(packet);

//		logger.error(packet);
		return packet.parametersToMap();
	}
}
