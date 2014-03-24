package irt.gui_callibration.panels;

import irt.gui_callibration.controller.Controller;
import irt.serial_protocol.data.value.Enums.FalseOrTrue;
import irt.unit.groups.DeviceInformationGroup;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import jssc.SerialPortList;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class ConverterPanel extends JPanel {
	private static final long serialVersionUID = 2092539378659121768L;

	private final Logger logger = (Logger) LogManager.getLogger();

	private final ButtonGroup buttonGroup = new ButtonGroup();


	private JLabel lblSerialNumber;
	private JLabel lblUnitName;
	private JLabel lblPartNumber;
	private JLabel lblFirmwareVersion;
	private JLabel lblFirmwareBuildDate;
	private JLabel lblCounter;

	private JRadioButton rdbtnConverter;
	private JRadioButton rdbtnBuc;

	private JComboBox<String> comboBox;
	private JButton btnMute;
	private JTextField txtFrequency;

	public ConverterPanel(final Controller controller) {
		addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) {
				fillInfo(controller);
			}
			public void ancestorMoved(AncestorEvent event) {
			}
			public void ancestorRemoved(AncestorEvent event) {
			}
		});
		setName("Converter");
		
		lblSerialNumber 		= new JLabel();
		lblUnitName 			= new JLabel();
		lblPartNumber 			= new JLabel();
		lblFirmwareVersion 		= new JLabel();
		lblFirmwareBuildDate	= new JLabel();
		lblCounter 				= new JLabel();

		rdbtnConverter 	= new JRadioButton("Converter");
		rdbtnConverter.setEnabled(false);
		rdbtnBuc 		= new JRadioButton("BUC");
		rdbtnBuc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fillInfo(controller);
			}
		});

		DefaultComboBoxModel<String> defaultComboBoxModel = new DefaultComboBoxModel<String>(SerialPortList.getPortNames());
		defaultComboBoxModel.insertElementAt("Select Serial Port", 0);
		comboBox = new JComboBox<>(defaultComboBoxModel);
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(final ItemEvent e) {

				if(e.getStateChange()==ItemEvent.SELECTED)
					new SwingWorker<Void, Void>() {

						@Override
						protected Void doInBackground() throws Exception {

							boolean comPortIsSelected = comboBox.getSelectedIndex() != 0;
							String portName = (String) comboBox.getSelectedItem();

							controller.setConverterComPort(comPortIsSelected ? portName : null);

							rdbtnConverter.setEnabled(comPortIsSelected);
							if (rdbtnConverter.isSelected())
								rdbtnConverter.setSelected(comPortIsSelected);

							// TODO rdbtnBuc.setEnabled(comPortIsSelected);
							if (rdbtnBuc.isSelected())
								rdbtnBuc.setSelected(comPortIsSelected);

							if(comPortIsSelected)
								fillInfo(controller);
							else
								clearInfo();

							return null;
						}

					}.execute();
			}
		});
		comboBox.setSelectedIndex(0);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		
		rdbtnConverter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fillInfo(controller);
			}
		});
		buttonGroup.add(rdbtnConverter);
		
		rdbtnBuc.setEnabled(false);
		buttonGroup.add(rdbtnBuc);

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(rdbtnConverter)
						.addComponent(rdbtnBuc))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(rdbtnConverter)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(rdbtnBuc)))
					.addContainerGap())
		);
		
		JLabel lblSerialNumberTxt = new JLabel("Serial Number:");
		lblSerialNumberTxt.setHorizontalAlignment(SwingConstants.RIGHT);

		JLabel lblUnitNameTxt = new JLabel("Unit Name:");
		lblUnitNameTxt.setHorizontalAlignment(SwingConstants.RIGHT);

		JLabel lblPartNumberTxt = new JLabel("Part Number:");
		lblPartNumberTxt.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblFirmwareVersionTxt = new JLabel("Firmware Version:");
		lblFirmwareVersionTxt.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblFirmwareBuildDateTxt = new JLabel("Firmware Build Date:");
		lblFirmwareBuildDateTxt.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblCounterTxt = new JLabel("Counter:");
		lblCounterTxt.setHorizontalAlignment(SwingConstants.RIGHT);

		btnMute = new JButton("Mute");
		btnMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Void, Void>(){

					@Override
					protected Void doInBackground() throws Exception {
						boolean m = btnMute.getText().equals("Mute");

						if(controller.setConverterMute(m ? FalseOrTrue.TRUE :FalseOrTrue.FALSE)==FalseOrTrue.TRUE)
							btnMute.setText("Unmute");
						else
							btnMute.setText("Mute");

						return null;
					}
				}.execute();
			}
		});
		
		txtFrequency = new JTextField();
		txtFrequency.setColumns(10);
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
							.addComponent(lblFirmwareVersionTxt, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
							.addGroup(gl_panel.createSequentialGroup()
								.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING, false)
									.addComponent(lblPartNumberTxt, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(lblUnitNameTxt, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(lblSerialNumberTxt, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(lblFirmwareBuildDateTxt, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(lblCounterTxt, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
									.addComponent(lblSerialNumber)
									.addComponent(lblUnitName)
									.addComponent(lblPartNumber)
									.addComponent(lblFirmwareVersion)
									.addComponent(lblFirmwareBuildDate)
									.addComponent(lblCounter))))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(btnMute)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtFrequency, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(192, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSerialNumberTxt)
						.addComponent(lblSerialNumber))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUnitNameTxt)
						.addComponent(lblUnitName))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPartNumberTxt)
						.addComponent(lblPartNumber))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFirmwareVersionTxt)
						.addComponent(lblFirmwareVersion))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFirmwareBuildDateTxt)
						.addComponent(lblFirmwareBuildDate))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCounterTxt)
						.addComponent(lblCounter))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnMute)
						.addComponent(txtFrequency, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(122, Short.MAX_VALUE))
		);
		gl_panel.linkSize(SwingConstants.HORIZONTAL, new Component[] {lblSerialNumberTxt, lblUnitNameTxt, lblPartNumberTxt, lblFirmwareVersionTxt, lblFirmwareBuildDateTxt});
		panel.setLayout(gl_panel);
		setLayout(groupLayout);
	}

	private void clearInfo(){
		fillInfo(null, null, null, null, null, null);
	}

	private void fillInfo(DeviceInformationGroup deviceInformation) {
		logger.entry(deviceInformation);
		if(deviceInformation!=null && deviceInformation.hasAnswer())
			fillInfo(deviceInformation.getSerialNumber(),
					deviceInformation.getUnitName(),
					deviceInformation.getPartNumber(),
					deviceInformation.getFirmwareVersion(),
					deviceInformation.getFirmwareBuildDate(),
					deviceInformation.getUptimeCounter());
		else
			clearInfo();
	}

	private void fillInfo(String serialNumber, String unitName, String partNumber, String FirmwareVersion, String firmwareBuildDate, String counter){
		logger.entry(serialNumber, unitName, partNumber, FirmwareVersion, firmwareBuildDate, counter);
		lblSerialNumber		.setText(serialNumber);
		lblUnitName			.setText(unitName);
		lblPartNumber		.setText(partNumber);
		lblFirmwareVersion	.setText(FirmwareVersion);
		lblFirmwareBuildDate.setText(firmwareBuildDate);
		lblCounter			.setText(counter);
	}

	private void fillInfo(final Controller controller) {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				logger.entry(controller);

				if(rdbtnConverter.isSelected())
					fillInfo(controller.getDeviceInformation(btnMute, txtFrequency));
				else
					clearInfo();

				logger.exit();
				return null;
			}
		}.execute();
	}
}
