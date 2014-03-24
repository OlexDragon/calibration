package irt.gui_callibration.panels;

import irt.gui_callibration.controller.Controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;

import jssc.SerialPortList;

public class ToolsPanel extends JPanel {
	private static final long serialVersionUID = 6433123493656611447L;

	private JComboBox<String> comboBox;
	private JCheckBox chckbxSignalGenerator;
	private JCheckBox chckbxPowerMeter;
	private JLabel lblSgId;
	private JLabel lblPowerMeterId;

	public ToolsPanel(final Controller controller) {
		setName("Tools");
		
		lblSgId = new JLabel();
		lblSgId.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				String text = lblSgId.getText();
				controller.setSignalGenerator(text!=null && !text.isEmpty());
			}
		});
		lblPowerMeterId = new JLabel();
		lblPowerMeterId.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				String text = lblPowerMeterId.getText();
				controller.setPowerMeter(text!=null && !text.isEmpty());
			}
		});

		chckbxSignalGenerator = new JCheckBox("Signal Generator");
		chckbxPowerMeter = new JCheckBox("Power Meter");
		chckbxPowerMeter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						lblPowerMeterId.setText(controller.getPowerMeterId());
						setTitleColor(controller);
						return null;
					}

				}.execute();
			}
		});

		DefaultComboBoxModel<String> defaultComboBoxModel = new DefaultComboBoxModel<String>(SerialPortList.getPortNames());
		defaultComboBoxModel.insertElementAt("Select Serial Port", 0);
		comboBox = new JComboBox<>(defaultComboBoxModel);
		comboBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {

						boolean comPortIsSelected = comboBox.getSelectedIndex()!=0;
						String portName = (String) comboBox.getSelectedItem();

						controller.setToolsPort(comPortIsSelected ? portName : null);

						chckbxPowerMeter.setEnabled(comPortIsSelected);
						if(chckbxPowerMeter.isSelected()){
							chckbxPowerMeter.setSelected(comPortIsSelected);
							lblPowerMeterId.setText("");
						}

						chckbxSignalGenerator.setEnabled(comPortIsSelected);
						if(chckbxSignalGenerator.isSelected()){
							chckbxSignalGenerator.setSelected(comPortIsSelected);
							lblSgId.setText("");
						}

						return null;
					}
				}.execute();
			}
		});
		comboBox.setSelectedIndex(0);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		chckbxSignalGenerator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						lblSgId.setText(controller.getSignalGeneratorId());
						setTitleColor(controller);
						return null;
					}
					
				}.execute();
			}
		});
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbxSignalGenerator)
						.addComponent(lblSgId)
						.addComponent(chckbxPowerMeter)
						.addComponent(lblPowerMeterId))
					.addContainerGap(315, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(chckbxSignalGenerator)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblSgId)
					.addGap(18)
					.addComponent(chckbxPowerMeter)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblPowerMeterId)
					.addContainerGap(177, Short.MAX_VALUE))
		);
		gl_panel.linkSize(SwingConstants.VERTICAL, new Component[] {chckbxSignalGenerator, chckbxPowerMeter, lblSgId, lblPowerMeterId});
		gl_panel.linkSize(SwingConstants.HORIZONTAL, new Component[] {chckbxSignalGenerator, chckbxPowerMeter});
		panel.setLayout(gl_panel);
		setLayout(groupLayout);

	}

	private void setTitleColor(Controller controller) {
		String powerMater = lblPowerMeterId.getText();
		String signalGenerator = lblSgId.getText();

		boolean p = powerMater!=null && !powerMater.isEmpty();
		boolean s = signalGenerator!=null && !signalGenerator.isEmpty();

		if(p && s)
			controller.getLblTools().setBackground(Color.GREEN);
		else if(p || s)
			controller.getLblTools().setBackground(Color.YELLOW);
		else
			controller.getLblTools().setBackground(Color.RED);
	}
}