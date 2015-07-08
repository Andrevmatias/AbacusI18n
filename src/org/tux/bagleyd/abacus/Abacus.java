package org.tux.bagleyd.abacus;

/*
 * @(#)Abacus.java
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

import java.applet.Applet;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.tux.bagleyd.abacus.model.AbacusFormat;
import org.tux.bagleyd.abacus.model.NumberField;
import org.tux.bagleyd.abacus.view.AbacusDraw;
import org.tux.bagleyd.abacus.view.AbacusGeometry;
import org.tux.bagleyd.util.OrientDraw;

/**
 * The <code>Abacus</code> class extends the
 * <code>AbacusCanvas</code> for handling the responses to the user
 * for the <code>AbacusApplet</code> class.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */

public class Abacus extends AbacusCanvas implements MouseWheelListener,
		MouseListener, MouseMotionListener, KeyListener {
	private static final long serialVersionUID = 42L;

	public Abacus(Applet applet,
			Color fg, Color bg, Color borderc,
			Color beadc1, Color beadc2, Color beadc3,
			Color railc1, Color railc2, Color railc3, Color railc4,
			int delay, int rails, boolean vertical,
			int colorScheme, boolean slot,
			boolean diamond, int railIndex,
			boolean topOrient, boolean bottomOrient,
			int topNumber, int bottomNumber,
			int topFactor, int bottomFactor,
			int topSpaces, int bottomSpaces,
			int topPiece, int bottomPiece,
			int topPiecePercent, int bottomPiecePercent,
			int shiftPercent, int subdeck, int subbead,
			boolean sign, int decimalPosition,
			boolean group, int groupSize,
			boolean decimalComma, int base, int subbase,
			int anomaly, int shiftAnomaly,
			int anomalySq, int shiftAnomalySq,
			int displayBase, int pressOffsetY,
			boolean romanNumerals, boolean latin,
			boolean ancientRoman, boolean modernRoman,
			int mode, int submode) {
		firstPaint = true;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		this.addKeyListener(this);
		this.applet = applet;
		if (model == null) {
			model = new AbacusFormat(rails);
			field = new NumberField();
			geo = null;
		}
		model.setMode(mode);
		model.setSubmode(submode);
		model.setBase(base);
		model.setSubbase(subbase);
		model.setRails(rails);
		model.setVertical(vertical);
		model.setColorScheme(colorScheme);
		model.setSlot(slot);
		model.setDiamond(diamond);
		model.setRailIndex(railIndex);
		model.setOrientation(AbacusFormat.TOP, topOrient);
		model.setOrientation(AbacusFormat.BOTTOM, bottomOrient);
		model.setNumber(AbacusFormat.TOP, topNumber);
		model.setNumber(AbacusFormat.BOTTOM, bottomNumber);
		model.setFactor(AbacusFormat.TOP, topFactor);
		model.setFactor(AbacusFormat.BOTTOM, bottomFactor);
		model.setSpaces(AbacusFormat.TOP, topSpaces);
		model.setSpaces(AbacusFormat.BOTTOM, bottomSpaces);
		model.setPiece(AbacusFormat.TOP, topPiece);
		model.setPiece(AbacusFormat.BOTTOM, bottomPiece);
		model.setPiecePercent(AbacusFormat.TOP, topPiecePercent);
		model.setPiecePercent(AbacusFormat.BOTTOM, bottomPiecePercent);
		model.setShiftPercent(shiftPercent);
		model.setSubdeck(subdeck);
		model.setSubbead(subbead);
		model.setSign(sign);
		model.setDecimalPosition(decimalPosition);
		model.setGroupSize(groupSize);
		model.setAnomaly(anomaly);
		model.setShiftAnomaly(shiftAnomaly);
		model.setAnomalySq(anomalySq);
		model.setShiftAnomalySq(shiftAnomalySq);
		model.setModernRoman(modernRoman);
		field.setGroup(group);
		field.setDecimalComma(decimalComma);
		field.setDisplayBase(displayBase);
		field.setRomanNumerals(romanNumerals);
		field.setLatin(latin);
		field.setAncientRoman(ancientRoman);
		model.initializeAbacus();
		if (geo == null) {
			geo = new AbacusGeometry(model);
			aDraw = null;
		}
		if (geo.getCoreSize().x != getWidth()
				|| geo.getCoreSize().y != getHeight()) {
			geo.resize(getWidth(), getHeight());
			aDraw = null;
		}
		if (aDraw == null && (getWidth() != 0 || getHeight() != 0)) {
			aDraw = new AbacusDraw(model, geo, this);
		}
		vDraw = new OrientDraw(model.getVertical());
		geo.setFrameColor(fg);
		geo.setBackground(bg);
		geo.setForeground(borderc);
		geo.setBorderColor(borderc);
		geo.setRailColor(0, railc1);
		geo.setRailColor(1, railc2);
		geo.setRailColor(2, railc3);
		geo.setRailColor(3, railc4);
		geo.setSymbolColor(beadc1);
		geo.setBeadColor(0, beadc1);
		geo.setBeadColor(1, beadc2);
		geo.setBeadColor(2, beadc3);
		geo.setDelay(delay);
		geo.setNumberSlices((delay < 5 * AbacusGeometry.MAX_SLICES) ?
			delay / 5 + 1 : AbacusGeometry.MAX_SLICES);
		geo.setPressOffsetY(pressOffsetY);
		setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	public void processKey(char key, boolean control) {
		switch (key) {
		case 'h':
		case 'H':
			((AbacusApplet)applet).popApplet();
			break;
		case '>':
		case '.':
			speedUpAbacus();
			break;
		case '<':
		case ',':
			slowDownAbacus();
			break;
		case '@':
			((AbacusApplet)applet).toggleSound();
			break;
		case '?':
			((AbacusApplet)applet).descriptionHelp();
			break;
		case '!':
			((AbacusApplet)applet).featuresHelp();
			break;
		case '^':
			((AbacusApplet)applet).referencesHelp();
			break;
		case 'a':
		case 'A':
			((AbacusApplet)applet).aboutHelp();
			break;
		case ' ':
			if (demo)
				showMoreAbacus();
			break;
		case 'c':
		case 'C':
			clearAbacus();
			break;
		case '~':
		case '`':
			complementAbacus();
			break;
		case 'i':
		case 'I':
			incrementAbacus();
			break;
		case 'd':
		case 'D':
			decrementAbacus();
			break;
		case 'f':
		case 'F':
			changeFormatAbacus();
			break;
		case 'm':
		case 'M':
			changeMuseumAbacus();
			break;
		case 'v':
		case 'V':
			toggleRomanNumeralsAbacus();
			break;
		/*case '(':
		case ')':
			toggleOldRomanNumeralsAbacus();
			break;*/
		case 'g':
		case 'G':
			toggleGroupingAbacus();
			break;
		case 's':
		case 'S':
			toggleNegativeSignAbacus();
			break;
		case 'u':
		case 'U':
			toggleQuartersAbacus();
			break;
		case 'p':
		case 'P':
			toggleQuarterPercentsAbacus();
			break;
		case 't':
		case 'T':
			toggleTwelfthsAbacus();
			break;
		case 'b':
		case 'B':
			toggleSubdecksAbacus();
			break;
		case 'e':
		case 'E':
			toggleEighthsAbacus();
			break;
		case 'l':
		case 'L':
			toggleAnomalyAbacus();
			break;
		case 'w':
		case 'W':
			toggleWatchAbacus();
			break;
		case 'o':
		case 'O':
			toggleDemoAbacus();
			break;
		case '$':
			toggleTeachAbacus();
			break;
		case '+':
			toggleRightToLeftAbacusAdd();
			break;
		case '*':
			toggleRightToLeftAbacusMult();
			break;
		case 'n':
		case 'N':
			if (demo)
				showNextAbacus();
			break;
		case 'r':
		case 'R':
			if (demo)
				showRepeatAbacus();
			break;
		case 'j':
		case 'J':
			if (demo)
				showJumpAbacus();
			break;
		/*case '0':*/
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		/*case '7':*/
		case '8':
		/*case '9':*/
			if (demo && key >= '1' && key <= '5') {
				showChapterAbacus(key - '1');
			} else if (!demo && (key == 2 || key == 4 || key == 6 || key == 8)) {
				moveAbacusInput(lastX, lastY, key, control);
			}
			break;
		case 'q':
		case 'Q':
		case 'x':
		case 'X':
		case '\03': /* Control-C */
			if (demo) {
				toggleDemoAbacus();
				break;
			}
			((AbacusApplet)applet).quit();
			break;
		default:
		}
	}

	public void mousePressed(MouseEvent event) {
		int modifiers = event.getModifiers();

		if (mouseDown)
			return;
		// would need to reset Teach but modal
		if (demo) {
			abacusDemo.moreDemo();
			return;
		}
		if (((modifiers & Event.META_MASK) == 0) &&
				((modifiers & Event.ALT_MASK) == 0)) {
			selectAbacus(event.getX(), event.getY());
			mouseDown = true;
		} else if (modifiers == InputEvent.BUTTON3_MASK) {
			if ((event.getClickCount() & 1) == 1) {
				clearWithQueryAbacus();
			} else {
				clearAbacus();
			}
		}
	}

	public void mouseClicked(MouseEvent event) {
		/* Method required but mousePressed does work */
	}

	public void mouseReleased(MouseEvent event) {
		if (mouseDown) { // Do not care which mouse button
			releaseAbacus(event.getX(), event.getY());
			mouseDown = false;
		}
	}

	public void mouseEntered(MouseEvent event) {
		if (geo == null)
			return;
		geo.setForeground(geo.getFrameColor());
		framePaint = true;
		mouseDown = false;
		repaint();
	}

	public void mouseExited(MouseEvent event) {
		if (mouseDown && currentDeck >= 0) {
			currentSpace = -1;
			releasePaint = true;
		}
		geo.setForeground(geo.getBorderColor());
		framePaint = true;
		mouseDown = false;
		repaint();
	}

	public void mouseDragged(MouseEvent event) {
		lastX = event.getX();
		lastY = event.getY();
	}

	public void mouseMoved(MouseEvent event) {
		lastX = event.getX();
		lastY = event.getY();
	}

	public void mouseWheelMoved(MouseWheelEvent event) {
		int notches = event.getWheelRotation();
		int modifiers = event.getModifiers();

		if (((modifiers & Event.META_MASK) == 0) &&
				((modifiers & Event.ALT_MASK) == 0)) {
			wheelAbacus(notches, event.getX(), event.getY());
		}
	}

	public void keyPressed(KeyEvent event) {
		int modifiers = event.getModifiers();
		boolean control = ((modifiers & Event.CTRL_MASK) != 0);

		switch (event.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			((AbacusApplet)applet).shuffleDown();
			return;
		case KeyEvent.VK_UP:
			moveAbacusInput(lastX, lastY, '8', control);
			return;
		case KeyEvent.VK_LEFT:
			moveAbacusInput(lastX, lastY, '4', control);
			return;
		case KeyEvent.VK_RIGHT:
			moveAbacusInput(lastX, lastY, '6', control);
			return;
		case KeyEvent.VK_DOWN:
			moveAbacusInput(lastX, lastY, '2', control);
			return;
		default:
			processKey(event.getKeyChar(), control);
		}
	}

	public void keyReleased(KeyEvent event) {
		/* Method required but keyPressed does work */
	}

	public void keyTyped(KeyEvent event) {
		/* Method required but keyPressed does work */
	}
}
