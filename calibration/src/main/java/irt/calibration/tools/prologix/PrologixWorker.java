package irt.calibration.tools.prologix;

import static irt.calibration.exception.ExceptionWrapper.catchConsumerException;
import static irt.calibration.exception.ExceptionWrapper.catchRunnableException;
import static irt.calibration.helpers.OptionalIfElse.of;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.CalibrationApp;
import irt.calibration.exception.PrologixTimeoutException;
import irt.calibration.helpers.SerialPortWorker;
import irt.calibration.tools.CommandType;
import irt.calibration.tools.Eos;
import irt.calibration.tools.ToolCommand;
import javafx.scene.control.Alert.AlertType;
import jssc.SerialPort;
import jssc.SerialPortException;

public class PrologixWorker {
	private final static Logger logger = LogManager.getLogger();

	private SerialPort serialPort;

	public synchronized void preset(Consumer<byte[]> consumer, int timeout) throws SerialPortException, PrologixTimeoutException {

		writeThenRead(PrologixCommand.VER.getCommand(), timeout, consumer);
		Optional.ofNullable(serialPort).ifPresent(catchConsumerException(
				sp->{
					write(sp, PrologixCommand.SAVECFG.getCommand() + " 0");
					write(sp, PrologixCommand.MODE.getCommand() + " 1");
				}));
	}

	private void writeThenRead(String command, int timeout, Consumer<byte[]> consumer) throws SerialPortException, PrologixTimeoutException{

		final Optional<SerialPort> oSerialPort = getSerialPort();

		if(!oSerialPort.isPresent()) {
			CalibrationApp.showAlert("Serial Port.", "serial port is not ready.", AlertType.ERROR);
			return;
		}

		final SerialPort sp = oSerialPort.get();
		this.serialPort = sp;

		synchronized (SerialPortWorker.class) {

			write(sp, command);

			final long start = System.currentTimeMillis();
			long elapsedTime = 0;

			final ByteBuffer bb = ByteBuffer.allocate(Short.MAX_VALUE);
			byte[] crLf = Eos.LF.getBytes();

			while(true) {


					int inputBufferBytesCount = sp.getInputBufferBytesCount();
					if(inputBufferBytesCount>0) {

						bb.put(sp.readBytes(inputBufferBytesCount));
						int position = bb.position();
						boolean crLfIsPresent = Optional.of(position)

								.map(p->--p)
								.filter(p->bb.get(p)==crLf[0])
								.isPresent();

//						logger.error("{} : {} : {}", bb, new String(bb.array()).trim(), bb.array());
						if(crLfIsPresent) {
							byte[] bytes = new byte[position];
							bb.position(0);
							bb.get(bytes, 0, position);
							consumer.accept(bytes);
							logger.debug("elapsedTime={}; timeout={}; bytes={}", System.currentTimeMillis()-start, timeout, bytes);
							return;
						}
					}

					long millis = timeout - elapsedTime;

					if(millis <= 0)
						break;

					synchronized(SerialPortWorker.class){  try { SerialPortWorker.class.wait(millis); } catch (InterruptedException e1) { } }
					elapsedTime = System.currentTimeMillis()-start;
			}
			logger.debug("elapsedTime={}; timeout={};", elapsedTime, timeout);
		}

		throw new PrologixTimeoutException("writeThenRead(...) - Read data timeout. ( " + timeout + " milliseconds )");
	}

	private Optional<SerialPort> getSerialPort() {
		return Optional.ofNullable(serialPort).filter(SerialPort::isOpened);
	}

	public void setSerialPort(SerialPort serialPort) {
		this.serialPort = serialPort;
	}

	public void send(ToolCommand toolCommand, String value, int timeout, Consumer<byte[]> consumer) {

		getSerialPort()
		.ifPresent(
				catchConsumerException(
						sp->{

							Optional.ofNullable(toolCommand)
							.ifPresent(
									sc->
									of(
											Optional.ofNullable(value)
											.map(String::trim)
											.filter(v->!v.isEmpty()))

									// Value Exists
									.ifPresent(
											catchConsumerException(
													v->{
														final String command = sc.getCommand();
														if(CommandType.waitForAnswer(sc.getCommandType()) && command.isEmpty()) {
															writeThenRead(v, timeout, consumer);
															return;
														}
														synchronized (SerialPortWorker.class) {

															write(sp, command + " " + v);
														}
													}))

									// No Value
									.ifNotPresent(
											catchRunnableException(
													()->{
														CommandType commandType = sc.getCommandType();
														if(CommandType.waitForAnswer(commandType))
															writeThenRead(sc.getCommand(), timeout, consumer);
														else
															write(sc.getCommand());
													})));
						}));
	}

	private boolean write(SerialPort serialPort, String command) throws SerialPortException {

		serialPort.purgePort(SerialPort.PURGE_TXABORT | SerialPort.PURGE_TXCLEAR | SerialPort.PURGE_RXABORT | SerialPort.PURGE_RXCLEAR);

		final byte[] bytes = (command + Eos.LF).getBytes();

		logger.debug("'{} : {}'", command, bytes);

		return serialPort.writeBytes(bytes);
	}

	public void get(String command, Consumer<byte[]> consumer, int timeout) throws SerialPortException, PrologixTimeoutException {
		if(consumer==null)
			write(command);
		else
			writeThenRead(command, timeout, consumer);
		
	}

	private void write(String command) throws SerialPortException {

		final Optional<SerialPort> oSerialPort = getSerialPort();

		if(!oSerialPort.isPresent()) {
			CalibrationApp.showAlert("Serial Port.", "serial port is not ready.", AlertType.ERROR);
			return;
		}

		final SerialPort sp = oSerialPort.get();
		write(sp, command);
	}
}
