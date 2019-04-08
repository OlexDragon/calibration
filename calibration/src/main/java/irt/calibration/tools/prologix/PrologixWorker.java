package irt.calibration.tools.prologix;

import static irt.calibration.exception.ExceptionWrapper.catchConsumerException;
import static irt.calibration.exception.ExceptionWrapper.catchRunnableException;
import static irt.calibration.helpers.OptionalIfElse.of;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.CalibrationApp;
import irt.calibration.helpers.SerialPortWorker;
import irt.calibration.helpers.ThreadWorker;
import irt.calibration.tools.CommandType;
import irt.calibration.tools.Eos;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import jssc.SerialPort;
import jssc.SerialPortException;

public class PrologixWorker {
	private final static Logger logger = LogManager.getLogger();

	private CheckBox cbShowHelp;
	private ChoiceBox<String> chbPrologixSerialPort;
	private TextArea taPrologixAnswers;
	private PrologixCommand  selectedCommand;

	private final ChangeListener<? super PrologixCommand> commandListener = (o, ov, nv)->{

		selectedCommand = nv;
		taPrologixAnswers.setText(taPrologixAnswers.getText() + "\n" + selectedCommand + " : ");

		Optional.ofNullable(cbShowHelp).filter(CheckBox::isSelected).ifPresent(cb->showHelp(nv));
	};

	private SerialPort serialPort;

	public PrologixWorker(ChoiceBox<String> chbPrologixSerialPort, ChoiceBox<PrologixCommand> chbPrologixCommand, CheckBox cbShowHelp, TextArea taPrologixAnswers) {

		this.cbShowHelp = cbShowHelp;
		final SingleSelectionModel<PrologixCommand> selectionModel = chbPrologixCommand.getSelectionModel();
		cbShowHelp.setOnAction(e->Optional.ofNullable(selectionModel.getSelectedItem()).filter(sc->cbShowHelp.isSelected()).ifPresent(sc->showHelp(sc)));

		this.taPrologixAnswers = taPrologixAnswers;
		this.chbPrologixSerialPort = chbPrologixSerialPort;

		final PrologixCommand[] values = PrologixCommand.values();
		Arrays.sort(values, (a,b)->a.name().compareTo(b.name()));
		final ObservableList<PrologixCommand> observableArrayList = FXCollections.observableArrayList(values);
		chbPrologixCommand.setItems(observableArrayList);

		selectionModel.selectedItemProperty().addListener(commandListener);
		selectionModel.select(0);
		selectedCommand = selectionModel.getSelectedItem();
	}

	public synchronized void preset() throws SerialPortException {

		int timeout = 1000;
		writeThenRead(PrologixCommand.VER.getCommand(), timeout, bytes->{
			String string = new String(bytes);
			taPrologixAnswers.setText(string);
		});
		Optional.ofNullable(serialPort).ifPresent(catchConsumerException(
				sp->{
					write(sp, PrologixCommand.SAVECFG.getCommand() + " 0");
					write(sp, PrologixCommand.MODE.getCommand() + " 1");
				}));
	}

	private void writeThenRead(String command, int timeout, Consumer<byte[]> consumer){

		getSerialPort().ifPresent(
				catchConsumerException(

				sp->{

					if(this.serialPort!=sp)
						this.serialPort = sp;

					Thread thread = ThreadWorker.getThread(
							catchRunnableException(

							()->{

								synchronized (SerialPortWorker.class) {

									write(sp, command);

									final long start = System.currentTimeMillis();
									long elapsedTime = 0;

									final ByteBuffer bb = ByteBuffer.allocate(Short.MAX_VALUE);
									byte[] crLf = Eos.LF.getBytes();

									while(elapsedTime < timeout) {


											int inputBufferBytesCount = sp.getInputBufferBytesCount();
											if(inputBufferBytesCount>0) {

												bb.put(sp.readBytes(inputBufferBytesCount));
												int position = bb.position();
												boolean crLfIsPresent = Optional.of(position)

														.map(p->--p)
														.filter(p->bb.get(p)==crLf[0])
														.isPresent();

//												logger.error("{} : {} : {}", bb, new String(bb.array()).trim(), bb.array());
												if(crLfIsPresent) {
													byte[] bytes = new byte[position];
													bb.position(0);
													bb.get(bytes, 0, position);
													consumer.accept(bytes);
													return;
												}}

											long millis = timeout - elapsedTime;
											synchronized(SerialPortWorker.class){  try { SerialPortWorker.class.wait(millis); } catch (InterruptedException e1) { } }
											elapsedTime = System.currentTimeMillis()-start;
									}
								}

								CalibrationApp.showAlert("Timeout", "Read data timeout.", AlertType.ERROR);
							}));

					thread.start();
				}));
	}

	private Optional<SerialPort> getSerialPort() {
		return Optional.ofNullable(chbPrologixSerialPort.getUserData()).map(SerialPort.class::cast).filter(SerialPort::isOpened);
	}

	public void send(String value, int timeout) {

		getSerialPort()
		.ifPresent(
				catchConsumerException(
						sp->{

							Optional.ofNullable(selectedCommand)
							.ifPresent(
									sc->
									of(
											Optional.ofNullable(value)
											.map(String::trim)
											.filter(v->!v.isEmpty()))
									.ifPresent(
											catchConsumerException(
													v->{
														final String command = sc.getCommand();
														if(sc.getCommandType()!=CommandType.SET && command.isEmpty()) {
															writeThenRead(v, timeout);
															return;
														}
														synchronized (SerialPortWorker.class) {

															write(sp, command + " " + v);
														}
													}))
									.ifNotPresent(
											catchRunnableException(
													()->
													writeThenRead(sc.getCommand(), timeout))));
						}));
	}

	private void writeThenRead(String command, int timeout) throws SerialPortException {
		writeThenRead(command, timeout,
				bytes->{
					logger.debug("{}", bytes);
					taPrologixAnswers.setText(taPrologixAnswers.getText() + new String(bytes));
				});
	}

	private boolean write(SerialPort serialPort, String command) throws SerialPortException {

		serialPort.purgePort(SerialPort.PURGE_TXABORT | SerialPort.PURGE_TXCLEAR | SerialPort.PURGE_RXABORT | SerialPort.PURGE_RXCLEAR);

		final byte[] bytes = (command + Eos.LF).getBytes();

		logger.debug("'{} : {}'", command, bytes);

		return serialPort.writeBytes(bytes);
	}

	private void showHelp(PrologixCommand prologixCommand) {
		taPrologixAnswers.setText(prologixCommand.getDescription());
	}

	public void get(String command, Consumer<byte[]> consumer, int timeout) {
		if(consumer==null)
			write(command);
		else
			writeThenRead(command, timeout, consumer);
		
	}

	private void write(String command) {
		getSerialPort()
		.ifPresent(
				catchConsumerException(
						sp->{
							write(sp, command);
						}));
	}

	public void setAddress(Integer addr) {
		getSerialPort().ifPresent(catchConsumerException(sp->write(sp, PrologixCommand.ADDR.getCommand() + " " + addr)));
	}

	public void enableFontPanel() {
		getSerialPort().ifPresent(catchConsumerException(sp->write(sp, PrologixCommand.LOC.getCommand())));
	}
}
