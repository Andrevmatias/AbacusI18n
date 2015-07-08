package org.tux.bagleyd.abacus.learn;

/*
 * @(#)TestDialog.java
 *
 * Copyright 2011 - 2014  David A. Bagley, bagleyd@tux.org
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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.tux.bagleyd.abacus.AbacusApplet;
import org.tux.bagleyd.abacus.AbacusInterface;
import org.tux.bagleyd.util.ColumnLayout;
import org.tux.bagleyd.util.Icon;

/**
 * The <code>TestDialog</code> class holds the
 * <code>AbacusTest</code>.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */

public class TestDialog extends JDialog {
	private static final long serialVersionUID = 42L;
	static boolean active = false;
	AbacusApplet abacusApplet;
	AbacusTest abacusTest = null;
	JLabel boxLabel = null;
	JComboBox<Object> testComboBox = null;
	JLabel labelLabel = null;
	JTextField questionTextField = null;
	JButton button = null;
	JLabel percentValueLabel = null, percentDescriptionLabel;
	JLabel timerValueLabel = null, timerDescriptionLabel;
	int percentDone;
	int minutes;
	AbacusInterface.TestState state = AbacusInterface.TestState.start;
	CountdownTimer timer;
	Panel messagePanel = new Panel();
	Panel labelPanel = new Panel();
	Panel percentPanel = new Panel();
	Panel timerPanel = new Panel();
	Panel testPanel = new Panel();

	public TestDialog(Frame parent, AbacusApplet applet, AbacusTest test,
			String titleText, String[] tests, Icon icon,
			int min) {
		super(parent, titleText, false);
		abacusApplet = applet;
		abacusTest = test;
		minutes = min;
		if (icon != null) {
			messagePanel.add(icon);
		}
		testPanel.setLayout(new ColumnLayout(0, 10, 5,
			ColumnLayout.LEFT));
		messagePanel.add(testPanel);
		add(messagePanel, BorderLayout.LINE_START);
		setupStart(tests);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (abacusTest != null)
					if (state == AbacusInterface.TestState.start) {
						String string = questionTextField.getText().replaceAll("\\W+", "_");
						if (string == null || string.length() == 0) {
							button.setEnabled(false);
						} else {
							state = AbacusInterface.TestState.exam;
							abacusTest.returnStrings(testComboBox.getSelectedItem().toString(), string);
						}
					} else if (state == AbacusInterface.TestState.exam) {
						abacusTest.returnStrings(abacusApplet.getValue(), timer.getString());
					} else {
						dispose();
						System.exit(0);
					}
			}
		});
		questionTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (state == AbacusInterface.TestState.start) {
					String string = questionTextField.getText().replaceAll("\\W+", "_");
					if (string != null && string.length() != 0) {
						button.setEnabled(true);
					}
				}
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
				System.exit(0);
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

	public void setupStart(String[] tests) {
		boxLabel = new JLabel("Choose a test");
		testPanel.add(boxLabel);
		testComboBox = new JComboBox<Object>(tests);
		testPanel.add(testComboBox);
		labelLabel = new JLabel("Enter Your Name:");
		questionTextField = new JTextField(15);
		questionTextField.setEnabled(true);
		labelPanel.add(labelLabel);
		labelPanel.add(questionTextField);
		testPanel.add(labelPanel);
		button = new JButton("Start Test");
		testPanel.add(button);
		button.setEnabled(false);
	}

	public void setupExam() {
		boxLabel.setText("Currently taking");
		boxLabel.setEnabled(false);
		testComboBox.setEnabled(false);
		questionTextField.setEditable(false);
		button.setText("Record Answer");
		if (percentValueLabel == null) {
			percentPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
			percentValueLabel = new JLabel("0%");
			percentPanel.add(percentValueLabel);
			percentDescriptionLabel = new JLabel("done");
			percentPanel.add(percentDescriptionLabel);
			testPanel.add(percentPanel);
		}
		if (timerValueLabel == null) {
			timerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
			timerValueLabel = new JLabel(Integer.toString(minutes) + ":00");
			timer = new CountdownTimer(this, minutes);
			timerPanel.add(timerValueLabel);
			timerDescriptionLabel = new JLabel("left");
			timerPanel.add(timerDescriptionLabel);
			testPanel.add(timerPanel);
		}
	}

	public void setupFinish() {
		boxLabel.setText("Just finished");
		boxLabel.setEnabled(false);
		testComboBox.setEnabled(false);
		questionTextField.setEditable(false);
		button.setText("Done");
		testPanel.remove(percentPanel);
		testPanel.remove(timerPanel);
	}

	public void showTestLabel(String msg) {
		if (state == AbacusInterface.TestState.exam) {
			setupExam();
			labelLabel.setText(msg);
		}
	}

	public void showTestQuestion(String msg) {
		if (state == AbacusInterface.TestState.exam)
			questionTextField.setText(msg);
	}

	public void showTestPercentValue(String msg) {
		percentValueLabel.setText(msg);
	}

	public void showTestGrade(int numberCorrect, int numberQuestions,
			int grade, String gradeString) {
		state = AbacusInterface.TestState.finish;
		setupFinish();
		labelLabel.setText("Number Correct: " + numberCorrect +
			" of " + numberQuestions);
		questionTextField.setText("Grade : " + grade + " (" +
			gradeString + ")");
	}

	public static synchronized boolean getActive() {
		return TestDialog.active;
	}

	public void finishTimer() {
		abacusTest.returnStrings(abacusApplet.getValue(),
			timer.getString());
	}

	public void updateTimer(String string) {
		timerValueLabel.setText(string);
		if ("00:00".equals(string))
			finishTimer();
	}
}
