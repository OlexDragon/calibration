package irt.calibration.helpers;

import static irt.calibration.exception.ExceptionWrapper.catchConsumerException;
import static irt.calibration.exception.ExceptionWrapper.catchRunnableException;
import static irt.calibration.helpers.OptionalIfElse.of;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.CalibrationApp;
import irt.calibration.data.Eos;
import irt.calibration.data.prologix.PrologixCommands;
import irt.calibration.data.prologix.PrologixCommandsHelp;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import jssc.SerialPort;
import jssc.SerialPortException;

public class PrologixWorker {

	private final static Logger logger = LogManager.getLogger();

	private CheckBox cbShowHelp;
	private ChoiceBox<String> chbPrologixSerialPort;
	private TextArea taPrologixAnswers;
	private String selectedCommand;

	private final ChangeListener<? super PrologixCommands> commandListener = (o, ov, nv)->{

		selectedCommand = nv.getCommand();
		taPrologixAnswers.setText(taPrologixAnswers.getText() + "\n" + selectedCommand + " : ");

		Optional.ofNullable(cbShowHelp).filter(CheckBox::isSelected).ifPresent(cb->showHelp(nv));
	};

	private SerialPort serialPort;

	public PrologixWorker(ChoiceBox<String> chbPrologixSerialPort, ChoiceBox<PrologixCommands> chbPrologixCommand, CheckBox cbShowHelp, TextArea taPrologixAnswers) {

		this.cbShowHelp = cbShowHelp;
		cbShowHelp.setOnAction(e->Optional.ofNullable(chbPrologixCommand.getSelectionModel().getSelectedItem()).filter(sc->cbShowHelp.isSelected()).ifPresent(sc->showHelp(sc)));

		this.taPrologixAnswers = taPrologixAnswers;
		this.chbPrologixSerialPort = chbPrologixSerialPort;

		final ObservableList<PrologixCommands> observableArrayList = FXCollections.observableArrayList(PrologixCommands.values());
		chbPrologixCommand.setItems(observableArrayList);

		chbPrologixCommand.getSelectionModel().selectedItemProperty().addListener(commandListener);
	}

	public synchronized void preset() throws SerialPortException {

		int timeout = 1000;
		String command = PrologixCommands.VER.getCommand() + Eos.CR_LF;
		byte[] bytesToWrite = command.getBytes();

		writeThenRead(bytesToWrite, timeout, bytes->{
			String string = new String(bytes);
			taPrologixAnswers.setText(string);
		});
		Optional.ofNullable(serialPort).ifPresent(catchConsumerException(
				sp->{
					sp.purgePort(SerialPort.PURGE_TXABORT | SerialPort.PURGE_TXCLEAR | SerialPort.PURGE_RXABORT | SerialPort.PURGE_RXCLEAR);
					sp.writeBytes((PrologixCommands.SAVECFG.getCommand() + " 0" + Eos.CR_LF).getBytes());
					sp.writeBytes((PrologixCommands.MODE.getCommand() + " 1" + Eos.CR_LF).getBytes());
				}));
	}

	private void writeThenRead(byte[] bytesToWrite, int timeout, Consumer<byte[]> consumer) throws SerialPortException {

		Optional.ofNullable(chbPrologixSerialPort.getUserData()).map(SerialPort.class::cast).filter(SerialPort::isOpened).ifPresent(catchConsumerException(

				sp->{

					if(this.serialPort!=sp)
						this.serialPort = sp;

					sp.purgePort(SerialPort.PURGE_TXABORT | SerialPort.PURGE_TXCLEAR | SerialPort.PURGE_RXABORT | SerialPort.PURGE_RXCLEAR);
					sp.writeBytes(bytesToWrite);

					Thread thread = ThreadWorker.getThread(

							()->{

								final long start = System.currentTimeMillis();
								long elapsedTime = 0;

								final ByteBuffer byteBuffer = ByteBuffer.allocate(Short.MAX_VALUE);
								byte[] crLf = Eos.CR_LF.getBytes();

								while(elapsedTime < timeout) {

									try {


										int inputBufferBytesCount = sp.getInputBufferBytesCount();
										if(inputBufferBytesCount>0) {

											byteBuffer.put(sp.readBytes(inputBufferBytesCount));
											int position = byteBuffer.position();
											boolean crLfIsPresent = Optional.of(position)

													.map(p->--p)
													.filter(p->byteBuffer.get(p)==crLf[1])
													.map(p->--p)
													.filter(p->byteBuffer.get(p)==crLf[0])
													.isPresent();

											if(crLfIsPresent) {
												byte[] bytes = new byte[position];
												byteBuffer.position(0);
												byteBuffer.get(bytes, 0, position);
												consumer.accept(bytes);
												return;
											}}

										long millis = timeout - elapsedTime;
										synchronized(SerialPortWorker.class){  try { SerialPortWorker.class.wait(millis); } catch (InterruptedException e1) { } }
										elapsedTime = System.currentTimeMillis()-start;

									}catch (SerialPortException e) {
										logger.catching(e);
									}
								}

								CalibrationApp.showAlert("Timeout", "Read data timeout.", AlertType.ERROR);
							});

					thread.start();
				}));
	}

	public void send(String value, int timeout) {

		Optional.ofNullable(chbPrologixSerialPort.getUserData()).map(SerialPort.class::cast).filter(SerialPort::isOpened)
		.ifPresent(
				catchConsumerException(
						sp->{

							Optional.ofNullable(selectedCommand)
							.ifPresent(
									sc->
									of(
											Optional.ofNullable(value)
											.filter(v->!v.isEmpty()))
									.ifPresent(
											catchConsumerException(
													v->{
														synchronized (PrologixWorker.this) {

															sp.writeBytes((sc + " " + v + Eos.CR_LF).getBytes());
														}
													}))
									.ifNotPresent(
											catchRunnableException(
													()->
													writeThenRead((sc + Eos.CR_LF).getBytes(), timeout, bytes->taPrologixAnswers.setText(taPrologixAnswers.getText() + new String(bytes))))));
						}));
	}

	private void showHelp(PrologixCommands nv) {
		String value = PrologixCommandsHelp.valueOf(nv.name()).toString();
		taPrologixAnswers.setText(value);
	}
}
