package org.tux.bagleyd.util;

/*
 * @(#)OrientDraw.java
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

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;

/**
 * The <code>OrientDraw</code> class does all the low level graphics.
 * This is to have a easy way to switch from graphics that has a
 * horizontal orientation to a vertical orientation.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */

public class OrientDraw {
	/* Note On Draws:
	 * There is some overlap i.e. it draws some extra pixels.
	 * This is to synchronize logic for Java, X and Windows.
	 */
	Boolean vertical;

	public OrientDraw(boolean vert) {
		this.vertical = vert;
	}

	private static void orientDrawCircle(Graphics g,
			int diameter, int x, int y) {
		int xCenter = x - diameter / 2, yCenter = y - diameter / 2;

		if (diameter <= 0)
			return;
		g.drawOval(xCenter, yCenter, diameter + 1, diameter + 1);
		g.drawOval(xCenter, yCenter, diameter, diameter);
		g.drawOval(xCenter + 1, yCenter, diameter, diameter);
		g.drawOval(xCenter, yCenter + 1, diameter, diameter);
		g.drawOval(xCenter + 1, yCenter + 1, diameter, diameter);
	}

	private static void orientFillCircle(Graphics g,
			int diameter, int x, int y) {
		if (diameter <= 0)
			return;
		g.fillOval(x - diameter / 2, y - diameter / 2,
			diameter + 1, diameter + 1);
	}

	private static void orientFillPolygon(Graphics g,
			Color fillColor, Color lineColor,
			Point[] list, int n) {
		int[] tempListX = new int[n];
		int[] tempListY = new int[n];

		for (int i = 0; i < n; i++) {
			tempListX[i] = list[i].x;
			tempListY[i] = list[i].y;
		}
		g.setColor(fillColor);
		g.fillPolygon(tempListX, tempListY, n);
		g.setColor(lineColor);
		g.drawPolygon(tempListX, tempListY, n);
	}

	private static void fillRectClipX(Graphics g,
			int dx, int dy, int sx, int sy,
			int ox, int wox, int wsx) {
		int nox = ox, nsx = sx;

		if (ox + sx < wox || ox > wox + wsx || wsx <= 0)
			return;
		if (nox < wox) {
			nox = wox;
			nsx = sx - wox + ox;
		}
		if (nox + nsx > wox + wsx) {
			nsx = wsx + wox - nox;
		}
		g.fillRect(dx + nox, dy, nsx, sy);
	}

	private static void fillRectClipY(Graphics g,
			int dx, int dy, int sx, int sy,
			int oy, int woy, int wsy) {
		int noy = oy, nsy = sy;

		if (oy + sy < woy || oy > woy + wsy || wsy <= 0)
			return;
		if (noy < woy) {
			noy = woy;
			nsy = sy - woy + oy;
			
		}
		if (noy + nsy > woy + wsy) {
			nsy = wsy + woy - noy;
		}
		g.fillRect(dx, dy + noy, sx, nsy);
	}

	public void drawLine(Graphics g, int x1, int y1, int x2, int y2) {
		if (vertical) {
			g.drawLine(y1, x1, y2, x2);
		} else {
			g.drawLine(x1, y1, x2, y2);
		}
	}

	public void drawRect(Graphics g, int i, int j, int l, int h) {
		if (vertical) {
			g.drawRect(j, i, h, l);
		} else {
			g.drawRect(i, j, l, h);
		}
	}

	public void fillRect(Graphics g, int i, int j, int l, int h) {
		if (vertical) {
			g.fillRect(j, i, h, l);
		} else {
			g.fillRect(i, j, l, h);
		}
	}

	public void drawCircle(Graphics g, int d, int i, int j) {
		if (vertical) {
			orientDrawCircle(g, d, j, i);
		} else {
			orientDrawCircle(g, d, i, j);
		}
	}

	public void fillCircle(Graphics g, int d, int i, int j) {
		if (vertical) {
			orientFillCircle(g, d, j, i);
		} else {
			orientFillCircle(g, d, i, j);
		}
	}

	public void fillPolygon(Graphics g, Color fillColor, Color lineColor,
			Point[] list, int n) {
		if (vertical) {
			int i, t;

			for (i = 0; i < n; i++) {
				t = list[i].x;
				list[i].x = list[i].y;
				list[i].y = t;
			}
			orientFillPolygon(g, fillColor, lineColor, list, n);
		} else {
			orientFillPolygon(g, fillColor, lineColor, list, n);
		}
	}
	
	public void fillRectClip(Graphics g, int dx, int dy, int sx, int sy,
			int o, int wo, int ws) {
		if (vertical) {
			fillRectClipX(g, dy, dx, sy, sx, o, wo, ws);
		} else {
			fillRectClipY(g, dx, dy, sx, sy, o, wo, ws);
		}
	}
}
