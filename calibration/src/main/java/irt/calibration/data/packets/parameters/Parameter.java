package irt.calibration.data.packets.parameters;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import irt.calibration.data.packets.parameters.ids.ParameterID;

public class Parameter {
//
//	private final static Logger logger = LogManager.getLogger();

	private final byte id;
	private byte[] data;

	private Parameter(byte parameterID) {
		this.id = parameterID;
		data = new byte[0];
	}

	protected Parameter(ParameterID parameterID) {
		this(parameterID.toByte());
	}

	public byte[] toBytes() {
		return ByteBuffer.allocate(3 + data.length).put(id).putShort((short) data.length).put(data).array();
	}

	public static Parameter[] parse(byte[] array) {

		List<Parameter> result = new ArrayList<>();
		final ByteBuffer byteBuffer = ByteBuffer.wrap(array);
		byteBuffer.position(0);

		while(byteBuffer.position()<(byteBuffer.limit()-3)) {
			Parameter p = newParameter(byteBuffer);
			result.add(p);
		}

		Parameter[] a = new Parameter[result.size()];
		return result.toArray(a);
	}

	public byte getId() {
		return id;
	}

	public byte[] getData() {
		return data;
	}

	public Parameter putData(byte[] data) {
		this.data = data==null ? new byte[0] : data;
		return this;
	}

	@Override
	public String toString() {
		return "Parameter [parameterID=" + id + ", size=" + data.length + ", data=" + Arrays.toString(data) + "]";
	}

	private static Parameter newParameter(ByteBuffer byteBuffer) {

		final Parameter parameter = new Parameter(byteBuffer.get());

		final int size = byteBuffer.getShort() & 0xFFFF;
		byte[] data = new byte[size];
		byteBuffer.get(data);

		parameter.putData(data);

		return parameter;
	}
}
