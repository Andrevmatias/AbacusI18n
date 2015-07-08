package org.tux.bagleyd.util;

/*
 * @(#)Slider.java
 *
 * Mostly from Graphic Java Mastering the JFC Volume 1: AWT
 * 3rd Edition by David M. Geary, Example 15-2 pp 544-545
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The <code>Slider</code> class combines a JSlider and a Label
 * in one widget.
 *
 * @author David M. Geary
 */

public class Slider extends JPanel {
	private static final long serialVersionUID = 42L;
	JSlider	slider;
	JLabel	label;
	String string;
	int inc = 1;
	private static final int DEF_WIDTH = 104;
	private static final int DEF_HEIGHT = 40;
	int width = DEF_WIDTH, height = DEF_HEIGHT;

	public Slider(String labelString, int initialValue,
			int min, int max, int pixelWidth) {
		width = pixelWidth;
		string = "  " + labelString;
		label = new JLabel((initialValue * inc) + string,
			SwingConstants.CENTER);
		label.setHorizontalTextPosition(SwingConstants.RIGHT);
		slider = new JSlider(SwingConstants.HORIZONTAL,
			min, max, initialValue);
	setLayout(new BorderLayout());
		setPreferredSize(new Dimension(width, height));
		add(label, BorderLayout.PAGE_START);
		add(slider, BorderLayout.CENTER);

		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				label.setText((slider.getValue() * inc) + string);
			}
		});
	}

	public Slider(String labelString, int initialValue,
			int min, int max) {
		this(labelString, initialValue, min, max, DEF_WIDTH);
	}

	public void addChangeListener(ChangeListener l) {
		slider.addChangeListener(l);
	}

	public void removeChangeListener(ChangeListener l) {
		slider.removeChangeListener(l);
	}

	public int getOrientation() {
		return slider.getOrientation();
	}

	public void setOrientation(int orient) {
		slider.setOrientation(orient);
	}

	public void setWidth(int width) {
		this.width = width;
		setPreferredSize(new Dimension(width, height));
	}

	public synchronized int getValue() {
		return slider.getValue();
	}

	public int getMinimum() {
		return slider.getMinimum();
	}

	public int getMaximum() {
		return slider.getMaximum();
	}

	public void setIncrement(int i) {
		inc = i;
		label.setText((slider.getValue() * inc) + string);
	}

	public synchronized void setValue(int value) {
		if (value > slider.getMaximum()) {
			slider.setMaximum(value);
		}
		slider.setValue(value);
		label.setText((slider.getValue() * inc) + string);
	}

	public void setMinimum(int min) {
		slider.setMinimum(min);
	}

	public void setMaximum(int max) {
		slider.setMaximum(max);
	}
}
