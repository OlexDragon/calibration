package irt.gui_callibration.panels;

import irt.gui_callibration.controller.Controller;
import irt.serial_protocol.ComPort;
import irt.serial_protocol.data.ThrowException;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class ToolsPanel extends JPanel {
	private static final long serialVersionUID = 6433123493656611447L;

	private final static Logger logger = (Logger) LogManager.getLogger();

	protected static final String SERIAL_PORT = "toolsSerialPort";
	protected static final Preferences PREFS = Preferences.userRoot().node("IRT Technologies inc.");

	private JComboBox<String> comboBox;
	private JCheckBox chckbxSignalGenerator;
	private JCheckBox chckbxPowerMeter;
	private JLabel lblSgId;
	private JLabel lblPowerMeterId;
	private JTextField txtSgPower;
	private JTextField txtSgFrequency;

	public ToolsPanel(final Controller controller) {
		addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) {

				ComPort comPort = controller.getConverterPort();
				ConverterPanel.fillComboBox(comboBox, PREFS.get(SERIAL_PORT, "COM1"), comPort != null ? comPort.getPortName() : null);
			}

			public void ancestorMoved(AncestorEvent event) {
			}
			public void ancestorRemoved(AncestorEvent event) {
			}
		});
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
						synchronized (logger) {
							lblPowerMeterId.setText(controller.getPowerMeterId());
							setTitleColor(controller);
						}
						return null;
					}

				}.execute();
			}
		});

		comboBox = new JComboBox<>();
		comboBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {

						boolean comPortIsSelected = comboBox.getSelectedIndex()!=0;
						String portName = (String) comboBox.getSelectedItem();

						if(controller.setToolsPort(comPortIsSelected ? portName : null))
							PREFS.put(SERIAL_PORT, portName);

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
						synchronized (logger) {
							if (chckbxSignalGenerator.isSelected()) {
								try (ComPort comPort = controller.openToolsPort()) {
									lblSgId.setText(Controller.getSignalGeneratorId(comPort));
									setTitleColor(controller);
									String text = lblSgId.getText();
									if (text != null && !text.isEmpty()) {
										txtSgPower.setText(Controller.getSgPower(comPort));
										txtSgFrequency.setText(Controller.getSgFrequency(comPort));
									}
								}

							} else {
								lblSgId.setText("");
								txtSgPower.setText("");
								txtSgFrequency.setText("");
							}
						}
						return null;
					}
					
				}.execute();
			}
		});
		
		txtSgPower = new JTextField();
		txtSgPower.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSgPower(txtSgPower, controller);
			}
		});
		txtSgPower.setColumns(10);
		
		txtSgFrequency = new JTextField();
		txtSgFrequency.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSgFrequency(txtSgFrequency, controller);
			}
		});
		txtSgFrequency.setColumns(10);
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(chckbxSignalGenerator)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(txtSgPower, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtSgFrequency, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblSgId)
						.addComponent(chckbxPowerMeter)
						.addComponent(lblPowerMeterId))
					.addContainerGap(129, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(chckbxSignalGenerator)
						.addComponent(txtSgPower, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtSgFrequency, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblSgId)
					.addGap(18)
					.addComponent(chckbxPowerMeter)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblPowerMeterId)
					.addContainerGap(124, Short.MAX_VALUE))
		);
		gl_panel.linkSize(SwingConstants.VERTICAL, new Component[] {lblSgId, lblPowerMeterId, chckbxSignalGenerator, chckbxPowerMeter});
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

	public static void setSgPower(final JTextField txtSgPower, final Controller controller) {
		new SwingWorker<Void, Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					txtSgPower.setText(controller.setSignalGeneratorPower(txtSgPower.getText()));
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, ex.getLocalizedMessage());
					ThrowException.throwException(logger, ex);
				}
				return null;
			}
			
		}.execute();
	}

	public static void setSgFrequency(final JTextField txtSgFrequency, final Controller controller) {
		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				txtSgFrequency.setText(controller.setFrequency(txtSgFrequency.getText()));
				return null;
			}
		}.execute();
	}
}
