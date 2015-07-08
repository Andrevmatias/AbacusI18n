package org.tux.bagleyd.util;

/*
 * @(#)Colour.java
 *
 * Copyright 2014	David A. Bagley, bagleyd@tux.org
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

import java.awt.Color;

/**
 * The <code>Colour</code> class handles color methods. 
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */

public class Colour {
	private int alphaPart;
	private int redPart;
	private int greenPart;
	private int bluePart;

	public static final Color black = new Color(0, 0, 0);
	public static final Color blue = new Color(0, 0, 255);
	public static final Color brown = new Color(139, 115, 85);
	public static final Color cyan = new Color(0, 255, 255);
	public static final Color darkBrown = new Color(127, 63, 0);
	public static final Color darkRed = new Color(139, 0, 0);
	public static final Color gainsboro = new Color(220, 220, 220);
	public static final Color green = new Color(0, 255, 0);
	public static final Color gold = new Color(255, 215, 0);
	public static final Color gray25 = new Color(64, 64, 64);
	public static final Color gray50 = new Color(127, 127, 127);
	public static final Color gray75 = new Color(191, 191, 191);
	public static final Color indianRed = new Color(205, 92, 92);
	public static final Color lightSteelBlue = new Color(176, 196, 222);
	public static final Color lightTan = new Color(210, 180, 140);
	public static final Color limeGreen = new Color(50, 205, 50);
	public static final Color magenta = new Color(255, 0, 255);
	public static final Color orange = new Color(255, 165, 0);
	public static final Color pink = new Color(255, 192, 203);
	public static final Color purple = new Color(160, 32, 240);
	public static final Color red = new Color(255, 0, 0);
	public static final Color seaGreen = new Color(46, 139, 87);
	public static final Color silver = new Color(202, 225, 255);
	public static final Color steelBlue = new Color(174, 178, 195);
	public static final Color tan = new Color(139, 126, 102);
	public static final Color white = new Color(255, 255, 255);
	public static final Color yellow = new Color(255, 255, 0);

	public Colour(Color color) {
		alphaPart = color.getAlpha();
		redPart = color.getRed();
		greenPart = color.getGreen();
		bluePart = color.getBlue();
	}

	/*public Colour(int color) {
		alphaPart = (0xFF000000 & color) >> 24;
		redPart = (0x00FF0000 & color) >> 16;
		greenPart = (0x0000FF00 & color) >> 8;
		bluePart = 0x000000FF & color;
	}*/

	public Color getColor() {
		return new Color(redPart, greenPart, bluePart);
	}


	/*public int getColor() {
		return ((alphaPart << 24) + (redPart << 16)
			+ (greenPart << 8) + bluePart);
	}*/

	public Color brighter() {
		brighterColor();
		return new Color(newAlpha, newRed, newGreen, newBlue);
	}

	/*public int brighter() {
		brighterColor();
		return ((newAlpha << 24) + (newRed << 16) + (newGreen << 8) + newBlue);
	}*/

	public Color darker() {
		darkerColor();
		return new Color(newAlpha, newRed, newGreen, newBlue);
	}

	/*public int darker() {
		darkerColor();
		return ((newAlpha << 24) + (newRed << 16) + (newGreen << 8) + newBlue);
	}*/

	private int newAlpha, newRed, newGreen, newBlue;

	/* Pure colors can get brighter */
	private void brighterColor() {
		newRed = redPart;
		newGreen = greenPart;
		newBlue = bluePart;
		final double FACTOR = 0.8;

		/* From 2D group:
		 * 1. black.brighter() should return grey
		 * 2. applying brighter to blue will always return blue, brighter
		 * 3. non pure color (non zero rgb) will eventually return white
		 */
		int intensity = (int) ((1 - FACTOR) * 0xFF);

		if (newRed >= 0 && newRed < intensity)
			newRed = intensity;
		if (newGreen >= 0 && newGreen < intensity)
			newGreen = intensity;
		if (newBlue >= 0 && newBlue < intensity)
			newBlue = intensity;
		newAlpha = alphaPart;
		newRed = Math.min((int) (newRed / FACTOR), 0xFF);
		newGreen = Math.min((int) (newGreen / FACTOR), 0xFF);
		newBlue = Math.min((int) (newBlue / FACTOR), 0xFF);
	}

	private void darkerColor() {
		final double FACTOR = 0.8;

		newAlpha = alphaPart;
		newRed = (int) (redPart * FACTOR);
		newGreen = (int) (greenPart * FACTOR);
		newBlue = (int) (bluePart * FACTOR);
	}
}
