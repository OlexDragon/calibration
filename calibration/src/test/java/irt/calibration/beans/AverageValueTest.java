package irt.calibration.beans;

import static org.junit.Assert.assertEquals;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class AverageValueTest {
	private final static Logger logger = LogManager.getLogger();

	@Test
	public void highPeakTest() {
		Average average = new Average();
		IntStream.range(615, 700).forEach(average::addValue);
		for(int i=0; i<100; i++)
			average.addValue(10 + i%3);
		double averageValue = average.getAverageValue();
		logger.error("{} : {}", averageValue, average);

		assertEquals(11, averageValue, 0.2);
	}

	@Test
	public void lowPeakTest() {
		Average average = new Average();
		IntStream.range(-15, 0).forEach(average::addValue);
		for(int i=0; i<100; i++)
			average.addValue(10 + i%7);
		double averageValue = average.getAverageValue();
		logger.error("{} : {}", averageValue, average);

		assertEquals(13, averageValue, 0.2);
	}

	@Test
	public void negotiveTest() {
		Average average = new Average();
		IntStream.range(-15, 0).forEach(average::addValue);
		for(int i=0; i<100; i++)
			average.addValue(-100 + i%7);
		logger.error(average);
		double averageValue = average.getAverageValue();
		logger.error("{} : {}", averageValue, average);

		assertEquals(-97, averageValue, 0.2);
	}

	@Test
	public void PowerMeterValueTest(){
		double[] array = new double[] {6.0777, 6.0134, 5.9743, 6.0328, 6.1095, 6.216, 6.2345, 6.2468, 6.1222, 5.9547, 5.828, 5.8008, 6.0392, 6.216, 6.4096, 6.5206, 6.5551, 6.6232, 6.6513, 6.6457, 6.5148, 6.4567, 6.4508, 6.48, 6.4567, 6.362, 6.2652, 6.3139, 6.2835, 6.253, 6.1912, 6.1474, 6.1285, 6.1474, 6.1285, 6.0521, 5.9151, 5.9151, 6.0199, 6.0004, 5.8818, 5.6975, 5.5124, 5.3418, 5.3342, 5.4019, 5.4612, 5.5124, 5.5124, 5.6625, 5.6625, 5.6766, 5.7322, 5.8617, 6.0392, 6.1032, 6.16, 6.0457, 6.0521, 5.9874, 6.1159, 6.185, 6.2468, 6.2835, 6.368, 6.2896, 6.1787, 6.1912, 6.2284, 6.35, 6.4917, 6.6624, 6.789, 6.8268, 6.7835, 6.6457, 6.5608, 6.5551, 6.5493, 6.5836, 6.5493, 6.5836, 6.6792, 6.8749, 7.0109, 7.057, 7.1378, 7.1826, 7.1428, 7.0621, 7.057, 7.0774, 7.1528, 7.227, 7.2999, 7.2465, 7.1777, 7.0774};

		final double average = DoubleStream.of(array).average().getAsDouble();
		assertEquals( 6.315032653061224, average, 0.000000000000001);
	}

}
