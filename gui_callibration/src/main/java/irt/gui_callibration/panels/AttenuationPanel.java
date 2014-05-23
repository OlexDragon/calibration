package irt.gui_callibration.panels;

import irt.converter.ConverterWorker;
import irt.converter.groups.DeviceDebugGroup;
import irt.converter.groups.DeviceDebugGroup.ConverterADC;
import irt.converter.groups.Group.MuteStatus;
import irt.gui_callibration.CallibrationGui;
import irt.gui_callibration.controller.Controller;
import irt.gui_callibration.workers.NumberWorker;
import irt.power_meter.PowerMeterWorker;
import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.RegisterValue;
import irt.serial_protocol.data.ThrowException;
import irt.serial_protocol.data.value.Value;
import irt.serial_protocol.data.value.ValueDouble;
import irt.signal_generator.SignalGeneratorWorker;
import irt.signal_generator.data.SG_8648.OnOrOff;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class AttenuationPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private final Logger logger = (Logger) LogManager.getLogger();
	public static final Preferences PREFS = CallibrationGui.PREFS;

	private static final String ATTENUATION_STEP = "attenuationStep";
	private static final String MIN_POWER = "min_power";
	private static final String MAX_POWER = "max_power";
	private static final String OFFSET = "offset";

	private static final ConverterWorker CONVERTER_WORKER = new ConverterWorker();
	private static final DeviceDebugGroup DEVICE_DEBUG_GROUP = CONVERTER_WORKER.getDeviceDebugGroup();

	private JTextField txtSgFrequency;
	private JTextField txtSgPower;
	private JTextField txtPowerMeter;
	private JTextField txtStep;
	private JTextField txtGain;

	public static ConverterADC adc;
	public static String zeroAttenuationGain;
	public static JTextArea textArea;

	private int attenuationStep;

	private JTextField txtDac1;
	private JTextField txtDac2;
	private JTextField txtMaxOutputPower;
	private JTextField txtMinOutputPower;
	private JButton btnStartCallibration;

	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton rbtnDAC1;
	private JRadioButton rbtnDAC2;

	private ValueDouble tmpValueDouble = (ValueDouble) new ValueDouble(0, 1).setPrefix(" dBm");

	private Window owner;
	private JButton btnMute;
	private JTextField txtFrequency;
	private JButton btnGetGain;
	private JTextField txtOffset;

	private static boolean running;

	public AttenuationPanel(Window owner, final Controller controller) {
		setName("Attenuation");
		this.owner = owner;

		addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) {
				fillAll(controller);
			}
			public void ancestorMoved(AncestorEvent event) {
			}
			public void ancestorRemoved(AncestorEvent event) {
			}
		});

		JLabel lblSgFrequency = new JLabel("SG Frequency");
		lblSgFrequency.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblSgPower = new JLabel("SG Power");
		lblSgPower.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txtSgFrequency = new JTextField();
		txtSgFrequency.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Void, Void>(){

					@Override
					protected Void doInBackground() throws Exception {
						txtSgFrequency.setText(controller.setFrequency(txtSgFrequency.getText()));
						return null;
					}
					
				}.execute();
			}
		});
		txtSgFrequency.setColumns(10);
		
		txtSgPower = new JTextField();
		txtSgPower.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ToolsPanel.setSgPower(txtSgPower, controller);
			}
		});
		txtSgPower.setColumns(10);
		
		JLabel lblPowerMeter = new JLabel("Power Meter");
		lblPowerMeter.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txtPowerMeter = new JTextField();
		txtPowerMeter.setEditable(false);
		txtPowerMeter.setColumns(10);

		JLabel lblStep = new JLabel("Step");
		lblStep.setHorizontalAlignment(SwingConstants.RIGHT);

		txtStep = new JTextField();
		txtStep.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				getStep(txtStep.getText());
			}
		});
		txtStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getStep(txtStep.getText());
			}
		});
		txtStep.setText(""+(attenuationStep = PREFS.getInt(ATTENUATION_STEP, 100)));
		txtStep.setColumns(10);

		JScrollPane scrollPane = new JScrollPane();

		btnStartCallibration = new JButton("Start Callibration");
		btnStartCallibration.setEnabled(false);
		btnStartCallibration.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Void, Void>(){

					@Override
					protected Void doInBackground() throws Exception {
						if(running){
							JOptionPane.showMessageDialog(AttenuationPanel.this.owner, "Program has not finished the previous job.");
							return null;
						}

						new AttenuationCalibrationDialog(AttenuationPanel.this.owner, controller);
						return null;
					}
					
				}.execute();

				controller.startAttenuationCallibration(txtMinOutputPower.getText(), txtMaxOutputPower.getText(), attenuationStep);
			}
		});

		JLabel lblGain = new JLabel("Gain");
		lblGain.setHorizontalAlignment(SwingConstants.RIGHT);

		txtGain = new JTextField();
		txtGain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setZeroAttenuationGain(txtGain.getText());
			}
		});
		txtGain.setColumns(10);

		rbtnDAC1 = new JRadioButton("DAC 1");
		rbtnDAC1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				adc = ConverterADC.DAC1;
				if(btnGetGain.isEnabled())
					btnStartCallibration.setEnabled(true);
			}
		});
		buttonGroup.add(rbtnDAC1);
		rbtnDAC1.setToolTipText("<html>L to RF<br />RF to L</html>");

		rbtnDAC2 = new JRadioButton("DAC 2");
		rbtnDAC2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adc = ConverterADC.DAC2;
				if(btnGetGain.isEnabled())
					btnStartCallibration.setEnabled(true);
			}
		});
		buttonGroup.add(rbtnDAC2);
		rbtnDAC2.setToolTipText("<html>IF to L<br />L to IF</html>");
		
		txtDac1 = new JTextField();
		txtDac1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDAC(controller, txtDac1, ConverterADC.DAC1);
			}
		});
		txtDac1.setColumns(10);
		
		txtDac2 = new JTextField();
		txtDac2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDAC(controller, txtDac2, ConverterADC.DAC2);
			}
		});
		txtDac2.setColumns(10);
		
		JLabel lblMaxOutputPower = new JLabel("Max.Out.Power");
		lblMaxOutputPower.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblMinOutputPower = new JLabel("Min.Out.Power");
		lblMinOutputPower.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txtMaxOutputPower = new JTextField();
		txtMaxOutputPower.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String maxPowerStr = tmpValueDouble.setValue(txtMaxOutputPower.getText()).toString();
				txtMaxOutputPower.setText(maxPowerStr);
				PREFS.put(MAX_POWER, maxPowerStr);
			}
		});
		txtMaxOutputPower.setText(PREFS.get(MAX_POWER, "0"));
		txtMaxOutputPower.setColumns(10);
		
		txtMinOutputPower = new JTextField();
		txtMinOutputPower.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long maxPower = tmpValueDouble.setValue(txtMaxOutputPower.getText()).getValue();
				long minPower = tmpValueDouble.setValue(txtMinOutputPower.getText()).getValue();
				if(maxPower-minPower<10){
					tmpValueDouble.setValue(maxPower-10);
				}
				String minPowerStr = tmpValueDouble.toString();
				txtMinOutputPower.setText(minPowerStr);
				PREFS.put(MIN_POWER, minPowerStr);
			}
		});
		txtMinOutputPower.setText(PREFS.get(MIN_POWER, "-10"));
		txtMinOutputPower.setColumns(10);
		
		btnMute = new JButton("Unmute");
		btnMute.setMargin(new Insets(2, 2, 2, 2));
		btnMute.setEnabled(false);
		btnMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConverterPanel.muteButtonAction(btnMute, controller);
			}
		});
		
		JLabel lblFrequency = new JLabel("Frequency");
		lblFrequency.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txtFrequency = new JTextField();
		txtFrequency.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConverterPanel.setFrequency(txtFrequency, controller);
			}
		});
		txtFrequency.setColumns(10);
		
		btnGetGain = new JButton("Get Gain");
		btnGetGain.setMargin(new Insets(2, 2, 2, 2));
		btnGetGain.setEnabled(false);
		btnGetGain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fillAll(controller);
			}
		});
		
		txtOffset = new JTextField(PREFS.get(OFFSET, "0"));
		txtOffset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PREFS.put(OFFSET, txtOffset.getText());
			}
		});
		txtOffset.setToolTipText("Offset");
		txtOffset.setColumns(10);

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSgFrequency, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblPowerMeter, Alignment.TRAILING)
								.addComponent(lblStep, Alignment.TRAILING)
								.addComponent(lblGain, Alignment.TRAILING)
								.addComponent(rbtnDAC1, Alignment.TRAILING)
								.addComponent(rbtnDAC2, Alignment.TRAILING)
								.addComponent(lblFrequency, Alignment.TRAILING)
								.addComponent(lblMaxOutputPower, Alignment.TRAILING)
								.addComponent(lblMinOutputPower, Alignment.TRAILING))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(txtSgFrequency, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtPowerMeter, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtStep, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtGain, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtDac1, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtDac2, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtFrequency, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtMaxOutputPower, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtMinOutputPower, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)))
						.addComponent(btnStartCallibration)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnGetGain)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnMute))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(lblSgPower)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtSgPower, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtOffset, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblSgFrequency)
								.addComponent(txtSgFrequency, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(txtOffset, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtSgPower, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblSgPower))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblPowerMeter)
								.addComponent(txtPowerMeter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblStep)
								.addComponent(txtStep, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblGain)
								.addComponent(txtGain, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnStartCallibration)
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(rbtnDAC1)
								.addComponent(txtDac1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(rbtnDAC2)
								.addComponent(txtDac2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblFrequency)
								.addComponent(txtFrequency, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnMute)
								.addComponent(btnGetGain))
							.addGap(24)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblMaxOutputPower)
									.addGap(9))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(txtMaxOutputPower, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(txtMinOutputPower, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(3)
									.addComponent(lblMinOutputPower)))))
					.addContainerGap())
		);
		groupLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {lblSgFrequency, lblPowerMeter, lblStep, lblGain, lblFrequency});
		groupLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {lblMaxOutputPower, lblMinOutputPower});
		groupLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {txtSgFrequency, txtPowerMeter, txtStep, txtGain, txtDac1, txtDac2, txtMaxOutputPower, txtMinOutputPower, txtFrequency});
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		setLayout(groupLayout);
		
	}

	private void getStep(String text) {
		attenuationStep = NumberWorker.stringToUnsignedInt(text, 100);
		PREFS.putInt(ATTENUATION_STEP, attenuationStep);
		txtStep.setText(""+attenuationStep);
	}

	//Serial Port should by opened
	private void setGain(final Controller controller) throws Exception {
		if(controller.isPowerMeter() && controller.isSignalGenerator()) {

			SignalGeneratorWorker signalGeneratorWorker = controller.getSignalGeneratorWorker();

			ValueDouble powerMeterMeasurement = getPowerMeterMeasurement(signalGeneratorWorker, controller.getPowerMeterWorker());
			txtPowerMeter.setText(powerMeterMeasurement.toString());

			ValueDouble power = signalGeneratorWorker.getSignalGenerator().getValuePower();
			power.setPrefix(" dBm");
			powerMeterMeasurement.subtract(power.getValue());

			ValueDouble offset = new ValueDouble(0, 1);
			offset.setPrefix(" dB");
			String offs = offset.setValue(txtOffset.getText()).toString();
			txtOffset.setText(offs);
			PREFS.put(OFFSET, offs);
			powerMeterMeasurement.subtract(offset.getValue());

			String string = powerMeterMeasurement.toString();
			txtGain.setText(string);
			setZeroAttenuationGain(string);
		}
	}

	//Serial Port should by opened
	private ValueDouble getPowerMeterMeasurement(SignalGeneratorWorker signalGeneratorWorker, PowerMeterWorker powerMeterWorker) throws Exception {

		ValueDouble pmPower = new ValueDouble(0, 1);

		signalGeneratorWorker.setRFOn(OnOrOff.ON);

		Thread.sleep(500);
		pmPower.setValue(powerMeterWorker.measure());

		signalGeneratorWorker.setRFOn(OnOrOff.OFF);

		return pmPower;
	}

	private void setZeroAttenuationGain(String string) {

		if(string!=null && !string.isEmpty())
			zeroAttenuationGain = "zero-attenuation-gain\t"+string;
		else
			zeroAttenuationGain = "";

		setTextArea();
	}

	private void setDAC(final Controller controller, final JTextField txtDac, final ConverterADC adc) {
		new SwingWorker<RegisterValue, Void>(){

			@Override
			protected RegisterValue doInBackground() throws Exception {
				RegisterValue dac = null;
				try (ComPort unitComPort = controller.openConverterPort()) {

					String text = txtDac.getText();
					
					if (text != null){
						int indexOfDot = text.indexOf('.');
						if(indexOfDot>0)
							text = text.substring(0, indexOfDot);

						if(!(text = text.replaceAll("\\D", "")).isEmpty()) {
							dac = DEVICE_DEBUG_GROUP.getADCRegister(
																unitComPort,
																adc.setValue(
																		new Value(Long.parseLong(text), 0, 4096, 0)));
						}
					}
				} catch (Throwable ex) {
					ThrowException.throwException(logger, ex);
				}
				return dac;
			}

			@Override
			protected void done() {

				try {
					RegisterValue dac = get();
					if (dac != null) {
						Value value = dac.getValue();
						txtDac.setText(value.toString());
					}
				} catch (InterruptedException | ExecutionException ex) {
					ThrowException.throwException(logger, ex);
				}
			}
			
		}.execute();
	}

	private void fillAll(final Controller controller) {
		new SwingWorker<Void, Void>() {


			@Override
			protected Void doInBackground() throws Exception {
				if(running){
					JOptionPane.showMessageDialog(owner, "Program has not finished the previous job.");
					return null;
				}

				running = true;
				boolean isSignalGenerator = controller.isSignalGenerator();

				try (ComPort toolsComPort = controller.openToolsPort()) {
					if (toolsComPort != null) {
						if (isSignalGenerator) {
							txtSgPower.setText(Controller.getSgPower(toolsComPort));
							txtSgFrequency.setText(Controller.getSgFrequency(toolsComPort));
						}


						if (controller.getDeviceInformation() != null) {

							try(ComPort unitComPort = controller.openConverterPort()){

								RegisterValue dac1 = DEVICE_DEBUG_GROUP.getADCRegister(unitComPort, ConverterADC.DAC1);
								if(dac1!=null){
									Value value = dac1.getValue();
									txtDac1.setText(value.toString());
								}
								RegisterValue dac2 = DEVICE_DEBUG_GROUP.getADCRegister(unitComPort, ConverterADC.DAC2);
								if(dac2!=null){
									Value value = dac2.getValue();
									txtDac2.setText(value.toString());
								}

								controller.setMute(unitComPort, MuteStatus.UNMUTED);
								setGain(controller);

								controller.getConverterMuteAndFrequency(unitComPort, btnMute, txtFrequency);
								txtSgPower.setEnabled(isSignalGenerator);
								txtSgFrequency.setEnabled(isSignalGenerator);
								btnGetGain.setEnabled(isSignalGenerator);
								btnMute.setEnabled(isSignalGenerator);
								if(rbtnDAC1.isSelected() || rbtnDAC2.isSelected())
									btnStartCallibration.setEnabled(true);
							}
						}
					}
				}
				running = false;
				return null;
			}
		}.execute();
	}

	public static void setTextArea() {
		textArea.setText(zeroAttenuationGain);
	}
}
