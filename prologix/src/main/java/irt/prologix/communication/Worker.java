package irt.prologix.communication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import irt.prologix.communication.Tools.Commands;
import irt.prologix.data.PrologixGpibUsbController.DeviceType;
import irt.prologix.data.PrologixGpibUsbController.Eos;
import irt.prologix.data.PrologixGpibUsbController.FalseOrTrue;

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

		if(prologixWorker.getAddr()!=addr)
			prologixWorker.setAddr(addr);
	}

	public String getId() throws Exception {
		logger.entry();
		checkAddress();
		byte[] read = getPrologixWorker().sendCommand(Commands.ID.getCommand(), true, Eos.CR_LF, 1000);
		return logger.exit(read!=null ? new String(read) : null);
	}

	public abstract byte getAddr();
}
