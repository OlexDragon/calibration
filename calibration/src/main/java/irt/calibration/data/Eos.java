package irt.calibration.data;

public enum Eos{
	CR_LF	("\r\n"	),
	RC		("\r"	),
	LF		("\n"	);

	private String eos;

	private Eos(String eos){
		this.eos = eos;
	}

	public byte[] getBytes() {
		return eos.getBytes();
	}

	@Override
	public String toString() {
		return eos;
	}
}
