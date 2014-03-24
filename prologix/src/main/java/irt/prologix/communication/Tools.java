package irt.prologix.communication;

import irt.prologix.data.PrologixGpibUsbController.CommandsInterface;
import irt.prologix.data.PrologixGpibUsbController.Eos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public abstract class Tools {

	protected static final Logger LOGGER = (Logger) LogManager.getLogger();
	protected final Logger logger = (Logger) LogManager.getLogger(getClass().getName());

	private String id;

	public enum Commands implements CommandsInterface{
		ID			("*IDN"			),
		AMPLITUDE	("POW:AMPL"		),
		FREQUENCY	("FREQ:CW"		),
		RF_ON		("OUTP:STAT"	),
		POWER_REF	("POW:REF:STAT"	),
		MEASURE		("*cls;meas"	);

		private String command;
		private Object value;

		private Commands(String command){
			this.command = command;
		}

		public Object getValue() {
			return value;
		}

		public CommandsInterface setValue(Object value) {
			this.value = value;
			return this;
		}

		@Override
		public byte[] getCommand(){

			String c = command + (value!=null ? " "+value : "?");
			value =null;
			LOGGER.trace(c);

			return (c + Eos.LF).getBytes();
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
