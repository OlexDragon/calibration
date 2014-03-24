package irt.gui_callibration.panels;

import irt.gui_callibration.controller.Controller;

public class OutputPowerPanel extends PowerPanelAbstract{
	private static final long serialVersionUID = 1721161264577099385L;

	public OutputPowerPanel(Controller controller) {
		super(controller, "Output");
	}

	@Override
	protected String getToolsName() {
		return "PM:";
	}

	@Override
	protected String powerLutEntry() {
		return "out-power-lut-entry";
	}

	@Override
	protected String powerLutSize() {
		return "out-power-lut-size";
	}
}
