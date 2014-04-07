package irt.gui_callibration.panels;

import irt.converter.groups.Group.UnitType;
import irt.gui_callibration.CallibrationMonitor;
import irt.gui_callibration.controller.Controller;
import irt.measurement.data.Table;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;

public abstract class PowerPanelAbstract extends JPanel implements CallibrationMonitor {
	private static final long serialVersionUID = 1L;

	private JLabel lblSgValue;
	private JLabel lblUnitValue;
	private JTextArea textArea;
	private JLabel lblPrecision;
	private JTextField txtPrecision;
	protected JPanel pnlExtra;

	protected Table table;

	protected UnitType unitType;

	public PowerPanelAbstract(Controller controller, String title) {
		setBorder(new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP, null, null));

		unitType = controller.getUnitType();

		JLabel lblSg = new JLabel(getToolsName());
		lblSgValue = new JLabel();
		JLabel lblUnit = new JLabel("Unit:");
		lblUnitValue = new JLabel();

		JScrollPane scrollPane = new JScrollPane();
		textArea = new JTextArea();
		
		lblPrecision = new JLabel("Precision(%):");
		
		txtPrecision = new JTextField();
		txtPrecision.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPrecision();
			}
		});
		txtPrecision.setColumns(10);
		
		JButton btnRecalculate = new JButton("Recalculate");
		btnRecalculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPrecision();
			}

		});
		btnRecalculate.setMargin(new Insets(0, 0, 0, 0));
		
		pnlExtra = new JPanel();

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, 0, 0, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblPrecision)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtPrecision, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnRecalculate, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblSg)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblSgValue)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblUnit)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblUnitValue)
							.addComponent(pnlExtra)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblSg, Alignment.TRAILING)
						.addComponent(lblSgValue, Alignment.TRAILING)
						.addComponent(lblUnit, Alignment.TRAILING)
						.addComponent(lblUnitValue, Alignment.TRAILING)
						.addComponent(pnlExtra, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPrecision)
						.addComponent(txtPrecision, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnRecalculate))
					.addGap(9)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE))
		);
		scrollPane.setViewportView(textArea);
		setLayout(groupLayout);
	}

	protected abstract String getToolsName();

	@Override
	public void setTable(Table table) {
		this.table = table;
		if(table!=null){
			table.setLutSizeName(powerLutSize());
			table.setLutValueName(powerLutEntry());
		}
	}

	protected abstract String powerLutEntry();

	protected abstract String powerLutSize();

	@Override
	public void setRowValues(final double key, final double value) {

		new SwingWorker<Void, Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				lblSgValue.setText(""+value);
				lblUnitValue.setText(""+key);

				table.add(key, value);
				textArea.setText(table.toString());
				return null;
			}
			
		}.execute();
	}

	protected void setPrecision() {
		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				String text = txtPrecision.getText();
				if(table!=null && text!=null && !(text = text.replaceAll("[^\\d.]", "")).isEmpty())
					table.setAccuracy(Double.parseDouble(text));
				
				textArea.setText(table.toString());
				return null;
			}
		}.execute();
	}

	@Override
	public void clearTable() {
		table.clear();
	}
}
