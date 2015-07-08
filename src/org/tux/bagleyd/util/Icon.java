package org.tux.bagleyd.util;

/*
 * @(#)Icon.java
 *
 * Mostly from Graphic Java Mastering the JFC Volume 1: AWT
 * 3rd Edition by David M. Geary, Example 24-5,6 pp 807-809
 */

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics;
import java.net.URL;

/**
 * The <code>Icon</code> class makes it easier to put images in
 * your applets.
 *
 * @author	David M. Geary
 */

public class Icon extends Canvas {
	private static final long serialVersionUID = 42L;
	protected URL codeBase;
	protected String filename;
	protected Image image;
	protected int width, height;

	public Icon(Image image, int width, int height) {
		this.image = image;
		this.width = width;
		this.height = height;
	}
	public Dimension getPreferredSize() {
		return new Dimension(this.width, this.height);
	}
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, this);
	}
	public void update(Graphics g) {
		paint(g);
	}
	public void destroy() {
		image.flush();
	}
	public int getIconWidth() {
		return image.getWidth(null);
	}
	public int getIconHeight() {
		return image.getHeight(null);
	}
}
