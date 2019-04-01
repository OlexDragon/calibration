package irt.calibration.data;

import jssc.SerialPort;

public enum BaudRate {
    BAUDRATE_110	(SerialPort.BAUDRATE_110),
    BAUDRATE_300	(SerialPort.BAUDRATE_300),
    BAUDRATE_600	(SerialPort.BAUDRATE_600),
    BAUDRATE_1200	(SerialPort.BAUDRATE_1200),
    BAUDRATE_4800	(SerialPort.BAUDRATE_4800),
    BAUDRATE_9600	(SerialPort.BAUDRATE_9600),
    BAUDRATE_14400	(SerialPort.BAUDRATE_14400),
    BAUDRATE_19200	(SerialPort.BAUDRATE_19200),
    BAUDRATE_38400	(SerialPort.BAUDRATE_38400),
    BAUDRATE_57600	(SerialPort.BAUDRATE_57600),
    BAUDRATE_115200	(SerialPort.BAUDRATE_115200),
    BAUDRATE_128000	(SerialPort.BAUDRATE_128000),
    BAUDRATE_256000	(SerialPort.BAUDRATE_256000);

    private final int bautRate;

	private BaudRate(int bautRate) {
    	this.bautRate = bautRate;
    }

    public int getBautRate() {
		return bautRate;
	}

    @Override
    public String toString() {
    	return Integer.toString(bautRate);
    }
}
