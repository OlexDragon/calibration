package irt.gui_callibration;

import irt.gui_callibration.controller.Controller;
import irt.gui_callibration.panels.CallibrationPanel;
import irt.gui_callibration.panels.ConverterPanel;
import irt.gui_callibration.panels.ToolsPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

public class CallibrationGui extends JFrame {
	private static final long serialVersionUID = 9152419965306044578L;

	private Controller controller = new Controller();
	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CallibrationGui frame = new CallibrationGui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public CallibrationGui() {
		setMinimumSize(new Dimension(650, 300));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		ConverterPanel converterPanel = new ConverterPanel(controller);
		tabbedPane.addTab(converterPanel.getName(), converterPanel);
		
		ToolsPanel toolsPanel = new ToolsPanel(controller);
		tabbedPane.addTab(toolsPanel.getName(), toolsPanel);
		
		CallibrationPanel callibrationPanel = new CallibrationPanel(controller);
		tabbedPane.addTab(callibrationPanel.getName(), callibrationPanel);

		List<JLabel> titles = new ArrayList<>();
		for(int i=0; i<tabbedPane.getTabCount(); i++){
			String name = tabbedPane.getTitleAt(i);
			JLabel component = new JLabel(name);
			component.setOpaque(true);
			component.setName(name);

			if(i<2)
				component.setBackground(Color.YELLOW);
			else
				component.setBackground(Color.RED);
	
			tabbedPane.setTabComponentAt(i, component);
			titles.add(component);
		}

		controller.setTitles(titles);
	}
}
