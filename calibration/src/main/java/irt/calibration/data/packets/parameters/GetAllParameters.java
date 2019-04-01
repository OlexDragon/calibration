package irt.calibration.data.packets.parameters;

import irt.calibration.data.packets.parameters.ids.GeneralParameterID;

public class GetAllParameters extends Parameter {

	public GetAllParameters() {
		super(GeneralParameterID.ALL);
	}

}
