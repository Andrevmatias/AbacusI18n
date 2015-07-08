package org.tux.bagleyd.abacus.model;

/*
 * @(#)AbacusFormat.java
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

/**
 * The <code>AbacusFormat</code> class holds all abstract info about
 * the abacus widget.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */

import java.util.Random;

import org.tux.bagleyd.abacus.AbacusInterface;
//import org.tux.bagleyd.abacus.AbacusMath;

public class AbacusFormat {
	static final boolean debug = false;
	static public final int PLACE_SETTING = 2;
	static public final int TOP = 1;
	static public final int BOTTOM = 0;
	static public final int UP = 1;
	static public final int DOWN = 0;
	static public final int MAX_DECKS = 2;
	static public final int QUARTERS = 4;
	static public final int QUARTER_PERCENTS = 4;
	static public final int TWELFTHS = 12;
	static public final int EIGHTHS = 8;

	AbacusDeck[] decks = new AbacusDeck[AbacusFormat.MAX_DECKS];
	AbacusSubdeck[] subdecks = null;
	//AbacusMath abacusMath = new AbacusMath();
	int rails = AbacusInterface.DEFAULT_RAILS; /* number of columns of beads */
	int base; /* 10 usually */
	int shiftPercent, shiftAnomaly, shiftAnomalySq;
	int groupSize, anomaly, anomalySq, subdeck, subbead;
	int decimalPosition;
	int mode, submode, colorScheme = 0;
	boolean precedenceBegin, precedenceEnd, signBead = false;
	int subbase = TWELFTHS;
	int railIndex = 0;

	boolean enoughRails = true, startOperation, negativeResult;
	boolean sign = false, minusSign = false;
	boolean modernRoman = false;
	boolean group = false, decimalComma = false;
	boolean carryAnomaly = false, carryAnomalySq = false;
	boolean vertical = false, slot = false, diamond = false;
	Random generator;

	String museum = "--";
	String format = "Generic";

	public AbacusFormat(int rails) {
		/*public AbacusDeck(rails, int number, boolean orientation,
				int factor, int spaces*/
		decks[TOP] = new AbacusDeck(rails, 2, true, 5, 2);
		decks[BOTTOM] = new AbacusDeck(rails, 5, false, 1, 2);
	}

	protected String paramString() {
		return "abacus:" +
		",rails=" + rails +
		",vertical=" + vertical +
		",colorScheme=" + colorScheme +
		",slot=" + slot +
		",diamond=" + diamond +
		",railIndex=" + railIndex +
		",topOrient=" + decks[TOP].getOrientation() +
		",bottomOrient=" + decks[BOTTOM].getOrientation() +
		",topNumber=" + decks[TOP].getNumber() +
		",bottomNumber=" + decks[BOTTOM].getNumber() +
		",topFactor=" + decks[TOP].getFactor() +
		",bottomFactor=" + decks[BOTTOM].getFactor() +
		",topSpaces=" + decks[TOP].getSpaces() +
		",bottomSpaces=" + decks[BOTTOM].getSpaces() +
		",topPiece=" + decks[TOP].getPiece() +
		",bottomPiece=" + decks[BOTTOM].getPiece() +
		",topPiecePercent=" + decks[TOP].getPiecePercent() +
		",bottomPiecePercent=" + decks[BOTTOM].getPiecePercent() +
		",shiftPercent=" + shiftPercent +
		",subdeck=" + subdeck +
		",subbead=" + subbead +
		",sign=" + sign +
		",group=" + group +
		",groupSize=" + groupSize +
		",decimalComma=" + decimalComma +
		",base=" + base +
		",subbase=" + subbase +
		",anomaly=" + anomaly +
		",shiftAnomaly=" + shiftAnomaly +
		",anomalySq=" + anomalySq +
		",shiftAnomalySq=" + shiftAnomalySq +
		",modernRoman=" + modernRoman +
		",mode=" + mode +
		",submode=" + submode;
	}

	public int getRails() {
		return this.rails;
	}

	public void setRails(int value) {
		if (value > 0)
			this.rails = value;
		decks[TOP].setRailSize(value);
		decks[BOTTOM].setRailSize(value);
	}

	public int getMode() {
		return this.mode;
	}

	public void setMode(int value) {
		this.mode = value;
	}

	public int getSubmode() {
		return this.submode;
	}

	public void setSubmode(int value) {
		if (value >= 0 && value < AbacusInterface.MAX_MUSEUMS)
			this.submode = value;
	}

	public boolean getSlot() {
		if (mode == AbacusInterface.Modes.Roman.ordinal()) {
			if (!slot)
				System.out.println("Slot set to false, but Roman");
			return true;
		}
		return slot;
	}

	public void setSlot(boolean value) {
		slot = value;
	}

	public int getBase() {
		return this.base;
	}

	public void setBase(int value) {
		if (value > 1)
			this.base = value;
	}

	public int getDecimalPosition() {
		return this.decimalPosition;
	}

	public void setDecimalPosition(int value) {
		if (value >= 0)
			this.decimalPosition = value;
	}

	public int getShiftPercent() {
		return this.shiftPercent;
	}

	public void setShiftPercent(int value) {
		if (value >= 0)
			this.shiftPercent = value;
	}

	public int getSubdeck() {
		return this.subdeck;
	}

	public void setSubdeck(int value) {
		if (value >= 0)
			this.subdeck = value;
	}

	public int getSubbead() {
		return this.subbead;
	}

	public void setSubbead(int value) {
		if (value >= 0)
			this.subbead = value;
	}

	public int getSubbase() {
		return this.subbase;
	}

	public void setSubbase(int value) {
		if (value > 1)
			this.subbase = value;
	}

	public boolean getSign() {
		return this.sign;
	}

	public void setSign(boolean value) {
		this.sign = value;
	}

	public int getAnomaly() {
		return this.anomaly;
	}

	public void setAnomaly(int value) {
		this.anomaly = value;
	}

	public int getAnomalySq() {
		return this.anomalySq;
	}

	public void setAnomalySq(int value) {
		this.anomalySq = value;
	}

	public int getShiftAnomaly() {
		return this.shiftAnomaly;
	}

	public void setShiftAnomaly(int value) {
		this.shiftAnomaly = value;
	}

	public int getShiftAnomalySq() {
		return this.shiftAnomalySq;
	}

	public void setShiftAnomalySq(int value) {
		this.shiftAnomalySq = value;
	}

	public int getFactor(int index) {
		if (index >= 0 && index < decks.length)
			return this.decks[index].getFactor();
		return -1;
	}

	// FIXME for subdeck
	public int getFactor(int deck, int rail) {
		if (getSign() && rail == getRails() - 1) {
			return 0;
		} else if (getPiece(AbacusFormat.BOTTOM) != 0 &&
				(rail == getDecimalPosition() - 1)) {
			if (deck == AbacusFormat.TOP)
				return getPiece(AbacusFormat.BOTTOM);
		} else if (getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
				(rail == getDecimalPosition() -
				getShiftPercent() - 1 -
				((getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1))) {
			if (deck == AbacusFormat.TOP)
				return getPiecePercent(AbacusFormat.BOTTOM);
		} else {
			return getFactor(deck);
		}
		return 1;
	}

	public void setFactor(int index, int value) {
		if (index >= 0 && index < decks.length
				&& value > 0 && value <= base / 2)
			this.decks[index].setFactor(value);
	}

	public int getNumber(int index) {
		if (index >= 0 && index < decks.length)
			return this.decks[index].getNumber();
		return -1;
	}

	// FIXME for subdeck
	public int getNumber(int deck, int rail) {
		if (getSign() && rail == getRails() - 1) {
			return ((deck == AbacusFormat.BOTTOM) ? 1 : 0);
		} else if (getPiece(AbacusFormat.BOTTOM) != 0 &&
				(rail == getDecimalPosition() - 1)) {
			return getNumberPieces(deck);
		} else if (getPiecePercent(AbacusFormat.BOTTOM) != 0 &&
				(rail == getDecimalPosition() -
				getShiftPercent() - 1 -
				((getPiece(AbacusFormat.BOTTOM) == 0) ? 0 : 1))) {
			return getNumberPiecePercents(deck);
		} else {
			return getNumber(deck);
		}
	}

	public void setNumber(int index, int value) {
		if (index >= 0 && index < decks.length
				&& value > 0 && value <= base)
			this.decks[index].setNumber(value);
	}

	public int getSpaces(int index) {
		if (index >= 0 && index < decks.length)
			return this.decks[index].getSpaces();
		return -1;
	}

	public void setSpaces(int index, int value) {
		if (index >= 0 && index < decks.length
				&& value > 0)
			this.decks[index].setSpaces(value);
	}

	public int getRoom(int index) {
		if (index >= 0 && index < decks.length)
			return this.decks[index].getRoom();
		return -1;
	}

	public boolean getOrientation(int index) {
		if (index >= 0 && index < decks.length)
			return this.decks[index].getOrientation();
		return false;
	}

	public void setOrientation(int index, boolean value) {
		if (index >= 0 && index < decks.length) {
			this.decks[index].setOrientation(value);
		}
	}

	public int getPosition(int index, int rail) {
		if (index >= 0 && index < decks.length
				&& rail >= 0 && rail < rails) {
			return this.decks[index].getPosition(rail);
		}
		System.err.println("Error in getPosition: index=" + index
			+ ", rail=" + rail + ", rails=" + rails);
		return -1;
	}

	public void setPosition(int index, int rail, int value) {
		if (index >= 0 && index < decks.length
				&& rail >= 0 && rail < rails
				&& value >= 0) {
			this.decks[index].setPosition(rail, value);
			return;
		}
		System.err.println("Error in setPosition: index=" + index
			+ ", rail=" + rail + ", rails=" + rails);
	}

	public int getPiece(int index) {
		if (index >= 0 && index < decks.length)
			return this.decks[index].getPiece();
		return -1;
	}

	public void setPiece(int index, int value) {
		if (index >= 0 && index < decks.length)
			this.decks[index].setPiece(value);
	}

	public int getPiecePercent(int index) {
		if (index >= 0 && index < decks.length)
			return this.decks[index].getPiecePercent();
		return -1;
	}

	public void setPiecePercent(int index, int value) {
		if (index >= 0 && index < decks.length)
			this.decks[index].setPiecePercent(value);
	}

	public int getSubdecksNumber(int index) {
		if (index >= 0 && index < subdecks.length)
			return this.subdecks[index].getNumber();
		return -1;
	}

	public void setSubdecksNumber(int index, int value) {
		if (index >= 0 && index < subdecks.length)
			this.subdecks[index].setNumber(value);
	}

	public int getSubdecksSpaces(int index) {
		if (index >= 0 && index < subdecks.length)
			return this.subdecks[index].getSpaces();
		return -1;
	}

	public int getSubdecksRoom(int index) {
		if (index >= 0 && index < subdecks.length)
			return this.subdecks[index].getRoom();
		return -1;
	}

	public int getSubdecksPosition(int index) {
		if (index >= 0 && index < subdecks.length)
			return this.subdecks[index].getPosition();
		return -1;
	}

	public void setSubdecksPosition(int index, int value) {
		if (index >= 0 && index < subdecks.length)
			this.subdecks[index].setPosition(value);
	}

	public boolean getDiamond() {
		return diamond;
	}

	public void setDiamond(boolean value) {
		diamond = value;
	}

	public boolean getModernRoman() {
		return modernRoman;
	}

	public void setModernRoman(boolean value) {
		modernRoman = value;
	}

	public int getRailIndex() {
		return railIndex;
	}

	public void setRailIndex(int value) {
		if (railIndex >= 0)
			railIndex = value;
	}

	public int getColorScheme() {
		return colorScheme;
	}

	public void setColorScheme(int value) {
		if (colorScheme >= 0)
			colorScheme = value;
	}

	public int getGroupSize() {
		return groupSize;
	}

	public void setGroupSize(int value) {
		if (groupSize >= 0)
			groupSize = value;
	}

	public boolean getVertical() {
		return vertical;
	}

	public void setVertical(boolean value) {
		vertical = value;
	}

	/*public Abacus getAbacus(int auxiliary) {
		return null; //self reference, not sure if should be here or external
	}*/

	/* This is fast for small i. */
	static int rootInt(int i, int n) {
		int j = 0, k;
		int absI = (i >= 0) ? i : -i;
		int prod;

		if (n < 0 || i == 0 || (n % 2 == 0 && i < 0))
			return 0;
		if (n == 1)
			return i;
		absI = (i >= 0) ? i : -i;
		do {
			prod = 1;
			j++;
			for (k = 0; k < n; k++)
				prod *= j;
		} while (prod <= absI);
		return (i == absI) ? (j - 1) : (1 - j);
	}

	public static int convertBaseToBottom(int base) {
		int j;

		for (j = rootInt(base, 2); j > 1; j--) {
			if (base % j == 0) {
				return (base / j);
			}
		}
		return base;
	}

	/* This is setup for Roman abacus of 3 subdecks
	 * deck == 0 => 1/12 * pieceFactor
	 * deck == 1 => 1/4 * pieceFactor
	 * deck == 2 => 1/2 * pieceFactor
	 * For other subdecks its more whimsical. */
	public int convertRomanFactor(int deck) {
		int b = decks[BOTTOM].getPiece();
		int t = decks[TOP].getPiece();

		if (t != 0)
			b *= t;
		if (subbase == EIGHTHS) {
			if (deck <= 1)
				return 1;
			return deck * b / (subdeck + 2);
		}
		if (deck == 0)
			return 1;
		return deck * b / (subdeck + 1);
	}

	public int checkBottomSpace() {
		return decks[BOTTOM].getSpaces() + convertBaseToBottom(base) - 1;
	}

	public boolean checkPiece() {
		return (this.decks[BOTTOM].getPiece() != 0);
	}

	public boolean checkPiecePercent() {
		return (this.decks[BOTTOM].getPiece() != 0 &&
			this.decks[BOTTOM].getPiecePercent() != 0);
	}

	public boolean checkSubdeck(int position) {
		return (decks[BOTTOM].getPiece() != 0 &&
			decks[BOTTOM].getPiecePercent() == 0 &&
			slot && subdeck != 0 &&
			decks[BOTTOM].getRoom() >= subbead +
			subdeck * AbacusInterface.SUBDECK_SPACE &&
			decimalPosition == position);
	}

	public boolean checkAnomaly() {
		return (anomaly != 0);
	}

	public boolean checkAnomalySq() {
		return (anomalySq != 0);
	}

	public int getNumberPieces(int deck) {
		int pieces = 0;

		if (deck == BOTTOM) {
			pieces = decks[BOTTOM].getPiece();
			if (decks[TOP].getNumber() == 0 &&
					decks[TOP].getPiece() != 0)
				pieces *= decks[TOP].getPiece();
			if (decks[BOTTOM].getNumber() ==
					decks[TOP].getFactor() - 1)
				pieces -= 1;
		} else {
			if (decks[TOP].getNumber() != 0 && decks[TOP].getPiece() != 0) {
				pieces = decks[TOP].getPiece();
				if ((decks[TOP].getNumber() + 1) *
						decks[TOP].getFactor() == base)
					pieces -= 1;
			}
		}
		return pieces;
	}

	public int getNumberPiecePercents(int deck) {
		int piecePercents = 0;

		if (deck == BOTTOM) {
			piecePercents = decks[BOTTOM].getPiecePercent();
				if (decks[TOP].getNumber() == 0 &&
						decks[TOP].getPiecePercent() != 0)
					piecePercents *= decks[TOP].getPiecePercent();
				if (decks[BOTTOM].getNumber() ==
						decks[TOP].getFactor() - 1)
					piecePercents -= 1;
		} else {
			if (decks[TOP].getNumber() != 0 &&
					decks[TOP].getPiecePercent() != 0) {
				piecePercents = decks[TOP].getPiecePercent();
				if ((decks[TOP].getNumber() + 1) *
						decks[TOP].getFactor() == base)
					piecePercents -= 1;
			}
		}
		return piecePercents;
	}

	public int getValuePiece() {
		int sum = 0, localBase = 1;
		int factor = 1, rail = decimalPosition - 1;

		if (decks[BOTTOM].getPiece() != 0) {
			int deck;

			for (deck = BOTTOM; deck <= TOP; deck++) {
				int piecePosition = decks[deck].getPosition(rail);

				localBase = decks[BOTTOM].getPiece();
				if (deck == TOP) {
					if (decks[TOP].getPiece() == 0) {
						break;
					}
					factor = decks[BOTTOM].getPiece();
					localBase *= decks[TOP].getPiece();
				}
				if (decks[deck].getOrientation()) {
					sum += (getNumberPieces(deck) - piecePosition) * factor;
				} else {
					sum += piecePosition * factor;
				}
			}
		}
		return sum % localBase;
	}

	public int getValuePiecePercent() {
		int sum = 0, localBase = 1;
		int factor = 1, rail = decimalPosition - shiftPercent - 1 -
			((decks[BOTTOM].getPiece() == 0) ? 0 : 1);

		if (decks[BOTTOM].getPiece() != 0 &&
				decks[BOTTOM].getPiecePercent() != 0) {
			int deck;

			for (deck = BOTTOM; deck <= TOP; deck++) {
				int piecePercentPosition = decks[deck].getPosition(rail);

				localBase = decks[BOTTOM].getPiecePercent();
				if (deck == TOP) {
					if (decks[TOP].getPiecePercent() == 0) {
						break;
					}
					factor = decks[BOTTOM].getPiecePercent();
					localBase *= decks[TOP].getPiecePercent();
				}
				if (decks[deck].getOrientation()) {
					sum += (getNumberPiecePercents(deck) - piecePercentPosition) * factor;
				} else {
					sum += piecePercentPosition * factor;
				}
			}
		}
		return sum % localBase;
	}

	public int getValueSubdeck() {
		int sum = 0;

		if (checkSubdeck(3)) {
			int deck;

			for (deck = 0; deck < subdeck; deck++) {
				if (decks[BOTTOM].getOrientation()) {
					sum += (subdecks[deck].getNumber() -
						subdecks[deck].getPosition()) * convertRomanFactor(deck);
				} else {
					sum += subdecks[deck].getPosition() * convertRomanFactor(deck);
				}
			}
		}
		return sum;
	}

	public void setSpace(int deck) {
		int room, number;

		room = decks[deck].getNumber() + decks[deck].getSpaces();
		if (decks[BOTTOM].getPiece() != 0) {
			number = getNumberPieces(deck);
			if (room <= number)
				room = number + 1;
			if (decks[BOTTOM].getPiecePercent() != 0) {
				number = getNumberPiecePercents(deck);
				if (room <= number)
					room = number + 1;
			}
			if (deck == BOTTOM && checkSubdeck(3)) {
				number = subbead + subdeck *
					AbacusInterface.SUBDECK_SPACE - 1;
				if (room <= number)
					room = number + 1;
			}
		}
		if (decks[deck].getNumber() != 0) {
			if (room > decks[deck].getNumber() +
					decks[deck].getSpaces())
				decks[deck].setSpaces(room -
					decks[deck].getNumber());
		}
	}

	public int getNumberSubbeads(int localSubdecks) {
		if (subdeck != 0)
			return subbead / subdeck +
				((subbead % subdeck - localSubdecks <= 0) ? 0 : 1);
		return 0;
	}

	public int getNumberSubbeadsOffset(int localSubdecks) {
		int deck, nOffset = 0;
		int space = 1;

		if (localSubdecks < 0)
			return subbead +
				subdeck * AbacusInterface.SUBDECK_SPACE;
		for (deck = 0; deck < subdeck - 1 - localSubdecks; deck++) {
			nOffset += getNumberSubbeads(subdeck - 1 - deck) +
				space;
		}
		return nOffset;
	}

	public int getPositionSubdeck(int j) {
		int b1, b2, subj, d;

		subj = j;
		b1 = b2 = 0;
		for (d = subdeck - 1; d >= 0; d--) {
			b1 = b2;
			b2 = getNumberSubbeadsOffset(d - 1);
			if (subj <= b2) {
				subj = subj - b1;
				break;
			}
		}
		return subj;
	}

	public int getSubdeckPosition(int j) {
		int subj, d;

		subj = j;
		for (d = subdeck - 1; d >= 0; d--) {
			if (subj <= getNumberSubbeadsOffset(d - 1)) {
				break;
			}
		}
		return d;
	}

/*
	int numberSubdeck(int subbeads) {
		int deck, offset = 0;
		int space = 1;

		for (deck = 0; deck < subdeck; deck++) {
			offset += numberSubbeads(deck) + space;
			if (offset >= subbeads)
				return deck;
		}
		return subdeck;
	}
*/

	/*char decimalChar() {
		return ((decimalComma) ? ',' : '.');
	}

	char groupChar() {
		return ((decimalComma) ? '.' : ',');
	}*/

	public void setSubmodeFromMuseum() {
		if (museum.equalsIgnoreCase("it")) {
			submode = AbacusInterface.IT;
		} else if (museum.equalsIgnoreCase("uk")) {
			submode = AbacusInterface.UK;
		} else if (museum.equalsIgnoreCase("fr")) {
			submode = AbacusInterface.FR;
		} else {
			submode = generator.nextInt(AbacusInterface.MAX_MUSEUMS);
		}
	}

	static public boolean getSubmodeSlotsSeparate(int localSubmode) {
		return (localSubmode == AbacusInterface.UK);
	}

	public void checkBeads() {
		if (railIndex != 0)
			railIndex = 1;
		if (shiftPercent <= 0) {
			shiftPercent = AbacusInterface.DEFAULT_SHIFT_PERCENT;
		}
		if (shiftPercent >= rails) {
			shiftPercent = 2;
		}
		if (decks[BOTTOM].getPiece() > AbacusInterface.MAX_BASE) {
			System.out.println(
				"Bottom Piece must be less than or equal to " +
				AbacusInterface.MAX_BASE);
			decks[BOTTOM].setPiece(0);
		} else if (decks[BOTTOM].getPiece() != 0 &&
				decks[BOTTOM].getPiece() < AbacusInterface.MIN_BASE) {
			System.out.println(
				"Bottom Piece must be greater than or equal to " +
				AbacusInterface.MIN_BASE + ", or 0");
			decks[BOTTOM].setPiece(0);
		}
		if (decks[TOP].getPiece() != 0 &&
				decks[TOP].getPiece() < AbacusInterface.MIN_BASE) {
			System.out.println(
				"Top Piece must be greater than or equal to " +
				AbacusInterface.MIN_BASE + ", or 0");
			decks[TOP].setPiece(0);
		}
		if (decks[BOTTOM].getPiecePercent() > AbacusInterface.MAX_BASE) {
			System.out.println(
				"Bottom Piece Percent must be less than or equal to " +
				AbacusInterface.MAX_BASE);
			decks[BOTTOM].setPiecePercent(0);
		} else if (decks[BOTTOM].getPiecePercent() != 0 &&
				decks[BOTTOM].getPiecePercent() < AbacusInterface.MIN_BASE) {
			System.out.println(
				"Bottom Piece Percent must be greater than or equal to " +
				AbacusInterface.MIN_BASE + ", or 0");
			decks[BOTTOM].setPiecePercent(0);
		}
		if (decks[TOP].getPiecePercent() != 0 &&
				decks[TOP].getPiecePercent() < AbacusInterface.MIN_BASE) {
			System.out.println(
				"Top Piece Percent must be greater than or equal to " +
				AbacusInterface.MIN_BASE + ", or 0");
			decks[TOP].setPiecePercent(0);
		}
		if (base > AbacusInterface.MAX_BASE) {
			System.out.println(
				"Base must be less than or equal to " +
				AbacusInterface.MAX_BASE);
			base = AbacusInterface.DEFAULT_BASE;
		} else if (base < AbacusInterface.MIN_BASE) {
			System.out.println(
				"Base must be greater than or equal to " +
				AbacusInterface.MIN_BASE);
			base = AbacusInterface.DEFAULT_BASE;
		/*} else if (base != AbacusInterface.DEFAULT_BASE && demo) {
			System.out.println(
				"Base must be equal to " +
				AbacusInterface.DEFAULT_BASE + ", for demo");
			base = AbacusInterface.DEFAULT_BASE;*/
		}
		if (decks[BOTTOM].getPiece() != 0 &&
				(checkBottomSpace() < decks[BOTTOM].getPiece())) {
			System.out.println(
				"Bottom Spaces must be large enough with base when piece set");
			decks[BOTTOM].setSpaces(decks[BOTTOM].getSpaces() -
				(checkBottomSpace() - decks[BOTTOM].getPiece()));
		}
		if (decks[BOTTOM].getPiecePercent() > 1 &&
				(checkBottomSpace() < decks[BOTTOM].getPiecePercent())) {
			System.out.println(
				"Bottom Spaces must be large enough with base when piece percent set");
			decks[BOTTOM].setSpaces(decks[BOTTOM].getSpaces() -
				(checkBottomSpace() - decks[BOTTOM].getPiecePercent()));
		}
		if (anomaly < 0) {
			System.out.println(
				"Anomaly must be greater than or equal to 0");
			anomaly = 0;
		}
		if (anomaly >= base) {
			System.out.println(
				"Anomaly must be less than " + base);
			anomaly = 0;
		}
		if (shiftAnomaly <= 0) {
			System.out.println(
				"Shift Anomaly must be greater than 0");
			shiftAnomaly = AbacusInterface.DEFAULT_SHIFT_ANOMALY;
		}
		if (anomalySq < 0) {
			System.out.println(
				"Anomaly Squared must be greater than or equal to 0");
			anomalySq = 0;
		}
		if (anomalySq >= base) {
			System.out.println(
				"Anomaly Squared must be less than " + base);
			anomalySq = 0;
		}
		if (shiftAnomalySq <= 0) {
			System.out.println(
				"Shift Anomaly Squared must be greater than 0");
			shiftAnomalySq = AbacusInterface.DEFAULT_SHIFT_ANOMALY;
		}
		/*if (mode == AbacusInterface.Modes.Generic.ordinal() && demo) {
			System.out.println(
				"Format must not be \"Generic\", for demo");
		}*/
		if (mode == AbacusInterface.Modes.Medieval.ordinal()) {
			colorScheme = 0;
			decks[BOTTOM].setFactor(1);
			decks[TOP].setFactor(convertBaseToBottom(base));
			decks[BOTTOM].setNumber(decks[TOP].getFactor());
			decks[TOP].setNumber(base / decks[TOP].getFactor());
			decks[BOTTOM].setOrientation(AbacusInterface.DEFAULT_BOTTOM_ORIENT);
			decks[TOP].setOrientation(AbacusInterface.DEFAULT_BOTTOM_ORIENT);
			decks[BOTTOM].setSpaces(AbacusInterface.DEFAULT_BOTTOM_SPACES);
			decks[TOP].setSpaces(AbacusInterface.DEFAULT_TOP_SPACES);
			vertical = true;
			slot = false;
			diamond = false;
			railIndex = 3;
			setSpace(BOTTOM);
			setSpace(TOP);
		} else if (mode == AbacusInterface.Modes.Danish.ordinal()) {
			colorScheme = AbacusInterface.COLOR_HALF;
			decks[BOTTOM].setFactor(1);
			decks[TOP].setFactor(base);
			decks[BOTTOM].setNumber(base);
			decks[TOP].setNumber(0);
			decks[BOTTOM].setOrientation(AbacusInterface.DEFAULT_BOTTOM_ORIENT);
			decks[TOP].setOrientation(AbacusInterface.DEFAULT_TOP_ORIENT);
			decks[BOTTOM].setSpaces(4 * AbacusInterface.DEFAULT_BOTTOM_SPACES);
			decks[TOP].setSpaces(0);
			vertical = true;
			slot = false;
			diamond = false;
			railIndex = 1;
			setSpace(BOTTOM);
			setSpace(TOP);
		} else if (mode == AbacusInterface.Modes.Russian.ordinal()) {
			colorScheme = AbacusInterface.COLOR_MIDDLE |
				AbacusInterface.COLOR_FIRST;
			decks[BOTTOM].setFactor(1);
			decks[TOP].setFactor(base);
			decks[BOTTOM].setNumber(base);
			decks[TOP].setNumber(0);
			decks[BOTTOM].setOrientation(AbacusInterface.DEFAULT_TOP_ORIENT);
			decks[TOP].setOrientation(AbacusInterface.DEFAULT_TOP_ORIENT);
			decks[BOTTOM].setSpaces(AbacusInterface.DEFAULT_BOTTOM_SPACES);
			decks[TOP].setSpaces(0);
			vertical = true;
			slot = false;
			diamond = false;
			railIndex = 1;
			setSpace(BOTTOM);
			setSpace(TOP);
		} else if (mode == AbacusInterface.Modes.Japanese.ordinal() ||
				mode == AbacusInterface.Modes.Roman.ordinal()) {
			colorScheme = 0;
			decks[BOTTOM].setFactor(1);
			decks[TOP].setFactor(convertBaseToBottom(base));
			decks[BOTTOM].setNumber(decks[TOP].getFactor() - 1);
			decks[TOP].setNumber(base / decks[TOP].getFactor() - 1);
			decks[BOTTOM].setOrientation(AbacusInterface.DEFAULT_BOTTOM_ORIENT);
			decks[TOP].setOrientation(AbacusInterface.DEFAULT_TOP_ORIENT);
			if (subdeck < 0)
				subdeck = AbacusInterface.DEFAULT_SUBDECKS;
			vertical = false;
			if (mode == AbacusInterface.Modes.Japanese.ordinal()) {
				slot = false;
				diamond = true;
				decks[BOTTOM].setSpaces(AbacusInterface.DEFAULT_BOTTOM_SPACES - 1);
				decks[TOP].setSpaces(AbacusInterface.DEFAULT_TOP_SPACES - 1);
			} else if (mode == AbacusInterface.Modes.Roman.ordinal()) {
				slot = true;
				diamond = false;
				decks[BOTTOM].setSpaces(AbacusInterface.DEFAULT_BOTTOM_SPACES + 1);
				decks[TOP].setSpaces(AbacusInterface.DEFAULT_TOP_SPACES + 1);
			}
			railIndex = 0;
			setSpace(BOTTOM);
			setSpace(TOP);
		} else if (mode == AbacusInterface.Modes.Korean.ordinal()) {
			colorScheme = 0;
			decks[BOTTOM].setFactor(1);
			decks[TOP].setFactor(convertBaseToBottom(base));
			decks[BOTTOM].setNumber(decks[TOP].getFactor());
			decks[TOP].setNumber(base / decks[TOP].getFactor() - 1);
			decks[BOTTOM].setOrientation(AbacusInterface.DEFAULT_BOTTOM_ORIENT);
			decks[TOP].setOrientation(AbacusInterface.DEFAULT_TOP_ORIENT);
			decks[BOTTOM].setSpaces(AbacusInterface.DEFAULT_BOTTOM_SPACES - 1);
			decks[TOP].setSpaces(AbacusInterface.DEFAULT_TOP_SPACES - 1);
			vertical = false;
			slot = false;
			diamond = true;
			railIndex = 0;
			setSpace(BOTTOM);
			setSpace(TOP);
		} else if (mode == AbacusInterface.Modes.Chinese.ordinal()) {
			colorScheme = 0;
			decks[BOTTOM].setFactor(1);
			decks[TOP].setFactor(convertBaseToBottom(base));
			decks[BOTTOM].setNumber(decks[TOP].getFactor());
			decks[TOP].setNumber(base / decks[TOP].getFactor());
			decks[BOTTOM].setOrientation(AbacusInterface.DEFAULT_BOTTOM_ORIENT);
			decks[TOP].setOrientation(AbacusInterface.DEFAULT_TOP_ORIENT);
			decks[BOTTOM].setSpaces(AbacusInterface.DEFAULT_BOTTOM_SPACES);
			decks[TOP].setSpaces(AbacusInterface.DEFAULT_TOP_SPACES);
			vertical = false;
			slot = false;
			diamond = false;
			railIndex = 0;
			setSpace(BOTTOM);
			setSpace(TOP);
		}
		//if (demo && !aux) { /* Trying to keep these at a minimum... */
		/*	if (rails < AbacusInterface.MIN_DEMO_RAILS) {
				System.out.println(
					"Number of rails must be at least " +
					AbacusInterface.MIN_DEMO_RAILS +
					", for demo");
				for (int i = 0; i < AbacusInterface.MIN_DEMO_RAILS - rails; i++)
					((AbacusApplet)applet).callbackAbacus(this,
						AbacusInterface.ACTION_INCREMENT);
				rails = AbacusInterface.MIN_DEMO_RAILS;
			}
			if (rails - decimalPosition <
					AbacusInterface.MIN_DEMO_RAILS) {
				if (decks[BOTTOM].getPiecePercent() != 0) {
					decks[BOTTOM].getPiecePercent() = 0;
					((AbacusApplet)applet).callbackAbacus(this,
						AbacusInterface.ACTION_QUARTER_PERCENT);
				}
				if (decks[BOTTOM].getPiece() != 0) {
					decks[BOTTOM].getPiece() = 0;
					((AbacusApplet)applet).callbackAbacus(this,
						AbacusInterface.ACTION_QUARTER);
				}
				decimalPosition = 0;
			}
		} else*/ {
			if (rails < AbacusInterface.MIN_RAILS) {
				System.out.println(
					"Number of rails must be at least " +
					AbacusInterface.MIN_RAILS);
			}
			if (decks[TOP].getFactor() < 1 ||
					decks[TOP].getFactor() > base) {
				System.out.println(
					"Factor of Top Beads out of bounds, use 1.." +
					base);
				decks[TOP].setFactor(5);
			}
			if (decks[BOTTOM].getFactor() < 1 ||
					decks[BOTTOM].getFactor() >
					base) {
				System.out.println(
					"Factor of Bottom Beads out of bounds, use 1.." +
					base);
				decks[BOTTOM].setFactor(1);
			}
		}
		if (decks[TOP].getNumber() < 0 ||
				decks[TOP].getNumber() > base) {
			System.out.println(
				"Number of Top Beads out of bounds, use 1.." +
				base);
			decks[TOP].setNumber(2);
		}
		if (decks[BOTTOM].getNumber() < 0 ||
				decks[BOTTOM].getNumber() > base) {
			System.out.println(
				"Number of Bottom Beads out of bounds, use 1.." +
				base);
			decks[BOTTOM].setNumber(5);
		}
		if (decks[TOP].getSpaces() < 0) {
			System.out.println(
				"Number of Top Spaces must be at least 0");
			decks[TOP].setSpaces(2);
		}
		if (decks[BOTTOM].getSpaces() < 0) {
			System.out.println(
				"Number of Bottom Spaces must be at least 0");
			decks[BOTTOM].setSpaces(2);
		}
		if (decks[TOP].getSpaces() == 0 && decks[BOTTOM].getSpaces() == 0) {
			System.out.println(
				"Number of Top plus Bottom Spaces must be at least 1");
			decks[BOTTOM].setSpaces(2);
		}
		if (groupSize < 2) {
			System.out.println(
				"Group Size must be at least 2");
			groupSize = AbacusInterface.DEFAULT_GROUP_SIZE;
		}
	}

	public boolean checkMove(int deck, int rail , int number, boolean print) {
		int deck_number = 0, deck_position = 0;

		if (deck < 0 || deck > 2) {
			if (print)
				System.out.println(
					"Corrupted deck input value " + deck +
					" out of bounds, use 0..2, ignoring");
			return false;
		}
		if (rail < -decimalPosition ||
				rail >= rails - decimalPosition) {
			if (print)
				System.out.println(
					"Number of rails too small for input, needs rail " +
					rail);
			enoughRails = false;
			return false;
		}
		enoughRails = true;
		if (deck == PLACE_SETTING) {
			// moving decimal point
			if (number + decimalPosition >= rails ||
					number + decimalPosition < 0) {
				System.out.println(
					"Corrupted number, input value " +
					number + " out of bounds, use " +
					(-decimalPosition) + ".." +
					(rails - decimalPosition));

				return false;
			}
			return true;
		}
		deck_number = decks[deck].getNumber();
		deck_position = decks[deck].getPosition(rail + decimalPosition);
		if (decks[deck].getOrientation() &&
				(number < -deck_number + deck_position ||
				number > deck_position)) {
			if (print)
				System.out.println(
					"Corrupted number for input value " +
					number + " out of bounds, use " +
					(-deck_number + deck_position) + ".." +
					deck_position);
			return false;
		}
		if (!decks[deck].getOrientation() &&
				(number < -deck_position ||
				number > deck_number - deck_position)) {
			if (print)
				System.out.println(
					"Corrupted number for input value " +
					(-deck_position) + ".." +
					(deck_number - deck_position));
			return false;
		}
		return true;
	}

	public void resetSubdecks() {
		int deck, ndecks;

		ndecks = (subdeck <= 0) ? AbacusInterface.DEFAULT_SUBDECKS :
			subdeck;
		subdecks = new AbacusSubdeck[ndecks];
		for (deck = 0; deck < ndecks; deck++) {
			subdecks[deck] = new AbacusSubdeck(
				getNumberSubbeads(deck),
				AbacusInterface.SUBDECK_SPACE);
			subdecks[deck].setPosition((decks[BOTTOM].getOrientation()) ?
				subdecks[deck].getNumber() : 0);
		}
	}

	// needs to be unlinked from numDigits
	public void resetBeads() {
		int deck, rail;

		//currentDeck = AbacusInterface.ACTION_IGNORE;
		//numDigits = rails + CARRY + 1;
		for (deck = BOTTOM; deck <= TOP; deck++) {
			decks[deck].setRailSize(rails);
			for (rail = 0; rail < rails; rail++) {
				decks[deck].setPosition(rail,
					(decks[deck].getOrientation()) ?
					decks[deck].getNumber() : 0);
			}
		}
		if (sign) {
			rail = rails - 1;
			decks[BOTTOM].setPosition(rail,
				(decks[BOTTOM].getOrientation()) ? 1 : 0);
		}
		if (decks[BOTTOM].getPiece() != 0) {
			rail = decimalPosition - 1;
			decks[BOTTOM].setPosition(rail,
				(decks[BOTTOM].getOrientation()) ?
				getNumberPieces(BOTTOM) : 0);
			if (decks[TOP].getNumber() != 0 &&
					decks[TOP].getPiece() != 0) {
				decks[TOP].setPosition(rail,
					(decks[TOP].getOrientation()) ?
					getNumberPieces(TOP) : 0);
			}
		}
		if (decks[BOTTOM].getPiece() != 0 &&
				decks[BOTTOM].getPiecePercent() != 0) {
			rail = decimalPosition - shiftPercent - 1 -
				((decks[BOTTOM].getPiece() == 0) ? 0 : 1);
			decks[BOTTOM].setPosition(rail,
				(decks[BOTTOM].getOrientation()) ?
				getNumberPiecePercents(BOTTOM) : 0);
			if (decks[TOP].getNumber() != 0 &&
					decks[TOP].getPiecePercent() != 0) {
				decks[TOP].setPosition(rail,
					(decks[TOP].getOrientation()) ?
					getNumberPiecePercents(TOP) : 0);
			}
		}
		resetSubdecks();
		/*digits = new StringBuffer(numDigits);
		digits.setLength(numDigits);
		for (rail = 0; rail < numDigits - 1; rail++)
			digits = digits.insert(rail, '0');*/
	}

	public static int newPos(int dir, int inc) {
		return (((dir == UP) ? -1 : 1) * inc);
	}

	// needs to be unlinked from digit
	void shiftBar(int oldDecimalPosition) {
		int deck, rail;
		int[] pieces = new int[MAX_DECKS];
		int[] piecePercents = new int[MAX_DECKS];
		int pieceRail = decimalPosition - 1;
		int piecePercentRail = decimalPosition - shiftPercent -
			1 - ((decks[BOTTOM].getPiece() == 0) ? 0 : 1);
		int oldPieceRail = oldDecimalPosition - 1;
		int oldPiecePercentRail = oldDecimalPosition - shiftPercent -
			1 - ((decks[BOTTOM].getPiece() == 0) ? 0 : 1);
		//char p = '0', pp = '0';

		pieces[TOP] = 0;
		pieces[BOTTOM] = 0;
		piecePercents[TOP] = 0;
		piecePercents[BOTTOM] = 0;
		if (decks[BOTTOM].getPiece() != 0) {
			//int digit = rails + CARRY - oldDecimalPosition;

			pieces[BOTTOM] =
				decks[BOTTOM].getPosition(oldPieceRail);
			if (decks[TOP].getPiece() != 0 ||
					decks[TOP].getNumber() == 0)
				pieces[TOP] =
					decks[TOP].getPosition(oldPieceRail);
			//p = digits.charAt(digit);
			//digits.setCharAt(digit, '0');

		}
		if (decks[BOTTOM].getPiecePercent() != 0) {
			//int digit = rails + CARRY -
			//	oldDecimalPosition + shiftPercent +
			//	((decks[BOTTOM].getPiece() == 0) ? 0 : 1);

			piecePercents[BOTTOM] =
				decks[BOTTOM].getPosition(oldPiecePercentRail);
			if (decks[TOP].getPiecePercent() != 0 ||
					decks[TOP].getNumber() == 0)
				piecePercents[TOP] =
					decks[TOP].getPosition(oldPiecePercentRail);
			//pp = digits.charAt(digit);
			//digits.setCharAt(digit, '0');
		}
		// shift around
		if (oldDecimalPosition < decimalPosition) {
			for (rail = oldPieceRail; rail < pieceRail; rail++) {
				for (deck = BOTTOM; deck <= TOP; deck++) {
					decks[deck].setPosition(rail,
						decks[deck].getPosition(rail + 1));
					if (decks[BOTTOM].getPiecePercent() != 0)
						decks[deck].setPosition(rail - shiftPercent - 1,
							decks[deck].getPosition(rail - shiftPercent));
				}
			}
			/*for (rail = rails + CARRY - oldDecimalPosition;
					rail > rails + CARRY -
					decimalPosition; rail--) {
				digits.setCharAt(rail, digits.charAt(rail - 1));
				if (decks[BOTTOM].getPiecePercent() != 0)
					digits.setCharAt(rail + shiftPercent + 1,
						digits.charAt(rail + shiftPercent));
			}*/
		} else if (oldDecimalPosition > decimalPosition) {
			for (rail = oldPieceRail; rail > pieceRail; rail--) {
				for (deck = BOTTOM; deck <= TOP; deck++) {
					decks[deck].setPosition(rail,
						decks[deck].getPosition(rail - 1));
					if (decks[BOTTOM].getPiecePercent() != 0)
						decks[deck].setPosition(rail - shiftPercent - 1,
							decks[deck].getPosition(rail - shiftPercent - 2));
				}
			}
			/*for (rail = rails + CARRY - oldDecimalPosition;
					rail < rails + CARRY -
					decimalPosition; rail++) {
				digits.setCharAt(rail, digits.charAt(rail + 1));
				if (decks[BOTTOM].getPiecePercent() != 0)
					digits.setCharAt(rail + shiftPercent + 1,
						digits.charAt(rail + shiftPercent + 2));
			}*/
		}
		if (decks[BOTTOM].getPiece() != 0) {
			decks[BOTTOM].setPosition(pieceRail, pieces[BOTTOM]);
			if (decks[TOP].getPiece() == 0 ||
					decks[TOP].getNumber() == 0)
				decks[TOP].setPosition(pieceRail, 0);
			else
				decks[TOP].setPosition(pieceRail, pieces[TOP]);
//			digits.setCharAt(rails + CARRY - decimalPosition, p);
		}
		if (decks[BOTTOM].getPiecePercent() != 0) {
			decks[BOTTOM].setPosition(piecePercentRail, piecePercents[BOTTOM]);
			if (decks[TOP].getPiecePercent() == 0 &&
					decks[TOP].getNumber() == 0)
				decks[TOP].setPosition(piecePercentRail, 0);
			else
				decks[TOP].setPosition(piecePercentRail, piecePercents[TOP]);
/*			digits.setCharAt(rails + CARRY -
				decimalPosition + shiftPercent +
				((decks[BOTTOM].getPiece() == 0) ? 0 : 1), pp);*/
		}
	}

	public void initializeAbacus() {
		//decks[deck].position = null;

		/*numSlices = ((delay < 5 * MAX_SLICES) ? delay / 5 + 1 :
			MAX_SLICES);*/
		/*if (decks[BOTTOM].getPiece() != 0)
			decimalPosition++;
		if (decks[BOTTOM].getPiecePercent() != 0)
			decimalPosition++;*/
	
		/*for (deck = 0; deck < MAX_DECKS; deck++)
			decks[deck] = new AbacusDeck(deck);*/
		//digits = null;
		//mode = AbacusInterface.setModeFromFormat();
		//setSubmodeFromMuseum();
		checkBeads();
		resetBeads();
		//resizeAbacus();
		generator = new Random(System.nanoTime());
	}

	public void changeMuseumAbacus() {
		//setMuseum(++submode);
		//((AbacusApplet)applet).callbackAbacus(this,
		//	AbacusInterface.ACTION_MUSEUM);
	}


	public int getMuseum() {
		return submode;
	}

	/*public void toggleRomanNumeralsAbacus() {
		toggleRomanNumeralsDisplay();
	}

	void toggleGroupingAbacus() {
		toggleGroupDisplay();
	}

	void toggleNegativeSignAbacus() {
		toggleSignRail();
	}*/
}
