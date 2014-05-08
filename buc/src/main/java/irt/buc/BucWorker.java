package irt.buc;

import irt.buc.groups.ConfigurationGroup;
import irt.buc.groups.DeviceDebugGroup;
import irt.buc.groups.DeviceInformationGroup;
import irt.converter.ConverterWorker;

public class BucWorker extends ConverterWorker {

	private static final byte DEFAULT_ADDRESS = (byte) 254;

	public enum OutoutPowerDetectorSource{
		ON_BOARD_DETECTOR,
		OUTPUT_DEVICE_CURRENT,
		INPUT_POWER_PLUS_GAIN 
	}

	public BucWorker() { }

	public BucWorker(byte address) {
		configurationGroup.setAddress(address);
		deviceDebugGroup.setAddress(address);
		deviceInformationGroup.setAddress(address);
		monitorConverterGroup.setAddress(address);
		
	}

	@Override
	public ConfigurationGroup newConfigurationGroup() {
		return new ConfigurationGroup(DEFAULT_ADDRESS);
	}

	@Override
	public DeviceInformationGroup newDeviceInformationGroup() {
		return new DeviceInformationGroup(DEFAULT_ADDRESS);
	}

	@Override
	public DeviceDebugGroup newDeviceDebugGroup() {
		return new DeviceDebugGroup(DEFAULT_ADDRESS);
	}

//	@Override
//	public MeasurementGroup newMeasurementGroup() {
//		return new MeasurementGroup(DEFAULT_ADDRESS);
//	}

	
}
