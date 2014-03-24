package irt.prologix.communication;

import irt.prologix.communication.Tools.Commands;
import irt.prologix.data.PrologixGpibUsbController.DeviceType;
import irt.prologix.data.PrologixGpibUsbController.Eos;
import irt.serial_protocol.data.value.Enums.FalseOrTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public abstract class Worker {

	protected Logger logger = (Logger) LogManager.getLogger(getClass().getName());

	private PrologixWorker prologixWorker;

	public Worker(PrologixWorker prologixWorker) {
		this.prologixWorker = prologixWorker;
		if (prologixWorker != null) {
			try {

				if (prologixWorker.getSaveConfig() != FalseOrTrue.FALSE)
					prologixWorker.setSaveConfig(FalseOrTrue.FALSE);

				if (prologixWorker.getMode() != DeviceType.CONTROLLER)
					prologixWorker.setMode(DeviceType.CONTROLLER);

				if (prologixWorker.isReadAfterWrite()!=FalseOrTrue.TRUE)
					prologixWorker.setReadAfterWrite(FalseOrTrue.TRUE);

			} catch (Exception e) {
				logger.catching(e);
			}
		}
	}


	public PrologixWorker getPrologixWorker() {
		return prologixWorker;
	}

	protected void checkAddress() throws Exception {
		byte addr = getAddr();

		Byte a = prologixWorker.getAddr();
		logger.trace("addr={}; a={}", addr, a);

		if(a!=null){
			if(a!=addr)
				prologixWorker.setAddr(addr);
		}else
			logger.warn("can not get address");
	}

	public String getId() throws Exception {
		logger.entry();
		checkAddress();
		byte[] read = getPrologixWorker().sendCommand(Commands.ID, true, Eos.LF, 2000);
		return logger.exit(read!=null ? new String(read) : null);
	}

	public abstract byte getAddr();
}
