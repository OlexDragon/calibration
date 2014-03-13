package irt.prologix.communication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import irt.prologix.data.PrologixGpibUsbController.Eos;

public abstract class Tools {

	protected static final Logger LOGGER = (Logger) LogManager.getLogger();
	protected final Logger logger = (Logger) LogManager.getLogger(getClass().getName());

	private String id;

	public enum Commands{
		ID			("*IDN"			),
		AMPLITUDE	("POW:AMPL"		),
		FREQUENCY	("FREQ:CW"		),
		RF_ON		("OUTP:STAT"	),
		POWER_REF	("POW:REF:STAT"	),
		MEASURE		("*cls;meas"	);

		private String command;

		private Commands(String command){
			this.command = command;
		}

		public byte[] getCommand(){
			String comm = command+"?";
			LOGGER.trace(comm);
			return (comm + Eos.LF).getBytes();
		}

		public byte[] getCommand(String value){
			String comm = command+" "+value;
			LOGGER.trace(comm);
			return (comm + Eos.LF).getBytes();
		}

		@Override
		public String toString() {
			return command;
		}
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public abstract byte getAddr();
}
