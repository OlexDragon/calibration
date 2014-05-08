package irt.converter;

import irt.converter.data.UnitValue;
import irt.converter.groups.ConfigurationGroup;
import irt.converter.groups.DeviceDebugGroup;
import irt.converter.groups.DeviceDebugGroup.Params;
import irt.converter.groups.DeviceInformationGroup;
import irt.converter.groups.Group.MuteStatus;
import irt.converter.groups.MeasurementGroup;
import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.PacketWork.PacketId;
import irt.serial_protocol.data.RegisterValue;
import irt.serial_protocol.data.packet.Packet;
import irt.serial_protocol.data.packet.PacketHeader;
import irt.serial_protocol.data.packet.PacketHeader.Group;
import irt.serial_protocol.data.packet.PacketHeader.Type;
import irt.serial_protocol.data.packet.Payload;
import irt.serial_protocol.data.value.Enums.FalseOrTrue;

public class ConverterWorker {

	protected ConfigurationGroup 		configurationGroup		= newConfigurationGroup();
	protected DeviceDebugGroup 			deviceDebugGroup		= newDeviceDebugGroup();
	protected DeviceInformationGroup 	deviceInformationGroup	= newDeviceInformationGroup();
	protected MeasurementGroup 			monitorConverterGroup 	= newMeasurementGroup();

	public DeviceInformationGroup newDeviceInformationGroup() {
		return new DeviceInformationGroup();
	}

	public ConfigurationGroup newConfigurationGroup() {
		return new ConfigurationGroup();
	}

	public DeviceDebugGroup newDeviceDebugGroup() {
		return new DeviceDebugGroup();
	}

	public MeasurementGroup newMeasurementGroup() {
		return new MeasurementGroup();
	}

	public boolean isMute(ComPort comPort){
		return configurationGroup.getMute(comPort)==MuteStatus.MUTED;
	}

	public boolean setMute(ComPort comPort, boolean mute){
		return configurationGroup.setMute(comPort, mute ? MuteStatus.MUTED : MuteStatus.UNMUTED)==MuteStatus.MUTED;
	}

	public UnitValue getAttenuation(ComPort comPort){
		return configurationGroup.getAttenuation(comPort);
	}

	public UnitValue setAttenuation(ComPort comPort, short attenuation){
		return configurationGroup.setAttenuation(comPort, attenuation);
	}

	public DeviceInformationGroup getUnitInfo(ComPort comPort){
		deviceInformationGroup.getAll(comPort);
		return deviceInformationGroup;
	}

	public RegisterValue getOutputPower(ComPort comPort){
		return deviceDebugGroup.getOutputPower(comPort);
	}

	public ConfigurationGroup getConfigurationGroup() {
		return configurationGroup;
	}

	public DeviceDebugGroup getDeviceDebugGroup() {
		return deviceDebugGroup;
	}

	public DeviceInformationGroup getDeviceInformationGroup() {
		return deviceInformationGroup;
	}

	public MeasurementGroup getMonitorConverterGroup() {
		return monitorConverterGroup;
	}

	public void setCalibrationMode(ComPort comPort, FalseOrTrue falseOrTrue) {
		deviceDebugGroup.setPacket(
				new Packet(
						new PacketHeader(
								Type.COMMAND,
								Group.DEVICE_DEBAG,
								PacketId.DEVICE_DEBAG_CALIBRATION_MODE
						),
						new Payload(
								Params.CALIBRATION_MODE.getId(),
								Packet.toBytes(falseOrTrue.ordinal())
						)
				)
		);
		comPort.send(deviceDebugGroup.getPacket());
	}
}
