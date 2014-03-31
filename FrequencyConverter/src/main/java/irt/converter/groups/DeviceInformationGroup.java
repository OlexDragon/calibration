package irt.converter.groups;

import java.util.concurrent.TimeUnit;

import irt.serial_protocol.data.StringData;
import irt.serial_protocol.data.packet.Packet;
import irt.serial_protocol.data.packet.PacketHeader.Group;

public class DeviceInformationGroup extends irt.converter.groups.Group{

	public enum Params implements Parameter{
		NONE				(Packet.IRT_SLCP_PARAMETER_NONE	),
		SERIAL_NUMBER		(Packet.IRT_SLCP_PARAMETER_DEVICE_INFORMATION_SERIAL_NUMBER),
		UNIT_NAME			(Packet.IRT_SLCP_PARAMETER_DEVICE_INFORMATION_UNIT_NAME),
		UNIT_PART_NUMBER	(Packet.IRT_SLCP_PARAMETER_DEVICE_INFORMATION_UNIT_PART_NUMBER),
		FIRMWARE_VERSION	(Packet.IRT_SLCP_PARAMETER_DEVICE_INFORMATION_FIRMWARE_VERSION),
		FIRMWARE_BUILD_DATE	(Packet.IRT_SLCP_PARAMETER_DEVICE_INFORMATION_FIRMWARE_BUILD_DATE),
		UNIT_UPTIME_COUNTER	(Packet.IRT_SLCP_PARAMETER_DEVICE_INFORMATION_UNIT_UPTIME_COUNTER),
		ALL					(Packet.IRT_SLCP_PARAMETER_ALL	);

		private byte parameter;

		private Params(byte parameter){
			this.parameter = parameter;
		}

		public byte getId() {
			return parameter;
		}
	}

	@Override
	public Group getGroup() {
		return Group.DEVICE_INFO;
	}

	public String getSerialNumber(){

		StringData sn = getStringData(Params.SERIAL_NUMBER);

		return sn!=null ? sn.toString() : null;
	}

	public String getUnitName(){

		StringData sn = getStringData(Params.UNIT_NAME);

		return sn!=null ? sn.toString() : null;
	}

	public String getPartNumber(){

		StringData sn = getStringData(Params.UNIT_PART_NUMBER);

		return sn!=null ? sn.toString() : null;
	}

	public String getFirmwareVersion(){

		StringData sn = getStringData(Params.FIRMWARE_VERSION);

		return sn!=null ? sn.toString() : null;
	}

	public String getFirmwareBuildDate(){

		StringData sn = getStringData(Params.FIRMWARE_BUILD_DATE);

		return sn!=null ? sn.toString() : null;
	}

	public String getUptimeCounter(){
		return calculateTime(getInt(Params.UNIT_UPTIME_COUNTER));
	}

	public static String calculateTime(long seconds) {

		int day = (int) TimeUnit.SECONDS.toDays(seconds);
	    long hours = TimeUnit.SECONDS.toHours(seconds) 	  - TimeUnit.DAYS.toHours(day);
	    long minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(seconds));
	    long second = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds));

	    return (day>0 ? day+" day"+(day==1 ? ", " : "s, ") : "")+hours+ ":"+minute+":"+second;
	}
}
