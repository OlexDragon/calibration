package irt.serial_protocol.data.packet;

import irt.serial_protocol.data.PacketWork.PacketId;

import java.util.Arrays;

public class PacketHeader{
	public enum Type{
		SPONTANEOUS	(Packet.IRT_SLCP_PACKET_TYPE_SPONTANEOUS),
		RESPONSE	(Packet.IRT_SLCP_PACKET_TYPE_RESPONSE	),
		REQUEST		(Packet.IRT_SLCP_PACKET_TYPE_REQUEST	),
		COMMAND		(Packet.IRT_SLCP_PACKET_TYPE_COMMAND	),
		ACK			(Packet.IRT_SLCP_PACKET_TYPE_ACK		);

		private byte type;

		private Type(byte type){
			this.type = type;
		}

		public byte getType() {
			return type;
		}
	}

	public enum Group{
		NONE					(Packet.IRT_SLCP_PACKET_ID_NONE						),
		ALARM					(Packet.IRT_SLCP_PACKET_ID_ALARM					),
		CONFIGURATION			(Packet.IRT_SLCP_PACKET_ID_CONFIGURATION			),
		FILETRANSFER			(Packet.IRT_SLCP_PACKET_ID_FILETRANSFER				),
		MEASUREMENT				(Packet.IRT_SLCP_PACKET_ID_MEASUREMENT				),
		RESET					(Packet.IRT_SLCP_PACKET_ID_RESET					),
		UNKNOWN6				((byte)6),
		UNKNOWN7				((byte)7),
		DEVICE_INFO				(Packet.IRT_SLCP_PACKET_ID_DEVICE_INFO				),
		PROFILE					(Packet.IRT_SLCP_PACKET_ID_CONFIG_PROFILE			),
		GET_CONFIG				(Packet.IRT_SLCP_PACKET_ID_GET_CONFIG				),
		NETWORK					(Packet.IRT_SLCP_PACKET_ID_NETWORK					),
		DEVICE_DEBAG			(Packet.IRT_SLCP_PACKET_ID_DEVICE_DEBAG				),
		PRODUCTION_GENERIC_SET_1(Packet.IRT_SLCP_PACKET_ID_PRODUCTION_GENERIC_SET_1	),
		DEVELOPER_GENERIC_SET_1	(Packet.IRT_SLCP_PACKET_ID_DEVELOPER_GENERIC_SET_1	);

		private byte id;

		private Group(byte id){
			this.id = id;
		}

		public byte getId() {
			return id;
		}
	}

	public static final int SIZE = 7;
	byte[] packetHeader;

	public PacketHeader(byte[] hrader) {
		if(hrader!=null && hrader.length>=SIZE)
			packetHeader = Arrays.copyOf(hrader, SIZE);
	}

	public PacketHeader() {
		packetHeader = new byte[SIZE];
	}

	public PacketHeader(Type type, Group group, PacketId packetId) {
		this();
		setType(type.getType());
		setGroupId(group.getId());
		setPacketId(packetId);
	}

	/*	private byte	type;		0
 * 	private short 	packetId;	1,2
	private byte 	groupId;	3
	private short 	reserved;	4,5
	private byte 	code; 		6
*/
	public byte[]	asBytes()		{ return packetHeader;		}
	public byte		getType	()		{ return packetHeader[0];	}
	public short	getPacketId()	{ return (short) Packet.shiftAndAdd(Arrays.copyOfRange(packetHeader, 1, 3));	}
	public byte		getGroupId()	{ return packetHeader[3];	}
	public short	getReserved()	{ return (short) Packet.shiftAndAdd(Arrays.copyOfRange(packetHeader, 4, 6));	}
	public byte		getOption()		{ return packetHeader[6];	}

	public byte[] set(byte[]data){
		if(data!=null && data.length>=SIZE)
			packetHeader = Arrays.copyOf(data, SIZE);

		return data!=null && data.length>SIZE ? Arrays.copyOfRange(data, SIZE, data.length) : null;
	}

	public void setType		(byte type) 				{ packetHeader[0] = type;}
	public void setPacketId	(PacketId packetId)			{ System.arraycopy(Packet.toBytes((short)packetId.ordinal()), 0, packetHeader, 1, 2);	}
	public void setGroupId	(byte irtSlcpPacketGroupId) { packetHeader[3] = irtSlcpPacketGroupId;}
	public void setError	(byte option)			 	{ packetHeader[6] = option;	}

	@Override
	public String toString() {
		return "PacketHeader [type="+getTypeStr()+",packetId=" +getPacketIdEnum()+",groupId=" +getGroupIdStr()+",reserved=" +getReserved()+",option=" +getOptionStr()+"]";
	}

	public PacketId getPacketIdEnum() {
		return PacketId.values()[getPacketId()];
	}

	public String getOptionStr() {
		return packetHeader!=null ? getOptionStr(getOption()) : null;
	}

	public static String getOptionStr(byte code) {

		if(code<0)
			code = (byte) -code;

		String codeStr = null;
		switch (code) {
		case Packet.ERROR_NO_ERROR:
			codeStr = "No error(" + code + ")";
			break;
		case 1:
			codeStr = "System internal(" + code + ")";
			break;
		case 2:
			codeStr = "Write error(" + code + ")";
			break;
		case 3:
			codeStr = "Function not implemented(" + code + ")";
			break;
		case 4:
			codeStr = "Value outside of valid range(" + code + ")";
			break;
		case 5:
			codeStr = "Requested information can�t be generated(" + code + ")";
			break;
		case 6:
			codeStr = "Command can�t be executed(" + code + ")";
			break;
		case 7:
			codeStr = "Invalid data format(" + code + ")";
			break;
		case 8:
			codeStr = "Invalid value(" + code + ")";
			break;
		case 9:
			codeStr = "Not enough memory (" + code + ")";
			break;
		case Packet.ERROR_REQUESTED_ELEMENT_NOT_FOUND:
			codeStr = "Requested element not found(" + code + ")";
			break;
		case 11:
			codeStr = "Timed out(" + code + ")";
			break;
		case 20:
			codeStr = "Communication problem(" + code + ")";
			break;
		default:
			codeStr = "" + code;
		}
		return codeStr;
	}

	public String getTypeStr() {
		String typeStr = null;
		if(packetHeader!=null)
		switch(getType()){
		case Packet.IRT_SLCP_PACKET_TYPE_SPONTANEOUS:
			typeStr = "Spontaneous("+ Packet.IRT_SLCP_PACKET_TYPE_SPONTANEOUS+")";
			break;
		case Packet.IRT_SLCP_PACKET_TYPE_RESPONSE:
			typeStr = "Response("+ Packet.IRT_SLCP_PACKET_TYPE_RESPONSE+")";
			break;
		case Packet.IRT_SLCP_PACKET_TYPE_REQUEST:
			typeStr = "Request("+ Packet.IRT_SLCP_PACKET_TYPE_REQUEST+")";
			break;
		case Packet.IRT_SLCP_PACKET_TYPE_COMMAND:
			typeStr = "Command("+ Packet.IRT_SLCP_PACKET_TYPE_COMMAND+")";
			break;
		case Packet.IRT_SLCP_PACKET_TYPE_ACK:
			typeStr = "Acknowledgement("+ Packet.IRT_SLCP_PACKET_TYPE_ACK+")";
			break;
		default:
			typeStr = ""+(getType()&0xFF);
		}
		return typeStr;
	}

	private String getGroupIdStr() {
		String typeStr = null;
		if(packetHeader!=null)
		switch(getGroupId()){
		case Packet.IRT_SLCP_PACKET_ID_ALARM:
			typeStr = "Alarm("+ Packet.IRT_SLCP_PACKET_ID_ALARM+")";
			break;
		case Packet.IRT_SLCP_PACKET_ID_CONFIGURATION:
			typeStr = "Configuration("+ Packet.IRT_SLCP_PACKET_ID_CONFIGURATION+")";
			break;
		case Packet.IRT_SLCP_PACKET_ID_FILETRANSFER:
			typeStr = "FileTranster("+ Packet.IRT_SLCP_PACKET_ID_FILETRANSFER+")";
			break;
		case Packet.IRT_SLCP_PACKET_ID_MEASUREMENT:
			typeStr = "Measurement("+ Packet.IRT_SLCP_PACKET_ID_MEASUREMENT+")";
			break;
		case Packet.IRT_SLCP_PACKET_ID_RESET:
			typeStr = "Reset("+ Packet.IRT_SLCP_PACKET_ID_RESET+")";
			break;
		case Packet.IRT_SLCP_PACKET_ID_DEVICE_INFO:
			typeStr = "DeviceInfo("+ Packet.IRT_SLCP_PACKET_ID_DEVICE_INFO+")";
			break;
		case Packet.IRT_SLCP_PACKET_ID_CONFIG_PROFILE:
			typeStr = "SaveConfigProfile("+ Packet.IRT_SLCP_PACKET_ID_CONFIG_PROFILE+")";
			break;
		case Packet.IRT_SLCP_PACKET_ID_GET_CONFIG:
			typeStr = "GetConfig("+ Packet.IRT_SLCP_PACKET_ID_GET_CONFIG+")";
			break;
		case Packet.IRT_SLCP_PACKET_ID_DEVELOPER_GENERIC_SET_1:
			typeStr = "DeveloperGeneric("+ Packet.IRT_SLCP_PACKET_ID_DEVELOPER_GENERIC_SET_1+")";
			break;
		case Packet.IRT_SLCP_PACKET_ID_DEVICE_DEBAG:
			typeStr = "Device Debug("+ Packet.IRT_SLCP_PACKET_ID_DEVICE_DEBAG+")";
			break;
		default:
			typeStr = ""+(getType()&0xFF);
		}
		return typeStr;
	}
}
