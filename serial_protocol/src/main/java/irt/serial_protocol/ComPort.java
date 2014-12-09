package irt.serial_protocol;

import irt.serial_protocol.data.Checksum;
import irt.serial_protocol.data.ToHex;
import irt.serial_protocol.data.packet.LinkHeader;
import irt.serial_protocol.data.packet.LinkedPacket;
import irt.serial_protocol.data.packet.Packet;
import irt.serial_protocol.data.packet.PacketHeader;
import irt.serial_protocol.data.packet.ParameterHeader;
import irt.serial_protocol.data.packet.Payload;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Timer;

import jssc.SerialPort;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class ComPort extends SerialPort implements AutoCloseable {

	private static final Logger logger = (Logger) LogManager.getLogger();

	public static final byte FLAG_SEQUENCE	= 0x7E;
	public static final byte CONTROL_ESCAPE= 0x7D;

	private boolean run = true;
	private int timeout = 3000;
	private int timesTimeout;
	private Timer timer;
	private SerialPortEvent serialPortEvent = new SerialPortEvent();
	private LinkHeader linkHeader;
	private short packetId;
	private boolean isSerialPortEven;
	private boolean isComfirm;

	private static List<ComPort> comPorts = new ArrayList<>();

	public static ComPort getInstance(String portName) throws SerialPortException{
		logger.entry(portName);

		ComPort comPort = null;
		portName = portName.toUpperCase();

		if(portName!=null && portName.startsWith("COM")){
			for(ComPort cp:comPorts){
				String cpName = cp.getPortName();
				if(cpName!=null && cpName.equals(portName)){
					comPort = cp;
					break;
				}
			}

			if(comPort==null){
				comPort = new ComPort(portName);
				comPorts.add(comPort);
			}

			comPort.openPort();
		}

		return logger.exit(comPort);
	}

	private ComPort(String portName) throws SerialPortException {
		super(portName);

		timer = new Timer(timeout, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {

//				try {
//					Console.appendLn("Timeout", "Timer");
//					synchronized (ComPort.this) {
//						closePort();
//					}
//				} catch (SerialPortException e) {
//					comPortLogger.catching(e);
//				}
			}
		});
		timer.setRepeats(false);
		openPort();
	}

	public synchronized Packet send(Packet p){
		logger.entry(p);

		PacketHeader ph = p.getHeader();
		byte groupId = ph.getGroupId();
		packetId = ph.getPacketId();
		Packet packet;

		if(p instanceof LinkedPacket){
			linkHeader = ((LinkedPacket)p).getLinkHeader();
			packet = new LinkedPacket(linkHeader);
		}else{
			linkHeader = null;
			packet = new Packet();
		}

		int runTimes = 0;
		byte[] readData;
		int ev;
		byte[] cs;
		PacketHeader packetHeader;
		List<Payload> payloadsList;
		ParameterHeader parameterHeader;
		try {

if(!isRun())
	setRun(true);

do{

	Checksum checksum = null;

			timer.restart();
			clear();

			byte[] data = preparePacket(p.asBytes());
			logger.debug("send: {}", ToHex.bytesToHex(data));

//			String prefix = (runTimes+1)+") send";

//			comPortLogger.info(marker, ">> {}: {}", prefix, p);
//			comPortLogger.info(marker, ">> {}: {}", prefix, hexStr);

			if(isRun() && data!=null){
				writeBytes(data);

				if ((isConfirmBytes()) && isFlagSequence()){

					if(linkHeader!=null)
						if((readData=readLinkHeader())!=null)
							checksum = new Checksum(readData);
						else{
							break;
						}

					if((readData=readHeader())!=null) {
						if(checksum!=null)
							checksum.add(readData);
						else
							checksum = new Checksum(readData);

						packetHeader = new PacketHeader(readData);

						if (packetHeader.asBytes() != null && packetHeader.getGroupId()==groupId) {
							packet.setHeader(packetHeader);
							payloadsList = new ArrayList<>();

							while ((readData = readParameterHeader())!=null && isRun()) {

								if (containsFlagSequence(readData)) {
									cs = checksum.getChecksumAsBytes();

									if (cs[0] == readData[0] && cs[1] == readData[1]){
										timesTimeout = 0;
										packet.setPayloads(payloadsList);
									}

									break;
								}
								checksum.add(readData);
								parameterHeader = new ParameterHeader(readData);

								ev = parameterHeader.getSize();
								if(parameterHeader.getCode()>30 || ev>2000){
									break;
								}
								if (ev >= 0 && (readData = readBytes(ev))!=null) {

									checksum.add(readData);
									Payload payload = new Payload(parameterHeader,	readData);
									payloadsList.add(payload);
								}else{
									break;
								}
							}					
						}
					}
				}
				if(isRun())
					writeBytes(getAcknowledge());
			}else
				setRun(false);
}while(isComfirm && packet.getPayloads()==null && ++runTimes<3 && isRun());//if error repeat up to 3 times

			if(packet.getHeader()==null || packet.getPayloads()==null && isRun())
					packet = p;

		} catch (SerialPortException e) {
			logger.catching(e);
			if(timesTimeout<3){
				timesTimeout++;
				setRun(false);
			}
		}

		timer.stop();
		return logger.exit(packet);
	}

	private byte[] getAcknowledge() {
		byte[] b;

		if(linkHeader!=null)
			b = Arrays.copyOf(linkHeader.asBytes(), 7);
		else
			b = new byte[3];

		int idPosition = b.length-3;
		b[idPosition] = (byte) 0xFF;

		byte[] packetId = Packet.toBytes(this.packetId);
		System.arraycopy(packetId, 0, b, ++idPosition, 2);

		return preparePacket(b);
	}

	public static byte[] preparePacket(byte[] data) {
		if(data!=null){
			byte[] p = new byte[data.length*3];
			int index = 0;
			p[index++] = FLAG_SEQUENCE;
			for(int i=0; i< data.length; i++, index ++){
				index = checkControlEscape(data, i, p, index);
			}
		
			byte[] csTmp = Packet.toBytes((short)new Checksum(data).getChecksum());
			for(int i=1; i>=0; i--, index ++)
				index = checkControlEscape(csTmp, i, p, index);

			p[index++] = FLAG_SEQUENCE;

			data = new byte[index];
			System.arraycopy(p, 0, data, 0, index);
		}
		return data;
	}

	public static int checkControlEscape(byte[] surce, int surceIndex, byte[] destination, int destinationIndex) {
		if(surce[surceIndex]==FLAG_SEQUENCE || surce[surceIndex]==CONTROL_ESCAPE){
			destination[destinationIndex++] = CONTROL_ESCAPE;
			destination[destinationIndex]	= (byte) (surce[surceIndex] ^ 0x20);
		}else
			destination[destinationIndex] = (byte) surce[surceIndex];
		return destinationIndex;
	}


	private byte[] readLinkHeader() throws SerialPortException {
		return readBytes(LinkHeader.SIZE);
	}

	private byte[] readHeader() throws SerialPortException {
		return readBytes(PacketHeader.SIZE);
	}

	private byte[] readParameterHeader() throws SerialPortException {
		return readBytes(ParameterHeader.SIZE);
	}

	public boolean isFlagSequence() throws SerialPortException {

		byte[] readBytes = readByte(2500);
		boolean isFlagSequence = readBytes!=null && readBytes[0] == Packet.FLAG_SEQUENCE;

		return isFlagSequence;
	}

	private boolean containsFlagSequence(byte[] readBytes) {
		boolean isFlagSequence = false;
		for(byte b:readBytes)
			if(b==Packet.FLAG_SEQUENCE){
				isFlagSequence = true;
				break;
			}

		return isFlagSequence;
	}

	private boolean isConfirmBytes() throws SerialPortException {

		boolean isComfirm = false;
		int ev = linkHeader!=null ? 11 : 7;
		int index = ev - 3;

		byte[] readBytes = readBytes(ev,100);
		this.isComfirm = readBytes!=null && readBytes[0]==Packet.FLAG_SEQUENCE && readBytes[readBytes.length-1]==Packet.FLAG_SEQUENCE;

		if(!this.isComfirm && readBytes!=null && linkHeader!=null && readBytes[6]==Packet.FLAG_SEQUENCE)
					linkHeader = null;

		if(this.isComfirm){
			byte[] data = Arrays.copyOfRange(readBytes, 1, index);
			if((linkHeader==null || new LinkHeader(data).equals(linkHeader)) && packetId==getPacketId(linkHeader!=null, data)){
				Checksum cs = new Checksum(data);
				byte[] b = cs.getChecksumAsBytes();
				if(b[0]==readBytes[index] && b[1]==readBytes[++index])
					isComfirm = true;
			}
		}

		return isComfirm;
	}

	public byte[] send(byte[] buffer, int waitTyme, boolean needAnswer, String waitFor) throws SerialPortException {
		logger.entry(this, buffer, waitTyme, needAnswer, waitFor);
		byte[] read = null;

		if (isOpened() && buffer != null && buffer.length > 0) {

			clear();
			writeBytes(buffer);

			if (needAnswer){
				while (!contains(read, waitFor.getBytes()) && wait(1, waitTyme)) {
					byte[] r = readBytes(getInputBufferBytesCount());

					if (read == null)
						read = r;
					else {
						int destPos = read.length;
						read = Arrays.copyOf(read, read.length + r.length);
						System.arraycopy(r, 0, read, destPos, r.length);
					}
				}
				logger.trace("{}", read);
			}else
				logger.trace("not need answer");
		}else
			logger.warn("{}; {}", this, buffer);

		return read;
	}

	private boolean contains(byte[] original, byte[] lookinFor) {
		logger.trace("\n{},{}\n", original, lookinFor);
		boolean contains = lookinFor==null;

		if (lookinFor != null && original != null) {

			for (int x = 0; !contains && x < original.length; x++) {
				for (int y = 0; y < lookinFor.length; y++)
					if (original[x + y] == lookinFor[y])
						contains = true;
					else {
						contains = false;
						break;
					}
			}
		}

		return logger.exit(contains);
	}

	private short getPacketId(boolean isLinked, byte[] data) {
		return (short) Packet.shiftAndAdd(isLinked ? Arrays.copyOfRange(data, LinkHeader.SIZE+1, LinkHeader.SIZE+3) : Arrays.copyOfRange(data, 1, 3));
	}

	@Override
	public String toString() {
		return getPortName()+" is Opened="+isOpened()+"; run="+run;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	protected void finalize() throws Throwable {
		closePort();
	}

	public byte[] clear() throws SerialPortException {
		int waitTime = 20;
		byte[] readBytes = null;
		while(wait(1, waitTime)){
			readBytes = super.readBytes(getInputBufferBytesCount());
//			String readBytesStr = ToHex.bytesToHex(readBytes);
//			Console.appendLn(readBytesStr, "Clear");
//			comPortLogger.info(marker,"?? clear: {}", readBytesStr);
			logger.trace("cleart - {}", readBytes);
			if(waitTime!=100)
				waitTime = 100;
		}
		return readBytes;
	}

	public byte[] readByte(int timeout) throws SerialPortException {
		return readBytes(1, timeout);
	}

	@Override
	public byte[] readBytes(int byteCount) throws SerialPortException {
		return readBytes(byteCount, 50);
	}

	public byte[] readBytes(int byteCount, int waitTime) throws SerialPortException {

		byte[] readBytes = null;

		int escCount = 0;
		boolean hasEsc = false;

		do{
			byte[] tmpBytes = null;

			synchronized (this) {
				if(wait(hasEsc ? escCount : byteCount, waitTime) && isOpened())
					escCount = hasEsc(tmpBytes = super.readBytes(readBytes==null ? byteCount : escCount));
			}

//			Console.appendLn(ToHex.bytesToHex(tmpBytes), "Read");

			if(hasEsc){
				if(tmpBytes!=null){
					int index = readBytes.length;
					readBytes = Arrays.copyOf(readBytes, index+tmpBytes.length);
					System.arraycopy(tmpBytes, 0, readBytes, index, tmpBytes.length);
				}
			}else
				readBytes = tmpBytes;

			if(escCount>0){
				byteCount += escCount;
				hasEsc = true;
			}

		}while(escCount>0 && readBytes!=null && readBytes.length<byteCount);

		if(hasEsc)
			readBytes = byteStuffing(readBytes);

		logger.debug(ToHex.bytesToHex(readBytes));

		return readBytes;
	}

	public byte[] readBytes(byte[] readEnd, int waitTime) throws SerialPortException {

		int count = 0;
		byte[] readBytes = null;
		boolean isEnd = false;
		byte[] tmp = null;

		
		if(waitTime<=0)
			waitTime = 1;

		do{
			try {
				synchronized (this) {
					wait(waitTime);
				}
			} catch (InterruptedException e) {
				logger.catching(e);
			}

			if(getInputBufferBytesCount()>0){

				tmp = readBytes(getInputBufferBytesCount(), waitTime);

				if(tmp!=null){
					if(readBytes==null)
						readBytes = tmp;
					else{
						int l = readBytes.length;
						int tmpLength = tmp.length;
						readBytes = Arrays.copyOf(readBytes, l+tmpLength);
						System.arraycopy(tmp, 0, readBytes, l, tmpLength);
					}

					int end = readBytes.length-readEnd.length;

					if(end>0){
						for(byte b:readEnd)
							if(readBytes[end++]==b && ++count>=readEnd.length)
								isEnd = true;
					}
				}
			}else
				tmp = null;

		}while(!isEnd && tmp!=null);

		return readBytes;
	}

	private int hasEsc(byte[] readBytes) {

		int escCount = 0;

		if(readBytes!=null)
			for(byte b:readBytes)
				if(b==Packet.CONTROL_ESCAPE)
					escCount++;

		return escCount;
	}

	private byte[] byteStuffing(byte[] readBytes) {

		int index = 0;
		if(readBytes!=null){

			for(int i=0; i<readBytes.length; i++)

				if(readBytes[i]==Packet.CONTROL_ESCAPE){
					if(++i<readBytes.length)
						readBytes[index++] = (byte) (readBytes[i]^0x20);
				}else
					readBytes[index++] = readBytes[i];
		}

		return readBytes==null ? null : index==readBytes.length ? readBytes : Arrays.copyOf(readBytes, index);
	}

	public void setRun(boolean run) {
		logger.entry(run);
		synchronized (this) {
			this.run = run;
			notify();
		}
	}

	public synchronized boolean isRun() {
		return run;
	}

	public boolean wait(int eventValue, int waitTime) throws SerialPortException {
		logger.entry(eventValue, waitTime);
		boolean isReady = false;
		long start = System.currentTimeMillis();
		long waitTimeL = waitTime*eventValue;
		long elapsedTime = 0;

		while(isOpened() && !(isReady = getInputBufferBytesCount()>=eventValue) && (elapsedTime=System.currentTimeMillis()-start)<waitTimeL && isRun()){
			synchronized (this) {

				try { wait(waitTimeL); } catch (InterruptedException e) {
					logger.catching(e);
				}

				if(isSerialPortEven)
					isSerialPortEven = false;
			}
		};
		if(isSerialPortEven)
			isSerialPortEven = false;

		logger.trace("elapsedTime={}", elapsedTime);
		return logger.exit(isReady);
	}

	@Override
	public boolean openPort() throws SerialPortException {
		logger.entry();

		boolean isOpened;

		synchronized (logger) {
			isOpened = isOpened();

			logger.debug(this);

			if (!isOpened) {
				isOpened = super.openPort();
				if (isOpened){
					addEventListener(serialPortEvent);
					setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				}
			}
		}
		setRun(isOpened);
		return logger.exit(isOpened);
	}

	@Override
	public boolean closePort() throws SerialPortException {

		boolean isClosed = !isOpened();
		logger.trace("1) Port Name={}, port is Closed={}", getPortName(), isClosed);

		setRun(false);
		synchronized (logger) {
			if (!isClosed) {
				try {
					removeEventListener();
				} catch (Exception e) {
					
					logger.catching(e);
				}
				boolean isPurged = purgePort(PURGE_RXCLEAR | PURGE_TXCLEAR | PURGE_RXABORT | PURGE_TXABORT);
				isClosed = super.closePort();
				logger.debug("2) closePort()is Closed={}, is purged={}",isClosed, isPurged);
			}
		}

		return logger.exit(isClosed);
	}

	//*** Class SerialPortEvent *****************************************************
	private class SerialPortEvent implements SerialPortEventListener{

		@Override
		public void serialEvent(jssc.SerialPortEvent serialPortEvent) {

			synchronized (ComPort.this) {
				isSerialPortEven = true;
				ComPort.this.notify();
//				Console.appendLn("", "notify");
			}
		}
		
	}

	@Override
	public void close() throws Exception {
		closePort();
	}
}
