package org.tux.bagleyd.abacus.model;

/*
 * @(#)NumberField.java
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
 * The <code>NumberField</code> class holds info about the text
 * display for the abacus widget.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */

import org.tux.bagleyd.abacus.AbacusInterface;

public class NumberField {
	/* I guess the next one could be even more if you have a weird config */
	public static final int CARRY = 1;

	int displayBase = AbacusInterface.DEFAULT_BASE;
	boolean romanNumerals = false, latin = false;
	boolean ancientRoman = false;
	boolean group = false, decimalComma = false;
	boolean carryAnomaly = false, carryAnomalySq = false;
	//AbacusMath abacusMath = new AbacusMath();
	int numberDigits;
	StringBuffer digits = null;
	/*String multiplier, divisor;
	String expression, operateOn, origExpression;
	String lhOperand = "";
	String divResult = "", prevResult = "";
	char operand;*/

	protected String paramString() {
		return "abacus:" +
		"group=" + group +
		",decimalComma=" + decimalComma +
		",displayBase=" + displayBase +
		",romanNumerals=" + romanNumerals +
		",latin=" + latin +
		",ancientRoman=" + ancientRoman;
	}

	public int getDisplayBase() {
		return this.displayBase;
	}

	public void setDisplayBase(int value) {
		if (value > 1)
			this.displayBase = value;
	}

	public boolean getGroup() {
		return this.group;
	}

	public void setGroup(boolean value) {
		this.group = value;
	}

	public boolean getDecimalComma() {
		return this.decimalComma;
	}

	public void setDecimalComma(boolean value) {
		this.decimalComma = value;
	}

	public boolean getRomanNumerals() {
		return this.romanNumerals;
	}

	public void setRomanNumerals(boolean value) {
		this.romanNumerals = value;
	}

	public boolean getLatin() {
		return this.latin;
	}

	public void setLatin(boolean value) {
		this.latin = value;
	}

	public boolean getAncientRoman() {
		return this.ancientRoman;
	}

	public void setAncientRoman(boolean value) {
		this.ancientRoman = value;
	}

	public boolean getCarryAnomaly() {
		return this.carryAnomaly;
	}

	public void setCarryAnomaly(boolean value) {
		this.carryAnomaly = value;
	}

	public boolean getCarryAnomalySq() {
		return this.carryAnomalySq;
	}

	public void setCarryAnomalySq(boolean value) {
		this.carryAnomalySq = value;
	}

	public char decimalChar() {
		return ((decimalComma) ? ',' : '.');
	}

	public char groupChar() {
		return ((decimalComma) ? '.' : ',');
	}

	public int getNumberDigits() {
		return numberDigits;
	}

	public char getDigitCharAt(int position) {
		return digits.charAt(position);
	}

	public void setDigitCharAt(int position, char value) {
		digits.setCharAt(position, value);
	}

	public String getDigitString() {
		return digits.toString();
	}

	public void checkBeads() {
		if (displayBase > AbacusInterface.MAX_BASE) {
			System.out.println(
				"Display base must be less than or equal to " +
				AbacusInterface.MAX_BASE);
			displayBase = AbacusInterface.DEFAULT_BASE;
		} else if (displayBase < AbacusInterface.MIN_BASE) {
			System.out.println(
				"Display base must be greater than or equal to " +
				AbacusInterface.MIN_BASE);
			displayBase = AbacusInterface.DEFAULT_BASE;
		/*} else if (displayBase != AbacusInterface.DEFAULT_BASE && demo) {
			System.out.println(
				"Display base must be equal to " +
				AbacusInterface.DEFAULT_BASE + ", for demo");
			displayBase = AbacusInterface.DEFAULT_BASE;*/
		}
	}

	public void resetField(int rails) {
		numberDigits = rails + CARRY + 1;

		digits = new StringBuffer(numberDigits);
		digits.setLength(numberDigits);
		for (int rail = 0; rail < numberDigits - 1; rail++)
			digits = digits.insert(rail, '0');
	}

	public boolean isEmptyResultRegister() {
		int n = 0;
		boolean good = true;

		while (n < numberDigits - 1) {
			if (digits.charAt(n) != '0') {
				System.out.println("isEmptyResultRegister: " +
					n + ", " + digits.charAt(n));
				good = false;
				digits.setCharAt(n, '0');
			}
			n++;
		}
		return good;
	}

	// Possible on Chinese, Korean, Russian, Danish, Medieval
	public boolean isSuperSaturatedResultRegister() {
		return digits.charAt(0) != '0';
	}

	/*void shiftBar(int oldDecimalPosition) {
		int deck, rail;
		int[] pieces = new int[MAX_DECKS];
		int[] piecePercents = new int[MAX_DECKS];
		int pieceRail = decimalPosition - 1;
		int piecePercentRail = decimalPosition - shiftPercent -
			1 - ((decks[BOTTOM].getPiece() == 0) ? 0 : 1);
		int oldPieceRail = oldDecimalPosition - 1;
		int oldPiecePercentRail = oldDecimalPosition - shiftPercent -
			1 - ((decks[BOTTOM].getPiece() == 0) ? 0 : 1);
		char p = '0', pp = '0';

		pieces[TOP] = 0;
		pieces[BOTTOM] = 0;
		piecePercents[TOP] = 0;
		piecePercents[BOTTOM] = 0;
		if (decks[BOTTOM].getPiece() != 0) {
			int digit = rails + CARRY - oldDecimalPosition;

			pieces[BOTTOM] =
				decks[BOTTOM].getPosition(oldPieceRail);
			if (decks[TOP].getPiece() != 0 ||
					decks[TOP].getNumber() == 0)
				pieces[TOP] =
					decks[TOP].getPosition(oldPieceRail);
			p = digits.charAt(digit);
			digits.setCharAt(digit, '0');

		}
		if (decks[BOTTOM].getPiecePercent() != 0) {
			int digit = rails + CARRY -
				oldDecimalPosition + shiftPercent +
				((decks[BOTTOM].getPiece() == 0) ? 0 : 1);

			piecePercents[BOTTOM] =
				decks[BOTTOM].getPosition(oldPiecePercentRail);
			if (decks[TOP].getPiecePercent() != 0 ||
					decks[TOP].getNumber() == 0)
				piecePercents[TOP] =
					decks[TOP].getPosition(oldPiecePercentRail);
			pp = digits.charAt(digit);
			digits.setCharAt(digit, '0');
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
			for (rail = rails + CARRY - oldDecimalPosition;
					rail > rails + CARRY -
					decimalPosition; rail--) {
				digits.setCharAt(rail, digits.charAt(rail - 1));
				if (decks[BOTTOM].getPiecePercent() != 0)
					digits.setCharAt(rail + shiftPercent + 1,
						digits.charAt(rail + shiftPercent));
			}
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
			for (rail = rails + CARRY - oldDecimalPosition;
					rail < rails + CARRY -
					decimalPosition; rail++) {
				digits.setCharAt(rail, digits.charAt(rail + 1));
				if (decks[BOTTOM].getPiecePercent() != 0)
					digits.setCharAt(rail + shiftPercent + 1,
						digits.charAt(rail + shiftPercent + 2));
			}
		}
		if (decks[BOTTOM].getPiece() != 0) {
			decks[BOTTOM].position[pieceRail] = pieces[BOTTOM];
			if (decks[TOP].getPiece() == 0 ||
					decks[TOP].getNumber() == 0)
				decks[TOP].position[pieceRail] = 0;
			else
				decks[TOP].position[pieceRail] = pieces[TOP];
			digits.setCharAt(rails + CARRY - decimalPosition, p);
		}
		if (decks[BOTTOM].getPiecePercent() != 0) {
			decks[BOTTOM].position[piecePercentRail] =
				piecePercents[BOTTOM];
			if (decks[TOP].getPiecePercent() == 0 &&
					decks[TOP].getNumber() == 0)
				decks[TOP].position[piecePercentRail] = 0;
			else
				decks[TOP].position[piecePercentRail] =
					piecePercents[TOP];
			digits.setCharAt(rails + CARRY -
				decimalPosition + shiftPercent +
				((decks[BOTTOM].getPiece() == 0) ? 0 : 1), pp);
		}
	}*/

	public void initializeAbacus() {
		//decks[deck].position = null;


		digits = null;
	}
}
