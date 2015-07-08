package org.tux.bagleyd.util;

/*
 * @(#)ColumnLayout.java
 *
 * Mostly from Java Examples in a Nutshell
 * by David Flanagan pp 118-121
 */

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

/**
 * This LayoutManager arranges the components into a column.
 * Components are always given their preferred size.
 *
 * When you create a ColumnLayout, you may specify four values:
 * marginHeight -- how much space to leave on left and right
 * marginWidth -- how much space to leave on top and bottom
 * spacing -- how much vertical space to leave between items
 * alignment -- the horizontal position of the components:
 *      ColumnLayout.LEFT -- left-justify the components
 *      ColumnLayout.CENTER -- horizontally center the components
 *      ColumnLayout.RIGHT -- right-justify the components
 *
 * You never call the methods of a ColumnLayout object.  Just create one
 * and make it the layout manager for your container by passing it to
 * the addLayout() method of the Container object.
 *
 * @author	David Flanagan
 */

public class ColumnLayout implements LayoutManager2 {
	protected int marginHeight;
	protected int marginWidth;
	protected int spacing;
	protected int alignment;

	// Constants for the alignment argument to the constructor.
	public static final int LEFT = 0;
	public static final int CENTER = 1;
	public static final int RIGHT = 2;

	// The constructor
	public ColumnLayout(int marginHeight, int marginWidth,
	int spacing, int alignment) {
		this.marginHeight = marginHeight;
		this.marginWidth = marginWidth;
		this.spacing = spacing;
		this.alignment = alignment;
	}

	/** A default constructor that creates a ColumnLayout using 5-pixel
	 *  margin width and height, 5-pixel spacing, and left alignment */
	public ColumnLayout() { this(5, 5, 5, LEFT); }

	/** The method that actually performs the layout.  Called by the Container */
	public void layoutContainer(Container parent) {
		Insets insets = parent.getInsets();
		Dimension parent_size = parent.getSize();
		Component kid;
		int nkids = parent.getComponentCount();
		int x0 = insets.left + this.marginWidth;
		int x;
		int y = insets.top + this.marginHeight;

		for (int i = 0; i < nkids; i++) {
			kid = parent.getComponent(i);
			if (!kid.isVisible())
				continue;
			Dimension pref = kid.getPreferredSize();
			switch (this.alignment) {
			case CENTER:
				x = x0 + (parent_size.width - pref.width) / 2;
				break;
			case RIGHT:
				x = parent_size.width - insets.right - this.marginWidth - pref.width;
				break;
			case LEFT:
			default:
				x = x0;
				break;
			}
			kid.setBounds(x, y, pref.width, pref.height);
			y += pref.height + this.spacing;
		}
	}

	/** The Container calls this to find out how big the layout should be */
	public Dimension preferredLayoutSize(Container parent) {
		return layoutSize(parent, 1);
	}
	/** The Container calls this to find out how small the layout could be */
	public Dimension minimumLayoutSize(Container parent) {
		return layoutSize(parent, 2);
	}
	/** The Container calls this to find out how big the layout could be */
	public Dimension maximumLayoutSize(Container parent) {
		return layoutSize(parent, 3);
	}

	protected Dimension layoutSize(Container parent, int sizetype) {
		int nkids = parent.getComponentCount();
		Dimension size = new Dimension(0, 0);
		Insets insets = parent.getInsets();
		int num_visible_kids = 0;

		// Compute maximum width and total height of all visible kids
		for (int i = 0; i < nkids; i++) {
			Component kid = parent.getComponent(i);
			Dimension d;
			if (!kid.isVisible())
				continue;
			num_visible_kids++;
			if (sizetype == 1)
				d = kid.getPreferredSize();
			else if (sizetype == 2)
				d = kid.getMinimumSize();
			else
				d = kid.getMaximumSize();
			if (d.width > size.width)
				size.width = d.width;
			size.height += d.height;
		}

		// Now add in margins and stuff
		size.width += insets.left + insets.right + 2 * this.marginWidth;
		size.height += insets.top + insets.bottom + 2 * this.marginHeight;
		if (num_visible_kids > 1)
			size.height += (num_visible_kids - 1) * this.spacing;
		return size;
	}

	public void addLayoutComponent(String constraint, Component comp) {
		// Other LayoutManager(2) methods that are unused by this class
	}
	public void addLayoutComponent(Component comp, Object constraint) {
		// Other LayoutManager(2) methods that are unused by this class
	}
	public void removeLayoutComponent(Component comp) {
		// Other LayoutManager(2) methods that are unused by this class
	}
	public void invalidateLayout(Container parent) {
		// Other LayoutManager(2) methods that are unused by this class
	}
	public float getLayoutAlignmentX(Container parent) {
		return 0.5f;
	}
	public float getLayoutAlignmentY(Container parent) {
		return 0.5f;
	}
}
