package irt.gui_callibration.panels;

import irt.buc.BucWorker.OutoutPowerDetectorSource;
import irt.converter.groups.Group.UnitType;
import irt.gui_callibration.controller.Controller;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class CallibrationPanel extends JPanel {
	private static final String START_CALLIBRATION = "Start Callibration";

	private static final long serialVersionUID = -8155863148837162482L;

	private final Logger logger = (Logger) LogManager.getLogger();

	private JButton button;
	private JTextField txtSgPower;
	private JLabel lblStep;
	private JTextField txtOneStep;
	private JLabel lblSgFreq;
	private JTextField txtSqFreq;
	private JTextField txtSteps;

	private OutputPowerPanel outputPowerPanel;
	private InputPowerPanel inputPowerPanel;
	private JPanel panel;
	private JCheckBox chckbxInputPower;
	private JCheckBox chckbxOutputPower;
	private JComboBox<OutoutPowerDetectorSource> comboBox;

	public CallibrationPanel(final Controller controller) {
		setName("Power");

		addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) {
				new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						boolean readyForCalibration = controller.isSignalGenerator();
						txtSgPower.setEnabled(readyForCalibration);
						txtSqFreq.setEnabled(readyForCalibration);
						chckbxInputPower.setEnabled(readyForCalibration && controller.getUnitType()==UnitType.CONVERTER);
						chckbxOutputPower.setEnabled(controller.isPowerMeter());
						comboBox.setVisible(controller.getUnitType()==UnitType.BUC);

						if(readyForCalibration){
							txtSgPower.setText(controller.getSgPower());
							txtSqFreq.setText(controller.getSgFrequency());
						}

						inputPowerPanel.setVisible(controller.isInputPower());
						outputPowerPanel.setVisible(controller.isOutputPower());
						return null;
					}
				}.execute();
			}
			public void ancestorMoved(AncestorEvent event) {
			}
			public void ancestorRemoved(AncestorEvent event) {
			}
		});

		button = new JButton(START_CALLIBRATION);
		button.setEnabled(false);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (button.getText().equals(START_CALLIBRATION)) {
					txtSqFreq.setText(controller.setFrequency(txtSqFreq.getText()));
					txtSgPower.setText(controller.setSignalGeneratorStartPower(txtSgPower.getText()));
					controller.setSteps(txtSteps.getText(), txtOneStep.getText());
					final Thread callibrationThread = controller.startCallibration();
					button.setText("Stop");

					new SwingWorker<Void, Void>() {

						@Override
						protected Void doInBackground() throws Exception {

							callibrationThread.join();
							button.setText(START_CALLIBRATION);
							return null;
						}

					}.execute();
				}else{
					controller.stop();
				}
			}
		});
		button.setMargin(new Insets(0, 0, 0, 0));
		
		JLabel lblSgPower = new JLabel("SG Power:");
		
		txtSgPower = new JTextField();
		txtSgPower.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Void, Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						try{
							txtSgPower.setText(controller.setSignalGeneratorStartPower(txtSgPower.getText()));
						}catch(Exception ex){
							JOptionPane.showMessageDialog(null, ex.getLocalizedMessage());
							logger.catching(ex);
						}
						return null;
					}
					
				}.execute();
			}
		});
		txtSgPower.setColumns(10);
		
		lblStep = new JLabel("Step(dB):");
		
		txtOneStep = new JTextField();
		txtOneStep.setText("1");
		txtOneStep.setColumns(10);
		
		lblSgFreq = new JLabel("SG Freq:");
		lblSgFreq.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txtSqFreq = new JTextField();
		txtSqFreq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtSqFreq.setText(controller.setFrequency(txtSqFreq.getText()));
			}
		});
		txtSqFreq.setColumns(10);
		
		JLabel lblSteps = new JLabel("Steps:");
		lblSteps.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txtSteps = new JTextField();
		txtSteps.setText("100");
		txtSteps.setColumns(10);
		
		panel = new JPanel();
		
		chckbxInputPower = new JCheckBox("Input");
		chckbxInputPower.setEnabled(false);
		chckbxInputPower.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Void, Void>(){

					@Override
					protected Void doInBackground() throws Exception {
						boolean selected = chckbxInputPower.isSelected();
						controller.setInputPower(selected);
						inputPowerPanel.setVisible(selected);
						button.setEnabled(selected || chckbxInputPower.isSelected());
						return null;
					}
					
				}.execute();
			}
		});
		
		chckbxOutputPower = new JCheckBox("Output");
		chckbxOutputPower.setEnabled(false);
		chckbxOutputPower.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Void, Void>(){

					@Override
					protected Void doInBackground() throws Exception {
						boolean selected = chckbxOutputPower.isSelected();
						controller.setOutputPower(selected);
						outputPowerPanel.setVisible(selected);
						button.setEnabled(selected || chckbxInputPower.isSelected());
						return null;
					}
					
				}.execute();
			}
		});
		
		DefaultComboBoxModel<OutoutPowerDetectorSource> defaultComboBoxModel = new DefaultComboBoxModel<OutoutPowerDetectorSource>(OutoutPowerDetectorSource.values());
		comboBox = new JComboBox<>(defaultComboBoxModel);
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					controller.setOutoutPowerDetectorSource((OutoutPowerDetectorSource)comboBox.getSelectedItem());
				}
			}
		});
		comboBox.setVisible(false);
		comboBox.setSelectedIndex(1);

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSgPower)
								.addComponent(lblSgFreq))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(txtSqFreq, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(lblStep))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(txtSgPower, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(lblSteps, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
							.addGap(13)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(txtOneStep, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtSteps, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(chckbxInputPower, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(chckbxOutputPower, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addComponent(button, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED, 130, Short.MAX_VALUE))
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSgFreq)
						.addComponent(txtSqFreq, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblStep)
						.addComponent(txtOneStep, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(button))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSgPower)
						.addComponent(txtSgPower, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblSteps)
						.addComponent(txtSteps, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(chckbxInputPower)
						.addComponent(chckbxOutputPower)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(8)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE))
		);
		groupLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {lblSgPower, lblSgFreq});
		
		inputPowerPanel = new InputPowerPanel(controller);
		inputPowerPanel.setVisible(false);
		
		controller.setInputPowerMonitor(inputPowerPanel);
		outputPowerPanel = new OutputPowerPanel(controller);
		outputPowerPanel.setVisible(false);
		controller.setOutputPowerMonitor(outputPowerPanel);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addComponent(inputPowerPanel, 0, 0, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(outputPowerPanel, 0, 0, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addComponent(inputPowerPanel, 0, 0, Short.MAX_VALUE)
				.addComponent(outputPowerPanel, 0, 0, Short.MAX_VALUE)
		);
		panel.setLayout(gl_panel);
		setLayout(groupLayout);
		
	}
}
