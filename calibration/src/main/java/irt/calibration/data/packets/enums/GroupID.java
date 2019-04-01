package irt.calibration.data.packets.enums;

import java.util.Arrays;
import java.util.Optional;

public enum GroupID {

	SPECIAL			((byte) 0),		/* Reserved for special use. */
	ALARM			((byte) 1),		/* Alarm: message content is product specific. */
	CONFIGURATION	((byte) 2),		/* Configuration: content is product specific. */
	FILETRANSFER	((byte) 3),		/* File transfer: software upgrade command (optional). */
	MEASUREMENT		((byte) 4),		/* Measurement: device status, content is product specific. */
	RESET			((byte) 5),		/* Device reset: generic command. */
	DEVICE_INFO		((byte) 8),		/* Device information: generic command. */
	CONFIG_PROFILE	((byte) 9),		/* Save configuration: generic command. */
	PROTOCOL		((byte)10),		/* Packet protocol parameters configuration and monitoring. */
	REDUNDANCY		((byte)12),
	NETWORK			((byte)11),		/* Network configuration. */
	DEVICE_DEBAG	((byte) 61),	/* Device Debug. */
	
	/* backwards compatibility - to be deleted */
	PRODUCTION_GENERIC_SET_1 ((byte)100),
	DEVELOPER_GENERIC_SET_1 ((byte)120);

	private final byte groupID;

	private GroupID(byte groupID) {
		this.groupID = groupID;
	}

	public byte toByte() {
		return groupID;
	}

	public static Optional<GroupID> valueOf(byte groupID) {
		return Arrays.stream(values()).filter(v->v.groupID == groupID).findAny();
	}
}
