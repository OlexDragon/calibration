package irt.prologix.data;

public class PrologixGpibUsbController {
	
//	public static final int CR_LF 	= 0;
//	public static final int CR 		= 1;
//	public static final int LF 		= 2;
//	public static final int NONE 	= 3;
//	public static final int DON 	= 1;
//	public static final int NOT_FAUND = 0;
//	public static final int UNRECOGNIZED_COMMAND = -1;
	
	//Device type
	public enum DeviceType{
		DEVICE,
		CONTROLLER,
		FOR_BOTH
	}

	//Commands
	public enum Commands{
		ADDR		("++addr"		,DeviceType.FOR_BOTH	),
		EOI			("++eoi"		,DeviceType.FOR_BOTH	),
		EOS			("++eos"		,DeviceType.FOR_BOTH	),
		EOT_ENABLE	("++eot_enable"	,DeviceType.FOR_BOTH	),
		EOT_CHAR	("++eot_char"	,DeviceType.FOR_BOTH	),
		MODE		("++mode"		,DeviceType.FOR_BOTH	),
		RST			("++rst"		,DeviceType.FOR_BOTH	),
		SAVECFG		("++savecfg"	,DeviceType.FOR_BOTH	),
		VER			("++ver"		,DeviceType.FOR_BOTH	),
		HELP		("++help"		,DeviceType.FOR_BOTH	),

		READ_AFTER_WRITE		("++auto"		,DeviceType.CONTROLLER	),
		CLR			("++clr"		,DeviceType.CONTROLLER	),
		IFC			("++ifc"		,DeviceType.CONTROLLER	),
		LOC			("++loc"		,DeviceType.CONTROLLER	),
		READ		("++read"		,DeviceType.CONTROLLER	),
		READ_TMO_MS	("++read_tmo_ms",DeviceType.CONTROLLER	),
		SPOLL		("++spoll"		,DeviceType.CONTROLLER	),
		SRQ			("++srq"		,DeviceType.CONTROLLER	),
		TRG			("++trg"		,DeviceType.CONTROLLER	),

		LON			("++lon"		,DeviceType.DEVICE		),
		STATUS		("++status"		,DeviceType.DEVICE		);

		private String command;
		private DeviceType deviceType;

		private Commands(String command, DeviceType deviceType){
			this.command = command;
			this.deviceType = deviceType;
		}

		public byte[] getCommand(int value){
			return (toString()+" "+value+Eos.LF).getBytes();
		}

		public byte[] getCommand(){
			return (toString()+Eos.LF).getBytes();
		}

		@Override
		public String toString() {
			return command;
		}

		public DeviceType getDeviceType() {
			return deviceType;
		}
	}

	public enum FalseOrTrue{
		FALSE,
		TRUE
	}

	public enum Eos{
		CR_LF	("\r\n"	),
		RC		("\r"	),
		LF		("\n"	);

		private String eos;

		private Eos(String eos){
			this.eos = eos;
		}

		@Override
		public String toString() {
			return eos;
		}
	}

//	public static final String WRITE_END = "\n";
//	public static final String READ_END = "\r\n";

//parameters
//	private int addr;
//	private FalseOrTrue eoi;
//	/**
//	 * where 0-CR+LF, 1-CR, 2-LF, 3-None;
//	 */
//	private Eos 		eos;
//	private FalseOrTrue eot_enable;
//	private char 		eot_char;
//	/**
//	 * where true(1)-Controller, false(0)-Device;
//	 */
//	private DeviceType	mode;
//	private FalseOrTrue savecfg;
//	private String		ver;
//	private FalseOrTrue auto;
//	/**
//	 * timeout value between 1 and 3000 (milliseconds);
//	 */
//	private int 		read_tmo_ms;
//	private FalseOrTrue lon;
//	private int 		status;
//
//	public int getAddr() {
//		return addr;
//	}
//	public FalseOrTrue isEoi() {
//		return eoi;
//	}
//	public Eos getEos() {
//		return eos;
//	}
//	public FalseOrTrue getEot_enable() {
//		return eot_enable;
//	}
//	public char getEot_char() {
//		return eot_char;
//	}
//	public DeviceType getMode() {
//		return mode;
//	}
//	public FalseOrTrue getSavecfg() {
//		return savecfg;
//	}
//	public String getVer() {
//		return ver;
//	}
//	public FalseOrTrue getAuto() {
//		return auto;
//	}
//	public int getRead_tmo_ms() {
//		return read_tmo_ms;
//	}
//	public FalseOrTrue getLon() {
//		return lon;
//	}
//	public int getStatus() {
//		return status;
//	}
//	public void setAddr(int addr) {
//		this.addr = addr;
//	}
//	public void setEoi(FalseOrTrue eoi) {
//		this.eoi = eoi;
//	}
//	public void setEos(int eos) {
//		this.eos = Eos.values()[eos];
//	}
//	public void setEot_enable(FalseOrTrue eot_enable) {
//		this.eot_enable = eot_enable;
//	}
//	public void setEot_char(char eot_char) {
//		this.eot_char = eot_char;
//	}
//	public void setMode(DeviceType mode) {
//		this.mode = mode;
//	}
//	public void setSavecfg(FalseOrTrue savecfg) {
//		this.savecfg = savecfg;
//	}
//	public void setVer(String ver) {
//		this.ver = ver;
//	}
//	public void setAuto(FalseOrTrue auto) {
//		this.auto = auto;
//	}
//	public void setRead_tmo_ms(int read_tmo_ms) throws IllegalArgumentException{
//		if(read_tmo_ms>0 && read_tmo_ms<=3000)
//			this.read_tmo_ms = read_tmo_ms;
//		else
//			throw new IllegalArgumentException("Value "+read_tmo_ms+" out of range.(min=1, max=3000");
//	}
//	public void setLon(FalseOrTrue lon) {
//		this.lon = lon;
//	}
//	public void setStatus(int status) {
//		this.status = status;
//	}
}
