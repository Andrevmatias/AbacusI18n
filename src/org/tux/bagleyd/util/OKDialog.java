package org.tux.bagleyd.util;

/*
 * @(#)OKDialog.java
 *
 * Mostly from Java Sourcebook
 * by Ed Anuff, pp 287-289
 */

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 * The <code>OKDialog</code> class is a  <code>Dialog</code>
 * with an OK button.
 *
 * @author	Ed Anuff
 */

public class OKDialog extends Dialog {
	private static final long serialVersionUID = 42L;
	private static boolean active = false;

	public OKDialog(JFrame parent,
			String titleText, String messageText,
			String buttonText, Icon icon, boolean scroll) {
		super(parent, titleText, true);
		if (messageText != null) {
			if (scroll) {
				JTextArea textArea = new JTextArea(messageText, 0, 60);
				textArea.setLineWrap(true);
				textArea.setWrapStyleWord(true);
				JScrollPane scrollPane = new JScrollPane(
					textArea,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				add(scrollPane, BorderLayout.CENTER);
				if (icon != null)
					add(icon, BorderLayout.LINE_START);
			} else {
				Panel messagePanel = new Panel();
				if (icon != null)
					messagePanel.add(icon,
						BorderLayout.LINE_START);
				messagePanel.add(new
					MultiLineLabel(messageText),
					BorderLayout.CENTER);
				add(messagePanel, BorderLayout.LINE_START);
			}
		}
		JButton button = new JButton(buttonText);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(button);
		add("South", buttonPanel);
		pack();
		setActive(true);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				dispose();
				//else multiple OKDialogs can be opened
				setActive(false);
			}
		});
		addWindowListener(new WindowListener() {
			public void windowClosed(WindowEvent event) {
				// unused
			}
			public void windowIconified(WindowEvent event) {
				// unused
			}
			public void windowDeiconified(WindowEvent event) {
				// unused
			}
			public void windowActivated(WindowEvent event) {
				// unused
			}
			public void windowDeactivated(WindowEvent event) {
				// unused
			}
			public void windowOpened(WindowEvent event) {
				// unused
			}
			public void windowClosing(WindowEvent event) {
				dispose();
				setActive(false);
			}
		});
	}

	public static boolean getActive() {
		return OKDialog.active;
	}

	public static void setActive(boolean value) {
		active = value;
	}
}
