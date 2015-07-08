package org.tux.bagleyd.abacus.learn;

/*
 * @(#)TeachDialog.java
 *
 * Structure from Java Sourcebook by Ed Anuff, pp 287-289
 *
 * Copyright 2009 - 2014  David A. Bagley, bagleyd@tux.org
 *
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and
 * its documentation for any purpose and without fee is hereby granted,
 * provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in
 * supporting documentation, and that the name of the author not be
 * used in advertising or publicity pertaining to distribution of the
 * software without specific, written prior permission.
 *
 * This program is distributed in the hope that it will be "useful",
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.tux.bagleyd.abacus.Abacus;
import org.tux.bagleyd.abacus.AbacusApplet;
import org.tux.bagleyd.abacus.AbacusInterface;
import org.tux.bagleyd.util.ColumnLayout;
import org.tux.bagleyd.util.Icon;

/**
 * The <code>TeachDialog</code> class holds the
 * teach portion of Abacus.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */

public class TeachDialog extends Dialog {
	private static final long serialVersionUID = 42L;
	static boolean active = false;
	AbacusApplet abacusApplet;
	AbacusTeach abacusTeach;
	Abacus abacus;
	JTextField valueTextField = new JTextField("");
	JLabel[] teachText = new JLabel[3];

	public TeachDialog(Frame parent, AbacusApplet applet, Abacus abacus,
			String titleText, String msg,
			int line, Icon icon) {
		super(parent, titleText, true);
		final String big ="                                                                                                                                                                                                             ";

		if (line == 0)
			teachText[0] = new JLabel(msg + big);
		else if (teachText[0] == null)
			teachText[0] = new JLabel(big);
		if (line == 1)
			teachText[1] = new JLabel(msg + big);
		else if (teachText[1] == null)
			teachText[1] = new JLabel(big);
		if (line == 2)
			teachText[2] = new JLabel(msg + big);
		else if (teachText[2] == null)
			teachText[2] = new JLabel(big);
		this.abacusApplet = applet;
		this.abacus = abacus;
		abacusTeach = new AbacusTeach(applet);
		abacusTeach.reset();

		Panel messagePanel = new Panel();
		if (icon != null) {
			messagePanel.add(icon, BorderLayout.LINE_START);
		}
		Panel textPanel = new Panel();
		textPanel.setLayout(new ColumnLayout(0, 0, 0,
			ColumnLayout.LEFT));
		if (valueTextField != null) {
			valueTextField.setColumns(50);
			textPanel.add(valueTextField, BorderLayout.PAGE_START);
		}
		messagePanel.add(textPanel, BorderLayout.CENTER);
		if (teachText[0] != null) {
			textPanel.add(teachText[0], BorderLayout.PAGE_START);
		}
		if (teachText[1] != null) {
			textPanel.add(teachText[1], BorderLayout.PAGE_START);
		}
		if (teachText[2] != null) {
			textPanel.add(teachText[2], BorderLayout.PAGE_START);
		}
		add(messagePanel, BorderLayout.LINE_START);
		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {
				abacusApplet.processKey(event.getKeyChar());
			}
			public void keyReleased(KeyEvent event) {
				// unused
			}
			public void keyTyped(KeyEvent event) {
				// unused
			}
		});
		valueTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				teachStep(valueTextField.getText());
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
				//abacusApplet.processKey('q');
				teachText[0] = null;
				teachText[1] = null;
				teachText[2] = null;
				abacusApplet.killTeachDialog();
			}
		});
		// this solves the problem where the dialog was not getting
		// focus the second time it was displayed
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				requestFocusInWindow();
			}
		});
		//setSize(getPreferredSize());
		pack();
		//setSize(getMinimumSize());
	}

	void teachStep(String teachBuffer) {
		abacusTeach.teachStep(abacus, teachBuffer,
			AbacusInterface.PRIMARY);
	}

	public void showTeachMessage(String msg, int line) {
		//teachText[line].setSize(getPreferredSize());
		teachText[line].setText(msg);
	}

	public static synchronized boolean getActive() {
		return TeachDialog.active;
	}
}
