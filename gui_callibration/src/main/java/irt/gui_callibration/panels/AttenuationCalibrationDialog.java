package irt.gui_callibration.panels;

import irt.gui_callibration.controller.Controller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class AttenuationCalibrationDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private final Logger logger = (Logger) LogManager.getLogger();

	private final JPanel contentPanel = new JPanel();

	private JLabel lblAttenuation;

	private JLabel lblPmValue;

	private JLabel lblDacValue;

	public AttenuationCalibrationDialog(Window owner, final Controller controller) {
		super(owner, ModalityType.DOCUMENT_MODAL);
		addHierarchyListener(new HierarchyListener() {
			public void hierarchyChanged(HierarchyEvent e) {
				if(e.getChangeFlags()==HierarchyEvent.SHOWING_CHANGED && !isVisible())
					controller.stop();
			}
		});

		controller.setDialog(this);

		setResizable(false);

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JLabel lblDacValueTxt = new JLabel("DAC Value:");
		lblDacValueTxt.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lblDacValue 	= new JLabel();
		lblPmValue 		= new JLabel();
		lblAttenuation 	= new JLabel();

		JLabel lblPmValueTxt = new JLabel("PM Value:");
		lblPmValueTxt.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblAttenuationTxt = new JLabel("Attenuation:");
		lblAttenuationTxt.setHorizontalAlignment(SwingConstants.RIGHT);
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblAttenuationTxt)
						.addComponent(lblPmValueTxt)
						.addComponent(lblDacValueTxt))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblDacValue)
						.addComponent(lblPmValue)
						.addComponent(lblAttenuation))
					.addContainerGap(311, Short.MAX_VALUE))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDacValueTxt)
						.addComponent(lblDacValue))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPmValueTxt)
						.addComponent(lblPmValue))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAttenuationTxt)
						.addComponent(lblAttenuation))
					.addContainerGap(164, Short.MAX_VALUE))
		);
		gl_contentPanel.linkSize(SwingConstants.HORIZONTAL, new Component[] {lblDacValueTxt, lblPmValueTxt, lblAttenuationTxt});
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Stop");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
		setVisible(true);

	}

	public void setDacValue(String text) {
		lblDacValue.setText(text);
	}

	public void setPMValue(String text) {
		lblPmValue.setText(text);
	}

	public void setAttenuation(String text) {
		lblAttenuation.setText(text);
	}
}
