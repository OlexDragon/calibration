package irt.gui_callibration;

import irt.measurement.data.Table;

public interface CallibrationMonitor {

	public void setTable(Table table);
	public void setRowValues(double key, double value);
	public void clearTable();

}
