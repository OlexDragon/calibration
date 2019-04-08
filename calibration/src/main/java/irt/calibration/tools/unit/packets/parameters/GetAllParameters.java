package irt.calibration.tools.unit.packets.parameters;

import irt.calibration.tools.unit.packets.parameters.ids.GeneralParameterID;

public class GetAllParameters extends Parameter {

	public GetAllParameters() {
		super(GeneralParameterID.ALL);
	}

}
