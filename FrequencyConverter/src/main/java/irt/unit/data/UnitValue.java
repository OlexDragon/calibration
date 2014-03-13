package irt.unit.data;

public class UnitValue {

	private byte flags;
	private short value;

	public byte getFlags() {
		return flags;
	}
	public short getValue() {
		return value;
	}
	public void setFlags(byte flags) {
		this.flags = flags;
	}
	public void setValue(short value) {
		this.value = value;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + flags;
		result = prime * result + value;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnitValue other = (UnitValue) obj;
		if (flags != other.flags)
			return false;
		if (value != other.value)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "UnitValue [flags=" + flags + ", value=" + value + "]";
	}
}
