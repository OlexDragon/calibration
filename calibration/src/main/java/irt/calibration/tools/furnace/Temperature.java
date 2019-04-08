package irt.calibration.tools.furnace;

public class Temperature {

	private final double monitored;
	private final double target;
	private final double highLimit;
	private final double lowLimit;

	public Temperature(byte[] response) {
		String data = new String(response);	// ex. -39.8,-40.0,85.0,-55.0
		String[] values = data.split(",");
		monitored = Double.parseDouble(values[0]);
		target = Double.parseDouble(values[1]);
		highLimit = Double.parseDouble(values[2]);
		lowLimit = Double.parseDouble(values[3]);
	}

	public double getMonitored() {
		return monitored;
	}

	public double getTarget() {
		return target;
	}

	public double getHighLimit() {
		return highLimit;
	}

	public double getLowLimit() {
		return lowLimit;
	}

	@Override
	public String toString() {
		return "Temperature [monitored=" + monitored + ", target=" + target + ", highLimit=" + highLimit + ", lowLimit=" + lowLimit + "]";
	}
}
