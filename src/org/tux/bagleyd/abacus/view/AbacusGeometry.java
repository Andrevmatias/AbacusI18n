/*
 * @(#)AbacusGeometry.java
 *
 * Copyright 1994 - 2014	David A. Bagley, bagleyd@tux.org
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

package org.tux.bagleyd.abacus.view;

//import android.graphics.Point;
//import android.util.Log;
import java.awt.Color;
import java.awt.Point;

import org.tux.bagleyd.abacus.AbacusInterface;
import org.tux.bagleyd.abacus.model.AbacusFormat;

import org.tux.bagleyd.util.Colour;

/**
 * The <code>AbacusGeometry</code> class holds physical info about 
 * the abacus widget.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */

public class AbacusGeometry {
	public static final int MAX_SLICES = 10;

	Color frameColor = Colour.tan;
	Color background = Colour.steelBlue;
	Color borderColor = Colour.brown;
	Color symbolColor = Colour.magenta;
	Color lineColor = Colour.black;
	Color[] beadColor = new Color[3];
	Color[] railColor = new Color[4];
	Color[] beadShade = new Color[4 * beadColor.length];
	Color foreground = frameColor;
	int[] deckHeight = new int[AbacusFormat.MAX_DECKS];
	int delay, numberSlices;
	int totalWidth, railWidth;
	int midBeginX, middleBarHeight, middleBarY;
	Point pos, coreSize;
	Point frameSize, beadSize, totalSize;
	Point delta, offset;
	Point pressOffset;
	AbacusFormat model;

	public AbacusGeometry(AbacusFormat model) {
		this.model = model;
		coreSize = new Point(0, 0);
		pressOffset = new Point(1, 0);
		railColor[0] = Colour.gold;
		railColor[1] = Colour.silver;
		railColor[2] = Colour.purple;
		railColor[3] = Colour.black;
		beadColor[0] = Colour.darkRed;
		beadColor[1] = Colour.limeGreen;
		beadColor[2] = Colour.gainsboro;
		resetShade();
	}

	public void resizeCounter() {
		pos = new Point(java.lang.Math.max((frameSize.x -
			delta.x) / (model.getRails() + 1), delta.x),
			java.lang.Math.max((frameSize.y - 2 *
			delta.y - middleBarHeight) /
			(/*model.getRoom(AbacusFormat.TOP) +*/
			model.getRoom(AbacusFormat.BOTTOM) + 1), delta.y));
		for (int deck = AbacusFormat.BOTTOM; deck <= AbacusFormat.TOP; deck++) {
			deckHeight[deck] = pos.y * model.getRoom(deck) + delta.y + 2;
		}
		totalSize = new Point(pos.x * model.getRails() + delta.x + 2,
			deckHeight[AbacusFormat.BOTTOM]);
		offset = new Point(java.lang.Math.max((frameSize.x -
			totalSize.x) / 2, 1),
			java.lang.Math.max(((frameSize.y -
			totalSize.y - middleBarHeight + 5) / 2 + 1), 1));
		middleBarY = offset.y;
		railWidth = pos.x / 32 + 1;
		beadSize = new Point(pos.x - delta.x, pos.y - delta.y);
		if (beadSize.y > pos.x)
			beadSize.y = pos.x;
	}

	public void resize(int width, int height) {
		/* Determine size of client area */

		coreSize = new Point(width, height);
		frameSize = new Point(0, 0);
		offset = new Point(2, 2);
		delta = new Point(8, 1 + pressOffset.y);
		if (model.getVertical()) {
			frameSize.y = coreSize.x - 1;
			frameSize.x = coreSize.y - 1;
		} else {
			frameSize.x = coreSize.x - 1;
			frameSize.y = coreSize.y - 1;
		}
		middleBarHeight = frameSize.y /
			(model.getRoom(AbacusFormat.TOP) +
			model.getRoom(AbacusFormat.BOTTOM) + 5); /* 3 + fudge */
		if (middleBarHeight < 6)
			middleBarHeight = 6;
		if (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) {
			resizeCounter();
			return;
		}
		pos = new Point(java.lang.Math.max((frameSize.x -
			delta.x) / (model.getRails() + 1), delta.x),
			java.lang.Math.max((frameSize.y - 2 *
			delta.y - middleBarHeight) / (model.getRoom(AbacusFormat.TOP) +
			model.getRoom(AbacusFormat.BOTTOM) + 1), delta.y));
		totalSize = new Point(pos.x * model.getRails() + delta.x + 2, 0);
		for (int deck = AbacusFormat.BOTTOM; deck <= AbacusFormat.TOP; deck++) {
			deckHeight[deck] = pos.y * model.getRoom(deck) + delta.y + 2;
			totalSize.y += deckHeight[deck];
		}
		offset = new Point(java.lang.Math.max((frameSize.x -
			totalSize.x) / 2, 1),
			java.lang.Math.max(((frameSize.y -
			totalSize.y - middleBarHeight + 5) / 2 + 1), 1));
		middleBarY = model.getRoom(AbacusFormat.TOP) * pos.y +
			delta.y - 1 + offset.y;
		railWidth = 2;
		if (model.getSlot())
			railWidth = pos.x / 8 + 1;
		else
			railWidth = pos.x / 24 + 1;
		beadSize = new Point(pos.x - delta.x, pos.y - delta.y);
		// Android
		/*if (beadSize.x < beadSize.y && (beadSize.x % 2 == 0)) {
			beadSize.x++;
		}
		if (beadSize.y < beadSize.x && (beadSize.y % 2 == 0)) {
			beadSize.y++; // circle size dependent on radius not diameter
		}*/
	}

	public void resetShade() {
		for (int i = 0; i < beadColor.length; i++) {
			beadShade[4 * i] = brighter(beadColor[i]);
			beadShade[1 + 4 * i] = beadColor[i];
			beadShade[2 + 4 * i] = beadColor[i].darker();
			beadShade[3 + 4 * i] = beadShade[2 + 4 * i].darker();
		}
	}

	public Color getForeground() {
		return foreground;
	}

	public void setForeground(Color value) {
		foreground = value;
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color value) {
		background = value;
	}

	public Color getFrameColor() {
		return frameColor;
	}

	public void setFrameColor(Color value) {
		frameColor = value;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color value) {
		borderColor = value;
	}

	public Color getSymbolColor() {
		return symbolColor;
	}

	public void setSymbolColor(Color value) {
		symbolColor = value;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color value) {
		lineColor = value;
	}

	public Color getBeadColor(int index) {
		if (index >= 0 && index < beadColor.length) {
			return beadColor[index];
		}
		return null;
	}

	public void setBeadColor(int index, Color value) {
		if (index >= 0 && index < beadColor.length) {
			beadColor[index] = value;
		}
	}

	public Color getBeadShade(int index) {
		if (index >= 0 && index < beadShade.length) {
			return beadShade[index];
		}
		return null;
	}

	public void setBeadShade(int index, Color value) {
		if (index >= 0 && index < beadShade.length) {
			beadShade[index] = value;
		}
	}

	public Color getRailColor(int index) {
		if (index >= 0 && index < railColor.length) {
			return railColor[index];
		}
		return null;
	}

	public void setRailColor(int index, Color value) {
		if (index >= 0 && index < railColor.length) {
			railColor[index] = value;
		}
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int value) {
		if (value < 0) {
			delay = -value;
			return;
		}
		delay = value;
	}

	public int getNumberSlices() {
		return numberSlices;
	}

	public void setNumberSlices(int value) {
		if (value >= 0)
			numberSlices = value;
	}

	public int getDeckHeight(int index) {
		if (index >= 0 && index < deckHeight.length) {
			return deckHeight[index];
		}
		return -1;
	}

	public void setDeckHeight(int index, int value) {
		if (index >= 0 && index < deckHeight.length
				&& value > 0)
			deckHeight[index] = value;
	}

	public int getRailWidth() {
		return railWidth;
	}

	public void setRailWidth(int value) {
		if (value > 0)
			railWidth = value;
	}

	public int getMiddleBarHeight() {
		return middleBarHeight;
	}

	public int getMiddleBarY() {
		return middleBarY;
	}

	public Point getCoreSize() {
		return coreSize;
	}

	public Point getFrameSize() {
		return frameSize;
	}

	public Point getBeadSize() {
		return beadSize;
	}

	public Point getPos() {
		return pos;
	}

	public Point getDelta() {
		return delta;
	}

	public Point getOffset() {
		return offset;
	}

	public Point getPressOffset() {
		return pressOffset;
	}

	public void setPressOffsetY(int value) {
		pressOffset.y = value;
	}

	public Point getTotalSize() {
		return totalSize;
	}

	public int getMiddleBarPositionY() {
		int y = middleBarY;

		if (model.getVertical()) {
			return frameSize.y - middleBarHeight - y;
		}
		return y;
	}

	public int getRomanSubdeckPositionY(int subbeadsOffset) {
		int y = middleBarY + subbeadsOffset * pos.y + pos.y / 2;

		if (model.getVertical()) {
			return frameSize.y - middleBarHeight - y;
		}
		return y;
	}

	public int getMiddleBarPositionX(int rail) {
		return 1 - railWidth / 2 + offset.x +
			rail * pos.x - (pos.x - 1) / 2 + 1;
	}

	public int getRomanBarPositionX(int rail) {
		return 2 + offset.x +
			rail * pos.x - (pos.x - 1) / 2 + 1;
	}

	public int getRomanSubdeckPositionX(int rail) {
		return getRomanBarPositionX(rail) + 2 - beadSize.x / 2 + 1;
	}

	/* Pure colors can get brighter */
	static Color brighter(Color color) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		final double FACTOR = 0.8;

		/* From 2D group:
		 * 1. black.brighter() should return grey
		 * 2. applying brighter to blue will always return blue, brighter
		 * 3. non pure color (non zero rgb) will eventually return white
		 */
		int intensity = (int) ((1 - FACTOR) * 255);

		if (red >= 0 && red < intensity)
			red = intensity;
		if (green >= 0 && green < intensity)
			green = intensity;
		if (blue >= 0 && blue < intensity)
			blue = intensity;
		return new Color(Math.min((int) (red / FACTOR), 255),
			Math.min((int) (green / FACTOR), 255),
			Math.min((int) (blue / FACTOR), 255));
	}
}
