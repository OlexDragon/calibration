package irt.calibration.data.packets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import irt.calibration.tools.unit.packets.Checksum;
import irt.calibration.tools.unit.packets.PacketDeviceInfo;
import irt.calibration.tools.unit.packets.parents.Packet;

public class DeviceInfoPacketTest {

	private final static Logger logger = LogManager.getLogger();

	private final static byte[] sample = new byte[] {(byte)0x7E, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x08, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x29, (byte)0x23, (byte)0x7E};

//	DeviceInfoPacket [timestamp=391740621009956, priority=IMPORTANT, linkHeader=null, header=PacketHeader [type=Request(2),packetId=DEVICE_INFO(1),groupId=DEVICE_INFO(8),reserved=0,option=No error(0)], payloads=Payload [ParameterHeader [cod=-1,size=0], buffer=null]]
//1) send : 7E 02 00 01 08 00 00 00 FF 00 00 96 A2 7E 


	@Test
	public void checksumTest() {
		byte[] packet = new byte[] {(byte)0x7E, (byte)0x02, (byte)0x00, (byte)0x01, (byte)0x08, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x96, (byte)0xA2, (byte)0x7E};

		final byte[] checksum = Checksum.getChecksumAsBytes(Arrays.copyOfRange(packet, 1, packet.length - 3));
		assertArrayEquals(Arrays.copyOfRange(packet, packet.length - 3, packet.length - 1), checksum);
	}

	@Test
	public void packetDeviceInfoTest() {
		Packet packet = new PacketDeviceInfo((Byte)null);
		byte[] bytes = packet.toBytes();
		logger.error("\n{}\n{}", sample, bytes);

		assertEquals(sample.length, bytes.length);
//		assertArrayEquals(sample, bytes);
	}
}
