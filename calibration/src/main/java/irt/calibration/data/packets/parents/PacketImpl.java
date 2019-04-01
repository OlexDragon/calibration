package irt.calibration.data.packets.parents;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.data.packets.Checksum;
import irt.calibration.data.packets.enums.Error;
import irt.calibration.data.packets.enums.GroupID;
import irt.calibration.data.packets.enums.PacketID;
import irt.calibration.data.packets.enums.PacketType;
import irt.calibration.data.packets.parameters.Parameter;
import irt.calibration.data.packets.parameters.ids.ParameterIDUnknown;
import irt.calibration.data.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.exception.PacketErrorException;
import irt.calibration.exception.PacketException;
import irt.calibration.helpers.OptionalIfElse;

public abstract class PacketImpl implements Packet {

	private final static Logger logger = LogManager.getLogger();

	private Byte addr;
	private final byte control = 0;
	private final short protocol = 0;

	private final PacketType packetType;
	private final GroupID	 groupID;
	private final PacketID packetID;
	private final Error error;
	private Parameter[] parameters;

	protected abstract Class<? extends Converter<?>> getConverterClass();

	protected PacketImpl(Byte addr, PacketType packetType, GroupID groupID, PacketID packetID, Parameter... parameters) {

		Objects.requireNonNull(parameters);
		if(parameters.length==0)
			throw new InvalidParameterException("The package must have at least one parameter.");

		this.addr = addr;
		this.packetType = packetType;
		this.packetID = packetID;
		this.groupID = groupID;
		error = Error.NO_ERROR;
		this.parameters = parameters;
	}

	protected PacketImpl(byte[] array) {

		byte[] packet = Packet.removeControlEscape(array);

		final byte[] p = packet;
		int lastIndex = IntStream.range(1, packet.length).filter(i->p[i]==FLAG_SEQUENCE).map(i->i+1).findFirst().orElse(0);

		if(lastIndex<6)
			throw new PacketException("The packet does not have end. (FLAG_SEQUENCE)", array);

		if(lastIndex<packet.length)
			packet = Arrays.copyOfRange(packet, 0, lastIndex);

		checkSumTest(packet);
			


		final int indexOfType = IntStream.range(0, 6)
				.filter(index->p[index]==PacketType.RESPONSE.toByte())
				.filter(index->index==1 || index==5)						// Converter index == 1 BUC index == 5
				.findAny().orElseThrow(()->new PacketException("Wring Packet Type", array));

		this.packetType = PacketType.RESPONSE;
		this.addr = Optional.of(indexOfType).filter(index->index==5).map(index->p[1]).orElse(null);
		this.packetID = getPacketID(indexOfType + 1, array);
		this.groupID = GroupID.valueOf(array[indexOfType + 3]).orElseThrow(()->new PacketException("Wrong Group ID", array));
		this.error = Error.valueOf(array[indexOfType + 6]).orElseThrow(()->new PacketException("Wrong Packet Error", array));
		Optional.of(error).filter(e->e!=Error.NO_ERROR).ifPresent(e->new PacketErrorException(getClass(), e));
		this.parameters = Parameter.parse(Arrays.copyOfRange(packet, indexOfType + 7, packet.length));
	}

	private PacketID getPacketID(int position, byte[] array) {

		final ByteBuffer byteBuffer = ByteBuffer.allocate(2).put(array[position]).put(array[++position]);
		byteBuffer.position(0);
		final short pID = byteBuffer.getShort();

		return PacketID.valueOf(pID).orElseThrow(()->new PacketException("Wrong Packet ID", array));
	}

	@Override
	public Byte getAddress() {
		return addr;
	}

	@Override
	public void setAddress(Byte addr) {
		this.addr = addr;
	}

	@Override
	public Error getError() {
		return error;
	}

	@Override
	public byte[] toBytes() {

		ByteBuffer byteBuffer = ByteBuffer.allocate(Short.MAX_VALUE).put(FLAG_SEQUENCE);

		Optional.ofNullable(addr).ifPresent(a->byteBuffer.put(a).put(control).putShort(protocol));

		byteBuffer
		.put(packetType.toByte())
		.put(packetID.toBytes())
		.put(groupID.toByte())
		.putShort((short)0)				// reserved
		.put(error.toByte());

		Optional.ofNullable(parameters)
		.map(Arrays::stream)
		.orElse(Stream.empty())
		.map(Parameter::toBytes)
		.forEach(bytes->byteBuffer.put(bytes));

		addChecksum(byteBuffer);

		addControlEscape(byteBuffer);

		byteBuffer.put(FLAG_SEQUENCE);

		byte[] array = new byte[byteBuffer.position()];
		byteBuffer.position(0);
		byteBuffer.get(array);
		return array;
	}

	private void addControlEscape(ByteBuffer byteBuffer) {
		byte[] array = new byte[byteBuffer.position() - 1];
		byteBuffer.position(1);
		byteBuffer.get(array);
		byteBuffer.position(1);
		byteBuffer.put(Packet.addControlEscape(array));
	}

	private void addChecksum(ByteBuffer byteBuffer) {
		byte[] array = new byte[byteBuffer.position() - 1];
		byteBuffer.position(1);
		byteBuffer.get(array);
		byteBuffer.put(Checksum.getChecksumAsBytes(array));
	}

	private void checkSumTest(byte[] array) {

		byte[] packet = Arrays.copyOfRange(array, 1, array.length - 3);
		byte[] checksum =  Arrays.copyOfRange(array, array.length - 3, array.length - 1);
		final byte[] test = Checksum.getChecksumAsBytes(packet);

		if(checksum[0]==test[0] && checksum[1]==test[1])
			return;

		throw new PacketException("Wrong checksum. " + Arrays.toString(checksum) + "!=" + Arrays.toString(test), array);
	}

	@Override
	public GroupID getGroupID() {
		return groupID;
	}

	@Override
	public PacketID getPacketID() {
		return packetID;
	}

	@Override
	public Parameter[] getParameters() {
		return parameters;
	}

	@Override
	public Map<Converter<?>, Parameter> parametersToMap() {

		Map<Converter<?>, Parameter> map = new HashMap<>();
		final Parameter[] parameters = getParameters();

		Optional.ofNullable(parameters).map(Arrays::stream).orElse(Stream.empty())
		.forEach(
				parameter->{

					final Class<? extends Converter<?>> parameterIDClass = getConverterClass();
					try {

						Method method = parameterIDClass.getMethod("valueOf", Parameter.class);
						Converter<?> parameterID = (Converter<?>) method.invoke(null, parameter);
						OptionalIfElse.of(Optional.ofNullable(parameterID))
						.ifPresent(pID->map.put(pID, parameter))
						.ifNotPresent(()->map.put(new ParameterIDUnknown().setTitle(getClass().getSimpleName()), parameter));

					} catch (Exception e) {
						logger.catching(e);
					}
					
				});

		return map;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [addr=" + addr + ", control=" + control + ", protocol="
				+ protocol + ", packetType=" + packetType + ", groupID=" + groupID + ", packetID=" + packetID
				+ ", error=" + error + ", parameters=" + Arrays.toString(parameters) + "]";
	}
}
