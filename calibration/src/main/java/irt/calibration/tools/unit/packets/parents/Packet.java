package irt.calibration.tools.unit.packets.parents;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

import irt.calibration.tools.unit.packets.enums.Error;
import irt.calibration.tools.unit.packets.enums.GroupID;
import irt.calibration.tools.unit.packets.enums.PacketID;
import irt.calibration.tools.unit.packets.parameters.Parameter;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.Converter;

public interface Packet {

	public static final byte FLAG_SEQUENCE	= 0x7E;
	public static final byte CONTROL_ESCAPE= 0x7D;

	Byte getAddress();
	void setAddress(Byte addr);
	GroupID getGroupID();
	PacketID getPacketID();
	Error getError();
	Map<Converter<?>, Parameter> parametersToMap();
	Parameter[] getParameters();
	byte[] toBytes();

	public static byte[] addControlEscape(byte[] source) {

		ByteBuffer byteBuffer = ByteBuffer.allocate(source.length * 2);

		for(byte b: source) {
			if(b==FLAG_SEQUENCE || b==CONTROL_ESCAPE){

				byteBuffer.put(CONTROL_ESCAPE);
				byteBuffer.put((byte) (b ^ 0x20));
				
			}else
				byteBuffer.put(b);
		}

		byte[] result = new byte[byteBuffer.position()];
		byteBuffer.position(0);
		byteBuffer.get(result);

		return result;
	}

	public static byte[] removeControlEscape(byte[] source) {

		ByteBuffer byteBuffer = ByteBuffer.allocate(source.length);

		for(int i=0; i<source.length; i++) {

			if(source[i]==CONTROL_ESCAPE){

				i++;

				if(i==source.length)
					break;

				byteBuffer.put((byte) (source[i] ^ 0x20));
				
			}else
				byteBuffer.put(source[i]);
		}

		byte[] result = new byte[byteBuffer.position()];
		byteBuffer.position(0);
		byteBuffer.get(result);

		return result;
	}

	public static Function<byte[], Boolean> readPacket(ByteBuffer byteBuffer){
		return bytes->{

			Optional.ofNullable(bytes).ifPresent(byteBuffer::put);
			final int position = byteBuffer.position();

			byte[] array = new byte[position];
			byteBuffer.position(0);
			byteBuffer.get(array);

			final long countFlagSequence = IntStream.range(0, position).parallel().filter(index->array[index]==FLAG_SEQUENCE).count();

			return countFlagSequence<4;	// read more if FLAG_SEQUENCE count less then 4
		};
	}
}
