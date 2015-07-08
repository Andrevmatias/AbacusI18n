package org.tux.bagleyd.abacus;

/*
 * @(#)AbacusCanvas.java
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
import java.awt.Canvas;
import java.awt.Graphics;
import java.util.Random;

import org.tux.bagleyd.abacus.learn.AbacusDemo;
import org.tux.bagleyd.abacus.model.AbacusFormat;
import org.tux.bagleyd.abacus.model.NumberField;
import org.tux.bagleyd.abacus.view.AbacusDraw;
import org.tux.bagleyd.abacus.view.AbacusGeometry;
import org.tux.bagleyd.util.OrientDraw;

/**
 * The <code>AbacusCanvas</code> class does all the graphics for
 * <code>AbacusApplet</code> class.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */

public class AbacusCanvas extends Canvas {
	private static final long serialVersionUID = 42L;
	AbacusMath abacusMath = new AbacusMath();
	protected volatile AbacusFormat model = null;
	protected volatile NumberField field = null;
	protected volatile AbacusGeometry geo = null;
	protected volatile AbacusDraw aDraw = null;

	boolean debug = false;
	int currentDeck = AbacusInterface.ACTION_IGNORE;
	int currentRail, currentPosition;
	int currentSpace = -1;
	int lastX, lastY;
	String multiplier, divisor;
	boolean demo = false, aux = false;
	boolean precedenceBegin, precedenceEnd, signBead = false;

	Applet applet = null;
	boolean firstPaint = false, resizePaint = false;
	boolean framePaint = false, resetPaint = false;
	boolean selectPaint = false, releasePaint = false;
	int wheelPaint = 0;
	boolean mouseDown = false;
	boolean enoughRails = true, startOperation, negativeResult;
	boolean minusSign = false;
	OrientDraw vDraw = null;
	Random generator;
	AbacusDemo abacusDemo = null;
	String expression, operateOn, origExpression;
	String lhOperand = "";
	String divResult = "", prevResult = "";
	char operand;
	String museum = "--";
	String format = "Generic";

	public void clearAbacus() {
		/* Should check if one really wants to destroy calculations. */
		if (mouseDown)
			return;
		resetPaint = true;
		repaint();
	}

	public void clearWithQueryAbacus() {
		if (!field.isEmptyResultRegister() && !aux) {
			clearAbacus();
		}
	}

	public int getRails() {
		return model.getRails();
	}

	public int getMode() {
		return model.getMode();
	}

	public void setMode(int value) {
		model.setMode(value);
	}

	public boolean getSlot() {
		// not sure why needed
		if (model.getMode() == AbacusInterface.Modes.Roman.ordinal())
			return true;
		return model.getSlot();
	}

	public int getBase() {
		return model.getBase();
	}

	public int getDecimalPosition() {
		return model.getDecimalPosition();
	}

	public int getShiftPercent() {
		return model.getShiftPercent();
	}

	public int getSubdeck() {
		return model.getSubdeck();
	}

	public int getSubbase() {
		return model.getSubbase();
	}

	public boolean getSign() {
		return model.getSign();
	}

	public int getAnomaly() {
		return model.getAnomaly();
	}

	public int getAnomalySq() {
		return model.getAnomalySq();
	}

	public int getShiftAnomaly() {
		return model.getShiftAnomaly();
	}

	public int getShiftAnomalySq() {
		return model.getShiftAnomalySq();
	}

	public int getTopFactor() {
		return model.getFactor(AbacusFormat.TOP);
	}

	public int getBottomFactor() {
		return model.getFactor(AbacusFormat.BOTTOM);
	}

	public int getTopNumber() {
		return model.getNumber(AbacusFormat.TOP);
	}

	public int getBottomNumber() {
		return this.model.getFactor(AbacusFormat.BOTTOM);
	}

	public int getTopPiece() {
		return model.getPiece(AbacusFormat.TOP);
	}

	public int getBottomPiece() {
		return this.model.getPiece(AbacusFormat.BOTTOM);
	}

	public int getTopPiecePercent() {
		return model.getPiecePercent(AbacusFormat.TOP);
	}

	public int getBottomPiecePercent() {
		return this.model.getPiecePercent(AbacusFormat.BOTTOM);
	}
	
	public static int convertBaseToBottom(int base) {
		return AbacusFormat.convertBaseToBottom(base);
	}

	public boolean checkPiece() {
		return model.checkPiece();
	}
	
	public boolean checkPiecePercent() {
		return model.checkPiecePercent();
	}
	
	public boolean checkSubdeck(int value) {
		return model.checkSubdeck(value);
	}

	public boolean checkAnomaly() {
		return model.checkAnomaly();
	}

	public boolean checkAnomalySq() {
		return model.checkAnomalySq();
	}

	public int getDisplayBase() {
		return field.getDisplayBase();
	}

	public boolean getDecimalComma() {
		return field.getDecimalComma();
	}

	public Abacus getAbacus(int auxiliary) {
		return ((AbacusApplet)applet).getAbacus(auxiliary);
	}

	public void setValuesBaseAbacus(int base, int displayBase) {
		model.setBase(base);
		field.setDisplayBase(displayBase);
		initializeAbacus();
		resizePaint = true;
		repaint();
	}

	public void setValuesModeAbacus(
			boolean topOrient, boolean bottomOrient,
			int topNumber, int bottomNumber,
			int topFactor, int bottomFactor,
			int topSpaces, int bottomSpaces,
			int base, int displayBase, int mode) {
		model.setOrientation(AbacusFormat.TOP, topOrient);
		model.setOrientation(AbacusFormat.BOTTOM, bottomOrient);
		model.setNumber(AbacusFormat.TOP, topNumber);
		model.setNumber(AbacusFormat.BOTTOM, bottomNumber);
		model.setFactor(AbacusFormat.TOP, topFactor);
		model.setFactor(AbacusFormat.BOTTOM, bottomFactor);
		model.setSpaces(AbacusFormat.TOP, topSpaces);
		model.setSpaces(AbacusFormat.BOTTOM, bottomSpaces);
		model.setBase(base);
		model.setMode(mode);
		field.setDisplayBase(displayBase);
		initializeAbacus();
		resizePaint = true;
		repaint();
	}

	public void setValuesTeachAbacus(int displayBase, boolean sign,
			int anomaly, int anomalySq) {
		field.setDisplayBase(displayBase);
		model.setSign(sign);
		model.setAnomaly(anomaly);
		model.setAnomaly(anomalySq);
		initializeAbacus();
		resizePaint = true;
		repaint();
	}

	boolean abacusMoveFast(int deck, int rail, int number) {
		//no delay - used for chinese -> japanese conversion
		if (!enoughRails)
			return false;
		if (model.checkMove(deck, rail, number, false)) {
			moveBeadsByValue(deck, rail + model.getDecimalPosition(), number,
				false);
			return true;
		} /*else {
			if (number > 0)
				if (deck > 0) {
					abacusMoveFast(AbacusFormat.TOP, rail, -1);
					abacusMoveFast(BOTTOM, rail + 1, 1);
				} else {
					//move the bead on upper deck
					abacusMoveFast(AbacusFormat.TOP, rail, 1);
					//take away beads.
					abacusMoveFast(BOTTOM, rail,
						number - 5);
				}
			else
				if (deck > 0) {
					abacusMoveFast(AbacusFormat.TOP, rail, 2 + number);
					abacusMoveFast(BOTTOM, rail + 1, -1);
				} else {
					//move the bead on upper deck
					abacusMoveFast(AbacusFormat.TOP, rail, -1);
					//take away beads.
					abacusMoveFast(BOTTOM, rail, 5 + number);
				}
		}*/
		return false;
	}

	public void abacusMove(int auxiliary, int deck, int rail, int number) {
		/*this.aux = auxiliary;
		this.deck = deck;
		this.rail = rail;
		this.number = number;*/
		((AbacusApplet)applet).callbackAbacusDemo(auxiliary, deck, rail, number);
	}

	void directMove(int deck, int rail, int number) {
		if (model.checkMove(deck, rail, number, true)) {
			moveBeadsByValue(deck, rail + model.getDecimalPosition(), number,
				false);
		}
	}

	void checkBeads() {
		if (model.getBase() != AbacusInterface.DEFAULT_BASE && demo) {
			System.out.println(
				"Base must be equal to " +
				AbacusInterface.DEFAULT_BASE + ", for demo");
			model.setBase(AbacusInterface.DEFAULT_BASE);
		} else if (field.getDisplayBase() != AbacusInterface.DEFAULT_BASE && demo) {
			System.out.println(
				"Display base must be equal to " +
				AbacusInterface.DEFAULT_BASE + ", for demo");
			field.setDisplayBase(AbacusInterface.DEFAULT_BASE);
		}
		if (model.getMode() == AbacusInterface.Modes.Generic.ordinal() && demo) {
			System.out.println(
				"Format must not be \"Generic\", for demo");
		}
		model.checkBeads();
		field.checkBeads();
		vDraw = new OrientDraw(model.getVertical());
		if (demo && !aux) { /* Trying to keep these at a minimum... */
			if (model.getRails() < AbacusInterface.MIN_DEMO_RAILS) {
				System.out.println(
					"Number of rails must be at least " +
					AbacusInterface.MIN_DEMO_RAILS +
					", for demo");
				for (int i = 0; i < AbacusInterface.MIN_DEMO_RAILS - model.getRails(); i++)
					((AbacusApplet)applet).callbackAbacus(this,
						AbacusInterface.ACTION_INCREMENT);
					model.setRails(AbacusInterface.MIN_DEMO_RAILS);
			}
			if (model.getRails() - model.getDecimalPosition() <
					AbacusInterface.MIN_DEMO_RAILS) {
				if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0) {
					model.setPiecePercent(AbacusFormat.BOTTOM, 0);
					((AbacusApplet)applet).callbackAbacus(this,
						AbacusInterface.ACTION_QUARTER_PERCENT);
				}
				if (model.getPiece(AbacusFormat.BOTTOM) != 0) {
					model.setPiece(AbacusFormat.BOTTOM, 0);
					((AbacusApplet)applet).callbackAbacus(this,
						AbacusInterface.ACTION_QUARTER);
				}
				model.setDecimalPosition(0);
			}

		}
	}

	void resetBeads() {
		currentDeck = AbacusInterface.ACTION_IGNORE;
		field.resetField(model.getRails());
		model.resetBeads();
		model.resetSubdecks();
	}

	// mostly depends on field not other input
	void setResultRegister(int deck, int rail, int number) {
		if (debug)
			System.out.println("setResultRegister: deck " + deck +
				", rail " + rail + ", number " + number);
		int n = 0, s = 0, i;
		int m = 0, o = 0, half;
		int rom = AbacusMath.sizeofRoman(model.getBase(), field.getRomanNumerals(),
			field.getAncientRoman());
		String st;
		StringBuffer buffer = new StringBuffer(12 + 3 * field.getNumberDigits() / 2 +
			rom);
		boolean anomalyActive;

		/* n digits above decimal *
		 * m digits below decimal */
		while (n < field.getNumberDigits() - 2 - model.getDecimalPosition() &&
				field.getDigitCharAt(n) == '0')
			n++;
		while (m < model.getDecimalPosition() - 1 &&
				field.getDigitCharAt(field.getNumberDigits() - NumberField.CARRY - m) == '0')
			m++;
		while (o < field.getNumberDigits() - 1 && field.getDigitCharAt(o) == '0')
			o++;
		half = field.getNumberDigits() - model.getDecimalPosition() - n - 1;
		s = (model.getSign() &&
			(((model.getPosition(AbacusFormat.BOTTOM, model.getRails() - 1) == 1 &&
			!model.getOrientation(AbacusFormat.BOTTOM)) ||
			(model.getPosition(AbacusFormat.BOTTOM, model.getRails() - 1) == 0 &&
			model.getOrientation(AbacusFormat.BOTTOM))) ||
			(model.getNumber(AbacusFormat.TOP) != 0 &&
			model.getPiecePercent(AbacusFormat.TOP) != 0 &&
			((model.getPosition(AbacusFormat.TOP, model.getRails() - 1) == 1 &&
			!model.getOrientation(AbacusFormat.TOP)) ||
			(model.getPosition(AbacusFormat.TOP, model.getRails() - 1) == 0 &&
			model.getOrientation(AbacusFormat.TOP))))) &&
			o < field.getNumberDigits() - 1) ? 1 : 0;
		buffer.setLength(12 + field.getNumberDigits() + rom);
		if (s == 1)
			buffer.setCharAt(0, '-');
		for (i = 0; i < half; i++)
			buffer.setCharAt(s + i, field.getDigitCharAt(n + i));
		buffer.setCharAt(s + half, field.decimalChar());
		if (model.getPiece(AbacusFormat.BOTTOM) != 0) {
			StringBuffer stringBuf =
				new StringBuffer(field.getNumberDigits() + 2);
			StringBuffer midBuf =
				new StringBuffer(field.getNumberDigits() + 2);
			StringBuffer finalBuf =
				new StringBuffer(field.getNumberDigits() + 2);
			int pieces = model.getPiece(AbacusFormat.BOTTOM);
			int precision;

			precision = ((model.getPiecePercent(AbacusFormat.BOTTOM) == 0) ?
				1 : 2) * model.getShiftPercent() + 2;
			if ((precision <= 2 * model.getShiftPercent() + 2) &&
				model.checkSubdeck(3)) {
				precision = 2 * model.getShiftPercent() + 2;
			}
			if (precision <= model.getDecimalPosition())
				precision = model.getDecimalPosition() +
					((model.getPiecePercent(AbacusFormat.BOTTOM) == 0) ? 1 : 0);
			if (model.getPiece(AbacusFormat.TOP) != 0)
				pieces *= model.getPiece(AbacusFormat.TOP);
			stringBuf.setLength(field.getNumberDigits() + 2);
			midBuf.setLength(field.getNumberDigits() + 2);
			finalBuf.setLength(field.getNumberDigits() + 2);
			AbacusMath.dividePieces(midBuf, model.getBase(), pieces,
				AbacusMath.char2Int(field.getDigitCharAt(n + half)),
				precision, field.decimalChar());
			if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0) {
				int piecePercents =
					model.getPiecePercent(AbacusFormat.BOTTOM);

				if (model.getPiecePercent(AbacusFormat.TOP) != 0)
					piecePercents *=
						model.getPiecePercent(AbacusFormat.TOP);
				/* get piecePercent part */
				AbacusMath.dividePieces(stringBuf, model.getBase(), piecePercents,
					AbacusMath.char2Int(field.getDigitCharAt(n + half + model.getShiftPercent() + 1)),
					precision, field.decimalChar());
				/* shift to proper piece percent place */
				AbacusMath.shiftDecimal(finalBuf, stringBuf.toString(),
					model.getShiftPercent(), 0, field.decimalChar());
				/* shift out piece part */
				/*shiftDecimal(midBuf, buffer, -1, 0, field.decimalChar()); */
				AbacusMath.addStrings(stringBuf, finalBuf.toString(),
					midBuf.toString(), model.getBase(), field.decimalChar());
				for (i = 0; i < model.getDecimalPosition() - m; i++)
					buffer.setCharAt(s + half + 1 + i,
						field.getDigitCharAt(n + half + 1 + i));
				if (m == model.getDecimalPosition() - 1)
					m--;
				buffer.setLength(s + half + model.getDecimalPosition() - m);
				/* shift out piecePercent part */
				AbacusMath.shiftDecimal(midBuf, buffer.toString(),
					-1, model.getShiftPercent(), field.decimalChar());
				/* get pieces + piece percent + normal */
				AbacusMath.addStrings(finalBuf, stringBuf.toString(),
					midBuf.toString(), model.getBase(), field.decimalChar());
			} else if (model.checkSubdeck(3)) {
				int pieceFractions = model.getPiece(AbacusFormat.BOTTOM);

				if (model.getPiece(AbacusFormat.TOP) != 0)
					pieceFractions *= model.getPiece(AbacusFormat.TOP);
				/* get pieceFraction part */
				AbacusMath.dividePieces(finalBuf, model.getBase(),
					pieceFractions * model.getSubbase(),
					AbacusMath.char2Int(field.getDigitCharAt(n + half + 1)),
					precision, field.decimalChar());
				/* get piece + pieceFraction */
				AbacusMath.addStrings(stringBuf, finalBuf.toString(),
					midBuf.toString(), model.getBase(), field.decimalChar());
				/* No fixing because no digits after Roman fraction */
				/* get pieces + piece percent + normal */
				AbacusMath.addStrings(finalBuf, stringBuf.toString(),
					buffer.toString(), model.getBase(), field.decimalChar());

			} else {
				for (i = 0; i < model.getDecimalPosition() - m; i++)
					buffer.setCharAt(s + half + 1 + i,
						field.getDigitCharAt(n + half + 1 + i));
				if (m == model.getDecimalPosition() - 1)
					m--;
				buffer.setLength(s + half + model.getDecimalPosition() - m);
				AbacusMath.addStrings(finalBuf, buffer.toString(),
					midBuf.toString(), model.getBase(), field.decimalChar());
			}
			buffer = new StringBuffer(finalBuf.toString().trim());
		}
		if (model.getPiece(AbacusFormat.BOTTOM) == 0 &&
				model.getPiecePercent(AbacusFormat.BOTTOM) == 0) {
			int dOffset = model.getDecimalPosition() - m + 1;

			if (dOffset < 0)
				dOffset = 0;
			for (i = 0; i < dOffset; i++) {
				buffer.setCharAt(s + half + 1 + i,
					field.getDigitCharAt(n + half + i));
			}
			buffer = new StringBuffer(buffer.toString().trim());
		}
		while (buffer.length() > 3 &&
				buffer.charAt(buffer.length() - 1) == '0' &&
				buffer.charAt(buffer.length() - 2) != field.decimalChar()) {
			buffer.setLength(buffer.length() - 1);
		}
		if (debug)
			System.out.println("setResultRegister: buffer " +  buffer.toString());
		if (((AbacusApplet)applet).getScript()) {
			AbacusApplet.callbackAbacus(
				/*AbacusInterface.ACTION_SCRIPT*/
				AbacusInterface.PRIMARY, deck,
				rail - model.getDecimalPosition(), number);
		}
		anomalyActive = model.checkAnomaly();
		if (model.getBase() != field.getDisplayBase() || anomalyActive) {
			StringBuffer buff = new StringBuffer(1024);

			buff.setLength(1024);
			if (anomalyActive) {
				AbacusMath.convertString(buff, buffer.toString(), model.getBase(),
					field.getDisplayBase(), model.getDecimalPosition(),
					model.getAnomaly(), model.getShiftAnomaly(), field.getCarryAnomaly(),
					model.getAnomalySq(), model.getShiftAnomalySq(),
					field.getCarryAnomalySq(), field.decimalChar());
			} else {
				AbacusMath.convertString(buff, buffer.toString(), model.getBase(),
					field.getDisplayBase(), model.getDecimalPosition(),
					0, model.getShiftAnomaly(), false,
					0, model.getShiftAnomalySq(), false,
					field.decimalChar());
			}
			buffer = new StringBuffer(buff.toString().trim());
		}
		if (field.getRomanNumerals()) {
			int pieces = model.getPiece(AbacusFormat.BOTTOM);

			if (model.getPiece(AbacusFormat.TOP) != 0)
				pieces *= model.getPiece(AbacusFormat.TOP);
			StringBuffer romanString = new StringBuffer(rom);

			abacusMath.string2Roman(romanString, buffer.toString(),
				field.getDisplayBase(), pieces,
				model.getValuePiece(), model.getValueSubdeck(), model.getSubbase(),
				field.decimalChar(), field.getAncientRoman(), field.getLatin());
			buffer.append("	  ");
			buffer.append(romanString);
		}
		if (field.getGroup()) {
			st = AbacusMath.string2Group(buffer.toString(),
				model.getGroupSize(), field.decimalChar(), field.groupChar());
		} else {
			st = new String(buffer);
		}
		if (st.indexOf('.') >= 0 && st.substring(st.indexOf('.') +
				1).trim().length() == 0)
			//trim last "." if nothing behind it
			st = st.substring(0, st.indexOf('.'));
		//System.out.println(displayBase + "#displayBase#");
		//System.out.println(st + "##st");
		((AbacusApplet)applet).callbackAbacus(this, st);
	}

	void animateSlide(int deck, int rail, int bead, int position,
			int j, int spaces, int dir, int animationDelay) {
		int space, inc, aBead, numBeads;
		int gapJ;
		int posOff, beadOff;

		if (dir == AbacusFormat.UP)
			numBeads = bead - position;
		else
			numBeads = position - bead + 1;
		for (space = 0; space < spaces; space++) {
			gapJ = geo.getPos().y / geo.getNumberSlices();
			if (gapJ == 0)
				gapJ++;
			for (inc = 0; inc < geo.getPos().y + gapJ; inc += gapJ) {
				if (inc > geo.getPos().y) {
					gapJ = geo.getPos().y + gapJ - inc;
					inc = geo.getPos().y;
				}
				for (aBead = numBeads - 1; aBead >= 0; aBead--) {
					beadOff = AbacusFormat.newPos(dir, aBead);
					posOff = AbacusFormat.newPos(dir, (aBead + space));
					/* actual bead, bead position */
					aDraw.drawBead(deck, rail, bead + beadOff, j,
						true, true, false, 0, 0,
						AbacusFormat.newPos(dir, inc) +
						posOff * geo.getPos().y);
					/* Erase old slivers */
					if ((model.getVertical() && dir == AbacusFormat.DOWN) ||
							(!model.getVertical() &&
							dir == AbacusFormat.UP)) {
						aDraw.drawRail(deck, rail,
							j + posOff, false,
							geo.getPos().y - inc, gapJ);
					} else {
						aDraw.drawRail(deck, rail,
							j + posOff, false,
							inc - gapJ +
							geo.getPressOffset().y, gapJ);
					}
				}
				try {
					Thread.sleep(animationDelay);
				} catch (Exception e) {
					//e.printStackTrace();
					return;
				}
			}
		}
	}

	void addBead(int d, int p) {
		int position = field.getNumberDigits() - 2 - p;
		if (position < 0) {
			System.out.println("addBead(" + d + "," + p + ")" +
				field.getNumberDigits());
		}
		int digit = AbacusMath.char2Int(field.getDigitCharAt(position));
		int b = model.getBase();

		digit += d;
		if ((model.getPiece(AbacusFormat.BOTTOM) != 0 &&
				(p == model.getDecimalPosition() - 1)) ||
				(model.checkSubdeck(3) &&
				(p == model.getDecimalPosition() - 2))) {
			b = model.getPiece(AbacusFormat.BOTTOM);
			if (model.getPiece(AbacusFormat.TOP) != 0)
				b *= model.getPiece(AbacusFormat.TOP);
		} else if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
				(p == model.getDecimalPosition() -
				model.getShiftPercent() - 1 -
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1))) {
			b = model.getPiecePercent(AbacusFormat.BOTTOM);
			if (model.getPiecePercent(AbacusFormat.TOP) != 0)
				b *= model.getPiecePercent(AbacusFormat.TOP);
		}
		field.setDigitCharAt(position, AbacusMath.int2Char(digit % b));
		if (digit >= b) {
			if (model.checkSubdeck(3) &&
					(p + 1 == model.getDecimalPosition() - 1))
				addBead(digit / b, p + 1);
			else if ((model.getPiece(AbacusFormat.BOTTOM) != 0 &&
					(p + 1 == model.getDecimalPosition() - 1)) ||
					(model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
					(p + 1 == model.getDecimalPosition() -
					model.getShiftPercent() - 1 -
					((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1))))
				addBead(digit / b, p + 2);
			else {
				addBead(digit / b, p + 1);
				if (model.getAnomaly() != 0) {
					if (p + 1 == model.getShiftAnomaly() +
							model.getDecimalPosition()) {
						field.setCarryAnomaly(true);
					} else if (model.getAnomalySq() != 0 &&
							p + 1 == model.getShiftAnomaly() +
							model.getShiftAnomalySq() +
							model.getDecimalPosition()) {
						field.setCarryAnomalySq(true);
					}
				}
			}
		}
	}

	void subBead(int d, int p) {
		int position = field.getNumberDigits() - 2 - p;
		if (position < 0) {
			System.out.println("subBead(" + d + "," + p + ")" +
				field.getNumberDigits());
		}
		int digit = AbacusMath.char2Int(field.getDigitCharAt(position));
		int b = model.getBase();

		digit -= d;
		if ((model.getPiece(AbacusFormat.BOTTOM) != 0 &&
				(p == model.getDecimalPosition() - 1)) ||
				(model.checkSubdeck(3) &&
				(p == model.getDecimalPosition() - 2))) {
			b = model.getPiece(AbacusFormat.BOTTOM);
			if (model.getPiece(AbacusFormat.TOP) != 0)
				b *= model.getPiece(AbacusFormat.TOP);
		} else if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
				(p == model.getDecimalPosition() -
				model.getShiftPercent() - 1 -
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1))) {
			b = model.getPiecePercent(AbacusFormat.BOTTOM);
			if (model.getPiecePercent(AbacusFormat.TOP) != 0)
				b *= model.getPiecePercent(AbacusFormat.TOP);
		}
		field.setDigitCharAt(position,
			AbacusMath.int2Char(((digit + b) % b)));
		if (digit < 0) {
			if (model.checkSubdeck(3) &&
					(p + 1 == model.getDecimalPosition() - 1))
				subBead(1 + (-1 - digit) / b, p + 1);
			else if ((model.getPiece(AbacusFormat.BOTTOM) != 0 &&
					(p + 1 == model.getDecimalPosition() - 1)) ||
					(model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
					(p + 1 == model.getDecimalPosition() -
					model.getShiftPercent() - 1 -
					((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1))))
				subBead(1 + (-1 - digit) / b, p + 2);
			else {
				subBead(1 + (-1 - digit) / b, p + 1);
				if (model.getAnomaly() != 0) {
					if (p + 1 == model.getShiftAnomaly() +
							model.getDecimalPosition()) {
						field.setCarryAnomaly(false);
					} else if (model.getAnomalySq() != 0 &&
							p + 1 == model.getShiftAnomaly() +
							model.getShiftAnomalySq() +
							model.getDecimalPosition()) {
						field.setCarryAnomalySq(false);
					}
				}
			}
		}
	}

	void moveUp(int deck, int rail, int j,
			int factor, int spaces,
			int fast, int moveDelay) {
		int position, rdeck, rj, rpos;

		if (j > AbacusInterface.MAX_BASE + spaces) {
			System.out.println("moveUp: corruption " + j + " > " +
				(AbacusInterface.MAX_BASE + spaces) + ".");
			return;
		}
		if (model.checkSubdeck(3) && rail == model.getDecimalPosition() - 2) {
			position = model.getSubdecksPosition(deck);
			rdeck = AbacusFormat.BOTTOM;
			rj = j + model.getNumberSubbeadsOffset(deck);
			rpos = position + model.getNumberSubbeadsOffset(deck);
		} else {
			position = model.getPosition(deck, rail);
			rdeck = deck;
			rj = j;
			rpos = position;
		}
		if (j > position + spaces) {
			int temp = rpos;

			if (fast == AbacusInterface.INSTANT || geo.getDelay() == 0) {
				int l;

				for (l = 0; l < spaces; l++) {
					int k;

					for (k = temp + spaces + 1; k <= rj; k++) {
						aDraw.drawBead(rdeck, rail,
							k - spaces, k - l,
							false, false, false,
							0, 0, 0);
						aDraw.drawBead(rdeck, rail,
							k - spaces, k - l - 1,
							true, false, false,
							0, 0, 0);
					}
					if (l + 1 != spaces) {
					    try {
						Thread.sleep(geo.getDelay() / fast);
					    } catch (Exception e) {
						//e.printStackTrace();
					    }
					}
				}
			} else {
				animateSlide(rdeck, rail, rj - spaces, rpos,
					rj, spaces, AbacusFormat.UP,
					moveDelay / (geo.getNumberSlices() * fast));
			}
			((AbacusApplet)applet).callbackAbacus(this,
				AbacusInterface.ACTION_MOVE);
			if (((AbacusApplet)applet).getToggleSound()) {
				try {
					((AbacusApplet)applet).playBumpAudio();
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
			if (model.checkSubdeck(3) && rail == model.getDecimalPosition() - 2) {
				model.setSubdecksPosition(deck, j - spaces);
			} else {
				model.setPosition(deck, rail, j - spaces);
			}
			if (model.getOrientation(rdeck)) {
				subBead(factor * (rj - spaces - temp), rail);
				setResultRegister(deck, rail, -(rj - spaces - temp));
			} else {	/* decks[rdeck].orientation == AbacusFormat.DOWN */
				addBead(factor * (rj - spaces - temp), rail);
				setResultRegister(deck, rail, rj - spaces - temp);
			}
		}
	}

	void moveDown(int deck, int rail, int j, int factor, int spaces,
			int fast, int moveDelay) {
		int position, rdeck, rj, rpos;

		if (-j > AbacusInterface.MAX_BASE) {
			System.out.println("moveDown: corruption " + -j +
				" > " + AbacusInterface.MAX_BASE + ".");
			return;
		}
		if (model.checkSubdeck(3) && rail == model.getDecimalPosition() - 2) {
			position = model.getSubdecksPosition(deck);
			rdeck = AbacusFormat.BOTTOM;
			rj = j + model.getNumberSubbeadsOffset(deck);
			rpos = position + model.getNumberSubbeadsOffset(deck);
		} else {
			position = model.getPosition(deck, rail);
			rdeck = deck;
			rj = j;
			rpos = position;
		}
		if (j <= position) {
			int temp = rpos;

			if (fast == AbacusInterface.INSTANT || geo.getDelay() == 0) {
				int l;

				for (l = 0; l < spaces; l++) {
					int k;

					for (k = temp; k >= rj; k--) {
						aDraw.drawBead(rdeck, rail, k, k + l,
							false, false, false,
							0, 0, 0);
						aDraw.drawBead(rdeck, rail, k,
							k + l + 1,
							true, false, false,
							0, 0, 0);
					}
					if (l + 1 != spaces) {
					    try {
						Thread.sleep(geo.getDelay() / fast);
					    } catch (Exception e) {
						//e.printStackTrace();
					    }
					}
				}
			} else {
				animateSlide(rdeck, rail, rj, rpos,
					rj, spaces, AbacusFormat.DOWN,
					moveDelay / (geo.getNumberSlices() * fast));
			}
			((AbacusApplet)applet).callbackAbacus(this,
				AbacusInterface.ACTION_MOVE);
			if (((AbacusApplet)applet).getToggleSound()) {
				try {
					((AbacusApplet)applet).playBumpAudio();
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
			if (model.checkSubdeck(3) && rail == model.getDecimalPosition() - 2) {
				model.setSubdecksPosition(deck, j - 1);
			} else {
				model.setPosition(deck, rail, j - 1);
			}
			if (model.getOrientation(rdeck)) {
				addBead(factor * (temp - rj + 1), rail);
				setResultRegister(deck, rail, temp - rj + 1);
			} else {	/* decks[rdeck].orientation == AbacusFormat.DOWN */
				subBead(factor * (temp - rj + 1), rail);
				setResultRegister(deck, rail, -(temp - rj + 1));
			}
		}
	}

	// Working from moveBeadsByValue
	void placeCounters(int deck, int rail, int number) {
		int oldNumber = model.getPosition(deck, rail);
		int newNumber = number + oldNumber;

		if (oldNumber > 0)
			aDraw.drawCounters(deck, rail, oldNumber, false, false);
		model.setPosition(deck, rail, newNumber); // puts on table
		aDraw.drawCounters(deck, rail, newNumber, true, false);
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_MOVE);
		if (number != 0 && ((AbacusApplet)applet).getToggleSound()) {
			try {
				((AbacusApplet)applet).playBumpAudio();
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		if (number < 0) {
			subBead(-number * model.getFactor(deck, rail), rail);
		} else {
			addBead(number * model.getFactor(deck, rail), rail);
		}
		//System.out.println("placeCounters: deck " + deck +
		//	", rail " + rail + ", number " + number +
		//	", oldNumber " + oldNumber +
		//	", newNumber " + newNumber +
		//	", digits " + field.getDigitString());
		setResultRegister(deck, rail, newNumber);
	}

	void removeAllCounters(int deck, int rail) {
		int number = model.getPosition(deck, rail);

		model.setPosition(deck, rail, 0); // remove from table
		//System.out.println("removeAllCounters: deck " + deck +
		//	", rail " + rail + ", number " + number);
		aDraw.drawCounters(deck, rail, number, false, false);
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_MOVE);
		if (number != 0 && ((AbacusApplet)applet).getToggleSound()) {
			try {
				((AbacusApplet)applet).playBumpAudio();
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		subBead(number * model.getFactor(deck, rail), rail);
		setResultRegister(deck, rail, 0);
	}

	void moveBeadsUp(int deck, int rail, int j, boolean fast) {
		int factor = 1, pieces, piecePercents, spaces;

		if (debug)
			System.out.println("moveBeadsUp: deck " + deck +
				", rail " + rail + ", j " + j);
		if (model.getSign() && (rail == model.getRails() - 1)) {
			if (deck == AbacusFormat.BOTTOM) {
				factor = 0;
				spaces = model.getRoom(deck) - 1;
				if (spaces > 0)
					moveUp(deck, rail, j, factor, spaces,
						AbacusInterface.NORMAL,
						(fast) ? 0 : geo.getDelay() *
						model.getSpaces(AbacusFormat.BOTTOM) /
						(model.getRoom(AbacusFormat.BOTTOM) - 1));
			}
		} else if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
				(rail == model.getDecimalPosition() - 1)) {
			pieces = model.getNumberPieces(deck);
			if (deck == AbacusFormat.TOP)
				factor *= model.getPiece(AbacusFormat.BOTTOM);
			spaces = model.getRoom(deck) - pieces;
			if (spaces > 0)
				moveUp(deck, rail, j, factor, spaces,
					AbacusInterface.NORMAL,
					(fast) ? 0 : geo.getDelay() *
					model.getSpaces(deck) /
					(model.getRoom(deck) - pieces));
		} else if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
				(rail == model.getDecimalPosition() -
				model.getShiftPercent() - 1 -
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1))) {
			piecePercents = model.getNumberPiecePercents(deck);
			if (deck == AbacusFormat.TOP)
				factor *= model.getPiecePercent(AbacusFormat.BOTTOM);
			spaces = model.getRoom(deck) - piecePercents;
			if (spaces > 0)
				moveUp(deck, rail, j, factor, spaces,
					AbacusInterface.NORMAL,
					(fast) ? 0 : geo.getDelay() *
					model.getSpaces(AbacusFormat.BOTTOM) /
					(model.getRoom(AbacusFormat.BOTTOM) - piecePercents));
		} else if (model.checkSubdeck(3) && (rail == model.getDecimalPosition() - 2 ||
				rail == model.getDecimalPosition() - 3)) {
			if (rail == model.getDecimalPosition() - 2) {
				moveUp(deck, rail, j, model.convertRomanFactor(deck),
					AbacusInterface.SUBDECK_SPACE,
					AbacusInterface.NORMAL,
					(fast) ? 0 :
					geo.getDelay() * AbacusInterface.SUBDECK_SPACE);
			}
		} else if (j > model.getPosition(deck, rail) +
				model.getSpaces(deck)) {
			factor = model.getFactor(deck);
			spaces = model.getSpaces(deck);
			//System.out.println("moveUp(" + deck + "," + j + "," + factor + "," + rail + ")");
			moveUp(deck, rail, j, factor, spaces,
				AbacusInterface.NORMAL,
				(fast) ? 0 : geo.getDelay());
		}
	}

	void moveBeadsDown(int deck, int rail, int j, boolean fast) {
		int factor = 1, pieces, piecePercents, spaces;

		if (debug)
			System.out.println("moveBeadsDown: deck " + deck +
				", rail " + rail + ", j " + j);
		if ((rail == model.getRails() - 1) && model.getSign()) {
			if (deck == AbacusFormat.BOTTOM) {
				factor = 0;
				spaces = model.getRoom(deck) - 1;
				if (spaces > 0)
					moveDown(deck, rail, j, factor, spaces,
						AbacusInterface.NORMAL,
						(fast) ? 0 : geo.getDelay() *
						model.getSpaces(AbacusFormat.BOTTOM) /
						(model.getRoom(AbacusFormat.BOTTOM) - 1));
			}
		} else if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
				(rail == model.getDecimalPosition() - 1)) {
			pieces = model.getNumberPieces(deck);
			if (deck == AbacusFormat.TOP)
				factor *= model.getPiece(AbacusFormat.BOTTOM);
			spaces = model.getRoom(deck) - pieces;
			if (spaces > 0)
				moveDown(deck, rail, j, factor, spaces,
					AbacusInterface.NORMAL,
					(fast) ? 0 : geo.getDelay() *
					model.getSpaces(AbacusFormat.BOTTOM) /
					(model.getRoom(AbacusFormat.BOTTOM) - pieces));
		} else if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
				(rail == model.getDecimalPosition() -
				model.getShiftPercent() - 1 -
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1))) {
			piecePercents = model.getNumberPiecePercents(deck);
			if (deck == AbacusFormat.TOP)
				factor *= model.getPiecePercent(AbacusFormat.BOTTOM);
			spaces = model.getRoom(deck) - piecePercents;
			if (spaces > 0)
				moveDown(deck, rail, j, factor, spaces,
					AbacusInterface.NORMAL,
					(fast) ? 0 : geo.getDelay() *
					model.getSpaces(AbacusFormat.BOTTOM) /
					(model.getRoom(AbacusFormat.BOTTOM) - piecePercents));
		} else if (model.checkSubdeck(3) && (rail == model.getDecimalPosition() - 2 ||
				rail == model.getDecimalPosition() - 3)) {
			if (rail == model.getDecimalPosition() - 2) {
				moveDown(deck, rail, j, model.convertRomanFactor(deck),
					AbacusInterface.SUBDECK_SPACE,
					AbacusInterface.NORMAL,
					(fast) ? 0 :
					geo.getDelay() * AbacusInterface.SUBDECK_SPACE);
			}
		} else if (j <= model.getPosition(deck, rail)) {
			factor = model.getFactor(deck);
			spaces = model.getSpaces(deck);
			//System.out.println("moveDown(" + deck + "," + j + "," + factor + "," + rail + ")");
			moveDown(deck, rail, j, factor, spaces,
				AbacusInterface.NORMAL,
				(fast) ? 0 : geo.getDelay());
		}
	}

	void moveAbacusInput(int x, int y, char letter, boolean control) {
		if (mouseDown)
			return;
		if (control) {
			if (model.getVertical() && letter == '2') {
				decrementAbacus();
			} else if (!model.getVertical() && letter == '4') {
				decrementAbacus();
			} else if (!model.getVertical() && letter == '6') {
				incrementAbacus();
			} else if (model.getVertical() && letter == '8') {
				incrementAbacus();
			}
		} else {
			if (letter == '6' || letter == '4') {
				int notches = (letter == '6') ? -1 : 1;
				if (model.getVertical() && positionToRail(notches, x, y)) {
					wheelPaint = notches;
					repaint();
				} else if ((!model.getVertical() && (y >= geo.getMiddleBarPositionY()) &&
						(y <= geo.getMiddleBarPositionY() + geo.getMiddleBarHeight()))) {
					int newPosition = model.getDecimalPosition() + notches;
					if (newPosition >= 0 && newPosition < model.getRails())
						setDecimal(newPosition);
				}
			} else if (letter == '8' || letter == '2') {
				int notches = (letter == '8') ? -1 : 1;

				if (!model.getVertical() && positionToRail(notches, x, y)) {
					wheelPaint = notches;
					repaint();
				} else if ((model.getVertical() && (x <= geo.getFrameSize().y - 1 - geo.getMiddleBarPositionY()) &&
						(x >= geo.getFrameSize().y - 1 - geo.getMiddleBarPositionY() - geo.getMiddleBarHeight()))) {
					int newPosition = model.getDecimalPosition() - notches;
					if (newPosition >= 0 && newPosition < model.getRails())
						setDecimal(newPosition);
				}
			}
		}
	}

	boolean positionToCounter(int positionX, int positionY) {
		int x = positionX, y = positionY;

		if (model.getVertical()) {
			int temp = x;

			x = y;
			y = geo.getFrameSize().y - 1 - temp;
		}
		x -= geo.getOffset().x;
		y -= geo.getOffset().y;
		currentDeck = (((x - geo.getBeadSize().x / 2) % geo.getPos().x)
			> geo.getPos().x / 2) ? 0 : 1;
		if (model.getNumber(currentDeck) == 0) {
			return false;
		}
		currentRail = model.getRails() - 1 -
			(x - geo.getBeadSize().x / 2) /
			geo.getPos().x;
		if (currentRail < 0) {
			currentDeck = 0;
			currentRail = 0;
		}
		currentPosition = (positionX >= geo.getMiddleBarPositionY()) ? 0 : 1;
		return true;
	}

	boolean moveCounters() {
		int counters = model.getPosition(currentDeck, currentRail);
		if (currentPosition == 1) {
			if (counters + 1 > model.getNumber(currentDeck, currentRail))
				return false;
			aDraw.drawCounters(currentDeck, currentRail, counters, false, false);
			counters++;
			addBead(model.getFactor(currentDeck, currentRail), currentRail);
		} else {
			if (counters - 1 < 0)
				return false;
			aDraw.drawCounters(currentDeck, currentRail, counters, false, false);
			counters--;
			subBead(model.getFactor(currentDeck, currentRail), currentRail);
		}
		// puts on table
		model.setPosition(currentDeck, currentRail, counters);
		aDraw.drawCounters(currentDeck, currentRail, counters, true, false);
		setResultRegister(currentDeck, currentRail, counters);
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_MOVE);
		if (((AbacusApplet)applet).getToggleSound()) {
			try {
				((AbacusApplet)applet).playBumpAudio();
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		return true;
	}

	boolean positionToBead(int positionX, int positionY) {
		int pieces, piecePercents;
		int x = positionX, y = positionY;

		if (model.getVertical()) {
			int temp = x;

			x = y;
			y = geo.getFrameSize().y - 1 - temp;
		}
		x -= geo.getOffset().x;
		y -= geo.getOffset().y;
		if (y > geo.getDeckHeight(AbacusFormat.TOP)) {
			y = y - geo.getDeckHeight(AbacusFormat.TOP) + 3 -
				geo.getMiddleBarHeight();
			currentDeck = AbacusFormat.BOTTOM;
		} else {
			currentDeck = AbacusFormat.TOP;
		}
		if (model.getNumber(currentDeck) == 0) {
			return false;
		}
		currentRail = model.getRails() - 1 - (x - geo.getDelta().x / 2) /
			geo.getPos().x;
		currentPosition = (y - geo.getDelta().y / 2) / geo.getPos().y + 1;
		if (currentRail < 0)
			currentRail = 0;
		else if (currentRail >= model.getRails())
			currentRail = model.getRails() - 1;
		if (currentPosition < 1)
			currentPosition = 1;
		else if (currentPosition > model.getRoom(currentDeck))
			currentPosition = model.getRoom(currentDeck);
		if (currentRail == model.getRails() - 1 && model.getSign()) {
			if (currentDeck == AbacusFormat.TOP)
				return false;
			return ((currentPosition == 1 &&
				model.getPosition(currentDeck, currentRail) == 1) ||
				(currentPosition == model.getRoom(currentDeck) &&
				model.getPosition(currentDeck, currentRail) == 0));
		}
		if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
				(currentRail == model.getDecimalPosition() - 1)) {
			pieces = model.getNumberPieces(currentDeck);
			if (currentDeck == AbacusFormat.TOP && pieces == 0)
				return false;
			return ((currentPosition >
				model.getPosition(currentDeck, currentRail) +
				model.getRoom(currentDeck) - pieces) ||
				(currentPosition <=
				model.getPosition(currentDeck, currentRail)));
		}
		if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
				(currentRail == model.getDecimalPosition() -
				model.getShiftPercent() - 1 -
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1))) {
			piecePercents = model.getNumberPiecePercents(currentDeck);
			if (currentDeck == AbacusFormat.TOP && piecePercents == 0)
				return false;
			return ((currentPosition >
				model.getPosition(currentDeck, currentRail) +
				model.getRoom(currentDeck) - piecePercents) ||
				(currentPosition <=
				model.getPosition(currentDeck, currentRail)));
		}
		if (model.checkSubdeck(3) && currentRail == model.getDecimalPosition() - 3) {
			return false;
		} else if (model.checkSubdeck(3) &&
				currentRail == model.getDecimalPosition() - 2) {
			int beads;

			if (currentDeck == AbacusFormat.TOP)
				return false;
			currentDeck = model.getSubdeckPosition(currentPosition);
			currentPosition = model.getPositionSubdeck(currentPosition);
			beads = model.getNumberSubbeads(currentDeck);
			return ((currentPosition >
				model.getSubdecksPosition(currentDeck) +
				model.getSubdecksRoom(currentDeck) - beads) ||
				(currentPosition <=
				model.getSubdecksPosition(currentDeck)));
		}
		return ((currentPosition >
			model.getPosition(currentDeck, currentRail) +
			model.getSpaces(currentDeck)) ||
			(currentPosition <=
			model.getPosition(currentDeck, currentRail)));
	}

	boolean positionToRail(int notches, int positionX, int positionY) {
		int pieces, piecePercents;
		int x = positionX, y = positionY;

		if (model.getVertical()) {
			int temp = x;

			x = y;
			y = geo.getFrameSize().y - 1 - temp;
		}
		x -= geo.getOffset().x;
		y -= geo.getOffset().y;
		if (y > geo.getDeckHeight(AbacusFormat.TOP)) {
			y = y - geo.getDeckHeight(AbacusFormat.TOP) - 3;
			currentDeck = AbacusFormat.BOTTOM;
		} else {
			currentDeck = AbacusFormat.TOP;
		}
		if (model.getNumber(currentDeck) == 0) {
			return false;
		}
		currentRail = model.getRails() - 1 - (x - geo.getDelta().x / 2) /
			geo.getPos().x;
		currentPosition = (y - geo.getDelta().y / 2) / geo.getPos().y + 1;
		if (currentRail < 0)
			currentRail = 0;
		else if (currentRail >= model.getRails())
			currentRail = model.getRails() - 1;
		if (currentPosition < 1)
			currentPosition = 1;
		else if (currentPosition > model.getRoom(currentDeck))
				currentPosition = model.getRoom(currentDeck);
		if (currentRail == model.getRails() - 1 && model.getSign()) {
			if (currentDeck == AbacusFormat.TOP)
				return false;
			return ((notches > 0 &&
				model.getPosition(currentDeck, currentRail) >= notches) ||
				(notches < 0 &&
				(model.getPosition(currentDeck, currentRail) <=
				notches + 1)));
		}
		if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
				(currentRail == model.getDecimalPosition() - 1)) {
			pieces = model.getNumberPieces(currentDeck);
			if (currentDeck == AbacusFormat.TOP && pieces == 0)
				return false;
			return ((notches > 0 &&
				model.getPosition(currentDeck, currentRail) >= notches) ||
				(notches < 0 &&
				(model.getPosition(currentDeck, currentRail) <=
				notches + pieces)));
		}
		if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
				(currentRail == model.getDecimalPosition() -
				model.getShiftPercent() - 1 -
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1))) {
			piecePercents = model.getNumberPiecePercents(currentDeck);
			if (currentDeck == AbacusFormat.TOP && piecePercents == 0)
				return false;
			return ((notches > 0 &&
				model.getPosition(currentDeck, currentRail) >= notches) ||
				(notches < 0 &&
				(model.getPosition(currentDeck, currentRail) <=
				notches + piecePercents)));
		}
		if (model.checkSubdeck(3) && currentRail == model.getDecimalPosition() - 3) {
			return false;
		} else if (model.checkSubdeck(3) &&
				currentRail == model.getDecimalPosition() - 2) {
			int beads;

			if (currentDeck == AbacusFormat.TOP)
				return false;
			currentDeck = model.getSubdeckPosition(currentPosition);
			beads = model.getNumberSubbeads(currentDeck);
			return ((notches > 0 &&
				model.getSubdecksPosition(currentDeck) >= notches) ||
				(notches < 0 &&
				(model.getSubdecksPosition(currentDeck) <=
				notches + beads)));
		}
		return ((notches > 0 &&
			model.getPosition(currentDeck, currentRail) >= notches) ||
			(notches < 0 &&
			(model.getPosition(currentDeck, currentRail) <=
			notches + model.getNumber(currentDeck))));
	}

	void moveBeadsByPos(int deck, int rail, int position, boolean fast) {
		int bead;

		if (debug)
			System.out.println("moveBeadsByPos: deck " + deck +
				", rail " + rail + ", position " + position);
		if (model.getSign() && (rail == model.getRails() - 1) && deck == AbacusFormat.TOP)
			return;
		if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
				(rail == model.getDecimalPosition() - 1) &&
				deck == AbacusFormat.TOP && (model.getNumber(AbacusFormat.TOP) == 0 ||
				model.getPiece(AbacusFormat.TOP) == 0))
			return;
		if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
				(rail == model.getDecimalPosition() -
				model.getShiftPercent() - 1 -
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1)) &&
				deck == AbacusFormat.TOP && (model.getNumber(AbacusFormat.TOP) == 0 ||
				model.getPiecePercent(AbacusFormat.TOP) == 0))
			return;
		if (model.checkSubdeck(3) && rail == model.getDecimalPosition() - 3)
			return;
		if (model.checkSubdeck(3) && rail == model.getDecimalPosition() - 2)
			bead = model.getSubdecksPosition(deck);
		else
			bead = model.getPosition(deck, rail);
		if (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) {
			int number = model.getNumber(deck, rail);
			int factor = model.getFactor(deck, rail);

			if (number - bead == bead)
				return;
			if (bead > 0)
				aDraw.drawCounters(deck, rail, bead, false, false);
			if (number - bead > bead) {
				for (int i = 0; i < number - 2 * bead; i++) {
					addBead(factor, rail);
				}
			} else if (number - bead < bead) {
				for (int i = 0; i < 2 * bead - number; i++) {
					subBead(factor, rail);
				}
			}
			model.setPosition(deck, rail, number - bead);
			aDraw.drawCounters(deck, rail, number - bead, true, false);
			setResultRegister(deck, rail, number - bead);
			if (((AbacusApplet)applet).getToggleSound()) {
				try {
					((AbacusApplet)applet).playBumpAudio();
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		} else {
			if (position <= bead) {
				moveBeadsDown(deck, rail, position, fast);
			} else {
				moveBeadsUp(deck, rail, position, fast);
			}
		}
	}

	void shiftBar(int oldDecimalPosition) {
		int deck, rail;
		int[] pieces = new int[AbacusFormat.MAX_DECKS];
		int[] piecePercents = new int[AbacusFormat.MAX_DECKS];
		int pieceRail = model.getDecimalPosition() - 1;
		int piecePercentRail = model.getDecimalPosition() - model.getShiftPercent() -
			1 - ((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1);
		int oldPieceRail = oldDecimalPosition - 1;
		int oldPiecePercentRail = oldDecimalPosition - model.getShiftPercent() -
			1 - ((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1);
		char p = '0', pp = '0';

		pieces[AbacusFormat.TOP] = 0;
		pieces[AbacusFormat.BOTTOM] = 0;
		piecePercents[AbacusFormat.TOP] = 0;
		piecePercents[AbacusFormat.BOTTOM] = 0;
		if (model.getPiece(AbacusFormat.BOTTOM) != 0) {
			int digit = model.getRails() + NumberField.CARRY - oldDecimalPosition;

			pieces[AbacusFormat.BOTTOM] =
				model.getPosition(AbacusFormat.BOTTOM, oldPieceRail);
			if (model.getPiece(AbacusFormat.TOP) != 0 ||
					model.getNumber(AbacusFormat.TOP) == 0)
				pieces[AbacusFormat.TOP] =
					model.getPosition(AbacusFormat.TOP, oldPieceRail);
			p = field.getDigitCharAt(digit);
			field.setDigitCharAt(digit, '0');

		}
		if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0) {
			int digit = model.getRails() + NumberField.CARRY -
				oldDecimalPosition + model.getShiftPercent() +
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1);

			piecePercents[AbacusFormat.BOTTOM] =
				model.getPosition(AbacusFormat.BOTTOM, oldPiecePercentRail);
			if (model.getPiecePercent(AbacusFormat.TOP) != 0 ||
					model.getNumber(AbacusFormat.TOP) == 0)
				piecePercents[AbacusFormat.TOP] =
					model.getPosition(AbacusFormat.TOP, oldPiecePercentRail);
			pp = field.getDigitCharAt(digit);
			field.setDigitCharAt(digit, '0');
		}
		/* shift around */
		if (oldDecimalPosition < model.getDecimalPosition()) {
			for (rail = oldPieceRail; rail < pieceRail; rail++) {
				for (deck = AbacusFormat.BOTTOM; deck <= AbacusFormat.TOP; deck++) {
					model.setPosition(deck, rail,
						model.getPosition(deck, rail + 1));
					if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0)
						model.setPosition(deck, rail - model.getShiftPercent() - 1,
							model.getPosition(deck, rail - model.getShiftPercent()));
				}
			}
			for (rail = model.getRails() + NumberField.CARRY - oldDecimalPosition;
					rail > model.getRails() + NumberField.CARRY -
					model.getDecimalPosition(); rail--) {
				field.setDigitCharAt(rail, field.getDigitCharAt(rail - 1));
				if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0)
					field.setDigitCharAt(rail + model.getShiftPercent() + 1,
						field.getDigitCharAt(rail + model.getShiftPercent()));
			}
		} else if (oldDecimalPosition > model.getDecimalPosition()) {
			for (rail = oldPieceRail; rail > pieceRail; rail--) {
				for (deck = AbacusFormat.BOTTOM; deck <= AbacusFormat.TOP; deck++) {
					model.setPosition(deck, rail,
						model.getPosition(deck, rail - 1));
					if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0)
						model.setPosition(deck, rail - model.getShiftPercent() - 1,
							model.getPosition(deck, rail - model.getShiftPercent() - 2));
				}
			}
			for (rail = model.getRails() + NumberField.CARRY - oldDecimalPosition;
					rail < model.getRails() + NumberField.CARRY -
					model.getDecimalPosition(); rail++) {
				field.setDigitCharAt(rail, field.getDigitCharAt(rail + 1));
				if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0)
					field.setDigitCharAt(rail + model.getShiftPercent() + 1,
						field.getDigitCharAt(rail + model.getShiftPercent() + 2));
			}
		}
		if (model.getPiece(AbacusFormat.BOTTOM) != 0) {
			model.setPosition(AbacusFormat.BOTTOM, pieceRail, pieces[AbacusFormat.BOTTOM]);
			if (model.getPiece(AbacusFormat.TOP) == 0 ||
					model.getNumber(AbacusFormat.TOP) == 0)
				model.setPosition(AbacusFormat.TOP, pieceRail, 0);
			else
				model.setPosition(AbacusFormat.TOP, pieceRail, pieces[AbacusFormat.TOP]);
			field.setDigitCharAt(model.getRails() + NumberField.CARRY - model.getDecimalPosition(), p);
		}
		if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0) {
			model.setPosition(AbacusFormat.BOTTOM, piecePercentRail,
				piecePercents[AbacusFormat.BOTTOM]);
			if (model.getPiecePercent(AbacusFormat.TOP) == 0 &&
					model.getNumber(AbacusFormat.TOP) == 0)
				model.setPosition(AbacusFormat.TOP, piecePercentRail, 0);
			else
				model.setPosition(AbacusFormat.TOP, piecePercentRail,
					piecePercents[AbacusFormat.TOP]);
			field.setDigitCharAt(model.getRails() + NumberField.CARRY -
				model.getDecimalPosition() + model.getShiftPercent() +
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1), pp);
		}
	}

	void clearAllBeads() {
		int rail, deck;

		for (rail = 0; rail < model.getRails(); rail++) {
			for (deck = AbacusFormat.BOTTOM; deck <= AbacusFormat.TOP; deck++) {
				if (model.getSign() && (rail == model.getRails() - 1) &&
						deck == AbacusFormat.TOP) {
					continue;
				}
				if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
						(rail == model.getDecimalPosition() - 1) &&
						deck == AbacusFormat.TOP &&
						(model.getNumber(AbacusFormat.TOP) == 0 ||
						model.getPiece(AbacusFormat.TOP) == 0)) {
					continue;
				}
				if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
						(rail == model.getDecimalPosition() -
						model.getShiftPercent() - 1 -
						((model.getPiece(AbacusFormat.BOTTOM) == 0) ?
						0 : 1)) &&
						deck == AbacusFormat.TOP &&
						(model.getNumber(AbacusFormat.TOP) == 0 ||
						model.getPiecePercent(AbacusFormat.TOP) == 0)) {
					continue;
				}
				if (model.checkSubdeck(3) && (rail ==
						model.getDecimalPosition() - 2)) {
					if (deck == AbacusFormat.BOTTOM) {
					  int d;

					  for (d = 0; d < model.getSubdeck(); d++)
					    if (model.getOrientation(AbacusFormat.BOTTOM))
					      moveBeadsUp(d, rail,
						model.getSubdecksRoom(d), true);
					    else
					      moveBeadsDown(d, rail, 1, true);
					}
					continue;
				}
				if (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) {
					removeAllCounters(deck, rail);
				} else {
					if (model.getOrientation(deck))
						moveBeadsUp(deck, rail,
							model.getRoom(deck), true);
					else	/* model.getOrientation(deck) == AbacusFormat.DOWN */
						moveBeadsDown(deck, rail, 1, true);
				}
			}
		}
	}

	/* used by AbacusTeach */
	public void drawBeadRail(int rail, boolean highlight) {
		aDraw.drawBeadRail(rail, highlight);
	}

	// TODO: Needed?
	void drawAllBeads() {
		int deck, rail, j, spaces;

		if (model.getSign()) {
			deck = AbacusFormat.BOTTOM;
			rail = model.getRails() - 1;
			aDraw.drawBead(deck, rail, 1, 1,
				(model.getPosition(deck, rail) == 1),
				false, false, 0, 0, 0);
			for (j = 2; j < model.getRoom(deck); j++)
				aDraw.drawBead(deck, rail, 0, j, false,
					false, false, 0, 0, 0);
			aDraw.drawBead(deck, rail, 1, model.getRoom(deck),
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
					aDraw.drawBead(deck, rail, j, j,
						true, false, false, 0, 0, 0);
				}
				for (j = model.getPosition(deck, rail) + 1;
						j < spaces +
						model.getPosition(deck, rail) +
						1; j++) {
					aDraw.drawBead(deck, rail, 0, j,
						false, false, false, 0, 0, 0);
				}
				for (j = spaces + model.getPosition(deck, rail) + 1;
						j <= model.getRoom(deck); j++) {
					aDraw.drawBead(deck, rail, j - spaces, j,
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
					aDraw.drawBead(deck, rail, j, j,
						true, false, false, 0, 0, 0);
				for (j = model.getPosition(deck, rail) + 1;
						j < spaces +
						model.getPosition(deck, rail) + 1; j++)
					aDraw.drawBead(deck, rail, 0, j,
						false, false, false, 0, 0, 0);
				for (j = spaces + model.getPosition(deck, rail) + 1;
				     j <= model.getRoom(deck); j++)
					aDraw.drawBead(deck, rail, j - spaces, j,
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
					aDraw.drawBead(d, rail, j + specialOffset,
						j + specialOffset,
						true, false, false, 0, 0, 0);
				}
				for (j = model.getSubdecksPosition(deck) + 1;
						j < spaces +
						model.getSubdecksPosition(deck) + 1;
						j++) {
					aDraw.drawBead(d, rail, specialOffset, j + specialOffset,
						false, false, false, 0, 0, 0);
				}
				for (j = spaces + model.getSubdecksPosition(deck) + 1;
						j <= model.getSubdecksRoom(deck);
						j++) {
					aDraw.drawBead(d, rail, j + specialOffset - spaces,
						j + specialOffset,
						true, false, false, 0, 0, 0);
				}
			}
		}
		for (rail = 0; rail < model.getRails() - ((model.getSign()) ? 1 : 0);
				rail++) {
			aDraw.drawBeadRail(rail, false);
		}
		setResultRegister(0, model.getDecimalPosition(), 0);
	}

	void setDecimal(int newRail) {
		int rail = newRail;
		int j;

		if (((AbacusApplet)applet).getScript()) {
			AbacusApplet.callbackAbacus(
				/*AbacusInterface.ACTION_SCRIPT*/
				AbacusInterface.PRIMARY, 2,
				rail - model.getDecimalPosition(), 0);
		}
		if (model.checkSubdeck(3)) {
			return;
		}
		if (rail <= model.getShiftPercent() + 1 &&
				model.getPiecePercent(AbacusFormat.BOTTOM) != 0) {
			model.setPiecePercent(AbacusFormat.BOTTOM, 0);
			model.setPiecePercent(AbacusFormat.TOP, 0);
			if (rail <= 0 && model.getPiece(AbacusFormat.BOTTOM) != 0)
				rail = 1;
			deleteSpecialRail(false, false, true);
			((AbacusApplet)applet).callbackAbacus(this,
				AbacusInterface.ACTION_QUARTER_PERCENT);
		} else if (rail <= 0 && model.getPiece(AbacusFormat.BOTTOM) != 0) {
			model.setPiece(AbacusFormat.BOTTOM, 0);
			model.setPiece(AbacusFormat.TOP, 0);
			deleteSpecialRail(false, true, false);
			((AbacusApplet)applet).callbackAbacus(this,
				AbacusInterface.ACTION_QUARTER);
		}
		if (model.getSign() && rail >= model.getRails() - 1)
			rail = model.getRails() - 2;
		/*if (model.getPiece(AbacusFormat.BOTTOM) != 0 ||
				decks[model.getPiecePercent(AbacusFormat.BOTTOM) {
			aDraw.drawFrame(false);
		}*/
		aDraw.drawFrame(false);
		j = model.getDecimalPosition();
		model.setDecimalPosition(rail);
		aDraw.drawFrame(true);
		if (model.getPiece(AbacusFormat.BOTTOM) != 0 ||
				model.getPiecePercent(AbacusFormat.BOTTOM) != 0) {
			shiftBar(j);
		}
		try {
			/* Fixes a drawing problem on lowest value beads, not sure why */
			Thread.sleep(100);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		aDraw.drawAllBeads();
		setResultRegister(0, model.getDecimalPosition(), 0);
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_PLACE);
		if (((AbacusApplet)applet).getToggleSound()) {
			try {
				((AbacusApplet)applet).playMoveAudio();
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}

	void moveBeadsByValue(int deck, int rail, int number, boolean fast) {
		if (debug)
			System.out.println("moveBeadsByValue: deck " + deck +
				", rail " + rail + ", number " + number);
		if (deck != AbacusFormat.BOTTOM && deck != AbacusFormat.TOP) {
			setDecimal(number + model.getDecimalPosition());
			return;
		}
		if (model.getSign() && (rail == model.getRails() - 1)) {
			if (deck == AbacusFormat.TOP)
				return;
			if (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) {
				if (number > model.getPosition(deck, rail))
					placeCounters(deck, rail, 1);
				return;
			}
			if (number <= model.getPosition(deck, rail)) {
				moveBeadsDown(deck, rail, 1, fast);
			} else {
				moveBeadsUp(deck, rail,
					model.getRoom(AbacusFormat.BOTTOM), fast);
			}
			return;
		} else if (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) {
			if (rail < model.getRails()) {
				placeCounters(deck, rail, number);
			}
		} else if ((model.getOrientation(deck) && number < 0) ||
				(!model.getOrientation(deck) && number > 0)) {
			int spaces = model.getSpaces(deck);

			if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
					rail == model.getDecimalPosition() - 1) {
				spaces = model.getRoom(deck) - model.getNumberPieces(deck);
			}
			if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
					rail == model.getDecimalPosition() - 1 -
					model.getShiftPercent() -
					((model.getPiece(AbacusFormat.BOTTOM) == 0) ?
					0 : 1)) {
				spaces = model.getRoom(deck) -
					model.getNumberPiecePercents(deck);
			}
			moveBeadsUp(deck, rail, spaces +
				model.getPosition(deck, rail) +
				((number >= 0) ? number : -number),
				fast);
		} else if ((!model.getOrientation(deck) && number < 0) ||
				(model.getOrientation(deck) && number > 0)) {
			moveBeadsDown(deck, rail,
				model.getPosition(deck, rail) + 1 -
				((number >= 0) ? number : -number),
				fast);
		}
	}

	boolean setBeadsForValue(String expression) {
		int i, val = -1, topUnits, bottomUnits;
		int percentPosition = model.getDecimalPosition() - model.getShiftPercent() -
			((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1);
		int nPieces = 0; int nPiecePercents = 0;

		if (debug)
			System.out.println("setBeadsForValue: expression " +
				expression + ", minusSign " + minusSign);
		for (i = 0; i < expression.trim().length() - NumberField.CARRY; i++) {
			char a = expression.charAt(i + NumberField.CARRY);

			val = AbacusMath.char2Int(a);
			if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
					model.getRails() - i != model.getDecimalPosition()) {
				nPieces = model.getPiece(AbacusFormat.BOTTOM);
				if (model.getPiece(AbacusFormat.TOP) != 0)
					nPieces *= model.getPiece(AbacusFormat.TOP);
			} else if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
					model.getRails() - i != percentPosition) {
				nPiecePercents = model.getPiecePercent(AbacusFormat.BOTTOM);
				if (model.getPiecePercent(AbacusFormat.TOP) != 0)
					nPiecePercents *=
						model.getPiecePercent(AbacusFormat.TOP);
			}
			if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
					model.getRails() - i == model.getDecimalPosition()) {
				if (val >= nPieces)
					val -= nPieces;
				if (model.getNumber(AbacusFormat.TOP) == 0) {
					bottomUnits = val % nPieces;
					moveBeadsByValue(AbacusFormat.BOTTOM,
						model.getRails() - i - 1,
						bottomUnits, true);
				} else {
					topUnits = val / model.getPiece(AbacusFormat.BOTTOM);
					bottomUnits = val % model.getPiece(AbacusFormat.BOTTOM);
					if (topUnits > model.getPiece(AbacusFormat.TOP)) {
						return false;
					}
					moveBeadsByValue(AbacusFormat.TOP,
						model.getRails() - i - 1,
						topUnits, true);
					moveBeadsByValue(AbacusFormat.BOTTOM,
						model.getRails() - i - 1,
						bottomUnits, true);
				}
			} else if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
					model.getRails() - i == percentPosition) {
				if (val >= nPiecePercents)
					val -= nPiecePercents;
				if (model.getNumber(AbacusFormat.TOP) == 0) {
					bottomUnits = val % nPiecePercents;
					moveBeadsByValue(AbacusFormat.BOTTOM,
						model.getRails() - i - 1,
						bottomUnits, true);
				} else {
					topUnits = val / model.getPiecePercent(AbacusFormat.BOTTOM);
					bottomUnits = val % model.getPiecePercent(AbacusFormat.BOTTOM);
					if (topUnits > model.getPiecePercent(AbacusFormat.TOP)) {
						return false;
					}
					moveBeadsByValue(AbacusFormat.TOP,
						model.getRails() - i - 1,
						topUnits, true);
					moveBeadsByValue(AbacusFormat.BOTTOM,
						model.getRails() - i - 1,
						bottomUnits, true);
				}
			} else {
				if (model.checkSubdeck(3) && model.getRails() - i <
						model.getDecimalPosition())
					continue;
				topUnits = val / model.getFactor(AbacusFormat.TOP);
				bottomUnits = (val % model.getFactor(AbacusFormat.TOP)) /
					model.getFactor(AbacusFormat.BOTTOM);
				if (topUnits > model.getNumber(AbacusFormat.TOP)) {
					return false;
				}
				moveBeadsByValue(AbacusFormat.TOP, model.getRails() - i - 1,
					topUnits, true);
				moveBeadsByValue(AbacusFormat.BOTTOM, model.getRails() - i - 1,
					bottomUnits, true);
			}
		}
		if (model.getSign()) {
			moveBeadsByValue(AbacusFormat.BOTTOM, model.getRails() - 1,
				(model.getOrientation(AbacusFormat.BOTTOM)) ?
				((minusSign) ? 0 : model.getRoom(AbacusFormat.BOTTOM) -
				model.getSpaces(AbacusFormat.BOTTOM)) :
				((minusSign) ? model.getRoom(AbacusFormat.BOTTOM) -
				model.getSpaces(AbacusFormat.BOTTOM) : 0), true);
		}
		if (debug)
			System.out.println("setBeadsForValue: return true");
		return true;
	}

	void complementRails() {
		int rail, deck;

		for (rail = 0; rail < model.getRails(); rail++) {
			for (deck = AbacusFormat.BOTTOM; deck <= AbacusFormat.TOP; deck++) {
				if (model.getSign() && (rail == model.getRails() - 1)) {
					continue;
				}
				if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
						(rail == model.getDecimalPosition() - 1)) {
					continue;
				}
				if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
						(rail == model.getDecimalPosition() -
						model.getShiftPercent() - 1 -
						((model.getPiece(AbacusFormat.BOTTOM) == 0) ?
						0 : 1))) {
					continue;
				}
				if (model.checkSubdeck(3) && (rail ==
						model.getDecimalPosition() - 2)) {
					continue;
				}
				{
					int bead = model.getPosition(deck, rail);
					int maxFactor;

					if (model.getOrientation(deck)) {
						bead = model.getNumber(deck) - bead;
					}
					// prime bases
					if (deck == AbacusFormat.TOP &&
							model.getNumber(AbacusFormat.BOTTOM) >= model.getBase() - 1) {
						continue;
					}
					maxFactor = (deck == AbacusFormat.BOTTOM) ?
						model.getFactor(AbacusFormat.TOP) :
						model.getBase() / model.getFactor(AbacusFormat.TOP);
					// not really well defined, so ignore
					if (bead >= maxFactor) {
						continue;
					}
					moveBeadsByValue(deck, rail, (maxFactor - 1) - 2 * bead, true);
				}
			}
		}
	}

	public void clearRails() {
		clearAllBeads();
		if (demo) {
			((AbacusApplet)applet).callbackAbacus(this,
				AbacusInterface.ACTION_CLEAR);
		}
		if (!field.isEmptyResultRegister()) {
			System.out.println("clearRails: corruption");
		}
		setResultRegister(0, model.getDecimalPosition(), 0); /* needed when 0 */
	}

	void checkDecimal() {
		if (model.getDecimalPosition() >= model.getRails() -
				((model.getSign()) ? 1 : 0) +
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1) +
				((model.getPiecePercent(AbacusFormat.BOTTOM) == 0) ? 0 : 1)) {
			aDraw.drawFrame(false);
			model.setDecimalPosition(model.getRails() - 1 -
				((model.getSign()) ? 1 : 0) +
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1) +
				((model.getPiecePercent(AbacusFormat.BOTTOM) == 0) ? 0 : 1));
			aDraw.drawFrame(true);
			setResultRegister(0, model.getDecimalPosition(), 0);
		}
	}

	/* via increment/decrement or by sign and quarters */
	void shiftRails(int oldRails, int newRails,
			boolean oldSign, boolean piece, boolean piecePercent) {
		AbacusFormat old = new AbacusFormat(model.getRails());
		int deck, rail, specialOffset;
		int[] piecePosition = new int[AbacusFormat.MAX_DECKS];
		int[] piecePercentPosition = new int[AbacusFormat.MAX_DECKS];
		int[] pieceNumber = new int[AbacusFormat.MAX_DECKS];
		int[] piecePercentNumber = new int[AbacusFormat.MAX_DECKS];
		int shift = newRails - oldRails;
		int decimalShift = 0;
		boolean signPosition = false;

		old.setSign(model.getSign());
		//old.decks = new AbacusDeck[AbacusFormat.MAX_DECKS]
		for (deck = AbacusFormat.BOTTOM; deck <= AbacusFormat.TOP; deck++) {
			piecePosition[deck] = 0;
			piecePercentPosition[deck] = 0;
			pieceNumber[deck] = model.getNumberPieces(deck);
			piecePercentNumber[deck] = model.getNumberPiecePercents(deck);
			old.setNumber(deck, model.getNumber(deck));
			old.setOrientation(deck, model.getOrientation(deck));
			old.setFactor(deck, model.getFactor(deck));
			old.setSpaces(deck, model.getSpaces(deck));
			old.setPiece(deck, model.getPiece(deck));
			old.setPiecePercent(deck, model.getPiecePercent(deck));
		}
		deck = AbacusFormat.BOTTOM;
		/* special items added already, 2 is a dummy value */
		if (oldSign)
			old.setSign(!model.getSign());
		if (piece) {
			old.setPiece(AbacusFormat.BOTTOM,
				(model.getPiece(AbacusFormat.BOTTOM) == 0) ? 2 : 0);
		}
		if (piecePercent) {
			old.setPiecePercent(AbacusFormat.BOTTOM,
				(model.getPiecePercent(AbacusFormat.BOTTOM) == 0) ? 2 : 0);
		}
		/* Save sign, this will be erased */
		if (old.getSign() && !oldSign)
			signPosition = ((model.getOrientation(deck) &&
				model.getPosition(deck, oldRails - 1) == 0) ||
				(!model.getOrientation(deck) &&
				model.getPosition(deck, oldRails - 1) != 0));
		old.setRails(oldRails + ((shift > 0) ? shift : 0));
		old.setDecimalPosition(model.getDecimalPosition());
		for (deck = AbacusFormat.BOTTOM; deck <= AbacusFormat.TOP; deck++) {
			/* Alloc space to save the rails */
			//old.setPosition(deck, = new int[old.rails];
			/* initialization could be wrong if oriented from top */
			/* current pieces will be initialized later */
			if (model.getOrientation(deck)) {
				for (rail = 0; rail < old.getRails(); rail++) {
					old.setPosition(deck, rail, model.getNumber(deck));
				}
				if (old.getPiece(AbacusFormat.BOTTOM) == 0 && piece)
					piecePosition[deck] = pieceNumber[deck];
				if (old.getPiecePercent(AbacusFormat.BOTTOM) == 0 &&
						piecePercent)
					piecePercentPosition[deck] =
						piecePercentNumber[deck];
			}
		}
		/* initialization from old */
		for (deck = AbacusFormat.BOTTOM; deck <= AbacusFormat.TOP; deck++) {
			specialOffset = 0;
			if (old.getPiece(AbacusFormat.BOTTOM) != 0 && !piece)
				specialOffset--;
			if (old.getPiecePercent(AbacusFormat.BOTTOM) != 0 && !piecePercent)
				specialOffset--;
			for (rail = oldRails - ((old.getSign()) ? 1 : 0) - 1; rail >= 0; rail--) {
				if (old.getPiece(AbacusFormat.BOTTOM) != 0 &&
						rail == model.getDecimalPosition() - 1) {
					if (old.getPiece(deck) != 0 && !piece)
						piecePosition[deck] =
							model.getPosition(deck, rail);
					specialOffset++;
				} else if (old.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
						rail == model.getDecimalPosition() -
						model.getShiftPercent() - 1 -
						((old.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1)) {
					if (old.getPiecePercent(deck) != 0 &&
							!piecePercent)
						piecePercentPosition[deck] =
							model.getPosition(deck, rail);
					specialOffset++;
				} else {
					old.setPosition(deck, rail + specialOffset,
						model.getPosition(deck, rail));
				}
			}
		}
		model.setRails(newRails);
		if (debug) {
			for (deck = AbacusFormat.TOP; deck >= AbacusFormat.BOTTOM; deck--) {
				for (rail = old.getRails() - 1; rail >= 0; rail--) {
					System.out.print(AbacusMath.int2Char(
						old.getPosition(deck, rail)));
				}
				System.out.print(":" + old.getRails() + ",p" +
					piecePosition[deck] + ",pp" +
					piecePercentPosition[deck]);
				if (old.getSign() && !oldSign)
					System.out.println(",s" + signPosition);
				else
					System.out.println();
			}
			System.out.println("shiftRails decimalPosition w" +
				model.getDecimalPosition() + ", rails w" + model.getRails() +
				", shift" + shift);
		}
		if (model.getDecimalPosition() > model.getRails() - 1 - ((model.getSign()) ? 1 : 0)) {
			decimalShift = model.getDecimalPosition();
			model.setDecimalPosition(model.getRails() - 1 - ((model.getSign()) ? 1 : 0));
			decimalShift -= model.getDecimalPosition();
		} else if (model.getDecimalPosition() < ((model.getSign()) ? 1 : 0)) {
			model.setDecimalPosition(0);
		}
		specialOffset = 0;
		if (piece && old.getPiece(AbacusFormat.BOTTOM) == 0) {
			model.setDecimalPosition(model.getDecimalPosition() + 1);
		}
		if (piece && old.getPiece(AbacusFormat.BOTTOM) != 0) {
			specialOffset--;
			model.setDecimalPosition(model.getDecimalPosition() - 1);
		}
		if (piecePercent && old.getPiecePercent(AbacusFormat.BOTTOM) == 0) {
			model.setDecimalPosition(model.getDecimalPosition() + 1);
		}
		if (piecePercent && old.getPiecePercent(AbacusFormat.BOTTOM) != 0) {
			specialOffset--;
			model.setDecimalPosition(model.getDecimalPosition() - 1);
		}
		if (debug)
			System.out.println("shiftRails decimalPosition w" +
				model.getDecimalPosition() + ", rails w" + model.getRails() +
				", shift" + shift + ", offset" + specialOffset);
		resetBeads();
		resizeAbacus();
		aDraw.drawAllBufferedBeads();
		aDraw.drawFrame(false);
		aDraw.drawFrame(true);
		aDraw.drawAllBeads();
		for (deck = AbacusFormat.BOTTOM; deck <= AbacusFormat.TOP; deck++) {
			int localOffset = specialOffset;

			for (rail = 0 ; rail < model.getRails() + ((specialOffset < 0) ? -specialOffset : 0) -
					(((old.getSign() && !oldSign) ||
					(!old.getSign() && oldSign)) ? 1 : 0) + 1;
					rail++) {
				if (rail + localOffset < 0 || rail + localOffset >= model.getRails())
					continue;
				if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
						rail + localOffset == model.getDecimalPosition() - 1) {
					if (deck != AbacusFormat.TOP || pieceNumber[AbacusFormat.TOP] != 0) {
						if (model.getOrientation(deck)) {
							moveBeadsByValue(deck, rail + localOffset,
								pieceNumber[deck] -
								piecePosition[deck], true);
						} else {
							moveBeadsByValue(deck, rail + localOffset,
								piecePosition[deck], true);
						}
					}
					localOffset++;
				} else if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
						rail + localOffset == model.getDecimalPosition() -
						model.getShiftPercent() - 1 -
						((model.getPiece(AbacusFormat.BOTTOM) == 0) ?
						0 : 1)) {
					if (deck != AbacusFormat.TOP || piecePercentNumber[AbacusFormat.TOP] != 0) {
						if (model.getOrientation(deck)) {
							moveBeadsByValue(deck, rail + localOffset,
								piecePercentNumber[deck] -
								piecePercentPosition[deck], true);
						} else {
							moveBeadsByValue(deck, rail + localOffset,
								piecePercentPosition[deck], true);
						}
					}
					localOffset++;
				}
				if (rail < oldRails) {
					if (model.getOrientation(deck)) {
						moveBeadsByValue(deck, rail + localOffset,
							model.getNumber(deck) -
							old.getPosition(deck, rail + decimalShift),
							true);
					} else {
						moveBeadsByValue(deck, rail + localOffset,
							old.getPosition(deck, rail + decimalShift),
							true);
					}
				}
			}
		}
		if ((old.getSign() && !oldSign) || (!old.getSign() && oldSign)) {
			deck = AbacusFormat.BOTTOM;
			rail = model.getRails() - 1;
			// not sure why needed
			if (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) {
				if (signPosition)
					addBead(0, rail);
			} else {
				if (model.getOrientation(deck)) {
					moveBeadsByPos(deck, rail,
						(signPosition) ?
						1 : model.getRoom(deck),
						true);
				} else {
					moveBeadsByPos(deck, rail,
						(signPosition) ?
						model.getRoom(deck) : 1,
						true);
				}
			}
		}
		if (debug) {
			for (deck = AbacusFormat.TOP; deck >= AbacusFormat.BOTTOM; deck--) {
				for (rail = model.getRails() - 1; rail >= 0; rail--) {
					System.out.print(AbacusMath.int2Char(
						model.getPosition(deck, rail)));
				}
				System.out.print(":" + model.getRails() + ",p" +
					piecePosition[deck] + ",pp" +
					piecePercentPosition[deck]);
				if (old.getSign() && !oldSign)
					System.out.println(",s" + signPosition);
				else
					System.out.println();
				if (deck == AbacusFormat.TOP) {
					for (rail = model.getRails() - 1; rail >= 0; rail--) {
						if (model.getDecimalPosition() == rail)
							System.out.print(".");
						else if ((rail > model.getDecimalPosition()) &&
								((model.getDecimalPosition() - rail) % 3 == 0))
							System.out.print(",");
						else if ((model.getPiece(AbacusFormat.BOTTOM) != 0) &&
								(rail == model.getDecimalPosition() - 1))
							System.out.print("+");
						else if ((model.getPiecePercent(AbacusFormat.BOTTOM) != 0) &&
								(rail == model.getDecimalPosition() -
								model.getShiftPercent() - 1 -
								((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1)))
							System.out.print("+");
						else
							System.out.print(" ");
					}
					System.out.println("");
				}
			}
		}
	}

	boolean insertSpecialRail(boolean negativeSign, boolean piece,
			boolean piecePercent) {
		int minRails = ((demo) ? AbacusInterface.MIN_DEMO_RAILS :
			AbacusInterface.MIN_RAILS);

		if (model.getRails() + 2 <= minRails + ((negativeSign) ? 1 : 0) +
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1) +
				((model.getPiecePercent(AbacusFormat.BOTTOM) == 0) ?
				0 : model.getShiftPercent() + 1))
			return false;
		shiftRails(model.getRails(), model.getRails() + 1,
			negativeSign, piece, piecePercent);
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_INCREMENT);
		return true;
	}

	boolean deleteSpecialRail(boolean negativeSign, boolean piece,
			boolean piecePercent) {
		int minRails = ((demo) ? AbacusInterface.MIN_DEMO_RAILS :
			AbacusInterface.MIN_RAILS);

		if (model.getRails() <= minRails + ((negativeSign) ? 1 : 0) +
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1) +
				((model.getPiecePercent(AbacusFormat.BOTTOM) == 0) ?
				0 : model.getShiftPercent() + 1) +
				(model.checkSubdeck(3) ? 2 : 0))
			return false;
		shiftRails(model.getRails(), model.getRails() - 1,
			negativeSign, piece, piecePercent);
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_DECREMENT);
		checkDecimal();
		return true;
	}

	void incrementRails() {
		if (mouseDown)
			return;
		if (model.getRails() >= 64)
			return;
		shiftRails(model.getRails(), model.getRails() + 1,
			false, false, false);
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_INCREMENT);
	}

	void decrementRails() {
		int minRails = ((demo) ? AbacusInterface.MIN_DEMO_RAILS :
			AbacusInterface.MIN_RAILS);

		if (mouseDown)
			return;
		if (model.getRails() <= minRails + ((model.getSign()) ? 1 : 0) +
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1) +
				((model.getPiecePercent(AbacusFormat.BOTTOM) == 0) ?
				0 : model.getShiftPercent() + 1) +
				(model.checkSubdeck(3) ? 2 : 0))
			return;
		shiftRails(model.getRails(), model.getRails() - 1,
			false, false, false);
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_DECREMENT);
		checkDecimal();
		drawAllBeads();
	}

	void reformatRails() {
		String buffer; /* new char[length(digits) + 1] */

		buffer = field.getDigitString();
		resetBeads();
		resizeAbacus();
		aDraw.drawAllBufferedBeads();
		aDraw.drawFrame(false);
		aDraw.drawFrame(true);
		aDraw.drawAllBeads();
		if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
				model.getDecimalPosition() - model.getShiftPercent() -
				((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1) < 1)
			model.setPiecePercent(AbacusFormat.BOTTOM, 0);
		if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
				model.getDecimalPosition() < 1)
			model.setPiece(AbacusFormat.BOTTOM, 0);
		if (!setBeadsForValue(buffer)) {
			resetBeads();
		}
	}

	void formatRails(int inc) {
		if (model.getMode() < AbacusInterface.Modes.Chinese.ordinal() ||
				model.getMode() > AbacusInterface.Modes.Generic.ordinal())
			model.setMode(AbacusInterface.Modes.Chinese.ordinal());
		else
			model.setMode((model.getMode() + inc) % (AbacusInterface.Modes.values().length - 1));
		if (demo && model.getMode() == AbacusInterface.Modes.Generic.ordinal()) {
			model.setMode(AbacusInterface.Modes.Chinese.ordinal());
		}
		if (model.getSign()) {
			if (model.getOrientation(AbacusFormat.BOTTOM))
				minusSign =
				model.getPosition(AbacusFormat.BOTTOM, model.getRails() - 1) == 0;
			else
				minusSign =
					model.getPosition(AbacusFormat.BOTTOM, model.getRails() - 1) != 0;
		}
		/* clear special beads */
		if (model.checkSubdeck(3)) {
			int deck = 0;

			for (deck = 0; deck < model.getSubdeck(); deck++) {
				moveBeadsDown(deck, model.getDecimalPosition() - 2,
					model.getSubdecksRoom(deck) - 1, true);
			}
			/* This should not be necessary. */
			field.setDigitCharAt(model.getRails() - 1, '0');
		}
		checkBeads();
		reformatRails();
		if (inc != 0)
			((AbacusApplet)applet).callbackAbacus(this,
				AbacusInterface.ACTION_FORMAT);
		if (((AbacusApplet)applet).getToggleSound()) {
			try {
				((AbacusApplet)applet).playDripAudio();
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		drawAllBeads(); // needed when ignoring last carry
	}

	public void toggleRomanNumeralsDisplay() {
		field.setRomanNumerals(!field.getRomanNumerals());
		setResultRegister(0, model.getDecimalPosition(), 0);
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_ROMAN_NUMERALS);
	}

	public void toggleGroupDisplay() {
		field.setGroup(!field.getGroup());
		setResultRegister(0, model.getDecimalPosition(), 0);
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_GROUP);
	}

	void toggleSignRail() {
		model.setSign(!model.getSign());
		if ((model.getSign() && ((((AbacusApplet)applet).getTeach()) ||
				!insertSpecialRail(true, false, false))) ||
				(!model.getSign() &&
				!deleteSpecialRail(true, false, false))) {
			model.setSign(!model.getSign());
			return;
		}
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_SIGN);
	}

	void togglePieceRail(int topPieces, int bottomPieces) {
		int[] oldPiece = new int[AbacusFormat.MAX_DECKS];

		oldPiece[AbacusFormat.TOP] = model.getPiece(AbacusFormat.TOP);
		oldPiece[AbacusFormat.BOTTOM] = model.getPiece(AbacusFormat.BOTTOM);
		if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
				model.getPiecePercent(AbacusFormat.BOTTOM) != 0)
			return;
		if (model.getNumber(AbacusFormat.TOP) == 0 && topPieces != 0) {
			if (model.getRoom(AbacusFormat.BOTTOM) <= topPieces * bottomPieces) {
				int room = topPieces * bottomPieces + 1;

				model.setSpaces(AbacusFormat.BOTTOM, room -
					model.getNumber(AbacusFormat.BOTTOM));
			}
		} else {
			if (model.checkBottomSpace() <= bottomPieces) {
				model.setSpaces(AbacusFormat.BOTTOM, model.getSpaces(AbacusFormat.BOTTOM) -
					(model.checkBottomSpace() - bottomPieces - 1));
				//decks[AbacusFormat.BOTTOM].room = model.getNumber(AbacusFormat.BOTTOM) +
				//	model.getSpaces(AbacusFormat.BOTTOM);
			}
		}
		model.setPiece(AbacusFormat.BOTTOM, (model.getPiece(AbacusFormat.BOTTOM) == 0) ?
			bottomPieces : 0);
		model.setPiece(AbacusFormat.TOP, (model.getPiece(AbacusFormat.TOP) != 0 ||
			model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : topPieces);
		if (model.checkSubdeck(2)) {
			model.setPosition(AbacusFormat.BOTTOM, 1, 0);
		}
		if (model.getPiece(AbacusFormat.BOTTOM) == 0) {
			if (!deleteSpecialRail(false, true, false)) {
				model.setPiece(AbacusFormat.BOTTOM, oldPiece[AbacusFormat.BOTTOM]);
				model.setPiece(AbacusFormat.TOP, oldPiece[AbacusFormat.TOP]);
			}
		} else {
			if (!insertSpecialRail(false, true, false)) {
				model.setPiece(AbacusFormat.BOTTOM, oldPiece[AbacusFormat.BOTTOM]);
				model.setPiece(AbacusFormat.TOP, oldPiece[AbacusFormat.TOP]);
			}
		}
		if (model.getPiece(AbacusFormat.BOTTOM) != oldPiece[AbacusFormat.BOTTOM] ||
				model.getPiece(AbacusFormat.TOP) != oldPiece[AbacusFormat.TOP]) {
			if (bottomPieces == AbacusFormat.QUARTERS && topPieces == 0)
				((AbacusApplet)applet).callbackAbacus(this,
					AbacusInterface.ACTION_QUARTER);
			else if (bottomPieces == AbacusFormat.TWELFTHS / 2 && topPieces == 2)
				((AbacusApplet)applet).callbackAbacus(this,
					AbacusInterface.ACTION_TWELFTH);
		}
	}

	void togglePiecePercentRail(int topPiecePercents, int bottomPiecePercents) {
		int[] oldPiecePercent = new int[AbacusFormat.MAX_DECKS];

		oldPiecePercent[AbacusFormat.TOP] = model.getPiecePercent(AbacusFormat.TOP);
		oldPiecePercent[AbacusFormat.BOTTOM] = model.getPiecePercent(AbacusFormat.BOTTOM);
		if (model.getDecimalPosition() < 1 + model.getShiftPercent())
			return;
		if (model.getPiece(AbacusFormat.BOTTOM) == 0 &&
				model.getPiecePercent(AbacusFormat.BOTTOM) == 0)
			return;
		if (model.getNumber(AbacusFormat.TOP) == 0 && topPiecePercents != 0) {
			if (model.getRoom(AbacusFormat.BOTTOM) <= topPiecePercents * bottomPiecePercents) {
				int room = topPiecePercents * bottomPiecePercents + 1;

				model.setSpaces(AbacusFormat.BOTTOM, room -
					model.getNumber(AbacusFormat.BOTTOM));
			}
		} else {
			if (model.checkBottomSpace() < bottomPiecePercents) {
				model.setSpaces(AbacusFormat.BOTTOM, model.getSpaces(AbacusFormat.BOTTOM) -
					(model.checkBottomSpace() - bottomPiecePercents));
				//decks[AbacusFormat.BOTTOM].room = model.getNumber(AbacusFormat.BOTTOM) +
				//	model.getSpaces(AbacusFormat.BOTTOM);
			}
		}
		model.setPiecePercent(AbacusFormat.BOTTOM,
			(model.getPiecePercent(AbacusFormat.BOTTOM) == 0) ?
			bottomPiecePercents : 0);
		model.setPiecePercent(AbacusFormat.TOP,
			(model.getPiecePercent(AbacusFormat.TOP) != 0 ||
			model.getPiecePercent(AbacusFormat.BOTTOM) == 0) ?
			0 : topPiecePercents);
		if (model.checkSubdeck(4)) {
			model.setPosition(AbacusFormat.BOTTOM, 2, 0);
		}
		if (model.getPiecePercent(AbacusFormat.BOTTOM) == 0) {
			if (!deleteSpecialRail(false, false, true)) {
				model.setPiecePercent(AbacusFormat.BOTTOM, oldPiecePercent[AbacusFormat.BOTTOM]);
				if (topPiecePercents != 0)
					model.setPiecePercent(AbacusFormat.TOP, oldPiecePercent[AbacusFormat.TOP]);
			}
		} else {
			if (!insertSpecialRail(false, false, true)) {
				model.setPiecePercent(AbacusFormat.BOTTOM, oldPiecePercent[AbacusFormat.BOTTOM]);
				if (topPiecePercents != 0)
					model.setPiecePercent(AbacusFormat.TOP, oldPiecePercent[AbacusFormat.TOP]);
			}
		}
		if ((model.getPiecePercent(AbacusFormat.BOTTOM) != oldPiecePercent[AbacusFormat.BOTTOM] ||
				model.getPiecePercent(AbacusFormat.TOP) !=
				oldPiecePercent[AbacusFormat.TOP]) &&
				bottomPiecePercents == AbacusFormat.QUARTER_PERCENTS &&
				topPiecePercents == 0)
			((AbacusApplet)applet).callbackAbacus(this,
				AbacusInterface.ACTION_QUARTER_PERCENT);
	}

	void toggleSubdeckRail(int ndecks, int nbeads) {
		int deck;

		if (model.getSubdeck() == 0) {
			model.setSubdeck(ndecks);
			model.setSubbead(nbeads);
			for (deck = 0; deck < ndecks; deck++) {
				model.setSubdecksNumber(deck,
					model.getNumberSubbeads(AbacusFormat.BOTTOM));
				//subdecks[deck].room =
				//	subdecks[deck].number + 1;
				model.setSubdecksPosition(deck,
					(model.getOrientation(AbacusFormat.BOTTOM)) ?
					model.getSubdecksNumber(deck) : 0);
			}
		} else {
			if (model.checkSubdeck(3)) {
				for (deck = 0; deck < model.getSubdeck(); deck++) {
					moveBeadsDown(deck,
						model.getDecimalPosition() - 2,
						model.getSubdecksRoom(deck) - 1, true);
				}
				/* This should not be necessary. */
				field.setDigitCharAt(model.getRails() - 1, '0');
			}
			model.setSubdeck(0);
		}
		if (model.getSign()) {
			if (model.getOrientation(AbacusFormat.BOTTOM))
				minusSign =
					model.getPosition(AbacusFormat.BOTTOM, model.getRails() - 1) == 0;
			else
				minusSign =
					model.getPosition(AbacusFormat.BOTTOM, model.getRails() - 1) != 0;
		}
		reformatRails();
	}

	void toggleAnomalyRails(int anomalyRail, int anomalySqRail) {
		if (model.getAnomaly() == 0) {
			if (((AbacusApplet)applet).getTeach())
				return;
			model.setAnomaly(anomalyRail);
			model.setAnomalySq(anomalySqRail);
		} else {
			model.setAnomaly(0);
			model.setAnomalySq(0);
		}
		if (model.getSign()) {
			if (model.getOrientation(AbacusFormat.BOTTOM))
				minusSign =
					(model.getPosition(AbacusFormat.BOTTOM, model.getRails() - 1) == 0);
			else
				minusSign =
					(model.getPosition(AbacusFormat.BOTTOM, model.getRails() - 1) != 0);
		}
		reformatRails();
		if (anomalySqRail == 0) {
			((AbacusApplet)applet).callbackAbacus(this,
				AbacusInterface.ACTION_ANOMALY);
		} else {
			((AbacusApplet)applet).callbackAbacus(this,
				AbacusInterface.ACTION_WATCH);
		}
	}

	void toggleVerticalRails() {
		aDraw.drawFrame(false);
		model.setVertical(!model.getVertical());
		vDraw = new OrientDraw(model.getVertical());
		resizeAbacus();
		aDraw.drawAllBufferedBeads();
		aDraw.drawFrame(true);
		aDraw.drawAllBeads();
	}

	void showMessage(String msg) {
		((AbacusApplet)applet).showMessage(msg);
	}

	void resizeAbacus() {
		geo.resize(getSize().width, getSize().height);
		aDraw = new AbacusDraw(model, geo, this);
	}

	public void initializeAbacus() {
		if (geo == null) {
			geo = new AbacusGeometry(model);
			geo.resize(getWidth(), getHeight());
			aDraw = null;
		} else if (geo.getCoreSize().x != getWidth()
				|| geo.getCoreSize().y != getHeight()) {
			geo.resize(getWidth(), getHeight());
			aDraw = null;
		}
		if (aDraw == null) {
			aDraw = new AbacusDraw(model, geo, this);
		}
		vDraw = new OrientDraw(model.getVertical());
		geo.setNumberSlices((geo.getDelay() < 5 * AbacusGeometry.MAX_SLICES) ? geo.getDelay() / 5 + 1 :
			AbacusGeometry.MAX_SLICES);
		if (model.getPiece(AbacusFormat.BOTTOM) != 0)
			model.setDecimalPosition(model.getDecimalPosition() + 1);
		if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0)
			model.setDecimalPosition(model.getDecimalPosition() + 1);
		abacusDemo = new AbacusDemo(applet, this);
		/*for (deck = 0; deck < AbacusFormat.MAX_DECKS; deck++)
			decks[deck].position = null;*/
		//model.setMode(AbacusInterface.setModeFromFormat());
		//setSubmodeFromMuseum();
		checkBeads();
		resetBeads();
		field.resetField(model.getRails());
		resizeAbacus();
		geo.resetShade();
		generator = new Random(System.nanoTime());
	}

	void exposeAbacus() {
		aDraw.drawFrame(false);
		aDraw.drawFrame(true);
		aDraw.drawAllBufferedBeads();
		aDraw.drawAllBeads();
	}

	void selectAbacus(int x, int y) {
		if (demo) {
			resetPaint = true;
			repaint();
			((AbacusApplet)applet).callbackAbacus(this,
				AbacusInterface.ACTION_CLEAR);
			currentDeck = AbacusInterface.ACTION_IGNORE;
			return;
		}
		int offset = (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) ?
			-geo.getBeadSize().x / 2 : geo.getRailWidth() / 2;

		if ((!model.getVertical() && (y >= geo.getMiddleBarPositionY()) &&
				(y <= geo.getMiddleBarPositionY() + geo.getMiddleBarHeight()))) {
			currentRail = model.getRails() - 1 -
				(x + 2 + offset -
				geo.getDelta().x - geo.getOffset().x) /
				geo.getPos().x;
			currentDeck = 2;
			currentPosition = AbacusInterface.ACTION_IGNORE;
		} else if ((model.getVertical() && (x >= geo.getMiddleBarPositionY()) &&
				(x <= geo.getMiddleBarPositionY() + geo.getMiddleBarHeight()))) {
			currentRail = model.getRails() - 1 -
				(y + 2 + offset -
				geo.getDelta().x - geo.getOffset().x) /
				geo.getPos().x;
			currentDeck = 2;
			currentPosition = AbacusInterface.ACTION_IGNORE;
		} else if (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) {
			if (positionToCounter(x, y)) {
				selectPaint = true;
				repaint();
			}
		} else if (positionToBead(x, y)) {
			selectPaint = true;
			repaint();
		} else {
			currentDeck = AbacusInterface.ACTION_IGNORE;
		}
	}

	void wheelAbacus(int notches, int x, int y) {
		// notches > 0 means move a down
		if (!model.getVertical() && positionToRail(notches, x, y)) {
			wheelPaint = notches;
			repaint();
		} else if ((model.getVertical() && (x <= geo.getFrameSize().y - 1 - geo.getMiddleBarPositionY()) &&
			(x >= geo.getFrameSize().y - 1 - geo.getMiddleBarPositionY() - geo.getMiddleBarHeight()))) {
			int newPosition = model.getDecimalPosition() - notches;
			if (newPosition >= 0 && newPosition < model.getRails())
				setDecimal(model.getDecimalPosition() - notches);
		/* think this recursion is ok as notches is not usually more than 2 */
		} else if (notches > 1) {
			wheelAbacus(notches - 1, x, y);
		} else if (notches < -1) {
			wheelAbacus(notches + 1, x, y);
		}
	}

	void releaseAbacus(int x, int y) {
		if (currentDeck < 0)
			return;
		if (model.getMode() == AbacusInterface.Modes.Medieval.ordinal() &&
				currentPosition != AbacusInterface.ACTION_IGNORE) {
			return;
		}
		if (currentPosition == AbacusInterface.ACTION_IGNORE) {
			int rail;

			int offset = (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) ?
				-geo.getBeadSize().x / 2 : geo.getRailWidth() / 2;
			if ((!model.getVertical() && (y >= geo.getMiddleBarPositionY()) &&
					(y <= geo.getMiddleBarPositionY() + geo.getMiddleBarHeight()))) {
				rail = model.getRails() - 1 -
					(x + 2 + offset -
					geo.getDelta().x - geo.getOffset().x) /
					geo.getPos().x;
				rail = getDecimalPosition() + rail - currentRail;
				if (rail < 0)
					rail = 0;
				if (rail >= model.getRails())
					rail = model.getRails() - 1;
				if (rail != getDecimalPosition()) {
					setDecimal(rail);
				}
			} else if ((model.getVertical() && (x >= geo.getMiddleBarPositionY()) &&
					(x <= geo.getMiddleBarPositionY() + geo.getMiddleBarHeight()))) {
				rail = model.getRails() - 1 -
					(y + 2 + offset -
					geo.getDelta().x - geo.getOffset().x) /
					geo.getPos().x;
				rail = getDecimalPosition() + rail - currentRail;
				if (rail < 0)
					rail = 0;
				if (rail >= model.getRails())
					rail = model.getRails() - 1;
				if (rail != getDecimalPosition()) {
					setDecimal(rail);
				}
			}
			currentDeck = AbacusInterface.ACTION_IGNORE;
			return;
		}
		currentSpace = 0;
		releasePaint = true;
		repaint();
	}

	public void complementAbacus() {
		complementRails();
	}

	public void incrementAbacus() {
		incrementRails();
	}

	public void decrementAbacus() {
		decrementRails();
	}

	public void setDelay(int newValue) {
		geo.setDelay(newValue);
	}

	public void speedUpAbacus() {
		int delay = geo.getDelay();

		if (delay == 0)
			return;
		delay -= 10;
		if (delay < 0)
			delay = 0;
		geo.setDelay(delay);
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_SPEED_UP);
	}

	public void slowDownAbacus() {
		geo.setDelay(geo.getDelay() + 10);
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_SLOW_DOWN);
	}

	public void changeFormatAbacus() {
		formatRails(1);
	}

	public void changeMuseumAbacus() {
		model.setSubmode(model.getSubmode() + 1);
		setMuseum(model.getSubmode());
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_MUSEUM);
	}

	public void setMuseum(int newValue) {
		model.setSubmode((newValue) % AbacusInterface.MAX_MUSEUMS);
		aDraw.drawFrame(false);
		aDraw.drawFrame(true);
		aDraw.drawAllBeads();
	}

	public int getMuseum() {
		return model.getSubmode();
	}

	public void toggleRomanNumeralsAbacus() {
		toggleRomanNumeralsDisplay();
	}

	void toggleGroupingAbacus() {
		toggleGroupDisplay();
	}

	void toggleNegativeSignAbacus() {
		toggleSignRail();
	}

	public void toggleQuartersAbacus() {
		togglePieceRail(0, AbacusFormat.QUARTERS);
	}

	public void toggleQuarterPercentsAbacus() {
		togglePiecePercentRail(0, AbacusFormat.QUARTER_PERCENTS);
	}

	public void toggleTwelfthsAbacus() {
		togglePieceRail(2, AbacusFormat.TWELFTHS / 2);
	}

	public void toggleSubdecksAbacus() {
		model.setSubbase(AbacusFormat.TWELFTHS);
		model.resetSubdecks();
		toggleSubdeckRail(AbacusInterface.DEFAULT_SUBDECKS,
			AbacusInterface.DEFAULT_SUBBEADS);
		((AbacusApplet)applet).callbackAbacus(this,
				AbacusInterface.ACTION_SUBDECK);
	}

	public void toggleEighthsAbacus() {
		model.setSubbase(AbacusFormat.EIGHTHS);
		model.resetSubdecks();
		toggleSubdeckRail(AbacusInterface.DEFAULT_SUBDECKS,
			AbacusInterface.DEFAULT_SUBBEADS);
		((AbacusApplet)applet).callbackAbacus(this,
				AbacusInterface.ACTION_EIGHTH);
	}

	/* Mesoamerican Nepohualtzintzin setting use with base 20 */
	public void toggleAnomalyAbacus() {
		toggleAnomalyRails(2, 0);
	}

	/* Babylonian Watch */
	public void toggleWatchAbacus() {
		toggleAnomalyRails(4, 4);
	}

	/*public void VerticalAbacus() {
		toggleVerticalRails();
	}*/

	public void setBaseAbacus(int newValue) {
		model.setBase(newValue);
		formatRails(0);
	}

	public void setDisplayBaseAbacus(int newValue) {
		field.setDisplayBase(newValue);
		setResultRegister(0, model.getDecimalPosition(), 0);
	}

	public void toggleDemoAbacus() {
		if (!((AbacusApplet)applet).mainAbacus(this))
			return;
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_DEMO);
		demo = ((AbacusApplet)applet).getDemo();
		checkBeads();
		resetBeads();
		repaint();
		if (demo) {
			((AbacusApplet)applet).callbackAbacus(this,
				AbacusInterface.ACTION_CLEAR);
			abacusDemo.clearDemo();
		} else {
			((AbacusApplet)applet).killDemoDialog();
		}
	}

	public void toggleTeachAbacus() {
		if (!((AbacusApplet)applet).mainAbacus(this))
			return;
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_TEACH);
		checkBeads();
		resetBeads();
		repaint();
		if (((AbacusApplet)applet).getTeach()) {
			((AbacusApplet)applet).callbackAbacus(this,
				AbacusInterface.ACTION_CLEAR);
		}
	}

	public void toggleRightToLeftAbacusAdd() {
		((AbacusApplet)applet).setRightToLeftAdd(
			!((AbacusApplet)applet).getRightToLeftAdd());
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_RIGHT_TO_LEFT_ADD);
	}

	public boolean getRightToLeftAdd() {
		return ((AbacusApplet)applet).getRightToLeftAdd();
	}

	public void toggleRightToLeftAbacusMult() {
		((AbacusApplet)applet).setRightToLeftMult(
			!((AbacusApplet)applet).getRightToLeftMult());
		((AbacusApplet)applet).callbackAbacus(this,
			AbacusInterface.ACTION_RIGHT_TO_LEFT_MULT);
	}

	public boolean getRightToLeftMult() {
		return ((AbacusApplet)applet).getRightToLeftMult();
	}

	public void showNextAbacus() {
		/*resetBeads();*/
		abacusDemo.queryDemo(abacusDemo.queryChapter(), true);
	}

	public void showRepeatAbacus() {
		abacusDemo.queryDemo(abacusDemo.queryChapter(), false);
	}

	public void showJumpAbacus() {
		abacusDemo.jumpDemo();
	}

	public void showChapterAbacus(int chapt) {
		abacusDemo.chapterDemo(chapt);
	}

	public void showMoreAbacus() {
		abacusDemo.moreDemo();
	}

	public void paint(Graphics g) {
		int deck, rail = 0, bead, j = 0;

		if (firstPaint) {
			if (model == null)
				return;
			initializeAbacus();
			firstPaint = false;
			resizePaint = true;
		}
		if (!framePaint && !resetPaint && !selectPaint &&
				!releasePaint && wheelPaint == 0)
			resizePaint = true;
		if (resizePaint) {
			resizeAbacus();
			exposeAbacus();
			resizePaint = false;
		}
		if (framePaint) {
			aDraw.drawFrame(true);
			framePaint = false;
			if (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) {
				aDraw.drawAllBeads();
			}
		}
		if (releasePaint) {
			if (currentDeck != AbacusInterface.ACTION_IGNORE) {
				deck = currentDeck;
				rail = currentRail;
				j = currentPosition;
				bead = j;
				if (model.checkSubdeck(3) && rail == model.getDecimalPosition() - 2) {
					if (bead > model.getSubdecksPosition(deck))
						bead -= model.getSubdecksSpaces(deck);
					bead = j + model.getNumberSubbeadsOffset(deck);
					j = bead;
					bead -= AbacusInterface.SUBDECK_SPACE;
					deck = AbacusFormat.BOTTOM;
				} else {
					if (bead > model.getPosition(deck, rail))
						bead -= model.getSpaces(deck);
				}
				aDraw.drawBead(deck, rail, bead, j,
					false, false, false, 1, 0, 0);
				aDraw.drawBead(deck, rail, bead, j,
					true, false, false, 0, 0, 0);
				if (currentSpace != -1) {
					moveBeadsByPos(currentDeck, rail,
						currentPosition, false);
				}
				currentDeck = AbacusInterface.ACTION_IGNORE;
			}
			releasePaint = false;
			selectPaint = false;
			wheelPaint = 0;
		}
		if (resetPaint) {
			clearRails();
			resetPaint = false;
		}
		if (selectPaint) {
			if (currentDeck != AbacusInterface.ACTION_IGNORE) {
				deck = currentDeck;
				rail = currentRail;
				j = currentPosition;
				if (model.getMode() == AbacusInterface.Modes.Medieval.ordinal()) {
					moveCounters();
				} else {
					bead = j;
					if (model.checkSubdeck(3) && rail == model.getDecimalPosition() - 2) {
						if (bead > model.getSubdecksPosition(deck))
							bead -= model.getSubdecksSpaces(deck);
						bead = j + model.getNumberSubbeadsOffset(deck);
						j = bead;
						bead -= AbacusInterface.SUBDECK_SPACE;
						deck = AbacusFormat.BOTTOM;
					} else {
						if (bead > model.getPosition(currentDeck, currentRail))
							bead -= model.getSpaces(currentDeck);
					}
					aDraw.drawBead(deck, rail, bead, j,
						true, false, false, 1, 0, 0);
				}
			}
			selectPaint = false;
			wheelPaint = 0;
		}
		if (wheelPaint != 0) {
			if (currentDeck != AbacusInterface.ACTION_IGNORE) {
				deck = currentDeck;
				rail = currentRail;
				int spaces;
				if (model.getSign() && rail == model.getRails() - 1) {
					spaces = model.getRoom(deck) - 1;
				} else if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
					(rail == model.getDecimalPosition() - 1)) {
					spaces = model.getRoom(deck) - model.getNumberPieces(deck);
				} else if (model.getPiece(AbacusFormat.BOTTOM) != 0 &&
					model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
					(rail == model.getDecimalPosition() - 1)) {
					spaces = model.getRoom(deck) - model.getNumberPieces(deck);
				} else if (model.getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
					(rail == model.getDecimalPosition() -
					model.getShiftPercent() - 1 -
					((model.getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1))) {
					spaces = model.getRoom(deck) - model.getNumberPiecePercents(deck);
				} else if (model.checkSubdeck(3) &&
					rail == model.getDecimalPosition() - 2) {
					currentDeck = model.getSubdeckPosition(currentPosition);
					spaces = 1;
					if (wheelPaint > 0) {
						j = model.getSubdecksPosition(currentDeck) + 1 - wheelPaint;
					} else { // (wheelPaint < 0)
						j = model.getSubdecksPosition(currentDeck) + spaces - wheelPaint;
					}
					moveBeadsByPos(currentDeck, rail, j, false);
					wheelPaint = 0;
					return;
				} else {
					spaces = model.getSpaces(deck);
				}
				if (wheelPaint > 0) {
					j = model.getPosition(deck, rail) + 1 - wheelPaint;
				} else { // (wheelPaint < 0)
					j = model.getPosition(deck, rail) + spaces - wheelPaint;
				}
				// This should not be needed.
				if (j > 0 && j <= model.getRoom(deck))
					moveBeadsByPos(deck, rail, j, false);
				else
					System.out.println("Consistency error " + j);
			}
			wheelPaint = 0;
		}
	}

	public void update(Graphics g) {
		paint(g); /* no erase */
	}

	void paintNow() {
		Graphics g = getGraphics();

		try {
			paint(g);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			g.dispose();
		}
	}
}
