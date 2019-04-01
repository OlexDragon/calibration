package irt.calibration.exception;

import irt.calibration.data.packets.enums.Error;
import irt.calibration.data.packets.parents.Packet;

public class PacketErrorException extends IllegalArgumentException{
	private static final long serialVersionUID = -3211581231686617310L;

	public PacketErrorException(Class<? extends Packet> packetCclass, Error error) {
		super("The packet " + packetCclass.getSimpleName() + " has eror: " + error.toString());
	}
}
