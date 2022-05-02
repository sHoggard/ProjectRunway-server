package server;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * AdminConsole is a console window that can be hidden. 
 * 
 * @author Vilhelm Sassi, Sebastian Hoggard, Daniel Petersén
 *
 */
@SuppressWarnings("serial")
public class AdminConsole extends MovableWindow {
	private JTextArea taConsole = new JTextArea();
	private OutputDuplicator outputDuplicator = new OutputDuplicator(this);
	private JScrollPane scroll;
	
	public AdminConsole() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		taConsole.addMouseMotionListener(this);
		taConsole.addMouseListener(this);
		scroll = new JScrollPane(taConsole);
		scroll.setPreferredSize(new Dimension(700,300));
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		panel.add(scroll);
		add(panel);
		setResizable(true);
		setUndecorated(true);
		pack();
		setVisible(false);
		setLocationRelativeTo(null);
	}

	/**
	 * Toggles the console's visibility on or off. 
	 * 
	 * @return
	 */
	public boolean toggleConsole() {
		if (isVisible()) {
			setVisible(false);
		} else {
			setVisible(true);
		}
		return isVisible();
	}

	/**
	 * Outputs given text in the console. 
	 * 
	 * @param str
	 */
	public void setText(String str) {
		taConsole.append(str);
	}

}
