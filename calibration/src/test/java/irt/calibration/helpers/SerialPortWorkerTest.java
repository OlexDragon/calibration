package irt.calibration.helpers;

import static org.junit.Assert.assertNotNull;

import java.nio.ByteBuffer;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import irt.calibration.data.packets.PacketMeasurementAll;
import irt.calibration.exception.ExceptionWrapper;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class SerialPortWorkerTest {

	private final static Logger logger = LogManager.getLogger();

	private SerialPort serialPort;

	@Before
	public void setup() {
		try {
			serialPort = new SerialPort("COM14");
			serialPort.openPort();
		}catch (Exception e) { }
	}

	@Test
	public void test() throws SerialPortException, InterruptedException {

		if(serialPort==null || !serialPort.isOpened())
			return;

		serialPort.purgePort(SerialPort.PURGE_TXABORT | SerialPort.PURGE_TXCLEAR | SerialPort.PURGE_RXABORT | SerialPort.PURGE_RXCLEAR);
//		serialPort.writeBytes(new byte[] {0x7E, 0x02, 0x74, 0x13, 0x08, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x02, 0x00, 0x00, 0x03, 0x00, 0x00, 0x04, 0x00, 0x00, 0x05, 0x00, 0x00, 0x06, 0x00, 0x00, 0x07, 0x00, 0x00, 0x54, (byte) 0xC9, 0x7E});
//		serialPort.writeBytes(new byte[] {0x7E, 0x02, 0x00, 0x32, 0x04, 0x00, 0x00, 0x00, (byte) 0xFF, 0x00, 0x00, 0x54, 0x74, 0x7E});
//		serialPort.writeBytes(new byte[] {126, 2, 0, 0, 4, 0, 0, 0, -1, 0, 0, -77, -110, 126});
		final byte[] bytes = new PacketMeasurementAll((Byte)null).toBytes();
		logger.error("{} : {}", bytes.length, bytes);
		serialPort.writeBytes(bytes);

		byte[] readBytes = null;
		final ByteBuffer byteBuffer = ByteBuffer.allocate(Short.MAX_VALUE);

		try {
			while(true) {
				readBytes = serialPort.readBytes(1, 500);
				logger.error("{}", readBytes);
				Optional.ofNullable(readBytes).ifPresent(byteBuffer::put);
			}
		} catch (SerialPortTimeoutException e) {}

		logger.error("{} : {}", byteBuffer, byteBuffer.array());
		assertNotNull(readBytes);
	}

	@After
	public void exit() {
		Optional.ofNullable(serialPort).filter(SerialPort::isOpened).ifPresent(ExceptionWrapper.catchConsumerException(SerialPort::closePort));
	}
}
