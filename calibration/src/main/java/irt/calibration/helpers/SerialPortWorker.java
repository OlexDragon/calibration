package irt.calibration.helpers;

import static irt.calibration.exception.ExceptionWrapper.catchConsumerException;
import static irt.calibration.exception.ExceptionWrapper.catchFunctionException;
import static irt.calibration.helpers.OptionalIfElse.of;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.data.BaudRate;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;

public class SerialPortWorker {

	private final static Logger logger = LogManager.getLogger();

	private final static String PORT = ".port";

	private final static String[] portNames					 = SerialPortList.getPortNames();
	private final static Set<ChoiceBox<String>> choiceBoxs	 = new HashSet<>();
	private final static Preferences prefs					 = Preferences.userNodeForPackage(SerialPortWorker.class);

	private final static ChangeListener<? super String> changeListener = (o, ov, nv)->getChangeListener(o, ov, nv);

	private static void getChangeListener(ObservableValue<? extends String> o, String ov, String nv) {

		final Object bean = ((ReadOnlyObjectProperty<?>)o).getBean();

		// Store port name
		choiceBoxs.stream()
		.filter(chb->chb.getSelectionModel()==bean)
		.findAny()
		.ifPresent(
				chb->{

					prefs.put(chb.getId() + PORT, nv);

					// Reconnect if connected
					Optional.ofNullable(chb.getUserData())
					.map(SerialPort.class::cast)
					.filter(SerialPort::isOpened)
					.ifPresent(
							sp->{
								final String baudRateStr = UnitWorker.prefs.get(chb.getId() + UnitWorker.PREFS_KEY_BAUDRATE, BaudRate.BAUDRATE_115200.name());
								BaudRate baudRate;
								try {
									baudRate = BaudRate.valueOf(baudRateStr);
								}catch (IllegalArgumentException e) {
									baudRate = BaudRate.BAUDRATE_115200;
								}
								reconnect(chb, baudRate);
							});
				});

		// Remove port name from other Choice Box
		removeFromOther(nv, bean);
	}

	private static void removeFromOther(String nv, final Object bean) {

		choiceBoxs.stream()
		.filter(chb->chb.getSelectionModel()!=bean)
		.forEach(
				chb->{

					final SingleSelectionModel<String> selectionModel = chb.getSelectionModel();
					final ReadOnlyObjectProperty<String> selectedItemProperty = selectionModel.selectedItemProperty();
					selectedItemProperty.removeListener(changeListener);

					final String selectedItem = selectionModel.getSelectedItem();

					final String[] array = Arrays.stream(portNames).filter(name->!name.equals(nv)).toArray(String[]::new);
					final ObservableList<String> observableArrayList = FXCollections.observableArrayList(array);
					chb.setItems(observableArrayList);
					selectionModel.select(selectedItem);

					selectedItemProperty.addListener(changeListener);
				});
	}

	public static void addChoiceBoxs(ChoiceBox<String> choiceBox) {

		ObservableList<String> value = FXCollections.observableArrayList(portNames);
		choiceBox.setItems(value);

		final Stream<ChoiceBox<String>> stream = choiceBoxs.stream();

		choiceBoxs.add(choiceBox);

		stream.filter(chb->chb.getSelectionModel().getSelectedIndex()>=0).forEach(chb->removeFromOther(chb.getSelectionModel().getSelectedItem(), chb));

		final SingleSelectionModel<String> selectionModel = choiceBox.getSelectionModel();

		of(Optional.ofNullable(prefs.get(choiceBox.getId() + PORT, null)))
		.ifPresent(portName->selectionModel.select(portName))
		.ifNotPresent(()->selectionModel.selectFirst());
	}

	public static void disconect() {
		choiceBoxs.stream().map(Node::getUserData).filter(sp->sp!=null).map(SerialPort.class::cast).filter(SerialPort::isOpened).forEach(

				serialPort -> {

					try {

						serialPort.closePort();

					} catch (SerialPortException e) {
						logger.catching(e);
					}
				});
	}

	/**
	 * @param choiceBox
	 * @return true if the serial port has been opened
	 */
	public static boolean closePort(ChoiceBox<String> choiceBox , Button button) {

		final Optional<SerialPort> oSerialPort = Optional.ofNullable(choiceBox.getUserData()).map(SerialPort.class::cast);

		final boolean closePort = closePort(oSerialPort);

		Optional.ofNullable(button).filter(b->closePort).ifPresent(b->Platform.runLater(()->b.setText("Connect")));

		return closePort;
	}

	private static boolean closePort(final Optional<SerialPort> oSerialPort) {

		return oSerialPort
				.filter(SerialPort::isOpened)
				.map(
						serialPort -> {
							try {
								synchronized (SerialPortWorker.class) {

									return serialPort.closePort();
								}
							} catch (SerialPortException e) {
								logger.catching(e);
							}
							return false;
						})
				.orElse(false);
	}

	public static boolean connect(ChoiceBox<String> choiceBox, Button btn) {
		return connect(choiceBox, btn, null);
	}

	public static boolean connect(ChoiceBox<String> choiceBox, Button button, BaudRate baudRate) {

			if(closePort(choiceBox, button))
					return false;

			if(!connect(choiceBox, baudRate))
				return false;

			button.setText("Disconnect");
			return true;
	}

	private static boolean reconnect(ChoiceBox<String> choiceBox, BaudRate baudRate){

		closePort(choiceBox, null);

		return connect(choiceBox, baudRate);
	}

	private static boolean connect(ChoiceBox<String> choiceBox, BaudRate baudRate) {

		return Optional.ofNullable(choiceBox.getSelectionModel().getSelectedItem())
				.map(SerialPort::new)
				.map(
						catchFunctionException(
								serialPort->{
									synchronized (SerialPortWorker.class) {

										if(!serialPort.openPort()) 
											return false;

										serialPort.addEventListener( e->{ synchronized(SerialPortWorker.class){ SerialPortWorker.class.notifyAll(); }});
										Optional.ofNullable(baudRate).map(BaudRate::getBautRate).ifPresent(catchConsumerException(br->serialPort.setParams(br, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)));
										choiceBox.setUserData(serialPort);
										return true;
									}
								}))
				.orElse(false);
	}

	UncaughtExceptionHandler uncaughtExceptionHandler = (thread, throwable)->{

		logger.error("\n{}", thread);
		logger.catching(throwable);
	};

	/**
	 * 
	 * @param chbUnitSerialPort - choice box with serial port in user data
	 * @param bytes	- bytes to write
	 * @param timeout 
	 * @param consumer	- byte[]: read bytes; Pair<Boolean, Integer>>: 'Boolean' true - read more, false - exit; Integer - number of byte to read.
	 */
	public static void writeThenRead(ChoiceBox<String> chbUnitSerialPort, byte[] bytes, int timeout, Function<byte[], Boolean> function) {

		Optional.ofNullable(chbUnitSerialPort.getUserData()).map(SerialPort.class::cast)
		.ifPresent(
				catchConsumerException(
						serialPort->{
							synchronized (SerialPortWorker.class) {
//								logger.error("Start");

								serialPort.purgePort(SerialPort.PURGE_TXABORT | SerialPort.PURGE_TXCLEAR | SerialPort.PURGE_RXABORT | SerialPort.PURGE_RXCLEAR);
								final boolean writeBytes = serialPort.writeBytes(bytes);

								if(!writeBytes)
									throw new SerialPortException(serialPort.getPortName(), "writeThenRead()", "It was impossible to write to the serial port.");

								long start = System.currentTimeMillis();
								long waitTime = timeout;

								while(waitTime>0 && serialPort.isOpened()) {

									byte[] readBytes = null;

									final int bufferCount = serialPort.getInputBufferBytesCount();
									if(bufferCount>0)
										readBytes = serialPort.readBytes(bufferCount);

									if(!function.apply(readBytes))
										return;

									synchronized (SerialPortWorker.class) { try{ SerialPortWorker.class.wait(waitTime); }catch (Exception e) { } }
//									logger.error("Wait: {}", readBytes);

									waitTime = timeout - ( System.currentTimeMillis() - start ); 
								}

//								logger.error("End");
								throw new SerialPortTimeoutException(serialPort.getPortName(), "writeThenRead()", timeout);
							}
						}));
	}

	public static void write(ChoiceBox<String> chbUnitSerialPort, byte[] bytes) {

		Optional.ofNullable(chbUnitSerialPort.getUserData()).map(SerialPort.class::cast).filter(SerialPort::isOpened)
		.ifPresent(
				catchConsumerException(
						serialPort->{
							synchronized (SerialPortWorker.class) {

								serialPort.purgePort(SerialPort.PURGE_TXABORT | SerialPort.PURGE_TXCLEAR | SerialPort.PURGE_RXABORT | SerialPort.PURGE_RXCLEAR);
								serialPort.writeBytes(bytes);
							}
						}));
	}
}
