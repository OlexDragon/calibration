package irt.calibration.data.packets.data;

import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import java.util.Arrays;

public class Power {

	private final Condition condition;
	private final Short value;

	public Power(byte[] bytes) {

		final ByteBuffer bb = ByteBuffer.wrap(bytes);
		switch(bb.limit()) {

		case 2:
			condition = Condition.UNKNOWN;
			value = bb.getShort();
			break;

		case 3:
			condition = Condition.values()[bb.get()&3];
			value = bb.getShort();
			break;

		default:
			throw new InvalidParameterException("Allowed length is 2 or 3 bytes." + Arrays.toString(bytes));
		}
	}

	public Short getValue() {
		return value;
	}

	public Condition getCondition() {
		return condition;
	}

	@Override
	public String toString() {
		return condition==Condition.UNDEFINED ? condition.toString() : condition + value.toString();
	}

	public enum Condition{
		UNDEFINED("UNDEFINED"),
		LESS("<"),
		IN_THE_RANGE(""),
		MORE_THAN(">"),
		UNKNOWN("");

		private String sign;

		private Condition(String sign) {
			this.sign = sign;
		}

		@Override
		public String toString() {
			return sign;
		}
	}
}
