package irt.serial_protocol.data;

import org.apache.logging.log4j.core.Logger;

public class ThrowException {

	public static void throwException(Logger logger, Throwable ex) {
		while(ex!=null){
			logger.catching(ex);
			ex = ex.getCause();
		}
	}
}
