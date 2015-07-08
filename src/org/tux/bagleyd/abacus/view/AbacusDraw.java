/*
 * @(#)AbacusDraw.java
 *
 * Copyright 1994 - 2014  David A. Bagley, bagleyd@tux.org
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

import java.awt.Canvas;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Image;

//import org.tux.bagleyd.util.Graphics;
import org.tux.bagleyd.util.OrientDraw;
import org.tux.bagleyd.abacus.AbacusInterface;
import org.tux.bagleyd.abacus.model.AbacusFormat;

/**
 * The <code>AbacusDraw</code> class does all the basic graphics for
 * <code>AbacusApplet</code> class.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */

public class AbacusDraw {

	Canvas canvas = null;
	AbacusFormat model = null;
	AbacusGeometry geo = null;
	OrientDraw vDraw = null;

	// .9.png may be the way to go here
	Image[][][] bufferedBeadImage = null;
	Graphics[][][] bufferedBeadGraphics = null; // needs canvas, set to null now

	//static void fill3DRect(Canvas g, int x, int y, int width, int height,
	//	boolean raised, boolean updown) {

		//c = g.getColor();
	//	int brighter = 1; //brighter(c);
	//	int darker = 2;//c.darker();

	//	if (!raised) {
	//		g.setColor(darker);
	//	}
	/*	if (updown) {
			g.fillRect(x + 1, y, width - 2, height);
			g.setColor(raised ? brighter : darker);
			g.drawLine(x, y, x + width - 1, y);
			g.setColor(raised ? darker : brighter);
			g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
		} else {
			g.fillRect(x, y + 1, width, height - 2);
			g.setColor(raised ? brighter : darker);
			g.drawLine(x, y, x, y + height - 1);
			g.setColor(raised ? darker : brighter);
			g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
		}
		g.setColor(c);
	}*/

	public AbacusDraw(AbacusFormat model,
			AbacusGeometry geo, Canvas canvas) {
		this.model = model;
		this.geo = geo;
		this.canvas = canvas;
		vDraw = new OrientDraw(model.getVertical());
		bufferedBeadImage = new Image[3][2][2];
		bufferedBeadGraphics = new Graphics[3][2][2];
		Point size = new Point(0, 0);
		if (model.getVertical()) {
			size.x = geo.getPos().y - geo.getPressOffset().y;
			size.y = geo.getBeadSize().x + geo.getPressOffset().x;
		} else {
			size.x = geo.getBeadSize().x + geo.getPressOffset().x;
			size.y = geo.getPos().y - geo.getPressOffset().y;
		}
		for (int color = 0; color < 3; color++)
			for (int pressed = 0; pressed < 2; pressed++)
				for (int shade = 0; shade < 2; shade++) {
					bufferedBeadImage[color][pressed][shade] =
						canvas.createImage(size.x, size.y);
					bufferedBeadGraphics[color][pressed][shade] =
						bufferedBeadImage[color][pressed][shade].getGraphics();
				}
	}

	public void drawFrame(boolean show) {
		Graphics g = canvas.getGraphics();

		if (g == null)
			return;
		try {
			drawFrame(g, show);
		} finally {
			g.dispose();
		}
	}

	public void drawAllBeads() {
		Graphics g = canvas.getGraphics();

		if (g == null)
			return;
		try {
			drawAllBeads(g);
		} finally {
			g.dispose();
		}
	}

	public void drawCounterLine(int deck, int rail,
			boolean highlight) {
		Graphics g = canvas.getGraphics();

		if (g == null || deck == 0)
			return;
		try {
			drawCounterLine(g, deck, rail, highlight);
		} finally {
			g.dispose();
		}
	}

	public void drawRail(int deck, int rail, int j,
			boolean highlight, int offsetX, int size) {
		Graphics g = canvas.getGraphics();

		if (g == null)
			return;
		try {
			drawRail(g, deck, rail, j, highlight, offsetX, size);
		} finally {
			g.dispose();
		}
	}

	public void drawBeadRail(int rail, boolean highlight) {
		Graphics g = canvas.getGraphics();

		if (g == null)
			return;
		try {
			drawBeadRail(g, rail, highlight);
		} finally {
			g.dispose();
		}
	}

	public void drawCounters(int deck, int rail, int bead, boolean show,
			boolean highlight) {
		Graphics g = canvas.getGraphics();

		if (g == null)
			return;
		try {
			drawCounters(g, deck, rail, bead, show, highlight);
		} finally {
			g.dispose();
		}
	}

	public void drawBead(int deck, int rail, int bead,
			int j, boolean show, boolean moving, boolean highlight,
			int pressed, int offsetX, int offsetY) {
		Graphics g = canvas.getGraphics();

		if (g == null)
			return;
		try {
			drawBead(g, deck, rail, bead, j,
				show, moving, highlight,
				pressed, offsetX, offsetY);
		} finally {
			g.dispose();
		}
	}

	public void drawAllBufferedBeads() {
		int color, pressed, shade;

		for (color = 0; color < 3; color++) {
			for (pressed = 0; pressed < 2; pressed++) {
				for (shade = 0; shade < 2; shade++) {
					drawBufferedBead(bufferedBeadGraphics
						[color][pressed][shade],
						color, pressed, shade, 0, 0);
				}
			}
		}
	}

	void copyImage(Graphics g, Image i, int dX, int dY, int sX, int sY,
			int imageWidth, int imageHeight) {
		g.drawImage(i, dX, dY, dX + imageWidth, dY + imageHeight,
			sX, sY, sX + imageWidth, sY + imageHeight, canvas);
	}

	void drawRomanMarkers(Graphics g, boolean show) {
		int rails = model.getRails();
		int decimalPosition = model.getDecimalPosition();
		int pRails = rails - decimalPosition -
			((model.getSign()) ? 1 : 0);

		drawRomanI(g, rails - decimalPosition, show);
		if (pRails - 1 > 0)
			drawRomanX(g, rails - decimalPosition - 1, show);
		if (pRails - 2 > 0)
			drawRomanC(g, rails - decimalPosition - 2, show);
		if (pRails - 3 > 0)
			drawRomanM(g, rails - decimalPosition - 3, show);
		if (pRails - 4 > 0)
			drawRomanx(g, rails - decimalPosition - 4, show);
		if (pRails - 5 > 0)
			drawRomanc(g, rails - decimalPosition - 5, show);
		if (pRails - 6 > 0)
			drawRomanm(g, rails - decimalPosition - 6, show);
		if (model.checkSubdeck(3)) {
			int subdeck = model.getSubdeck();

			drawRomanHalf(g, rails - model.getDecimalPosition() + 3,
				model.getNumberSubbeadsOffset(subdeck - 1), show);
			if (subdeck > 1)
				drawRomanQuarter(g, rails - model.getDecimalPosition() + 3,
					model.getNumberSubbeadsOffset(subdeck - 2), show);
			if (subdeck > 2)
				drawRomanTwelfth(g, rails - model.getDecimalPosition() + 3,
					model.getNumberSubbeadsOffset(subdeck - 3), show);
		}
	}

	void drawCounterLine(Graphics g, int deck, int rail, boolean highlight) {
		int dx, dy;
		int size = geo.getFrameSize().y - geo.getDelta().y;
		if (deck == 1)
			return;
		// FIXME why + 1?
		dx = geo.getMiddleBarPositionX(rail + 1) + 1 + geo.getBeadSize().x / 2 + 
			geo.getRailWidth() / 2;
		//dx = (model.getRails() - rail - 1) * geo.getPos().x +
		//	geo.getDelta().x + geo.getOffset().x + geo.getBeadSize().x;
		dy = geo.getFrameSize().y - 1 - geo.getDelta().y;
		if (model.getVertical())
			dy = geo.getFrameSize().y - dy - 1;
		if (highlight)
			g.setColor(geo.getRailColor(2));
		else if (geo.getForeground() == geo.getBorderColor()) 
			g.setColor(geo.getForeground());
		else
			g.setColor(geo.getRailColor(model.getRailIndex()));
		vDraw.fillRectClip(g,
			dx - geo.getRailWidth() / 2 + 1, dy,
			geo.getRailWidth(), size, 
			0, 0, size);
	}

	public void drawFrameCounter(Graphics g, boolean show) {
		drawDecimalSeparator(g, model.getRails() - model.getDecimalPosition(), show);
		for (int rail = 0; rail < model.getRails();
// - ((model.getSign()) ? 1 : 0);
				rail++) {
			drawCounterLine(g, 0, rail, false);
		}
		drawAllGroupSeparators(g, show);
		if (model.getSign())
			drawCounterNegative(g, 1, show);
		if (model.getPiece(AbacusFormat.BOTTOM) != 0)
			drawCounterPiece(g, model.getRails() - model.getDecimalPosition() + 1, show);
		if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0)
			drawCounterPiece(g, model.getRails() - model.getDecimalPosition() + 1 +
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1) +
				model.getShiftPercent(), show);
		boolean anomalyActive = model.checkAnomaly();
		if (anomalyActive)
			drawCounterAnomaly(g, model.getRails() - model.getDecimalPosition() -
				model.getShiftAnomaly(), show);
		if (anomalyActive && model.getAnomalySq() != 0)
			drawCounterAnomaly(g, model.getRails() - model.getDecimalPosition() - model.getShiftAnomaly() -
			model.getShiftAnomalySq(), show);
	}

	public void drawFrame(Graphics g, boolean show) {
		int deck, x, y, yOffset, yPrime;

		if (show) {
			if (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) {
				drawFrameCounter(g, show);
				return;
			}
			x = model.getRails() * geo.getPos().x + geo.getDelta().x - 1;
			g.setColor(geo.getForeground());
			/* Top/Left */
			vDraw.fillRect(g, 0, 0,
				geo.getOffset().x + 4, geo.getFrameSize().y); // was 1 added 3
			/* Bottom/Right */
			vDraw.fillRect(g, x + geo.getOffset().x, 0,
				geo.getFrameSize().x - (x + geo.getOffset().x), geo.getFrameSize().y);
			for (deck = AbacusFormat.UP; deck >= AbacusFormat.DOWN; deck--) {
				yOffset = (deck == AbacusFormat.UP) ? 0 : geo.getDeckHeight(AbacusFormat.TOP);
				y = model.getRoom(deck) * geo.getPos().y + geo.getDelta().y - 1;
				yPrime = y + yOffset + geo.getOffset().y;
				if (model.getVertical()) {
					yPrime = geo.getFrameSize().y - geo.getMiddleBarHeight() - yPrime;
					yOffset = geo.getFrameSize().y - geo.getOffset().y - yOffset;
				}
				if (deck == AbacusFormat.UP) {
					/* Right/Top */
					vDraw.fillRect(g, geo.getOffset().x + 4, yOffset, x - 4, geo.getOffset().y);
						if (!model.getSlot()) {
							/* Middle */
							vDraw.fillRect(g, geo.getOffset().x + 4, yPrime, x - 4, geo.getMiddleBarHeight());
						}
						boolean anomalyActive = model.checkAnomaly();

						if (model.getSlot() && !anomalyActive) {
							drawRomanMarkers(g, show);
						} else {
							drawDecimalSeparator(g, model.getRails() - model.getDecimalPosition(), show);
							if (!anomalyActive)
								drawAllGroupSeparators(g, show);
						}
						if (model.getSign())
							drawNegative(g, 1, show);
						if (model.getSlot()) {
							if (model.getPiece(AbacusFormat.BOTTOM) != 0)
								drawRomanPiece(g, model.getRails() -
									model.getDecimalPosition() + 1, show);
							if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0)
								drawRomanPiece(g, model.getRails() - model.getDecimalPosition() + 1 +
									((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1) +
									model.getShiftPercent(), show);
						} else {
							if (model.getPiece(AbacusFormat.BOTTOM) != 0)
								drawPiece(g, model.getRails() - model.getDecimalPosition() + 1, show);
							if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0)
								drawPiece(g, model.getRails() - model.getDecimalPosition() + 1 +
									((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1) +
									model.getShiftPercent(), show);
						}
						if (anomalyActive)
							drawAnomaly(g, model.getRails() - model.getDecimalPosition() -
								model.getShiftAnomaly(), show);
						if (anomalyActive && model.getAnomalySq() != 0)
							drawAnomaly(g, model.getRails() - model.getDecimalPosition() - model.getShiftAnomaly() -
								model.getShiftAnomalySq(), show);
				} else {
					g.setColor(geo.getForeground());
					if (model.getVertical()) {
							/* Left */
							g.fillRect(0, geo.getOffset().x + 4, yPrime + 3, x - 4);
						} else {
							/* Bottom */
							g.fillRect(geo.getOffset().x + 4, yPrime + geo.getMiddleBarHeight() - 3,
								x - 4, geo.getFrameSize().y - (yPrime + geo.getMiddleBarHeight() - 3));
							//Log.d("ADebugTag", "frameb: " + geo.getFrameSize().y);
							}
				}
			}
		} else {
			g.setColor(geo.getBackground());
			g.fillRect(0, 0, geo.getCoreSize().x - 1,
				geo.getCoreSize().y - 1);
		}
	}

	void drawRail(Graphics g, int deck, int rail, int j,
			boolean highlight, int offsetX, int size) {
		int dx, dy, yOffset, subj = 0, d = -1, room;

		yOffset = (deck == AbacusFormat.UP) ? 0 :
			geo.getDeckHeight(AbacusFormat.TOP) + geo.getMiddleBarHeight() - 3;
		dx = (model.getRails() - rail - 1) * geo.getPos().x + geo.getDelta().x + geo.getOffset().x;
		dy = (j - 1) * geo.getPos().y + geo.getDelta().y +
			yOffset + geo.getOffset().y - 1;
		if (model.getVertical())
			dy = geo.getFrameSize().y - geo.getBeadSize().y - dy - 1;
		dy -= geo.getPressOffset().y;
		room = model.getRoom(deck);
		if (model.checkSubdeck(3) && rail == model.getDecimalPosition() - 2) {
			if (AbacusFormat.getSubmodeSlotsSeparate(model.getSubmode())) {
				d = model.getSubdeckPosition(j);
				subj = model.getPositionSubdeck(j);
			} else {
				room = 0;
				for (d = 0; d < model.getSubdeck(); d++)
					room += model.getSubdecksRoom(d);
				d = -1;
			}
		}
		g.setColor(geo.getBackground());
		vDraw.fillRectClip(g, dx, dy,
			geo.getBeadSize().x + 2, geo.getBeadSize().y + 1 + 2 * geo.getPressOffset().y,
			0, offsetX, size);
		if (model.getSlot() && (j == 1 || (d != -1 && subj == 1))) {
			g.setColor((highlight) ? geo.getRailColor(2) : geo.getBorderColor());
			vDraw.fillRectClip(g,
				dx + geo.getBeadSize().x / 2 - geo.getRailWidth() / 2 + 1,
				dy,
				geo.getRailWidth(),
				5 * geo.getBeadSize().y / 8 + 4,
				3 * geo.getBeadSize().y / 8, offsetX, size);
			g.setColor(geo.getBackground());
			/* round off the top of rail */
			vDraw.fillRectClip(g,
				dx + geo.getBeadSize().x / 2 - geo.getRailWidth() / 2, dy,
				2, 1, 3 * geo.getBeadSize().y / 8, offsetX, size);
			vDraw.fillRectClip(g,
				dx + geo.getBeadSize().x / 2 + (geo.getRailWidth() - 1) / 2 + 1, dy,
				2, 1, 3 * geo.getBeadSize().y / 8, offsetX, size);
		} else if (model.getSlot() && (j == room ||
				(d != -1 && subj == model.getSubdecksRoom(d)))) {
			g.setColor((highlight) ? geo.getRailColor(2) : geo.getBorderColor());
			vDraw.fillRectClip(g,
				dx + geo.getBeadSize().x / 2 - geo.getRailWidth() / 2 + 1, dy,
				geo.getRailWidth(),
				5 * geo.getBeadSize().y / 8 + 3,
				0, offsetX, size);
			g.setColor(geo.getBackground());
			/* round off the bottom of rail */
			vDraw.fillRectClip(g,
				dx + geo.getBeadSize().x / 2 - geo.getRailWidth() / 2, dy,
				2, 1, 2 + 5 * geo.getBeadSize().y / 8, offsetX, size);
			vDraw.fillRectClip(g,
				dx + geo.getBeadSize().x / 2 + (geo.getRailWidth() - 1) / 2 + 1, dy,
				2, 1, 2 + 5 * geo.getBeadSize().y / 8, offsetX, size);
		} else {
			g.setColor((highlight) ? geo.getRailColor(2) :
				((model.getSlot()) ? geo.getBorderColor() : geo.getRailColor(model.getRailIndex())));
			vDraw.fillRectClip(g,
				dx + geo.getBeadSize().x / 2 - geo.getRailWidth() / 2 + 1, dy,
				geo.getRailWidth(), geo.getBeadSize().y + 1 + 2 * geo.getPressOffset().y,
				0, offsetX, size);
		}
	}

	void drawCounterDecimalSeparator(Graphics g, int rail, boolean show) {
		int x, y, wd, ht;

		if (model.getDecimalPosition() == 0)
			return;
		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		wd = 1 + geo.getRailWidth() * 2;
		x = geo.getMiddleBarPositionX(rail) + 2 +
			geo.getBeadSize().x / 2 + geo.getRailWidth() / 2;
		y = geo.getMiddleBarPositionY();
		ht = geo.getMiddleBarHeight() - 1;
		// make a diamond, not historical but need
		// something when there is a decimal point
		vDraw.drawLine(g, x - wd, y + ht / 2, x, y + ht);
		vDraw.drawLine(g, x - wd, y + ht / 2, x, y);
		vDraw.drawLine(g, x + wd, y + ht / 2, x, y + ht);
		vDraw.drawLine(g, x + wd, y + ht / 2, x, y);
		vDraw.drawLine(g, x - wd + 1, y + ht / 2, x, y + ht - 1);
		vDraw.drawLine(g, x - wd + 1, y + ht / 2, x, y + 1);
		vDraw.drawLine(g, x + wd - 1, y + ht / 2, x, y + ht - 1);
		vDraw.drawLine(g, x + wd - 1, y + ht / 2, x, y + 1);
	}

	void drawDecimalSeparator(Graphics g, int rail, boolean show) {
		int x, y;

		if (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) {
			drawCounterDecimalSeparator(g, rail, show);
			return;
		}
		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		x = geo.getMiddleBarPositionX(rail);
		y = geo.getMiddleBarPositionY();
		vDraw.fillRect(g, x, y, geo.getRailWidth() + 4, geo.getMiddleBarHeight());
	}

	void drawCounterGroupSeparator(Graphics g, int rail, boolean show) {
		int x, y, wd, ht;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		wd = 1 + geo.getRailWidth() * 2;
		x = geo.getMiddleBarPositionX(rail) + 2 +
			geo.getBeadSize().x / 2 + geo.getRailWidth() / 2;
		y = geo.getMiddleBarPositionY();
		ht = geo.getMiddleBarHeight() - 1;
		// make an X
		vDraw.drawLine(g, x + wd, y, x - wd, y + ht);
		vDraw.drawLine(g, x - wd, y, x + wd, y + ht);
		vDraw.drawLine(g, x + wd, y + 1, x - wd, y + ht - 1);
		vDraw.drawLine(g, x - wd, y + 1, x + wd, y + ht - 1);
		vDraw.drawLine(g, x + wd - 1, y, x - wd + 1, y + ht);
		vDraw.drawLine(g, x - wd + 1, y, x + wd - 1 , y + ht);
	}

	void drawGroupSeparator(Graphics g, int rail, boolean show) {
		int x, y;

		if (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) {
			drawCounterGroupSeparator(g, rail, show);
			return;
		}
		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		x = geo.getMiddleBarPositionX(rail);
		y = geo.getMiddleBarPositionY();
		vDraw.fillRect(g, x + 1, y + (geo.getMiddleBarHeight() - 1) / 4,
			geo.getRailWidth() + 2,
			geo.getMiddleBarHeight() - 2 * (geo.getMiddleBarHeight() - 1) / 4);
		vDraw.fillRect(g, x, y + (geo.getMiddleBarHeight() - 1) / 2,
			geo.getRailWidth() + 4, (geo.getMiddleBarHeight() - 1) % 2 + 1);
		vDraw.fillRect(g, x + geo.getRailWidth() / 2 + 1, y,
			geo.getRailWidth() % 2 + 2, geo.getMiddleBarHeight());
	}

	void drawAllGroupSeparators(Graphics g, boolean show) {
		int separator;

		for (separator = 1; separator <= ((model.getRails() - ((model.getSign()) ? 1 : 0) -
				model.getDecimalPosition() - 1) / model.getGroupSize());
				separator++)
			drawGroupSeparator(g, model.getRails() -
				model.getDecimalPosition() -
				model.getGroupSize() * separator, show);
	}

	void drawRomanI(Graphics g, int rail, boolean show) {
		int x, y;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		x = geo.getRomanBarPositionX(rail);
		y = geo.getMiddleBarPositionY();
		vDraw.fillRect(g, x, y, 2, geo.getMiddleBarHeight());
	}

	void drawRomanX(Graphics g, int rail, boolean show) {
		int x, y, ht;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		x = geo.getRomanBarPositionX(rail);
		y = geo.getMiddleBarPositionY();
		ht = geo.getMiddleBarHeight() - 1;
		vDraw.drawLine(g, x + 3, y, x - 3, y + ht);
		vDraw.drawLine(g, x + 2, y, x - 3 , y + ht - 1);
		vDraw.drawLine(g, x + 3, y + 1, x - 2, y + ht);
		vDraw.drawLine(g, x - 3, y, x + 3, y + ht);
		vDraw.drawLine(g, x - 3, y + 1, x + 2, y + ht);
		vDraw.drawLine(g, x - 2, y, x + 3, y + ht - 1);
	}

	void drawRomanC(Graphics g, int rail, boolean show) {
		int x, y, ht;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		x = geo.getRomanBarPositionX(rail);
		y = geo.getMiddleBarPositionY();
		ht = geo.getMiddleBarHeight() - 1;
		vDraw.fillRect(g, x - 2, y, 5, 1);
		vDraw.fillRect(g, x - 2, y + ht, 5, 1);
		vDraw.fillRect(g, x - 3, y + 1, 2, ht - 1);
		vDraw.drawLine(g, x + 3, y + 1, x + 2, y);
		vDraw.drawLine(g, x + 3, y + ht - 1, x + 2, y + ht);
	}

	void drawRomanM(Graphics g, int rail, boolean show) {
		int x, y, ht;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		x = geo.getRomanBarPositionX(rail);
		y = geo.getMiddleBarPositionY();
		ht = geo.getMiddleBarHeight() - 1;
		if (model.getModernRoman()) {
			vDraw.fillRect(g, x - 3, y, 2, ht + 1);
			vDraw.fillRect(g, x + 3, y, 2, ht + 1);
			vDraw.drawLine(g, x - 2, y, x, y + 2);
			vDraw.drawLine(g, x + 3, y, x + 1, y + 2);
			vDraw.drawLine(g, x - 3, y, x - 1, y + 1);
			vDraw.drawLine(g, x + 4, y, x + 2, y + 1);
			vDraw.fillRect(g, x, y + 2, 2, 2);
		} else {
			vDraw.fillRect(g, x, y, 2, ht + 1);
			vDraw.fillRect(g, x - 5, y + 3, 2, ht - 3);
			vDraw.fillRect(g, x - 4, y + 2, 3, 1);
			vDraw.fillRect(g, x - 4, y + ht, 3, 1);
			vDraw.fillRect(g, x + 5, y + 3, 2, ht - 3);
			vDraw.fillRect(g, x + 3, y + 2, 3, 1);
			vDraw.fillRect(g, x + 3, y + ht, 3, 1);
		}
	}

	void drawRomanx(Graphics g, int rail, boolean show) {
		int x, y, ht;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		x = geo.getRomanBarPositionX(rail);
		y = geo.getMiddleBarPositionY();
		ht = geo.getMiddleBarHeight() - 1;
		if (model.getModernRoman()) {
			vDraw.drawLine(g, x - 1, y + 2, x + 2, y + ht);
			vDraw.drawLine(g, x + 2, y + 2, x - 1, y + ht);
			vDraw.drawLine(g, x - 2, y + 2, x + 3, y + ht);
			vDraw.drawLine(g, x + 3, y + 2, x - 2, y + ht);
			vDraw.fillRect(g, x - 3, y, 8, 1);
		} else {
			vDraw.fillRect(g, x, y, 2, ht + 1);
			vDraw.fillRect(g, x - 3, y + 3, 1, ht - 3);
			vDraw.drawLine(g, x - 2, y + 2, x - 3, y + 3);
			vDraw.drawLine(g, x - 2, y + ht, x - 3, y + ht - 1);
			vDraw.fillRect(g, x + 4, y + 3, 1, ht - 3);
			vDraw.drawLine(g, x + 3, y + 2, x + 4, y + 3);
			vDraw.drawLine(g, x + 3, y + ht, x + 4, y + ht - 1);
			vDraw.fillRect(g, x - 5, y + 2, 1, ht - 2);
			vDraw.drawLine(g, x - 3, y, x - 5, y + 2);
			vDraw.drawLine(g, x - 4, y + ht, x - 5, y + ht - 1);
			vDraw.fillRect(g, x + 6, y + 2, 1, ht - 2);
			vDraw.drawLine(g, x + 4, y, x + 6, y + 2);
			vDraw.drawLine(g, x + 5, y + ht, x + 6, y + ht - 1);
		}
	}

	void drawRomanc(Graphics g, int rail, boolean show) {
		int x, y, ht;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		x = geo.getRomanBarPositionX(rail);
		y = geo.getMiddleBarPositionY();
		ht = geo.getMiddleBarHeight() - 1;
		if (model.getModernRoman()) {
			vDraw.fillRect(g, x - 2, y + 2, 6, 1);
			vDraw.fillRect(g, x - 2, y + ht, 6, 1);
			vDraw.fillRect(g, x - 3, y + 3, 2, ht - 3);
			vDraw.fillRect(g, x - 3, y, 8, 1);
		} else {
			vDraw.fillRect(g, x, y, 2, ht + 1);
			vDraw.drawLine(g, x - 2, y + 3, x - 3, y + 4);
			vDraw.drawLine(g, x - 3, y + 4, x - 2, y + 3);
			vDraw.drawLine(g, x - 2, y + ht, x - 3, y + ht - 1);
			vDraw.drawLine(g, x + 3, y + 3, x + 4, y + 4);
			vDraw.drawLine(g, x + 4, y + 4, x + 3, y + 3);
			vDraw.drawLine(g, x + 3, y + ht, x + 4, y + ht - 1);
			vDraw.fillRect(g, x - 3, y + 5, 1, ht - 5);
			vDraw.fillRect(g, x - 3, y + 1, 2, 1);
			vDraw.drawLine(g, x - 4, y + 2, x - 5, y + 3);
			vDraw.fillRect(g, x - 5, y + 3, 1, ht - 3);
			vDraw.drawLine(g, x - 4, y + ht, x - 5, y + ht - 1);
			vDraw.fillRect(g, x + 4, y + 5, 1, ht - 5);
			vDraw.fillRect(g, x + 3, y + 1, 2, 1);
			vDraw.drawLine(g, x + 5, y + 2, x + 6, y + 3);
			vDraw.fillRect(g, x + 6, y + 3, 1, ht - 3);
			vDraw.drawLine(g, x + 5, y + ht, x + 6, y + ht - 1);
			vDraw.fillRect(g, x - 7, y + 2, 1, ht - 2);
			vDraw.drawLine(g, x - 5, y, x - 7, y + 2);
			vDraw.drawLine(g, x - 6, y + ht, x - 7, y + ht - 1);
			vDraw.fillRect(g, x + 8, y + 2, 1, ht - 2);
			vDraw.drawLine(g, x + 6, y, x + 8, y + 2);
			vDraw.drawLine(g, x + 7, y + ht, x + 8, y + ht - 1);
		}
	}

	void drawRomanm(Graphics g, int rail, boolean show) {
		int x, y, ht;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		x = geo.getRomanBarPositionX(rail);
		y = geo.getMiddleBarPositionY();
		ht = geo.getMiddleBarHeight() - 1;
		if (model.getModernRoman()) {
			vDraw.fillRect(g, x - 3, y + 2, 2, ht - 1);
			vDraw.fillRect(g, x + 3, y + 2, 2, ht - 1);
			vDraw.drawLine(g, x - 2, y + 2, x, y + 4);
			vDraw.drawLine(g, x + 3, y + 2, x + 1, y + 4);
			vDraw.drawLine(g, x - 3, y + 2, x - 1, y + 3);
			vDraw.drawLine(g, x + 4, y + 2, x + 2, y + 3);
			vDraw.fillRect(g, x, y + 4, 2, 2);
			vDraw.fillRect(g, x - 3, y, 8, 1);
		} else {
			vDraw.fillRect(g, x - 5, y, 1, ht + 1);
			vDraw.fillRect(g, x + 5, y, 1, ht + 1);
			vDraw.fillRect(g, x - 4, y, 9, 1);
			vDraw.drawLine(g, x + 3, y + 2, x - 3, y + ht);
			vDraw.drawLine(g, x + 2, y + 2, x - 3, y + ht - 1);
			vDraw.drawLine(g, x + 3, y + 3, x - 2, y + ht);
			vDraw.drawLine(g, x - 3, y + 2, x + 3, y + ht);
			vDraw.drawLine(g, x - 3, y + 3, x + 2, y + ht);
			vDraw.drawLine(g, x - 2, y + 2, x + 3, y + ht - 1);
		}
	}

	void drawRomanHalf(Graphics g, int rail, int subbeadOffset, boolean show) {
		int x, y, ht;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		x = geo.getRomanSubdeckPositionX(rail);
		y = geo.getRomanSubdeckPositionY(subbeadOffset);
		ht = geo.getMiddleBarHeight() - 1;
		if (model.getSubmode() == AbacusInterface.IT) {
			vDraw.fillRect(g, x - 1, y, 4, 1);
			vDraw.fillRect(g, x - 1, y + ht, 4, 1);
			vDraw.drawLine(g, x - 2, y + 1, x - 1, y);
			vDraw.drawLine(g, x - 2, y + ht - 1, x - 1, y + ht);
			vDraw.drawLine(g, x + 3, y + 1, x + 2, y);
			vDraw.drawLine(g, x + 3, y + ht - 1, x + 2, y + ht);
			vDraw.drawLine(g, x - 2, y + 1, x + 3, y + ht - 1);
		} else if (model.getSubmode() == AbacusInterface.UK) {
			vDraw.fillRect(g, x, y, 2, 1);
			vDraw.fillRect(g, x - 2, y + ht, 6, 1);
			vDraw.drawLine(g, x - 1, y + 1, x, y);
			vDraw.drawLine(g, x + 2, y + 1, x + 1, y);
			vDraw.drawLine(g, x - 1, y + 1, x + 1, y + 3);
			vDraw.drawLine(g, x + 1, y + ht - 2, x - 1, y + ht);
			vDraw.fillRect(g, x + 1, y + 3, 1, ht - 5);
		} else if (model.getSubmode() == AbacusInterface.FR) {
			vDraw.fillRect(g, x - 1, y + 2, 3, 1);
			vDraw.fillRect(g, x - 1, y + ht, 5, 1);
			vDraw.drawLine(g, x + 1, y, x - 1, y + 2);
			vDraw.drawLine(g, x + 1, y + 3, x - 1, y + ht);
		}
	}

	void drawRomanQuarter(Graphics g, int rail, int subbeadOffset, boolean show) {
		int x, y, ht;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		x = geo.getRomanSubdeckPositionX(rail);
		y = geo.getRomanSubdeckPositionY(subbeadOffset);
		ht = geo.getMiddleBarHeight() - 1;
		if (model.getSubmode() == AbacusInterface.IT) {
			vDraw.fillRect(g, x - 1, y, 5, 1);
			vDraw.fillRect(g, x - 1, y + ht, 5, 1);
			vDraw.fillRect(g, x + 3, y + 1, 2, ht - 1);
			vDraw.drawLine(g, x - 1, y, x - 2, y + 1);
			vDraw.drawLine(g, x - 1, y + ht, x - 2, y + ht - 1);
		} else if (model.getSubmode() == AbacusInterface.UK) {
			vDraw.fillRect(g, x + 1, y + 2, 1, ht - 3);
			vDraw.drawLine(g, x - 1, y, x + 1, y + 2);
			vDraw.drawLine(g, x - 1, y + ht, x + 1, y + ht - 2);
		} else if (model.getSubmode() == AbacusInterface.FR) {
			vDraw.fillRect(g, x, y, 2, 1);
			vDraw.fillRect(g, x + 1, y + 1, 1, ht - 2);
			vDraw.drawLine(g, x - 1, y + 1, x + 1, y);
			vDraw.drawLine(g, x - 1, y + ht, x + 1, y + ht - 2);
		}
	}

	void drawRomanTwelfth(Graphics g, int rail, int subbeadOffset, boolean show) {
		int x, y, ht;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		x = geo.getRomanSubdeckPositionX(rail);
		y = geo.getRomanSubdeckPositionY(subbeadOffset);
		ht = geo.getMiddleBarHeight() - 1;
		if (model.getSubmode() == AbacusInterface.IT) {
			vDraw.fillRect(g, x - 3, y, 6, 1);
			vDraw.fillRect(g, x - 2, y + ht, 6, 1);
			vDraw.drawLine(g, x + 2, y, x - 2, y + ht);
			vDraw.drawLine(g, x - 3, y + ht, x + 3, y);
			vDraw.drawLine(g, x + 3, y, x - 3, y + ht);
		} else if (model.getSubmode() == AbacusInterface.UK) {
			vDraw.fillRect(g, x - 2, y + ht, 5, 1);
			vDraw.fillRect(g, x - 1, y, 3, 1);
			vDraw.fillRect(g, x + 2, y + 1, 1, 2);
			vDraw.drawLine(g, x - 2, y + 1, x - 1, y);
			vDraw.drawLine(g, x + 2, y + 1, x + 1, y);
			vDraw.drawLine(g, x + 2, y + 2, x - 2, y + ht);
		} else if (model.getSubmode() == AbacusInterface.FR) {
			vDraw.fillRect(g, x - 1, y + ht, 6, 1);
			vDraw.fillRect(g, x, y, 3, 1);
			vDraw.drawLine(g, x - 1, y + 1, x, y);
			vDraw.drawLine(g, x + 3, y + 1, x + 2, y);
			vDraw.drawLine(g, x + 3, y + 1, x - 1, y + ht);
		}
	}

	void drawCounterNegative(Graphics g, int rail, boolean show) {
		int x, y, wd, ht;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		wd = 1 + geo.getRailWidth() * 2;
		x = geo.getMiddleBarPositionX(rail) + 2 +
			geo.getBeadSize().x / 2 + geo.getRailWidth() / 2;
		y = geo.getMiddleBarPositionY();
		ht = geo.getMiddleBarHeight() - 1;
		vDraw.drawLine(g, x - wd, y + ht / 2 - 1, x + wd, y + ht / 2 - 1);
		vDraw.drawLine(g, x - wd, y + ht / 2, x + wd, y + ht / 2);
		if ((ht % 2) == 1) {
			vDraw.drawLine(g, x - wd, y + ht / 2 + 1, x + wd, y + ht / 2 + 1);
		}
	}

	void drawNegative(Graphics g, int rail, boolean show) {
		int x, y;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		x = geo.getMiddleBarPositionX(rail);
		y = geo.getMiddleBarPositionY();
		vDraw.fillRect(g, x, y + geo.getMiddleBarHeight() / 2 - 1,
			geo.getRailWidth() + 4, 2);
	}

	void drawCounterPiece(Graphics g, int rail, boolean show) {
		int x, y, wd, ht;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		wd = 1 + geo.getRailWidth() * 2;
		x = geo.getMiddleBarPositionX(rail) + 2 +
			geo.getBeadSize().x / 2 + geo.getRailWidth() / 2;
		y = geo.getMiddleBarPositionY();
		ht = geo.getMiddleBarHeight() - 1;
		vDraw.drawLine(g, x - wd, y + ht / 2 - 1, x + wd, y + ht / 2 - 1);
		vDraw.drawLine(g, x - wd, y + ht / 2, x + wd, y + ht / 2);
		if ((ht % 2) == 1) {
			vDraw.drawLine(g, x - wd, y + ht / 2 + 1, x + wd, y + ht / 2 + 1);
		}
		vDraw.fillRect(g, x - 1 - geo.getRailWidth() / 2, y + 1,
			geo.getRailWidth() + 2, ht - 1);
	}

	void drawPiece(Graphics g, int rail, boolean show) {
		int x, y;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		x = geo.getMiddleBarPositionX(rail);
		y = geo.getMiddleBarPositionY();
		vDraw.fillRect(g, x, y + geo.getMiddleBarHeight() / 2 - 1,
			geo.getRailWidth() + 4, 2);
		vDraw.fillRect(g, x + 2, y,
			geo.getRailWidth(), geo.getMiddleBarHeight());
	}

	void drawRomanPiece(Graphics g, int rail, boolean show) {
		int x, y, ht, woffset = 0, hoffset = 1;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		x = geo.getRomanBarPositionX(rail);
		y = geo.getMiddleBarPositionY();
		ht = geo.getMiddleBarHeight() - 1;
		if (!model.getSlot()) {
			woffset = ((geo.getRailWidth() & 1) == 1) ? 1 : 0;
			hoffset = ((geo.getRailWidth() & 1) == 1) ? 0 : 1;
		}
		vDraw.fillRect(g, x - 1 + woffset , y, 3 + hoffset, 1);
		vDraw.fillRect(g, x - 1 + woffset, y + ht, 3 + hoffset, 1);
		vDraw.fillRect(g, x - 2 + woffset, y + 1, 2, ht - 1);
		vDraw.fillRect(g, x + 1 + woffset + hoffset, y + 1, 2, ht - 1);
	}

	void drawCounterAnomaly(Graphics g, int rail, boolean show) {
		int x, y, wd, ht;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		wd = 1 + geo.getRailWidth() * 2;
		x = geo.getMiddleBarPositionX(rail) + 2 +
			geo.getBeadSize().x / 2 + geo.getRailWidth() / 2;
		y = geo.getMiddleBarPositionY();
		ht = geo.getMiddleBarHeight() - 1;
		vDraw.drawLine(g, x - wd, y + ht / 2 - 1, x + wd, y + ht / 2 - 1);
		vDraw.drawLine(g, x - wd, y + ht / 2, x + wd, y + ht / 2);
		if ((ht % 2) == 1) {
			vDraw.drawLine(g, x - wd, y + ht / 2 + 1, x + wd, y + ht / 2 + 1);
		}
		vDraw.drawLine(g, x + wd, y, x - wd, y + ht);
		vDraw.drawLine(g, x - wd, y, x + wd, y + ht);
		vDraw.drawLine(g, x + wd, y + 1, x - wd, y + ht - 1);
		vDraw.drawLine(g, x - wd, y + 1, x + wd, y + ht - 1);
		vDraw.drawLine(g, x + wd - 1, y, x - wd + 1, y + ht);
		vDraw.drawLine(g, x - wd + 1, y, x + wd - 1 , y + ht);
	}

	void drawAnomaly(Graphics g, int rail, boolean show) {
		int x, y, ht;

		g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		x = geo.getMiddleBarPositionX(rail);
		y = geo.getMiddleBarPositionY();
		ht = geo.getMiddleBarHeight() - 1;
		vDraw.drawLine(g, x + geo.getRailWidth() + 2, y + 1,
			x + 1, y + ht - 1);
		vDraw.drawLine(g, x + geo.getRailWidth() + 2, y,
			x , y + ht - 1);
		vDraw.drawLine(g, x + geo.getRailWidth() + 3, y + 1,
			x + 1, y + ht);
		vDraw.drawLine(g, x + 1, y + 1,
			x + geo.getRailWidth() + 2, y + ht - 1);
		vDraw.drawLine(g, x, y + 1,
			x + geo.getRailWidth() + 2, y + ht);
		vDraw.drawLine(g, x + 1, y,
			x + geo.getRailWidth() + 3, y + ht - 1);
	}

	void drawCounters(Graphics g, int deck, int rail, int count,
			boolean show, boolean highlight) {
		int dx, dy, xOffset;
		int offsetX = 0, offsetY = 0; //used?
		int pressed = 0; // used?

		if (g == null)
			return;
		xOffset = (deck == AbacusFormat.DOWN) ? 0 :
			geo.getPos().x / 2;
		dx = (model.getRails() - rail - 1) * geo.getPos().x +
			geo.getDelta().x + geo.getOffset().x + offsetX -
			xOffset + geo.getBeadSize().x / 2;
		dy = geo.getTotalSize().y / 2 + count * geo.getBeadSize().y / 2 +
			geo.getOffset().y + geo.getPressOffset().y +
			offsetY;
		if (highlight)
			g.setColor(geo.getBeadShade(9));
		else
			g.setColor((show) ? geo.getSymbolColor() : geo.getBackground());
		// geo.getPos().y - count * geo.getBeadSize().y / 2
		if (model.getVertical()) {
			dy = geo.getFrameSize().y - geo.getBeadSize().y -
				dy - 3 + 2 * geo.getPressOffset().y;
		}
		//drawCounterLine(g, deck, rail, highlight);
		dx += pressed * geo.getPressOffset().x;
		dy += pressed * geo.getPressOffset().y;
		for (int i = 0; i < count; i++) {
			if (geo.getBeadSize().x < 3) {
				vDraw.fillRect(g, dx, dy + i * 9,
					2, 2);
			} else {
				vDraw.fillCircle(g, geo.getBeadSize().x / 2,
					dx + geo.getBeadSize().x / 2/* -
					(geo.getBeadSize().x - geo.getBeadSize().y) / 2*/,
					dy + geo.getBeadSize().y / 2 + i * geo.getBeadSize().y);
			}
		}
		if (!show && deck == AbacusFormat.BOTTOM) {
			drawCounterLine(g, deck, model.getRails() - rail - 1, highlight);
		}
	}

	void drawBead(Graphics g, int deck, int rail, int bead,
			int j, boolean show, boolean moving, boolean highlight,
			int pressed, int offsetX, int offsetY) {
		int dx, dy, yOffset, special = 0, pieces, piecePercents;
		int color = 0;
		int d = -1, subj = 0, room;

		if (g == null)
			return;
		if (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) {
			return;
		}
		yOffset = (deck == AbacusFormat.UP) ? 0 :
			geo.getDeckHeight(AbacusFormat.TOP) + geo.getMiddleBarHeight() - 3;
		dx = (model.getRails() - rail - 1) * geo.getPos().x +
			geo.getDelta().x + geo.getOffset().x + offsetX;
		dy = (j - 1) * geo.getPos().y + geo.getDelta().y +
			yOffset + geo.getOffset().y - 2 + geo.getPressOffset().y +
			offsetY;
		room = model.getRoom(deck);
		if (model.checkSubdeck(3) && rail == model.getDecimalPosition() - 2) {
			if (AbacusFormat.getSubmodeSlotsSeparate(model.getSubmode())) {
				d = model.getSubdeckPosition(j);
				subj = model.getPositionSubdeck(j);
			} else {
				room = 0;
				for (d = 0; d < model.getSubdeck(); d++)
					room += model.getSubdecksRoom(d);
				d = -1;
			}
		}
		if (show) {
			int railI = model.getRailIndex();

			if (highlight)
				railI = 2;
			if ((rail == model.getRails() - 1) && model.getSign()) {
				special++;
			} else if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
				(rail == model.getDecimalPosition() - 1)) {
					pieces = model.getNumberPieces(AbacusFormat.BOTTOM);
					if ((model.getColorScheme() & AbacusInterface.COLOR_MIDDLE) != 0) {
				if ((((bead == pieces / 2) && ((pieces & 1) == 0)) ||
					bead == pieces / 2 + 1) && pieces > 2)
					special++;
					} else if ((model.getColorScheme() & AbacusInterface.COLOR_HALF) != 0) {
				if ((pieces & 1) != 0) {
						if (bead == pieces / 2 + 1)
					color++;
				} else if (bead > pieces / 2) {
						if (model.getOrientation(deck))
					color++;
				} else {
						if (!model.getOrientation(deck))
					color++;
				}
					} else {
				special++;
					}
			} else if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
				(rail == model.getDecimalPosition() -
				model.getShiftPercent() - 1 -
				((model.getPiece(AbacusFormat.BOTTOM) != 0) ? 1 : 0))) {
					piecePercents = model.getNumberPiecePercents(AbacusFormat.BOTTOM);
					if ((model.getColorScheme() & AbacusInterface.COLOR_MIDDLE) != 0) {
				if ((((bead == model.getPiecePercent(AbacusFormat.BOTTOM) / 2) &&
					((model.getPiecePercent(AbacusFormat.BOTTOM) & 1) == 0)) ||
					bead == model.getPiecePercent(AbacusFormat.BOTTOM) / 2 + 1) &&
					model.getPiecePercent(AbacusFormat.BOTTOM) > 2)
						special++;
					} else if ((model.getColorScheme() & AbacusInterface.COLOR_HALF) != 0) {
				if ((piecePercents & 1) != 0) {
						if (bead == piecePercents / 2 + 1)
					color++;
				} else if (bead > piecePercents / 2) {
					if (model.getOrientation(deck))
						color++;
				} else {
					if (!model.getOrientation(deck))
						color++;
				}
					} else {
				special++;
					}
			} else if (model.checkSubdeck(3) &&
				(rail == model.getDecimalPosition() - 2)) {
					if (((model.getSubdeck() - model.getSubdeckPosition(j)) % 2) == 0)
				special++;
			} else if (!((rail == model.getRails() - 1) && model.getSign()) &&
					(model.getColorScheme() & AbacusInterface.COLOR_MIDDLE) != 0) {
				if ((((bead == model.getNumber(deck) / 2) &&
					((model.getNumber(deck) & 1) == 0)) ||
					bead == model.getNumber(deck) / 2 + 1) &&
					model.getNumber(deck) > 2) {
				special++;
				}
				if ((model.getColorScheme() & AbacusInterface.COLOR_FIRST) != 0 &&
					deck == AbacusFormat.BOTTOM &&
					rail - model.getDecimalPosition() > 0 &&
					(rail - model.getDecimalPosition()) %
					model.getGroupSize() == 0) {
				if (bead == model.getNumber(deck) &&
						model.getOrientation(deck))
					special++;
				else if (bead == 1 &&
						!model.getOrientation(deck))
					special++;
				}
			} else if (!((rail == model.getRails() - 1) && model.getSign()) &&
					(model.getColorScheme() & AbacusInterface.COLOR_HALF) != 0) {
				if ((model.getNumber(deck) & 1) != 0) {
					if (bead == model.getNumber(deck) / 2 + 1)
						color++;
				} else if (bead > model.getNumber(deck) / 2) {
					if (model.getOrientation(deck))
						color++;
				} else {
					if (!model.getOrientation(deck))
						color++;
				}
			}
			if (model.getVertical()) {
				dy = geo.getFrameSize().y - geo.getBeadSize().y -
					dy - 3 + 2 * geo.getPressOffset().y;
			}
			dx += pressed * geo.getPressOffset().x;
			dy += pressed * geo.getPressOffset().y;
			if (!moving && pressed == 0) {
				/* Draw the rail around bead */
				g.setColor(geo.getBackground());
				vDraw.fillRect(g, dx, dy + geo.getBeadSize().y + geo.getPressOffset().y,
					geo.getBeadSize().x, 1);
				if (geo.getPressOffset().y != 0) {
					if (!model.getSlot() || (j != 1 && (d == -1 || subj != 1))) {
				g.setColor((model.getSlot()) ? geo.getBorderColor() : geo.getRailColor(railI));
				vDraw.fillRect(g,
					dx + geo.getBeadSize().x / 2 - geo.getRailWidth() / 2 + 1,
					dy - 1 + ((model.getVertical()) ? geo.getBeadSize().y : 0),
					geo.getRailWidth(), 3);
						}
						if (!model.getSlot() || (j != room && (d == -1 ||
					subj != model.getSubdecksRoom(d)))) {
				g.setColor((model.getSlot()) ? geo.getBorderColor() : geo.getRailColor(railI));
				vDraw.fillRect(g,
					dx + geo.getBeadSize().x / 2 - geo.getRailWidth() / 2 + 1,
					dy - 1 +
					((model.getVertical()) ? 0 : geo.getBeadSize().y),
					geo.getRailWidth(), 3);
					}
				}
			} else {
					/* Tweak */
					g.setColor(geo.getBackground());
					vDraw.fillRect(g, dx - geo.getPressOffset().x * pressed,
				dy - 2 * geo.getPressOffset().y * pressed + 1,
				1, geo.getBeadSize().y + 1);
					if (geo.getPressOffset().y != 0) {
						vDraw.fillRect(g, dx - geo.getPressOffset().x * pressed,
				dy - geo.getPressOffset().y * pressed,
				geo.getBeadSize().x + 1, 1);
						if (!model.getSlot() || (j != 1 && (d == -1 || subj != 1))) {
				g.setColor((model.getSlot()) ? geo.getBorderColor() : geo.getRailColor(railI));
				vDraw.fillRect(g,
					dx + geo.getBeadSize().x / 2 -
					geo.getRailWidth() / 2 - geo.getPressOffset().x * pressed + 1,
					dy - geo.getPressOffset().y * pressed,
					geo.getRailWidth(), 1);
						}
					}
			}
			{
				int sx, sy;
			if (model.getVertical()) {
				sx = geo.getPos().y - geo.getPressOffset().y;
				sy = geo.getBeadSize().x + 1;
				yOffset = dx;
				dx = dy + 1 - geo.getPressOffset().y;
				dy = yOffset;
			} else {
				sx = geo.getBeadSize().x + 1;
				sy = geo.getPos().y - geo.getPressOffset().y;
				dy = dy + 1 - geo.getPressOffset().y;
			}
			if (highlight)
				color = 2;
			/*drawBufferedBead(g, color, pressed, special, dx, dy);*/
			copyImage(g, bufferedBeadImage[color][pressed][special],
				dx, dy,
				0, 0,
				sx, sy);
			}
		} else {
			drawRail(g, deck, rail, j, highlight,
				((geo.getPressOffset().y == 0) ? 0 : pressed),
				geo.getBeadSize().y + 1 + 2 * geo.getPressOffset().y);
		}
	}

	void drawBufferedBead(Graphics g, int color, int pressed,
			int special, int dx, int dy) {
		int shadeFill, shadeLine, shadeDot;
		int railWid = Math.min(geo.getBeadSize().x - 5, geo.getRailWidth());

		if (pressed == 1) {
			/*if (diamond) {
				shadeDot = 2;
				shadeLine = shadeFill = 1;
			} else {*/
			shadeFill = 2;
			shadeLine = 1;
			shadeDot = 1;
			/*}*/
		} else {
			shadeFill = 1;
			shadeLine = 2;
			shadeDot = 0;
		}
		if (special == 1) {
			shadeFill++;
			shadeLine++;
			shadeDot++;
		}
		shadeFill += 4 * color;
		shadeLine += 4 * color;
		shadeDot += 4 * color;
		g.setColor(geo.getBackground());

		vDraw.fillRect(g, dx, dy, geo.getBeadSize().x + 1, geo.getPos().y);
		if (model.getDiamond()) {
			Point[] tempList = new Point[5];

			tempList[0] = new Point(dx +
				geo.getBeadSize().x / 2 + (railWid - 1) / 2 + 3,
				dy + geo.getBeadSize().y);
			tempList[1] = new Point(dx +
				geo.getBeadSize().x / 2 - railWid / 2 - 1,
				dy + geo.getBeadSize().y);
			tempList[2] = new Point(dx + 1, dy + geo.getBeadSize().y / 2);
			tempList[3] = new Point(dx + geo.getBeadSize().x,
				dy + geo.getBeadSize().y / 2);
			tempList[4] = new Point(dx +
				geo.getBeadSize().x / 2 + (railWid - 1) / 2 + 3,
				dy + geo.getBeadSize().y);
			vDraw.fillPolygon(g, geo.getBeadShade(shadeFill),
				geo.getBeadShade(shadeFill),
				tempList, 4);
			tempList[0] = new Point(dx +
				geo.getBeadSize().x / 2 - railWid / 2 - 1,
				dy);
			tempList[1] = new Point(dx +
				geo.getBeadSize().x / 2 + (railWid - 1) / 2 + 3,
				dy);
			tempList[2] = new Point(dx + geo.getBeadSize().x,
				dy + geo.getBeadSize().y / 2);
			tempList[3] = new Point(dx + 1, dy + geo.getBeadSize().y / 2);
			tempList[4] = new Point(dx +
				geo.getBeadSize().x / 2 - railWid / 2 - 1,
				dy);
			vDraw.fillPolygon(g, geo.getBeadShade(shadeDot),
				geo.getBeadShade(shadeDot),
				tempList, 4);
		} else {
				if (geo.getBeadSize().x >= geo.getBeadSize().y + railWid + 2) {
					{
			g.setColor(geo.getBeadShade(shadeLine));
			vDraw.drawCircle(g, geo.getBeadSize().y,
				dx + geo.getBeadSize().x / 2 -
				(geo.getBeadSize().x - geo.getBeadSize().y) / 2,
				dy + geo.getBeadSize().y / 2);
			vDraw.drawCircle(g, geo.getBeadSize().y,
				dx + geo.getBeadSize().x / 2 - 1 +
				(geo.getBeadSize().x - geo.getBeadSize().y) / 2,
				dy + geo.getBeadSize().y / 2);
					}
					{
			g.setColor(geo.getBeadShade(shadeLine));
			vDraw.drawRect(g, dx + geo.getBeadSize().x / 2 -
				(geo.getBeadSize().x - geo.getBeadSize().y) / 2, dy,
				geo.getBeadSize().x - geo.getBeadSize().y, geo.getBeadSize().y);
					}
			g.setColor(geo.getBeadShade(shadeFill));
			vDraw.fillCircle(g, geo.getBeadSize().y,
				dx + geo.getBeadSize().x / 2 -
				(geo.getBeadSize().x - geo.getBeadSize().y) / 2,
				dy + geo.getBeadSize().y / 2);
			vDraw.fillCircle(g, geo.getBeadSize().y,
				dx + geo.getBeadSize().x / 2 - 1 +
				(geo.getBeadSize().x - geo.getBeadSize().y) / 2,
				dy + geo.getBeadSize().y / 2);
			g.setColor(geo.getBeadShade(shadeFill));
			vDraw.fillRect(g, dx + geo.getBeadSize().x / 2 -
				(geo.getBeadSize().x - geo.getBeadSize().y) / 2, dy,
				geo.getBeadSize().x - geo.getBeadSize().y + 1,
				geo.getBeadSize().y);
			g.setColor(geo.getBeadShade(shadeDot));
			/*if (pressed == 0) {*/
				vDraw.fillCircle(g, geo.getBeadSize().y / 6,
					dx - geo.getBeadSize().y / 5 +
					geo.getBeadSize().x / 2 -
					(geo.getBeadSize().x - geo.getBeadSize().y) / 2,
					dy - geo.getBeadSize().y / 5 +
					(geo.getBeadSize().y - 1) / 2);
			/* } else {
				vDraw.fillCircle(g, geo.getBeadSize().y / 6,
					dx + geo.getBeadSize().y / 5 +
					geo.getBeadSize().x / 2 +
					(geo.getBeadSize().x - geo.getBeadSize().y) / 2,
					dy + geo.getBeadSize().y / 5 +
					(geo.getBeadSize().y - 1) / 2);
			}*/
			} else {
				/*(geo.getBeadSize().x < geo.getBeadSize().y + railWid + 2)*/
				int beadDiameter = geo.getBeadSize().x - railWid - 2;
				int beadOffset = geo.getBeadSize().y - beadDiameter;

			{
					g.setColor(geo.getBeadShade(shadeLine));
					vDraw.drawCircle(g, beadDiameter,
				dx + geo.getBeadSize().x / 2 -
				(geo.getBeadSize().x - beadDiameter) / 2,
				dy + beadDiameter / 2);
					vDraw.drawCircle(g, beadDiameter,
				dx + geo.getBeadSize().x / 2 +
				(geo.getBeadSize().x - beadDiameter) / 2,
				dy + beadDiameter / 2);
					vDraw.drawCircle(g, beadDiameter,
				dx + geo.getBeadSize().x / 2 -
				(geo.getBeadSize().x - beadDiameter) / 2,
				dy + beadDiameter / 2 + beadOffset);
					vDraw.drawCircle(g, beadDiameter,
				dx + geo.getBeadSize().x / 2 +
				(geo.getBeadSize().x - beadDiameter) / 2,
				dy + beadDiameter / 2 + beadOffset);
			}
			{
				g.setColor(geo.getBeadShade(shadeLine));
				vDraw.drawRect(g, dx + geo.getBeadSize().x / 2 -
			(geo.getBeadSize().x - beadDiameter) / 2, dy,
			geo.getBeadSize().x - beadDiameter,
			geo.getBeadSize().y);
				vDraw.drawRect(g, dx + geo.getBeadSize().x / 2 -
			(geo.getBeadSize().x - beadDiameter) / 2 -
			beadDiameter / 2,
			dy + beadDiameter / 2,
			geo.getBeadSize().x, beadOffset);
		}
					g.setColor(geo.getBeadShade(shadeFill));
					vDraw.fillCircle(g, beadDiameter,
				dx + geo.getBeadSize().x / 2 -
				(geo.getBeadSize().x - beadDiameter) / 2,
				dy + beadDiameter / 2);
					vDraw.fillCircle(g, beadDiameter,
				dx + geo.getBeadSize().x / 2 +
				(geo.getBeadSize().x - beadDiameter) / 2,
				dy + beadDiameter / 2);
					vDraw.fillCircle(g, beadDiameter,
				dx + geo.getBeadSize().x / 2 -
				(geo.getBeadSize().x - beadDiameter) / 2,
				dy + beadDiameter / 2 + beadOffset);
					vDraw.fillCircle(g, beadDiameter,
				dx + geo.getBeadSize().x / 2 +
				(geo.getBeadSize().x - beadDiameter) / 2,
				dy + beadDiameter / 2 + beadOffset);
					g.setColor(geo.getBeadShade(shadeFill));
					vDraw.fillRect(g, dx + geo.getBeadSize().x / 2 -
				(geo.getBeadSize().x - beadDiameter) / 2 + 1, dy,
				geo.getBeadSize().x - beadDiameter,
				geo.getBeadSize().y);
					vDraw.fillRect(g, dx + geo.getBeadSize().x / 2 -
				(geo.getBeadSize().x - beadDiameter) / 2 -
				beadDiameter / 2,
				dy + beadDiameter / 2,
				geo.getBeadSize().x + 1, beadOffset + 1);
					g.setColor(geo.getBeadShade(shadeDot));
					/*if (pressed == 0) {*/
				vDraw.fillCircle(g, beadDiameter / 6,
					dx - beadDiameter / 5 +
					geo.getBeadSize().x / 2 -
					(geo.getBeadSize().x -
					beadDiameter) / 2,
					dy - beadDiameter / 5 +
					(beadDiameter - 1) / 2);
					/*} else {
					vDraw.fillCircle(g, beadDiameter / 6,
						dx + beadDiameter / 5 +
						geo.getBeadSize().x / 2 +
						(geo.getBeadSize().x -
						beadDiameter) / 2,
						dy + beadDiameter / 5 +
						(beadDiameter - 1) / 2);
					}*/
			}
		}
	}

	void drawLineAndCounter(Graphics g, int rail, boolean highlight) {
		//only one has a line
		drawCounters(g, AbacusFormat.BOTTOM, rail,
			model.getPosition(AbacusFormat.BOTTOM, rail), false, highlight);
		drawCounters(g, AbacusFormat.BOTTOM, rail,
			model.getPosition(AbacusFormat.BOTTOM, rail), true, highlight);
		drawCounters(g, AbacusFormat.TOP, rail,
			model.getPosition(AbacusFormat.TOP, rail), true, highlight);
	}

	void drawBeadRail(Graphics g, int rail, boolean highlight) {
		int deck;

		if ((model.getPiece(AbacusFormat.BOTTOM) != 0 &&
				(rail == model.getDecimalPosition() - 1)) ||
				(model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
				(rail == model.getDecimalPosition() -
				model.getShiftPercent() - 1 -
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ?
				0 : 1))) || (model.checkSubdeck(3) &&
				(rail == model.getDecimalPosition() - 2 ||
				rail == model.getDecimalPosition() - 3))) {
			return;
		}
		if (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) {
			drawLineAndCounter(g, rail, highlight);
			return;
		}
		for (deck = AbacusFormat.BOTTOM; deck <= AbacusFormat.TOP; deck++) {
			int j;

			for (j = 1; j <= model.getPosition(deck, rail); j++)
				drawBead(g, deck, rail, j, j,
					true, false, highlight, 0, 0, 0);
			for (j = model.getPosition(deck, rail) + 1;
					j < model.getSpaces(deck) +
					model.getPosition(deck, rail) + 1; j++)
				drawBead(g, deck, rail, 0, j,
					false, false, highlight, 0, 0, 0);
			for (j = model.getSpaces(deck) +
					model.getPosition(deck, rail) + 1;
					j <= model.getRoom(deck); j++)
				drawBead(g, deck, rail,
					j - model.getSpaces(deck), j,
					true, false, highlight, 0, 0, 0);
		}
	}

	void drawAllCounters(Graphics g) {
		for (int deck = AbacusFormat.BOTTOM; deck <= AbacusFormat.TOP; deck++) {
			for (int rail = 0; rail < model.getRails(); rail++) {

				drawCounters(g, deck, rail, model.getPosition(deck, rail), true, false);
			}
		}
	}

	void drawAllBeads(Graphics g) {
		int deck, rail, j, spaces;

		if (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) {
			drawAllCounters(g);
			return;
		}
		if (model.getSign()) {
			deck = AbacusFormat.BOTTOM;
			rail = model.getRails() - 1;
			drawBead(g, deck, rail, 1, 1,
				(model.getPosition(deck, rail) == 1),
				false, false, 0, 0, 0);
			for (j = 2; j < model.getRoom(deck); j++)
				drawBead(g, deck, rail, 0, j, false,
					false, false, 0, 0, 0);
			drawBead(g, deck, rail, 1, model.getRoom(deck),
					(model.getPosition(deck, rail) == 0),
				false, false, 0, 0, 0);
		}
		if (model.getPiece(AbacusFormat.BOTTOM) != 0) {
			int pieces = 0;

			for (deck = AbacusFormat.BOTTOM; deck <= AbacusFormat.TOP; deck++) {
				rail = model.getDecimalPosition() - 1;
				pieces = model.getNumberPieces(deck);
				if (pieces == 0)
					continue;
				spaces = model.getRoom(deck) - pieces;
				for (j = 1; j <= model.getPosition(deck, rail);
						j++) {
					drawBead(g, deck, rail, j, j,
						true, false, false, 0, 0, 0);
				}
				for (j = model.getPosition(deck, rail) + 1;
						j < spaces +
						model.getPosition(deck, rail) +
						1; j++) {
					drawBead(g, deck, rail, 0, j,
						false, false, false, 0, 0, 0);
				}
				for (j = spaces + model.getPosition(deck, rail) + 1;
						j <= model.getRoom(deck); j++) {
					drawBead(g, deck, rail, j - spaces, j,
						true, false, false, 0, 0, 0);
				}
			}
		}
		if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
				model.getPiecePercent(AbacusFormat.BOTTOM) != 0) {
			int piecePercents = 0;

			for (deck = AbacusFormat.BOTTOM; deck <= AbacusFormat.TOP; deck++) {
				rail = model.getDecimalPosition() -
					model.getShiftPercent() - 1 -
					((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1);
				piecePercents = model.getNumberPiecePercents(deck);
				if (piecePercents == 0)
					continue;
				spaces = model.getRoom(deck) - piecePercents;
				for (j = 1; j <= model.getPosition(deck, rail); j++)
					drawBead(g, deck, rail, j, j,
						true, false, false, 0, 0, 0);
				for (j = model.getPosition(deck, rail) + 1;
						j < spaces +
						model.getPosition(deck, rail) + 1; j++)
					drawBead(g, deck, rail, 0, j,
						false, false, false, 0, 0, 0);
				for (j = spaces + model.getPosition(deck, rail) + 1;
						j <= model.getRoom(deck); j++)
					drawBead(g, deck, rail, j - spaces, j,
						true, false, false, 0, 0, 0);
			}
		}
		if (model.checkSubdeck(3)) {
			int specialOffset, d = AbacusFormat.BOTTOM;

			rail = model.getDecimalPosition() - 2;
			for (deck = 0; deck < model.getSubdeck(); deck++) {
				spaces = AbacusInterface.SUBDECK_SPACE;
				specialOffset = model.getNumberSubbeadsOffset(deck);
				for (j = 1; j <= model.getSubdecksPosition(deck);
						j++) {
					drawBead(g, d, rail, j + specialOffset,
						j + specialOffset,
						true, false, false, 0, 0, 0);
				}
				for (j = model.getSubdecksPosition(deck) + 1;
						j < spaces +
						model.getSubdecksPosition(deck) + 1;
						j++) {
					drawBead(g, d, rail, specialOffset, j + specialOffset,
						false, false, false, 0, 0, 0);
				}
				for (j = spaces + model.getSubdecksPosition(deck) + 1;
						j <= model.getSubdecksRoom(deck);
						j++) {
					drawBead(g, d, rail, j + specialOffset - spaces,
						j + specialOffset,
						true, false, false, 0, 0, 0);
				}
			}
		}
		for (rail = 0; rail < model.getRails() - ((model.getSign()) ? 1 : 0);
				rail++) {
			drawBeadRail(g, rail, false);
		}
		//setResultRegister(0, model.getDecimalPosition(), 0);
	}
}
