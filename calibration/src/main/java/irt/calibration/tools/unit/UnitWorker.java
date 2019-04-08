package irt.calibration.tools.unit;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.prefs.Preferences;
import java.util.stream.IntStream;

import irt.calibration.exception.PacketErrorException;
import irt.calibration.helpers.SerialPortWorker;
import irt.calibration.tools.BaudRate;
import irt.calibration.tools.unit.packets.PacketCalibrationMade;
import irt.calibration.tools.unit.packets.PacketMuteControl;
import irt.calibration.tools.unit.packets.PacketCalibrationMade.CalibrationModeStatus;
import irt.calibration.tools.unit.packets.PacketMuteControl.MuteStatus;
import irt.calibration.tools.unit.packets.enums.Error;
import irt.calibration.tools.unit.packets.parameters.Parameter;
import irt.calibration.tools.unit.packets.parameters.ids.enums.interfaces.Converter;
import irt.calibration.tools.unit.packets.parents.Packet;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

public class UnitWorker {
//	private final static Logger logger = LogManager.getLogger();

	public static final int WAIT_TIME = 1500;

	public final static String PREFS_KEY_BAUDRATE	 = ".BAUDRATE";

	public final static Preferences prefs = Preferences.userNodeForPackage(UnitWorker.class);

	private ChoiceBox<String> chbUnitSerialPort;
	private TextField tfUnitAddress;

	private BaudRate baudRateUnit;

	public UnitWorker(ChoiceBox<String> chbUnitSerialPort, TextField tfUnitAddress) {

		final String baudRateStr = prefs.get(chbUnitSerialPort.getId() + PREFS_KEY_BAUDRATE, BaudRate.BAUDRATE_115200.name());
		try {
			baudRateUnit	 = BaudRate.valueOf(baudRateStr);
		}catch (IllegalArgumentException e) {
			baudRateUnit = BaudRate.BAUDRATE_115200;
		}

		this.tfUnitAddress = tfUnitAddress;
		tfUnitAddress.focusedProperty().addListener(
				(o, ov, nv)->{
					final String text = tfUnitAddress.getText().replaceAll("\\D", "");

					if(text.isEmpty()) {
						prefs.remove("Unit Address");
						return;
					}
					prefs.putInt("Unit Address", Integer.parseInt(text));
				});

		Optional.ofNullable(prefs.getInt("Unit Address", -1))
		.filter(addr->addr>=0)
		.map(addr->Integer.toString(addr))
		.ifPresent(addr->Platform.runLater(()->tfUnitAddress.setText(addr)));;

		this.chbUnitSerialPort = chbUnitSerialPort;

		chbUnitSerialPort.setTooltip(new Tooltip("BAUDRATE: "+ baudRateUnit));
	}

	public void choiceBbaudRate() {

    	ChoiceDialog<BaudRate> dialog = new ChoiceDialog<>(baudRateUnit, BaudRate.values());
    	Optional.of(chbUnitSerialPort).map(Node::getScene).map(Scene::getWindow).ifPresent(dialog::initOwner);
    	dialog.setTitle("Choice Dialog");
    	dialog.setHeaderText(null);
    	dialog.setContentText("Choice Baud Rate");

    	dialog.showAndWait()
    	.ifPresent(
    			v->{

    				baudRateUnit = v;
    				prefs.put(chbUnitSerialPort.getId() + PREFS_KEY_BAUDRATE, v.name());
    				chbUnitSerialPort.setTooltip(new Tooltip("BAUDRATE: "+ baudRateUnit));
   			});
	}

	protected Byte getUnitAddress() {
		final Optional<Integer> oAddr = Optional.ofNullable(tfUnitAddress.getText())

				.map(t->t.replaceAll("\\D", ""))
				.filter(t->!t.isEmpty())
				.map(Integer::valueOf)
				.map(i->i&0xFF);

		Platform.runLater(()->tfUnitAddress.setText(oAddr.map(b->b.toString()).orElse("")));


		return oAddr.map(Integer::byteValue).orElse(null);
	}

	public byte[] writeThenRead(Packet packet, int timeout) {
		final ByteBuffer byteBuffer = ByteBuffer.allocate(Short.MAX_VALUE);
		final byte[] bytes = packet.toBytes();

		synchronized (UnitWorker.class) {

			SerialPortWorker.writeThenRead(chbUnitSerialPort, bytes, timeout, Packet.readPacket(byteBuffer));
			int position = byteBuffer.position();
			byte[] array = new byte[position];
			byteBuffer.position(0);
			byteBuffer.get(array);
			// Send back acknowledge
			final byte[] acknowledge = getAcknowledge(array);
			SerialPortWorker.write(chbUnitSerialPort, acknowledge);
			return acknowledge.length < array.length ? Arrays.copyOfRange(array, acknowledge.length, array.length) : new byte[0];
		}
	}

	private byte[] getAcknowledge(byte[] array) {
		int firstFlagSequence = IntStream.range(0, array.length).filter(index->array[index]==Packet.FLAG_SEQUENCE).findFirst().orElse(0);
		int secondFlagSequence = IntStream.range(firstFlagSequence + 1, array.length).filter(index->array[index]==Packet.FLAG_SEQUENCE).findFirst().orElse(0);

		return Arrays.copyOfRange(array, firstFlagSequence, secondFlagSequence + 1);
	}

	public boolean connect(Button btnUnitConnect) {
		return SerialPortWorker.connect(chbUnitSerialPort, btnUnitConnect, baudRateUnit);
	}

	public Map<Converter<?>, Parameter> setMuteStatus(MuteStatus muteStatus) {

		final Byte unitAddress = getUnitAddress();
		final Packet packetMuteControl = new PacketMuteControl(unitAddress, muteStatus);
		final byte[] array = writeThenRead(packetMuteControl, WAIT_TIME);
		final Packet packet = new PacketMuteControl(array);
//		logger.error(packet);
		return packet.parametersToMap();
	}

	public Map<Converter<?>, Parameter> setCalibrationMode(CalibrationModeStatus modeStatus) {

		final Byte unitAddress = getUnitAddress();
		final Packet packetMuteControl = new PacketCalibrationMade( unitAddress, modeStatus);
		final byte[] array = writeThenRead(packetMuteControl, WAIT_TIME);
		final Packet packet = new PacketCalibrationMade(array);
//		logger.error(packet);
		return packet.parametersToMap();
	}

	protected void errorCheck(final Packet packet) {
		Error error = packet.getError();
		if(error!=Error.NO_ERROR)
			throw new PacketErrorException(packet.getClass(), error);
	}
}
