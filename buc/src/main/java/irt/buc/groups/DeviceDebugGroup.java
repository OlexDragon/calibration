package irt.buc.groups;

import irt.serial_protocol.data.PacketWork.PacketId;
import irt.serial_protocol.data.RegisterValue;
import irt.serial_protocol.data.packet.LinkHeader;
import irt.serial_protocol.data.packet.LinkedPacket;
import irt.serial_protocol.data.packet.Packet;
import irt.serial_protocol.data.value.Value;

public class DeviceDebugGroup extends irt.converter.groups.DeviceDebugGroup {

	public enum BucADC implements ADCInterface{

		DEVICE_CURRENT_1		(new RegisterValue(5,  1, null), PacketId.DEVICE_DEBUG_OUTPUT_POWER),
		DEVICE_CURRENT_1_AVERAGE(new RegisterValue(5, 10, null), PacketId.DEVICE_DEBUG_OUTPUT_POWER);

		private RegisterValue registerValue;
		private PacketId packetId;


		private BucADC(RegisterValue registerValue, PacketId packetId){
			this.registerValue = registerValue;
			this.packetId = packetId;
		}
		public PacketId getPacketId() {
			return packetId;
		}

		public RegisterValue getRegisterValue() {
			RegisterValue rv = new RegisterValue(registerValue);
			registerValue.setValue(null);
			return rv;
		}

		public Params getParameter(){
			return Params.READ_WRITE;
		}

		@Override
		public String toString() {
			return registerValue+"; packetId="+packetId;
		}
		@Override
		public ADCInterface setValue(Value value) {
			registerValue.setValue(value);
			return this;
		}
	}

	public DeviceDebugGroup(Byte address) {
		setAddress(address);
	}

	@Override
	public Packet createPacket(ADCInterface adc) {
		return new LinkedPacket(new LinkHeader(getAddress(), (byte)0, (short)0), super.createPacket(adc));
	}

}
