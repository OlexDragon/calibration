package irt.calibration.data.packets;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import irt.calibration.data.packets.parents.Packet;

public class MeasurementAllPacketTest {

	private final static Logger logger = LogManager.getLogger();

	private final static byte[] sample = new byte[] {(byte)0x7E, (byte)0xFE, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x32, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x91, (byte)0xE9, (byte)0x7E};

//	MeasurementPacket [timestamp=132767539712392, priority=REQUEST, linkHeader=LinkHeader [addr=254, control=0, protocol=0], header=PacketHeader [type=Request(2),packetId=MEASUREMENT_ALL(50),groupId=MEASUREMENT(4),reserved=0,option=No error(0)], payloads=Payload [ParameterHeader [cod=-1,size=0], buffer=null]]
//1) send : 7E FE 00 00 00 02 00 32 04 00 00 00 FF 00 00 91 E9 7E 


	@Test
	public void packetImplTest() {
		Packet packet = new PacketMeasurementAll((byte) 254);
//		Packet packet = new MeasurementAllPacket(null);
		byte[] bytes = packet.toBytes();
		logger.error("\n{}\n{}", sample, bytes);

		assertEquals(sample.length, bytes.length);
		assertEquals(sample[0], bytes[0]);
		assertEquals(sample[1], bytes[1]);
		assertEquals(sample[2], bytes[2]);
		assertEquals(sample[3], bytes[3]);
		assertEquals(sample[4], bytes[4]);
		assertEquals(sample[5], bytes[5]);
		assertEquals(sample[8], bytes[8]);
		assertEquals(sample[9], bytes[9]);
		assertEquals(sample[10], bytes[10]);
		assertEquals(sample[11], bytes[11]);
		assertEquals(sample[12], bytes[12]);
		assertEquals(sample[13], bytes[13]);
		assertEquals(sample[14], bytes[14]);
		assertEquals(sample[17], bytes[17]);
	}
}
