package irt.gui_callibration.panels;

import irt.gui_callibration.controller.Controller;

public class InputPowerPanel extends PowerPanelAbstract{
	private static final long serialVersionUID = 7584883389855027199L;

	public InputPowerPanel(Controller controller) {
		super(controller, "Input");
	}

	@Override
	protected String getToolsName() {
		return "SG:";
	}

	@Override
	protected String powerLutEntry() {
		return "in-power-lut-entry";
	}

	@Override
	protected String powerLutSize() {
		return "in-power-lut-size";
	}
}
