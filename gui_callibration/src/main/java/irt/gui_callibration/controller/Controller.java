package irt.gui_callibration.controller;

import irt.converter.groups.ConfigurationGroup;
import irt.converter.groups.DeviceDebugGroup;
import irt.converter.groups.DeviceDebugGroup.ADC;
import irt.converter.groups.DeviceInformationGroup;
import irt.converter.groups.Group.UnitType;
import irt.gui_callibration.CallibrationMonitor;
import irt.measurement.data.Table;
import irt.power_meter.PowerMeterWorler;
import irt.power_meter.data.EPM_441A;
import irt.prologix.communication.PrologixWorker;
import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.Range;
import irt.serial_protocol.data.RegisterValue;
import irt.serial_protocol.data.value.Enums.FalseOrTrue;
import irt.serial_protocol.data.value.ValueDouble;
import irt.serial_protocol.data.value.ValueFrequency;
import irt.signal_generator.SignalGeneratorWorker;
import irt.signal_generator.data.SG_8648;
import irt.signal_generator.data.SG_8648.OnOrOff;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jssc.SerialPortException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class Controller {

	private final Logger logger = (Logger) LogManager.getLogger();

	private ComPort 				converterComPort;
	private PrologixWorker 			prologixWorker;
	private SignalGeneratorWorker 	signalGeneratorWorker;
	private PowerMeterWorler 		powerMeterWorler;

	private UnitType unitType = UnitType.CONVERTER;
	private byte address;

	private DeviceInformationGroup deviceInformation;
	private ValueDouble valueStep;

	private int numberOfSteps;

	private boolean inputPower;
	private boolean outputPower;
	private CallibrationMonitor inputPowerMonitor;
	private CallibrationMonitor outputPowerMonitor;

	private boolean isPowerMeter;
	private boolean isSignalGenerator;

	private ValueFrequency valueFrequency;

	private JLabel lblConverter;
	private JLabel lblTools;
	private JLabel lblPower;

	public JLabel getLblConverter() {
		return lblConverter;
	}

	public JLabel getLblTools() {
		return lblTools;
	}

	public JLabel getLblPower() {
		return lblPower;
	}

	//Tools
	public synchronized void setToolsPort(String portName) {
		if(portName!=null)
			try(ComPort comPort = new ComPort(portName)) {
				prologixWorker = new PrologixWorker(comPort);
			} catch (Exception e) {
				logger.catching(e);
			}
		else{
			signalGeneratorWorker = null;
			prologixWorker = null;
		}
	}

	public ComPort openToolsPort() throws SerialPortException {
		ComPort comPort = null;

		if(prologixWorker!=null ){
			comPort = prologixWorker.getComPort();
			comPort.setRun(true);
			comPort.openPort();
		}
		return comPort;
	}

	//Converter
	public void setConverterComPort(String portName) {
		if(portName!=null)
			try {
				converterComPort = new ComPort(portName);
			} catch (SerialPortException e) {
				logger.catching(e);
			}
		else
			converterComPort = null;
	}

	public ComPort getConverterPort() {
		return converterComPort;
	}

	public ComPort openConverterPort() throws SerialPortException {
		if(converterComPort!=null){
			converterComPort.setRun(true);
			converterComPort.openPort();
		}
		return converterComPort;
	}

	public synchronized String getPowerMeterId() {
		String id = "";

		try(ComPort comPort = openToolsPort()){
			powerMeterWorler = new PowerMeterWorler(prologixWorker, new EPM_441A());
			id = powerMeterWorler.getId();
		} catch (Exception e) {
			logger.catching(e);
		}
		return id;
	}

	public synchronized String getSignalGeneratorId() {
		String id = "";

		try(ComPort comPort = openToolsPort()){
			signalGeneratorWorker = new SignalGeneratorWorker(prologixWorker, new SG_8648());
			id = signalGeneratorWorker.getId();
		} catch (Exception e) {
			logger.catching(e);
		}
		return id;
	}

	public DeviceInformationGroup getDeviceInformation(JButton btnMute, JTextField txtFrequency) {
		logger.entry();
		deviceInformation = null;

		if(converterComPort!=null)
		try(ComPort comPort = openConverterPort()){
			deviceInformation = unitType==UnitType.CONVERTER ? new DeviceInformationGroup() : new irt.buc.groups.DeviceInformationGroup(address);
			logger.trace(deviceInformation);
			deviceInformation.setUnitType(unitType);
			deviceInformation.getAll(comPort);
			lblConverter.setBackground(deviceInformation.hasAnswer() ? Color.GREEN : Color.YELLOW);

			getConverterMuteAndFrequency(converterComPort, btnMute, txtFrequency);

		} catch (Exception e) {
			deviceInformation = null;
			logger.catching(e);
		}

		return logger.exit(deviceInformation);
	}

	public void setInputPower(boolean inputPower) {
		this.inputPower = inputPower;
	}

	public void setOutputPower(boolean outputPower) {
		this.outputPower = outputPower;
	}

	public boolean isReadyForCalibration() {
		return (inputPower || outputPower) && deviceInformation!=null;
	}

	public void startCallibration() {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				if(inputPower){
					Table inputPowerTable = new Table();
					inputPowerMonitor.setTable(inputPowerTable);
				}

				if(outputPower){
					Table outputPowerTable = new Table();
					outputPowerMonitor.setTable(outputPowerTable);
				}

				try (ComPort comPort = openConverterPort()) {
					try (ComPort toolsComPort = openToolsPort()) {
						if (inputPower || outputPower) {
							boolean ip = inputPower;
							boolean op = outputPower;
							boolean beginningOfTheTest = true;
							int opStartCount = 0;

							ConfigurationGroup configurationGroup = unitType == UnitType.CONVERTER ? new ConfigurationGroup() : new irt.buc.groups.ConfigurationGroup(address);
							configurationGroup.setMute(comPort, FalseOrTrue.FALSE);
							final DeviceDebugGroup deviceDebugGroup = unitType == UnitType.CONVERTER ? new DeviceDebugGroup() : new irt.buc.groups.DeviceDebugGroup(address);
							signalGeneratorWorker.setRFOn(OnOrOff.ON);

							long inUnitValue = Long.MIN_VALUE;
							long outputRegValue = Long.MIN_VALUE;
							long tmpRegValue;
							ValueDouble sgPower = new ValueDouble(0, 1);
							ValueDouble pmPower = new ValueDouble(0, 1);
							ValueDouble backupSgPower = new ValueDouble(0, 1);
							sgPower.setValue(signalGeneratorWorker.getPower());
							backupSgPower.setValue(sgPower.getValue());

							for (int i = 0; i < numberOfSteps && (ip || op); i++) {
								Thread.sleep(1000);
								//Input Power

							    Future<String> powerMetter = getPowerMetter(op, comPort);
							    Future<RegisterValue> inputPowerDetector = getPowerDetector(ip, comPort, deviceDebugGroup, ADC.INPUT_POWER);

							    if(inputPowerDetector!=null){
							    	RegisterValue inputAdcRegister = inputPowerDetector.get();
									tmpRegValue = inputAdcRegister.getValue().getValue();
							    	logger.trace("outputAdcRegister={}", inputAdcRegister);
									if(tmpRegValue > inUnitValue){
										inUnitValue = tmpRegValue;
										inputPowerMonitor.setRowValues(inUnitValue, sgPower.getDouble());
									}else
										ip = false;
							    }

							    Future<RegisterValue> outputPowerDetector = getPowerDetector(op, comPort, deviceDebugGroup, ADC.OUTPUT_POWER);
							    if(outputPowerDetector!=null && powerMetter!=null){
							    	RegisterValue outputAdcRegister = outputPowerDetector.get();
									tmpRegValue = outputAdcRegister.getValue().getValue();
							    	logger.trace("outputAdcRegister={}", outputAdcRegister);
									boolean put = Double.compare(tmpRegValue, outputRegValue)>0;

									if(put || beginningOfTheTest){

										if (beginningOfTheTest)
											if (!put) {
												opStartCount = 0;
												outputPowerMonitor.clearTable();
											} else if (opStartCount > 2)
												beginningOfTheTest = false;
											else
												opStartCount++;

										outputRegValue = tmpRegValue;

										String power = powerMetter.get();
										logger.trace("beginningOfTheTest={}, put={}, opStartCount={}; Power Meter = {}", beginningOfTheTest, put, opStartCount, power);
										pmPower.setValue(power);
										
										if(put)
											outputPowerMonitor.setRowValues(outputRegValue, pmPower.getDouble());
									}else{
										if(op){
											configurationGroup.setMute(comPort, FalseOrTrue.TRUE);
											op = false;
										}
									}
							    }

								sgPower.add(valueStep.getValue());
								signalGeneratorWorker.setPower(sgPower.getValue());
							}

							configurationGroup.setMute(comPort, FalseOrTrue.TRUE);
							signalGeneratorWorker.setRFOn(OnOrOff.OFF);
							signalGeneratorWorker.setPower(backupSgPower.getValue());
						}
					}
				} catch (Exception e) {
					logger.catching(e);
				}
			}

			private Future<String> getPowerMetter(boolean isOutputPower, ComPort comPort) {

				Future<String> future = null;
				if (isOutputPower) {
					ExecutorService executor = Executors.newSingleThreadExecutor();
					Callable<String> callable = new Callable<String>() {
						@Override
						public String call() throws Exception {
							return powerMeterWorler.measure();
						}
					};
					future = executor.submit(callable);
				}
				return future;
			}

			protected Future<RegisterValue> getPowerDetector(boolean isInputPower, final ComPort comPort, final DeviceDebugGroup deviceDebugGroup, final ADC adc) {

				Future<RegisterValue> future = null;
				if (isInputPower) {
					ExecutorService executor = Executors.newSingleThreadExecutor();
					Callable<RegisterValue> callable = new Callable<RegisterValue>() {
						@Override
						public RegisterValue call() throws Exception {
							return deviceDebugGroup.getADCRegister(comPort, adc, 5);
						}
					};
					future = executor.submit(callable);
				}
				return future;
			}
		});
	int priority = t.getPriority();
		if(priority>Thread.MIN_PRIORITY)
			t.setPriority(priority-1);
		t.setDaemon(true);
		t.start();
	}

	public synchronized String getSgFrequency() {
		String freq = "";
		if(signalGeneratorWorker!=null)
			try(ComPort comPort = openToolsPort()){
				freq = signalGeneratorWorker.getFrequency();
			} catch (Exception e) {
				logger.catching(e);
			}
		return freq;
	}

	public String setFrequency(String text) {
		String freq = "";
		if(signalGeneratorWorker!=null)
			try(ComPort comPort = openToolsPort()){
				freq = signalGeneratorWorker.setFrequency(text);
			} catch (Exception e) {
				logger.catching(e);
			}
		return freq;
	}

	public String setSignalGeneratorStartPower(String text) {
		String power = "";
		if(signalGeneratorWorker!=null)
			try(ComPort comPort = openToolsPort()){
				if(text!=null && !text.replaceAll("\\D", "").isEmpty())
					power = signalGeneratorWorker.setPower(text);
				else
					power = signalGeneratorWorker.setPower(-1000);
			} catch (Exception e) {
				logger.catching(e);
			}
		return power;
	}

	public synchronized String getSgPower() {
		String power = "";
		if(signalGeneratorWorker!=null)
			try(ComPort comPort = openToolsPort()){
				power = signalGeneratorWorker.getPower();
			} catch (Exception e) {
				logger.catching(e);
			}
		return power;
	}

	public void setInputPowerMonitor(CallibrationMonitor inputPowerMonitor) {
		this.inputPowerMonitor = inputPowerMonitor;
	}

	public void setOutputPowerMonitor(CallibrationMonitor outputPowerMonitor) {
		this.outputPowerMonitor = outputPowerMonitor;
	}

	public void setSteps(String numberOfSteps, String oneStep_DB) {
		valueStep = new ValueDouble(1, 1, 100, 1);// min step = 0.1 dB, max = 10 dB
		valueStep.setValue(oneStep_DB);
		this.numberOfSteps = Integer.parseInt(numberOfSteps);
	}

	public boolean isInputPower() {
		return inputPower;
	}

	public boolean isOutputPower() {
		return outputPower;
	}

	public void getConverterMuteAndFrequency(ComPort comPort, JButton btnMute, JTextField txtFrequency) {
			ConfigurationGroup configurationGroup = unitType==UnitType.CONVERTER ? new ConfigurationGroup() : new irt.buc.groups.ConfigurationGroup(address) ;

			FalseOrTrue mute = configurationGroup.getMute(comPort);
			if(mute!=null){
				if(mute==FalseOrTrue.FALSE)
					btnMute.setText("Mute");
				else
					btnMute.setText("Unmute");
			}else
				btnMute.setText("N/A");

			Range range = configurationGroup.getFrequencyRange(comPort);
			if(range!=null){
				Long frequency = configurationGroup.getFrequency(comPort);
				if(frequency!=null){
					valueFrequency = new ValueFrequency(frequency, range.getMaximum(), range.getMaximum());
					txtFrequency.setText(valueFrequency.toString());
				}else
					txtFrequency.setText("N/A");
			}
			logger.trace("Mute = {}, Frequency={}", mute, valueFrequency);
	}

	public FalseOrTrue setConverterMute(FalseOrTrue falseOrTrue) {
		FalseOrTrue mute = null;
		try(ComPort comPort = openConverterPort()){
			ConfigurationGroup configurationGroup = unitType==UnitType.CONVERTER ? new ConfigurationGroup() : new irt.buc.groups.ConfigurationGroup(address);
			mute = configurationGroup.setMute(comPort, falseOrTrue);
		} catch (Exception e) {
			logger.catching(e);
		}
		return mute;
	}

	public void setPowerMeter(boolean isPowerMeter) {
		this.isPowerMeter = isPowerMeter;
	}

	public void setSignalGenerator(boolean isSignalGenerator) {
		this.isSignalGenerator = isSignalGenerator;
	}

	public boolean isPowerMeter() {
		return isPowerMeter;
	}

	public boolean isSignalGenerator() {
		return isSignalGenerator;
	}

	public void setTitles(List<JLabel> titles) {
		for(JLabel l:titles)
			switch(l.getName()){
			case "Converter":
				lblConverter = l;
				break;
			case "Tools":
				lblTools = l;
				break;
			case "Power":
				lblPower = l;
			}
	}

	public UnitType getUnitType() {
		return unitType;
	}

	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
		lblConverter.setText(unitType.name());
	}

	public void setAddress(byte address) {
		this.address = address;
	}
}
