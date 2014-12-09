package irt.serial_protocol;

import irt.serial_protocol.data.PacketWork.PacketId;
import irt.serial_protocol.data.packet.LinkHeader;
import irt.serial_protocol.data.packet.LinkedPacket;
import irt.serial_protocol.data.packet.Packet;
import irt.serial_protocol.data.packet.PacketHeader;
import irt.serial_protocol.data.packet.ParameterHeader;
import irt.serial_protocol.data.packet.Payload;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Before;
import org.junit.Test;

public class ComPortTest {

	private final Logger logger = (Logger) LogManager.getLogger();

	private LinkedPacket linkedPacket;

	@Before
	public void setUp() throws Exception {
		logger.entry();
		linkedPacket = new LinkedPacket(new LinkHeader((byte)254, (byte)0, (short)0));
		PacketHeader header = new PacketHeader();

		//IRT Management Protocol Specification.docx

		header.setType(Packet.IRT_SLCP_PACKET_TYPE_REQUEST);	//Table 1 Type of packet
		header.setPacketId(PacketId.DEVICE_DEBAG_DEVICE_INFO);	//Packet ID represents unique identifier of “command/request – response” transaction. 
		header.setGroupId(Packet.IRT_SLCP_PACKET_ID_DEVICE_INFO);//Table 2 Group ID
		header.setError(Packet.ERROR_NO_ERROR);					//Table 3 Response message error codes
		linkedPacket.setHeader(header);
		ArrayList<Payload> payloads = new ArrayList<Payload>();
		Payload pl = new Payload(new ParameterHeader(Packet.IRT_SLCP_PARAMETER_ALL), null);
		payloads.add(pl);
		linkedPacket.setPayloads(payloads);
	}

	@Test
	public void test() throws Exception {
		logger.entry();
		Packet packet;
		try (ComPort comPort = ComPort.getInstance("COM12")){
			packet = comPort.send(linkedPacket);
		}
		for(Payload pl:packet.getPayloads())
			logger.debug(pl.getStringData());
	}

}
