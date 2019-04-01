package irt.calibration.helpers;

import java.util.Map;

import irt.calibration.data.packets.PacketMeasurementAll;
import irt.calibration.data.packets.PacketPowerInput;
import irt.calibration.data.packets.PacketPowerOutput;
import irt.calibration.data.packets.PacketTemperature;
import irt.calibration.data.packets.parameters.Parameter;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class MeasurementWorker extends UnitWorker {

	public MeasurementWorker(ChoiceBox<String> chbUnitSerialPort, TextField tfUnitAddress) {
		super(chbUnitSerialPort, tfUnitAddress);
	}

	public Map<Converter<?>, Parameter> getMeasurementAll() {

		final Byte unitAddress = getUnitAddress();
		PacketMeasurementAll packet = new PacketMeasurementAll(unitAddress);
		final byte[] array = writeThenRead(packet, WAIT_TIME);
		packet = new PacketMeasurementAll(array);

		errorCheck(packet);

		//		logger.error(packet);
		return packet.parametersToMap();
	}

	public Map<Converter<?>, Parameter> getPowerInput() {

		final Byte unitAddress = getUnitAddress();
		PacketPowerInput packet = new PacketPowerInput(unitAddress);
		final byte[] array = writeThenRead(packet, WAIT_TIME);
		packet = new PacketPowerInput(array);

		errorCheck(packet);

		//		logger.error(packet);
		return packet.parametersToMap();
	}

	public Map<Converter<?>, Parameter> getPowerOutput() {

		final Byte unitAddress = getUnitAddress();
		PacketPowerOutput packet = new PacketPowerOutput(unitAddress);
		final byte[] array = writeThenRead(packet, WAIT_TIME);
		packet = new PacketPowerOutput(array);

		errorCheck(packet);

//		logger.error(packet);
		return packet.parametersToMap();
	}

	public Map<Converter<?>, Parameter> getTemperature() {

		final Byte unitAddress = getUnitAddress();
		PacketTemperature packet = new PacketTemperature(unitAddress);
		final byte[] array = writeThenRead(packet, WAIT_TIME);
		packet = new PacketTemperature(array);

		errorCheck(packet);

//		logger.error(packet);
		return packet.parametersToMap();
	}
}
