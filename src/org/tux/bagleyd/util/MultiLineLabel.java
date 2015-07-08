package org.tux.bagleyd.util;

/*
 * @(#)MultilineLabel.java
 *
 * Mostly from Java Examples in a Nutshell
 * by David Flanagan, pp 132-135
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * The <code>MultilineLabel</code> class that displays multiple lines
 * of text with specified margins and alignment.
 *
 * @author	David Flanagan
 */

public class MultiLineLabel extends Component {
	private static final long serialVersionUID = 42L;
	public static final String nl = System.getProperty("line.separator");
	protected String label;		// The label, not broken into lines
	protected int marginWidth, marginHeight;
	protected int alignment;	// The alignment of the text
	public static final int LEFT = 0, CENTER = 1, RIGHT = 2;
	// Computed state values
	protected int numLines;
	protected String[] lines;	// The label, broken into lines
	protected int[] lineWidths;
	protected int maxWidth;		// The width of the longest line
	protected int lineHeight;	// Total height of the font
	protected int lineAscent;	// Font height above baseline
	protected boolean measured = false; // Have the lines been measured?
	protected boolean big = false;

	// Here are five versions of the constructor
	public MultiLineLabel(String label, int marginWidth, int marginHeight,
			int alignment, boolean big) {
		this.label = label;
		this.marginWidth = marginWidth;
		this.marginHeight = marginHeight;
		this.alignment = alignment;
		this.big = big;
		parseLabel();	//Break the label up into lines
	}

	public MultiLineLabel(String label, int marginWidth,
			int marginHeight, int alignment) {
		this(label, marginWidth, marginHeight, alignment, false);
	}

	public MultiLineLabel(String label, int marginWidth,
			int marginHeight) {
		this(label, marginWidth, marginHeight, LEFT, false);
	}

	public MultiLineLabel(String label, int alignment) {
		this(label, 10, 10, alignment, false);
	}

	public MultiLineLabel(String label, boolean big) {
		this(label, 10, 10, LEFT, big);
	}

	public MultiLineLabel(String label) {
		this(label, LEFT);
	}

	public MultiLineLabel() {
		this("");
	}

	// Methods to set and query the various attributes of the component
	// Note that some query methods are inherited from the superclass.
	public void setLabel(String label) {
		this.label = label;
		parseLabel();		// Break the label into lines
		measured = false;	// Note that we need to measure lines
		setSize(getPreferredSize());
		repaint();		// Request a redraw
	}

	public void setFont(Font f) {
		super.setFont(f);  // tell our superclass about the new font
		measured = false;  // Note that we need to remeasure lines
		repaint();		// Request a redraw
	}

	public void setForeground(Color color) {
		super.setForeground(color);  // tell superclass of new color
		repaint();	// Request a redraw (size is unchanged)
	}

	public void setAlignment(int align) {
		alignment = align;
		repaint();
	}

	public void setMarginWidth(int width) {
		marginWidth = width;
		repaint();
	}

	public void setMarginHeight(int height) {
		marginHeight = height;
		repaint();
	}

	public String getLabel() {
		return label;
	}

	public int getAlignment() {
		return alignment;
	}

	public int getMarginWidth() {
		return marginWidth;
	}

	public int getMarginHeight() {
		return marginHeight;
	}

	/*
	 * This method is called by a layout manager when it wants to
	 * know how big we would like to be.
	 */
	public Dimension getPreferredSize() {
		if (!measured)
			measure();
		return new Dimension((maxWidth + 2 * marginWidth),
			numLines * lineHeight + 2 * marginHeight);
	}

	/*
	 * This method is called when the layout manager wants to know
	 * the bare minimum amount of space we need to get by.
	 */
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	/*
	 * This method draws the label (same method that applets use).
	 * Note that it handles the margins and the alignment, but that
	 * it does not have to worry about the color or font; the superclass
	 * takes care of setting those in the Graphics object we are passed.
	 */
	public void paint(Graphics g) {
		int x, y;
		Dimension size = this.getSize();

		if (!measured)
			measure();
		y = lineAscent + (size.height - numLines * lineHeight) / 2;
		for (int i = 0; i < numLines; i++, y += lineHeight) {
			switch (alignment) {
			case CENTER:
				x = (size.width - lineWidths[i]) / 2;
				break;
			case RIGHT:
				x = size.width - marginWidth - lineWidths[i];
				break;
			case LEFT:
			default:
				x = marginWidth;
				break;
			}
			g.drawString(lines[i], x, y);
		}
	}

	/*
	 * This internal method breaks a specified label up into an array
	 * of lines.
	 */
	protected synchronized void parseLabel() {
		lines = label.split(nl);
		numLines = lines.length;
		lineWidths = new int[numLines];
	}

	/* This internal method figures out how large the font is,
	 * and how wide each line of the label is, and how wide the
	 * widest line is.
	 */
	protected synchronized void measure() {
		FontMetrics fm = getFontMetrics(this.getFont());

		lineHeight = fm.getHeight();
		lineAscent = fm.getAscent();
		maxWidth = 0;
		for (int i = 0; i < numLines; i++) {
			lineWidths[i] = fm.stringWidth(lines[i]);
			if (lineWidths[i] > maxWidth)
				maxWidth = lineWidths[i];
		}
		if (big)
			maxWidth *= 4;
		measured = true;
	}
}
