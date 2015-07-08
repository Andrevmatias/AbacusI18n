package org.tux.bagleyd.abacus.learn;

/*
 * @(#)DemoDialog.java
 *
 * Structure from Java Sourcebook by Ed Anuff, pp 287-289
 *
 * Copyright 2003 - 2014  David A. Bagley, bagleyd@tux.org
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.SwingUtilities;

import org.tux.bagleyd.abacus.AbacusApplet;
import org.tux.bagleyd.util.MultiLineLabel;
import org.tux.bagleyd.util.Icon;
/**
 * The <code>DemoDialog</code> class holds the
 * <code>AbacusDemo</code>.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */

public class DemoDialog extends Dialog {
	private static final long serialVersionUID = 42L;
	static boolean active = false;
	AbacusApplet abacusApplet;
	MultiLineLabel lessonText;

	public DemoDialog(Frame parent, AbacusApplet applet,
			String titleText, String messageText,
			Icon icon) {
		super(parent, titleText, true);
		lessonText = new MultiLineLabel(messageText, true);
		abacusApplet = applet;
		Panel messagePanel = new Panel();
		if (icon != null) {
			messagePanel.add(icon);
		}
		if (messageText != null) {
			messagePanel.add(lessonText);
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
				abacusApplet.processKey('q');
			}
		});
		// this solves the problem where the dialog was not getting
		// focus the second time it was displayed
		SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			requestFocusInWindow();
		}
		});
		pack();
	}

	public void showDemoMessage(String msg) {
		lessonText.setLabel(msg);
	}

	public static synchronized boolean getActive() {
		return DemoDialog.active;
	}
}
