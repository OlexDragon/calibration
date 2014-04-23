package irt.gui_callibration;

import irt.gui_callibration.controller.Controller;
import irt.gui_callibration.panels.CallibrationPanel;
import irt.gui_callibration.panels.ConverterPanel;
import irt.gui_callibration.panels.ToolsPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class CallibrationGui extends JFrame {
	private static final long serialVersionUID = 9152419965306044578L;

	private static final Logger logger = (Logger) LogManager.getLogger();

	public static final Preferences PREFS = Preferences.userRoot().node("IRT Technologies inc.");

	private Controller controller = new Controller(this);
	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CallibrationGui frame = new CallibrationGui();
					frame.setVisible(true);
				} catch (Exception e) {
					logger.catching(e);
				}
			}
		});
	}

	public CallibrationGui() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				int extendedState = getExtendedState();
				PREFS.putInt("ExtendedState", extendedState);
				if(extendedState==Frame.NORMAL){
					Rectangle bounds = getBounds();
					PREFS.putInt("x", bounds.x);
					PREFS.putInt("y", bounds.y);
					PREFS.putInt("width", bounds.width);
					PREFS.putInt("height", bounds.height);
				}
			}
		});

		setMinimumSize(new Dimension(650, 300));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

		int extendedState = PREFS.getInt("ExtendedState", Frame.NORMAL);
		setExtendedState(extendedState);
		setBounds(PREFS.getInt("x", 100), PREFS.getInt("y", 100), PREFS.getInt("width", 400), PREFS.getInt("height", 100));
	}
}
