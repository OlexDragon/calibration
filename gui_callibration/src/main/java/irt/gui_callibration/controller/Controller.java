package irt.gui_callibration.controller;

import irt.buc.BucWorker;
import irt.buc.BucWorker.OutoutPowerDetectorSource;
import irt.buc.groups.DeviceDebugGroup.BucADC;
import irt.converter.ConverterWorker;
import irt.converter.groups.ConfigurationGroup;
import irt.converter.groups.DeviceDebugGroup;
import irt.converter.groups.DeviceDebugGroup.ADCInterface;
import irt.converter.groups.DeviceDebugGroup.ConverterADC;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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

	private OutoutPowerDetectorSource outoutPowerDetectorSource = OutoutPowerDetectorSource.OUTPUT_DEVICE_CURRENT;

	private JFrame owner;

	private boolean running;

	public Controller(JFrame owner) {
		this.owner = owner;
	}

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
				JOptionPane.showMessageDialog(owner, e.getLocalizedMessage());
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
				JOptionPane.showMessageDialog(owner, e.getLocalizedMessage());
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
			JOptionPane.showMessageDialog(owner, e.getLocalizedMessage());
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
			JOptionPane.showMessageDialog(owner, e.getLocalizedMessage());
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
			JOptionPane.showMessageDialog(owner, e.getLocalizedMessage());
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

	public Thread startCallibration() {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				logger.debug("Start Callibration. inputPower={}, outputPower={}", inputPower, outputPower);

				running = true;
				if(inputPower){
					Table inputPowerTable = new Table();
					inputPowerMonitor.setTable(inputPowerTable);
				}

				if(outputPower){
					Table outputPowerTable = new Table();
					outputPowerMonitor.setTable(outputPowerTable);
				}

				try (ComPort comPort = openConverterPort(); ComPort toolsComPort = openToolsPort()) {

					if (inputPower || outputPower) {
						boolean ip = inputPower;
						boolean op = outputPower;
						boolean ipBeginningOfTheTest = true;
						int ipStartCount = 0;
						boolean opBeginningOfTheTest = true;
						int opStartCount = 0;

						ConverterWorker converterWorker = unitType == UnitType.CONVERTER ? new ConverterWorker() : new BucWorker(address);
						converterWorker.setMute(comPort, false);
						signalGeneratorWorker.setRFOn(OnOrOff.ON);

						long inUnitValue = Long.MIN_VALUE;
						long outputRegValue = Long.MIN_VALUE;
						long tmpRegValue;
						ValueDouble sgPower = new ValueDouble(0, 1);
						ValueDouble pmPower = new ValueDouble(0, 1);
						ValueDouble backupSgPower = new ValueDouble(0, 1);
						sgPower.setValue(signalGeneratorWorker.getPower());
						backupSgPower.setValue(sgPower.getValue());

						ADCInterface outputAdc = null;
						if (unitType == UnitType.CONVERTER)

							outputAdc = ConverterADC.OUTPUT_POWER;
						else
							switch (outoutPowerDetectorSource) {
							case OUTPUT_DEVICE_CURRENT:
								outputAdc = BucADC.DEVICE_CURRENT_1_AVERAGE;
								break;
							default:
								logger.warn("TODO - {}", outoutPowerDetectorSource);// TODO
							}

						for (int i = 0; running && i < numberOfSteps && (ip || op); i++) {
							if(unitType==UnitType.BUC && op)
								Thread.sleep(2000);

							Future<String> powerMetter = getPowerMetter(op, comPort);

							// Input Power
							Future<RegisterValue> inputPowerDetector = getCurrent(ip, comPort, converterWorker.getDeviceDebugGroup(), ConverterADC.INPUT_POWER);

							inputPower(inputPowerDetector);
							if (inputPowerDetector != null) {
								RegisterValue inputAdcRegister = inputPowerDetector.get();
								tmpRegValue = inputAdcRegister.getValue().getValue();
								logger.debug("outputAdcRegister.getValue()={}, sgPower={}", tmpRegValue, sgPower);
								boolean put = Double.compare(tmpRegValue, inUnitValue) > 0;

								if (put || ipBeginningOfTheTest) {

									if (ipBeginningOfTheTest)
										if (!put) {
											ipStartCount = 0;
											inputPowerMonitor.clearTable();
										} else if (ipStartCount > 3)
											ipBeginningOfTheTest = false;
										else
											ipStartCount++;

									inUnitValue = tmpRegValue;
									inputPowerMonitor.setRowValues(inUnitValue, sgPower.getDouble());
								} else
									ip = false;
							}

							// Output Power
							if (outputAdc != null) {
								Future<RegisterValue> outputCurrentDetector = getCurrent(op, comPort, converterWorker.getDeviceDebugGroup(), outputAdc);
								if (outputCurrentDetector != null && powerMetter != null) {

									tmpRegValue = outputCurrentDetector.get().getValue().getValue();
									logger.trace("outputAdcRegister.getValue()={}", tmpRegValue);

									boolean put = Double.compare(tmpRegValue, outputRegValue) > 0;
									if (put || opBeginningOfTheTest) {

										if (opBeginningOfTheTest)
											if (!put) {
												opStartCount = 0;
												outputPowerMonitor.clearTable();
											} else if (opStartCount > 3)
												opBeginningOfTheTest = false;
											else
												opStartCount++;

										outputRegValue = tmpRegValue;

										String power = powerMetter.get();
										logger.debug("\n\toutputRegValue={};\n\tPower Meter = {};\n\tbeginningOfTheTest={}, put={}, opStartCount={}", outputRegValue, power, opBeginningOfTheTest, put, opStartCount);
										pmPower.setValue(power);

										if (put)
											outputPowerMonitor.setRowValues(outputRegValue, pmPower.getDouble());

									} else {
										if (op) {
											converterWorker.setMute(comPort, true);
											op = false;
										}
									}
								}
							}

							sgPower.add(valueStep.getValue());
							signalGeneratorWorker.setPower(sgPower.getValue());
						}

						converterWorker.setMute(comPort, true);
						signalGeneratorWorker.setRFOn(OnOrOff.OFF);
						signalGeneratorWorker.setPower(backupSgPower.getValue());
					}
				} catch (Exception e) {
					logger.catching(e);
					JOptionPane.showMessageDialog(owner, e.getLocalizedMessage());
				}
				logger.debug("Callibration Ends.");
			}

			private void inputPower(Future<RegisterValue> inputPowerDetector) {
				// TODO Auto-generated method stub
				
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

			protected Future<RegisterValue> getCurrent(boolean doMeasurement, final ComPort comPort, final DeviceDebugGroup deviceDebugGroup, final ADCInterface adc) {
				logger.entry(doMeasurement, comPort, deviceDebugGroup, adc);

				Future<RegisterValue> future = null;
				if (doMeasurement) {

					ExecutorService executor = Executors.newSingleThreadExecutor();
					Callable<RegisterValue> callable = new Callable<RegisterValue>() {
						@Override
						public RegisterValue call() throws Exception {
							return deviceDebugGroup.getADCRegister(comPort, adc, unitType==UnitType.BUC ? 30 : 10);
						}
					};
					future = executor.submit(callable);
				}
				return logger.exit(future);
			}
		});
	int priority = t.getPriority();
		if(priority>Thread.MIN_PRIORITY)
			t.setPriority(priority-1);
		t.setDaemon(true);
		t.start();

		return t;
	}

	public synchronized String getSgFrequency() {
		String freq = "";
		if(signalGeneratorWorker!=null)
			try(ComPort comPort = openToolsPort()){
				freq = signalGeneratorWorker.getFrequency();
			} catch (Exception e) {
				logger.catching(e);
				JOptionPane.showMessageDialog(owner, e.getLocalizedMessage());
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
				JOptionPane.showMessageDialog(owner, e.getLocalizedMessage());
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
				JOptionPane.showMessageDialog(owner, e.getLocalizedMessage());
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
				JOptionPane.showMessageDialog(owner, e.getLocalizedMessage());
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
			mute = setMute(comPort, falseOrTrue);
		} catch (Exception e) {
			logger.catching(e);
			JOptionPane.showMessageDialog(owner, e.getLocalizedMessage());
		}
		return mute;
	}

	public FalseOrTrue setMute(ComPort comPort, FalseOrTrue falseOrTrue) {
		FalseOrTrue mute;
		ConfigurationGroup configurationGroup = unitType==UnitType.CONVERTER ? new ConfigurationGroup() : new irt.buc.groups.ConfigurationGroup(address);
		mute = configurationGroup.setMute(comPort, falseOrTrue);
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

		if(lblConverter!=null)
			lblConverter.setText(unitType.name());
	}

	public void setAddress(byte address) {
		this.address = address;
	}

	public void setOutoutPowerDetectorSource(OutoutPowerDetectorSource outoutPowerDetectorSource) {
		this.outoutPowerDetectorSource = outoutPowerDetectorSource;
	}

	public void stop() {
		running = false;
	}
}
