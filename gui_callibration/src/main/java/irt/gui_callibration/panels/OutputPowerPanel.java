package irt.gui_callibration.panels;

import irt.converter.groups.Group.UnitType;
import irt.gui_callibration.controller.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

public class OutputPowerPanel extends PowerPanelAbstract{
	private static final long serialVersionUID = 1721161264577099385L;

	private JTextField txtMultiplier;
	private JCheckBox chckbxMultiplier;

	public OutputPowerPanel(Controller controller) {
		super(controller, "Output");
		
		chckbxMultiplier = new JCheckBox("Multiplier:");
		chckbxMultiplier.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!chckbxMultiplier.isSelected())
					table.setMultiplier(0);
			}
		});
		
		txtMultiplier = new JTextField();
		txtMultiplier.setText("10.4");
		txtMultiplier.setColumns(3);

		GroupLayout groupLayout = new GroupLayout(pnlExtra);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(chckbxMultiplier)
					.addComponent(txtMultiplier, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
					.addComponent(chckbxMultiplier)
					.addComponent(txtMultiplier, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		pnlExtra.setLayout(groupLayout);
	}

	@Override
	protected String getToolsName() {
		return "PM:";
	}

	@Override
	protected String powerLutEntry() {
		return unitType==UnitType.CONVERTER ? "out-power-lut-entry" : "power-lut-entry";
	}

	@Override
	protected String powerLutSize() {
		return unitType==UnitType.CONVERTER ? "out-power-lut-size" : "power-lut-size";
	}

	@Override
	protected void setPrecision() {
		if(chckbxMultiplier.isSelected())
			table.setMultiplier(txtMultiplier.getText());
		super.setPrecision();
	}
}
