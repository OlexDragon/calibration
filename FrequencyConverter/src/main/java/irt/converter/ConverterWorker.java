package irt.converter;

import irt.converter.groups.ConfigurationGroup;
import irt.converter.groups.DeviceDebugGroup;
import irt.converter.groups.DeviceInformationGroup;
import irt.converter.groups.MeasurementGroup;
import irt.serial_protocol.ComPort;
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
		return configurationGroup.getMute(comPort)==FalseOrTrue.TRUE;
	}

	public boolean setMute(ComPort comPort, boolean mute){
		return configurationGroup.setMute(comPort, mute ? FalseOrTrue.TRUE : FalseOrTrue.FALSE)==FalseOrTrue.TRUE;
	}
}
