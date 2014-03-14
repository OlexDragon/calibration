package irt.measurement.data;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;

public class TableTest {

	private final Logger logger = (Logger) LogManager.getLogger();

	@Test
	public void testGetTable() {
		Table table = new Table();

		assertEquals("0\n", table.toString());

		table.add(1, 1);
		table.add(2, 4);
		table.add(3, 7);
		table.add(4, 10);
		table.add(5, 13);
		table.add(6, 16);
		String string = table.toString();
		logger.trace(string);
		assertEquals("6\n"
					+ "1.0 1.0\n"
					+ "2.0 4.0\n"
					+ "3.0 7.0\n"
					+ "4.0 10.0"
					+ "\n"
					+ "5.0 13.0\n"
					+ "6.0 16.0\n",
				string);

		table.setLutSizeName("lut-size");
		table.setLutValueName("lut-value");
		string = table.toString();
		logger.trace(string);
		assertEquals("lut-size 6\n"
					+ "lut-value 1.0 1.0\n"
					+ "lut-value 2.0 4.0\n"
					+ "lut-value 3.0 7.0\n"
					+ "lut-value 4.0 10.0\n"
					+ "lut-value 5.0 13.0\n"
					+ "lut-value 6.0 16.0\n",
				string);
	}

	@Test
	public void toAverage() {
		try {
			Table table = new Table();
			table.setAccuracy(1);

			for(int d=0; d<5; d++)
				table.add(d, d);

			String string = table.toString();
			logger.trace(string);
		} catch (Exception ex) {
			logger.catching(ex);
		}
	}
}
