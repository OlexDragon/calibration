package irt.calibration.helpers;

import java.util.Map;

import irt.calibration.data.packets.PacketDeviceInfo;
import irt.calibration.data.packets.parameters.Parameter;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;
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
