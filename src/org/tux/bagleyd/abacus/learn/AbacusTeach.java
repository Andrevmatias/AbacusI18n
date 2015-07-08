package org.tux.bagleyd.abacus.learn;

/*
 * @(#)AbacusTeach.java
 *
 * Copyright 2009 - 2014  David A. Bagley, bagleyd@tux.org
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
import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.tux.bagleyd.abacus.AbacusApplet;
import org.tux.bagleyd.abacus.AbacusCalc;
import org.tux.bagleyd.abacus.Abacus;
import org.tux.bagleyd.abacus.AbacusInterface;
import org.tux.bagleyd.abacus.AbacusMath;

/**
 * The <code>AbacusTeach</code> class handles the addition,
 * subtraction, and multiplication operations for the
 * <code>AbacusApplet</code> class.
 * Used tables from How to Learn Lee's Abacus by Lee Kai-chen.
 */

public class AbacusTeach {
	static int[] carry = new int[2]; /* 0 for planned, 1 for actual */
	Applet applet;
	StringBuffer aString = null, bString = null;
	StringBuffer cString = null, rString = null, sString = null;
	char op;
	static int lower = 0, upper = 0;
	int step = 0;
	static int rPosition = 0, aDigit = 0, bDigit = 0, bValue = 0;

	/* needed for division */
	static int qPosition = 0, reg = -1, regCount = 0, qDigit = 0;
	static int auxCount, primarySteps;
	BigDecimal divisor = BigDecimal.ZERO;

	static int digitSum, orderInc, orderSum, groupInc, groupSum;

	static int carryStep = 0;
	static boolean oldDebug = false;
	static final boolean debug = false;
	//static final boolean debug = true;
	static final boolean test = false;
	//static final boolean test = ((debug) ? false : true);
	static int state = 0; /* planned or actual */

	static int[] intGroup = null;

	public void reset() {
		step = 0;
	}

	public AbacusTeach(Applet applet) {
		this.applet = applet;
		step = 0;
	}

	/* This ignores strange characters */
	void simpleParser(Abacus abacus, String buffer) {
		int i;
		int numberCount = 0, decimalCount = 0;
		boolean digit = false, decimal = false, negate = false;
		int decimalPosition = abacus.getDecimalPosition();

		if (abacus.getBottomPiece() != 0)
			decimalPosition--;
		if (abacus.getDecimalPosition() >= abacus.getShiftPercent() &&
				abacus.getBottomPiecePercent() != 0)
			decimalPosition--;
		aString = new StringBuffer("");
		bString = new StringBuffer("");
		op = ' ';
		for (i = 0; i < buffer.length(); i++) {
			if (buffer.charAt(i) == 'q' ||
					buffer.charAt(i) == 'Q') {
				op = buffer.charAt(i);
				return;
			}
			if ((buffer.charAt(i) == '+' ||
					buffer.charAt(i) == '-') &&
					(numberCount == 0 || op != ' ') &&
					!digit) {
				if (buffer.charAt(i) == '-')
					negate = !negate;
			} else if (buffer.charAt(i) == '+' ||
					buffer.charAt(i) == '-' ||
					buffer.charAt(i) == '*' ||
					buffer.charAt(i) == '/' ||
					buffer.charAt(i) == 'v' ||
					buffer.charAt(i) == 'u') {
				if (op != ' ')
					return;
				op = buffer.charAt(i);
				numberCount++;
				decimalCount = 0;
				digit = false;
				decimal = false;
				negate = false;
			} else if (buffer.charAt(i) == '.' && !decimal) {
				decimal = true;
				if (numberCount == 0)
					aString.append('.');
				else
					bString.append('.');
			} else if (AbacusCalc.IS_DIGIT(buffer.charAt(i))) {
				if (!decimal || decimalCount < decimalPosition) {
					if (numberCount == 0) {
						if (!digit && negate)
							aString.append('-');
						aString.append(buffer.charAt(i));
					} else {
						if (!digit && negate)
							bString.append('-');
						bString.append(buffer.charAt(i));
					}
					negate = false;
				}
				if (decimal) {
					decimalCount++;
				}
				digit = true;
			}
		}
	}

	/* Idea taken from addition tables in "How to Learn Lee's Abacus", */
	/* made a little more generic, to handle other bases. */
	static void digitAdd(int a, int b, int carryIn,
			int base, int topFactor, int bottomNumber) {
		int newB = b + carryIn;
		int fractBase;
		int modB, modAns, ans;
		int modDivA, modDivAns, divAns;

		if (bottomNumber > base / 2) /* topFactor may not be set, Russian */
			fractBase = base;
		else
			fractBase = topFactor;
		modB = newB % fractBase;
		ans = a + newB;
		modAns = ans % fractBase;
		modDivA = (a % base) / fractBase;
		modDivAns = (ans % base) / fractBase;
		divAns = ans / base;
		if (modAns - modB >= 0)
			lower = modB;
		else
			lower = modB - fractBase;
		upper = modDivAns - modDivA;
		carry[state] = divAns;
		if (oldDebug)
			System.out.println("add:\t" + a + "\t" + b + "\t" +
				carryIn + "\t|\t" + lower + "\t" + upper +
				"\t" + carry[state]);
	}

	/* Idea taken from subtraction tables in "How to Learn Lee's Abacus, */
	/* made a little more generic, to handle other bases. */
	static void digitSubtract(int a, int b, int carryIn,
			int base, int topFactor, int bottomNumber) {
		int newB = b - carryIn;
		int fractBase;
		int modA, modB, ans;
		int divA, modDivAns, divAns;

		if (bottomNumber > base / 2) /* topFactor may not be set */
			fractBase = base;
		else
			fractBase = topFactor;
		modA = a % fractBase;
		modB = newB % fractBase;
		ans = a - newB;
		divA = a / fractBase;
		modDivAns = ((ans + base) % base) / fractBase;
		divAns = (ans - base + 1) / base;
		if (modA - modB >= 0)
			lower = -modB;
		else
			lower = fractBase - modB;
		upper = modDivAns - divA;
		carry[state] = divAns;
		if (oldDebug)
			System.out.println("sub:\t" + a + "\t" + b + "\t" +
				carryIn + "\t|\t" + lower + "\t" + upper +
				"\t" + carry[state]);
	}

/*
Guide:
2+1, 2+6, 2+4, 9+7, 36+75
10-3, 12-6, 100-58
67*2, 9*7, 26*14, 678*345
93/3, 1476/12, 638/22

Lee:
859*7, 54*23, 864*315, 26.5*3.4, .753*.04
4571/7, 7448/76, 72.61/2.74
625v, 1664.64v, 1004004v
12167u, 8615.125u, 8036.054027u
*/

	/* Unit Test */
	static void testTable() {
		int i, j, carryIn, k;
		int base = AbacusInterface.DEFAULT_BASE;
		int factor = 5;
		/*int base = 16;
		int factor = 4;*/
		int c = 1;
		/*int c = 2;*/

		for (k = 0 ; k < c; k++) {
			for (j = 0 ; j < base; j++) {
				for (i = 0 ; i < base; i++) {
					carryIn = k;
					/*digitAdd(i, j, carryIn, base,
						factor, factor);*/
					digitSubtract(i, j, carryIn, base,
						factor, factor);
					System.out.println(i + "\t" + j +
						"\t" + carryIn +
						"\t|\t" + lower + "\t" +
						upper + "\t" + carry[state]);
				}
			}
		}
	}

	/* Feedback lines to user */
	public void drawLineText(String text, int line) {
		((AbacusApplet)applet).setTeachMsg(text, line);
	}

	/* Position of decimal point in string */
	public static int getDecimalStringPosition(String string) {
		int i;

		for (i = 0; i < string.length(); i++) {
			if (string.charAt(i) == '.') {
				return i;
			}
		}
		return i;
	}

	/* Position of decimal point in int array */
	public static int getDecimalArrayPosition(int[] array) {
		int i;

		for (i = 0; i < array.length; i++) {
			if (array[i] == -1) {
				return i;
			}
		}
		return i;
	}

	/* Appends decimal where necessary for consistency */
	public static void decimalSafe(StringBuffer string) {
		int decimal = -1;

		decimal = getDecimalStringPosition(string.toString());
		if (decimal == string.length()) {
			string.append('.');
		}
	}

	/* Find decimal place offset given current position */
	static int decimalPlaceString(String string, int pos) {
		int i = getDecimalStringPosition(string);

		if (i == string.length() || i >= pos)
			i -= (pos + 1);
		else
			i -= pos;
		if (oldDebug)
			System.out.println("decimalPlaceString return " +
				i + ": " + string + ", position " + pos);
		return i;
	}

	/* Find decimal place offset given current position */
	static int decimalPlaceArray(int[] array, int pos) {
		int i = getDecimalArrayPosition(array);

		if (i == array.length || i >= pos)
			i -= (pos + 1);
		else
			i -= pos;
		return i;
	}

	/* Find position offset given current place */
	static int decimalOffsetString(String string, int place) {
		int i = getDecimalStringPosition(string);

		if (place >= 0)
			i -= (place + 1);
		else
			i -= place;
		return i;
	}

	/* Find decimal place offset given current position */
	static int decimalOffsetArray(int[] array, int place) {
		int i = getDecimalArrayPosition(array);

		if (place >= 0)
			i -= (place + 1);
		else
			i -= place;
		return i;
	}

	/* Contract StringBuffer to remove leading and trailing 0's */
	static void contractStringBuffer(StringBuffer string) {
		int offset = getDecimalStringPosition(string.toString());
		int i, length;

		for (i = 0; i < offset - 1; i++) {
			if (string.charAt(0) == '0')
				string.deleteCharAt(0);
			else
				break;
		}
		offset = getDecimalStringPosition(string.toString());
		length = string.length();
		if (offset < length)
			for (i = length - 1; i > 1; i--) {
				if (string.charAt(i) == '0')
					string.deleteCharAt(i);
				else
					break;
			}
		if (string.length() > 0 && string.charAt(0) == '.') /* normalize */
			string.insert(0, '0');
	}

	static void trimStringBuffer(StringBuffer string) {
		int last;

		contractStringBuffer(string);
		last = string.length() - 1;
		if (string.charAt(last) == '.')
			string.deleteCharAt(last);
	}

	static void stripDecimal(StringBuffer string) {
		int i, found = -1, length;

		for (i = 0; i < string.length(); i++) {
			if (string.charAt(i) == '.') {
				string.deleteCharAt(i);
				break;
			}
		}
		for (i = 0; i < string.length(); i++)
			if (string.charAt(i) != '0') {
				found = i;
				break;
			}
		if (found == -1) {
			string.setCharAt(0, '0');
			string.setLength(1);
			return;
		}
		if (found == 0)
			return;
		length = string.length();
		for (i = found; i < length; i++)
			string.setCharAt(i - found, string.charAt(i));
		string.setLength(length - found);
	}

	/* Strip StringBuffer of trailing len numbers */
	static void stripStringBuffer(StringBuffer string, int len, int steps) {
		int length, i;

		trimStringBuffer(string);
		stripDecimal(string);
		length = string.length();
		if (len >= 0) {
			if (length >= len)
				string.setLength(length - len);
		} else {
			/* if negative have to add back */
			if (length < steps) {
				for (i = length; i < steps; i++) {
					string.append("0");
				}
				string.setLength(i);
			}
		}
		if (oldDebug)
			System.out.println("stripStringBuffer: " + string +
				" " + string.length());
	}

	/* Expand StringBuffer to fit decimal place */
	static void expandStringBuffer(StringBuffer string, int place) {
		int offset = getDecimalStringPosition(string.toString());
		int prependOffset = place - offset + 1;
		int appendOffset = offset + 1 - string.length() - place;
		int i;

		decimalSafe(string);
		if (place >= 0) {
			for (i = 0; i < prependOffset; i++) {
				string.insert(0, '0');
			}
		} else {
			for (i = 0; i < appendOffset; i++) {
				string.append('0');
			}
		}
	}

	/* Shift StringBuffer to fit decimal place */
	static void shiftStringBuffer(StringBuffer string, int shift) {
		int offset = getDecimalStringPosition(string.toString());
		int newOffset, i;

		if (offset != string.length())
			string.deleteCharAt(offset);
		if (offset >= string.length()) {
			while (string.length() > 1 &&
					string.charAt(string.length() - 1) == '0') {
				string.deleteCharAt(string.length() - 1);
			}
		}
		if (offset == 1 && string.charAt(0) == '0') {
			while (string.length() > 1 &&
					string.charAt(0) == '0') {
				string.deleteCharAt(0);
				offset--;
			}
		}
		if (string.charAt(0) == '0' && string.length() == 1) {
			return;
		}
		newOffset = offset + shift;
		if (newOffset <= 0) {
			for (i = 0; i < -newOffset; i++) {
				string.insert(0, '0');
			}
			string.insert(0, "0.");
		} else if (newOffset >= string.length()) {
			newOffset -= string.length();
			for (i = 0; i < newOffset; i++) {
				string.append("0");
			}
		} else {
			string.insert(newOffset, '.');
		}
	}

	static void testShift(String string) {
		StringBuffer my = new StringBuffer(string);
		for (int i = -4; i < 5; i++) {
			StringBuffer yours = new StringBuffer(my);
			shiftStringBuffer(yours, i);
			System.out.println("number " + my + ", shift " + i +
				" " + yours);
		}
	}

	static void stringGroup(StringBuffer string, int[] integerGroup, char v) {
		for (int i = 0; i < integerGroup.length; i++) {
			if (integerGroup[i] == -1) {
				string.append(". ");
			} else {
				DecimalFormat formatter;

				if (v == 'v')
					formatter = new DecimalFormat("00");
				else /* u == 'u' */
					formatter = new DecimalFormat("000");
				string.append(formatter.format(integerGroup[i]) + " ");
			}
		}
	}

	/* Set string given change in rail (decimal position) */
	void setStringBuffer(Abacus abacus, StringBuffer string,
			int aux, int place, int lower, int upper) {
		int offset, digit;
		int topFactor = ((AbacusApplet)applet).getAbacus(aux).getTopFactor();
		int bottomNumber = ((AbacusApplet)applet).getAbacus(aux).getBottomNumber();

		if (bottomNumber <= abacus.getBase() / 2)
			digit = lower + upper * topFactor;
		else
			digit = lower;
		expandStringBuffer(string, place);
		offset = decimalOffsetString(string.toString(), place);
		string.setCharAt(offset,
			AbacusCalc.DIGIT_TO_CHAR((digit + abacus.getBase() +
			AbacusCalc.CHAR_TO_DIGIT(string.charAt(offset))) %
			abacus.getBase()));
	}

	/* Appends 0's where necessary to make adding easier
	/* (before and after decimal point) */
	public static void addSafe(StringBuffer aString, StringBuffer bString) {
		int aDecimal = -1, bDecimal = -1;
		int aCount, bCount, i;

		aDecimal = getDecimalStringPosition(aString.toString());
		bDecimal = getDecimalStringPosition(bString.toString());
		aCount = aString.length() - aDecimal;
		bCount = bString.length() - bDecimal;
		if (aCount > bCount) {
			for (i = 0; i < aCount - bCount; i++)
				bString.append('0');
		} else {
			for (i = 0; i < bCount - aCount; i++)
				aString.append('0');
		}
		if (aDecimal > bDecimal) {
			for (i = 0; i < aDecimal - bDecimal; i++)
				bString.insert(0, '0'); /* prepend */
		} else {
			for (i = 0; i < bDecimal - aDecimal; i++)
				aString.insert(0, '0'); /* prepend */
		}
	}

	/* Next rail according to step */
	int nextRail(int railStep, int aux, boolean rightToLeft,
			String string) {
		int n, rail;
		int count = string.length() - 1;
		int decimalPosition = string.length() - 1 -
			getDecimalStringPosition(string);
		int decimal = 0;
		Abacus abacus = ((AbacusApplet)applet).getAbacus(aux);
		int shiftPercent = abacus.getShiftPercent();
		boolean piece = abacus.checkPiece();
		boolean piecePercent = abacus.checkPiecePercent();
		boolean subdeck = abacus.checkSubdeck(3);

		n = railStep;
		if (rightToLeft) {
			if (carryStep != 0)
				n += (carryStep / 2);
			if (n >= decimalPosition -
					((piece) ? 1 : 0) -
					((piecePercent) ? 1 : 0) -
					((subdeck) ? 2 : 0)) {
				decimal++;
			}
			n += decimal;
			rail = n - decimalPosition;
			if (subdeck) {
				rail += 2;
			} else {
				if (rail >= -1 + ((piecePercent) ? 1 : 0) &&
						piece)
					rail++;
				if (rail >= -shiftPercent - 2 &&
						piecePercent)
					rail++;
				if (n >= decimalPosition) {
					rail--;
				}
			}
		} else {
			if (carryStep != 0)
				n += -(carryStep / 2);
			if (n >= count - decimalPosition +
					((piece) ? 1 : 0) +
					((piecePercent) ? 1 : 0) -
					((subdeck) ? 2 : 0)) {
				decimal++;
			}
			rail = count - n - decimalPosition - 1;
			n += decimal;
			if (subdeck) {
				rail += 3;
			} else {
				if (piece && n <= count - decimalPosition +
						((piecePercent) ? 1 : 0))
					rail++;
				if (n <= count - decimalPosition +
						shiftPercent + 2 &&
						piecePercent)
					rail++;
				if (n <= count - decimalPosition +
						shiftPercent + 2 &&
						piecePercent)
					rail++;
			}
		}
		if (oldDebug)
			System.out.println("nextRail: " + rail);
		return rail;
	}

	/* Next digit in string according to step */
	int nextCharPosition(int digitStep, int digitCarryStep, int aux,
			boolean rightToLeft, String string) {
		int n, a;
		int count = string.length() - 1;
		int decimal = 0;
		int decimalPosition = string.length() - 1 -
			getDecimalStringPosition(string);
		/* int shiftPercent = ((AbacusApplet)applet).getShiftPercent(aux); */
		Abacus abacus = ((AbacusApplet)applet).getAbacus(aux);
		boolean piece = abacus.checkPiece();
		boolean piecePercent = abacus.checkPiecePercent();
		boolean subdeck = abacus.checkSubdeck(3);

		if (string.length() == getDecimalStringPosition(string))
			count = string.length(); /* no decimal point */
		n = digitStep;
		if (rightToLeft) {
			if (digitCarryStep != 0)
				n += (digitCarryStep / 2);
			if (n >= decimalPosition -
					((piece) ? 1 : 0) -
					((piecePercent) ? 1 : 0) -
					((subdeck) ? 2 : 0)) {
				decimal++;
			}
			n += decimal;
			a = count - n;
		} else {
			if (digitCarryStep != 0)
				n += -(digitCarryStep / 2);
			if (n >= count - decimalPosition +
					((piece) ? 1 : 0) +
					((piecePercent) ? 1 : 0) -
					((subdeck) ? 2 : 0)) {
				decimal++;
			}
			n += decimal;
			a = n;
		}
		if (oldDebug)
			System.out.println("nextCharPosition: " + a +
				" at step " + digitStep + " in " + string +
				", n " + n + ", decimal " + decimal +
				", count " + count);
		return a;
	}

	/* Digit at position in string */
	static int nextChar(String string, int pos) {
		int digit;

		if (pos < 0 || pos >= string.length()) {
			digit = 0;
		} else {
			digit = AbacusCalc.CHAR_TO_DIGIT(string.charAt(pos));
		}
		if (oldDebug)
			System.out.println("nextChar: " + digit + " at " +
				pos + " in " + string);
		return digit;
	}

	static int placeGroup(int integerGroup[]) {
		int i = 0;

		for (i = 0; integerGroup[i] != -2; i++) {
			if (integerGroup[i] == -1) {
				return i;
			}
		}
		return i;
	}

	/* int value of string offset by place */
	static int andAbove(String string, int place, int base) {
		int i, value = 0, factor = 1, pos;
		int runover = decimalOffsetString(string, place) - string.length();
		int newPlace = place;

		if (runover > 0)
			newPlace += runover;
		for (i = 0; i < string.length(); i++) {
			pos = decimalOffsetString(string, newPlace + i);
			if (pos < 0)
				break;
			value += nextChar(string, pos) * factor;
			factor *= base;
		}
		for (i = 0; i < runover; i++) {
			value *= base;
		}
		return value;
	}

	/* number of places where int value of string offset by place */
	static int andAbovePlaces(String string, int place) {
		int i, j, pos = 0;
		int runover = decimalOffsetString(string, place) - string.length();
		int newPlace = place;
		boolean decimalValue = false;
		boolean intValue = false;

		if (runover > 0)
			newPlace += runover;
		for (i = 0; i < string.length(); i++) {
			pos = decimalOffsetString(string, newPlace + i);
			if (pos < 0) {
				break;
			}
		}
		/* Handle numbers < 1 */
		for (j = 0; j < string.length(); j++) {
			if (string.charAt(j) == '0') {
				if (decimalValue)
					i--;
			} else if (string.charAt(j) == '.') {
				i--;
				decimalValue = true;
			} else {
				if (!decimalValue)
					intValue = true;
				break;
			}
		}
		if (!intValue && i <= 0)
			i = 1;
		if (runover > 0)
			i += runover;
		if (oldDebug)
			System.out.println("andAbovePlaces: return " + i +
				" at place " + place + " in string " + string +
				", runover" + runover + ", pos " + pos +
				", intValue " + intValue);
		return i;
	}

	/*int digitInPlace(int value, int pos, int base) {
		int i;

		for (i = 0; i < pos; i++)
			value /= base;
		return value % base;
	}*/

	static int logInt(int number, int base) {
		int value = number;
		int count = 0;

		while (value >= base) {
			value /= base;
			count++;
		}
		return count;
	}

	static double expFloat(int place, int base) {
		double exp = 1.0;
		int i;

		for (i = 0; i < place; i++)
			exp /= base;
		return exp;
	}


	static int logFloat(double fract, int base) {
		double value = fract;
		int count = 0;

		if (fract <= 0)
			return 0; /* actually an error but do not care */
		if (fract > 1.0)
			while (value >= base) {
				value /= base;
				count++;
			}
		else if (fract < 1.0)
			while (value < 1.0) {
				value *= base;
				count--;
			}
		return count;
	}

	/* A little tricky as this is reentrant */
	/* Number of steps in addition and subtraction */
	static int addSteps(String string) {
		/* decimal included */
		return string.length() - 1;
	}

	/* Number of multiply steps in multiplication */
	static int multSteps(String aString, String bString) {
		/* decimal included */
		return (aString.length() - 1) * (bString.length() - 1);
	}

	/* Number of addition steps in multiplication */
	static int addMultSteps(String aString, String bString) {
		/* 2 digits per multiplication */
		return 2 * multSteps(aString, bString);
	}

	/* Finds out number of division steps */
	/* This can be better as answers with 0s have too many ops */
	static int divSteps(Abacus abacus, String aString, String bString,
			int decimalPosition) {
		AbacusCalc abacusCalc = new AbacusCalc(abacus);
		BigDecimal a, b, c;
		int count = 0;

		if (aString.length() == 0 || bString.length() == 0)
			return 0;
		a = abacusCalc.convertToDecimal(abacus.getBase(), aString);
		b = abacusCalc.convertToDecimal(abacus.getBase(), bString);
		if (b.compareTo(BigDecimal.ZERO) == 0)
			return 0;
		c = a.divide(b, decimalPosition, BigDecimal.ROUND_DOWN);
		if (c.compareTo(BigDecimal.ZERO) == 0) {
			return 0;
		}
		if (c.compareTo(BigDecimal.ONE) >= 0) {
			while (c.compareTo(BigDecimal.ONE) >= 0) {
				c = c.divide(new BigDecimal(abacus.getBase()));
				count++;
			}
		} else {
			while (c.compareTo(BigDecimal.ONE) < 0) {
				c = c.multiply(new BigDecimal(abacus.getBase()));
				count--;
			}
			count++;
		}
		return count;
	}

	/* Number of multiplying steps in division */
	static int multDivSteps(Abacus abacus, String aString, String bString) {
		/* decimal included */
		return divSteps(abacus, aString, bString,
			abacus.getDecimalPosition()) * (bString.length() - 1);
	}

	/* Number of subtraction steps for each multiplication in division */
	static int subMultDivSteps(Abacus abacus, String aString, String bString) {
		/* 2 digits per multiplication */
		return 2 * multDivSteps(abacus, aString, bString);
	}

	/* Finds head digits for division */
	static BigDecimal headDividend(Abacus abacus, String string, int len) {
		AbacusCalc abacusCalc = new AbacusCalc(abacus);

		if (string.length() == 0)
			return BigDecimal.ZERO;
		StringBuffer newString = new StringBuffer(string);
		shiftStringBuffer(newString, -len);
		return abacusCalc.convertToDecimal(abacus.getBase(), newString.toString());
	}

	/* Finds head digits for division */
	static BigDecimal headDivisor(Abacus abacus, String string) {
		AbacusCalc abacusCalc = new AbacusCalc(abacus);

		if (string.length() == 0)
			return BigDecimal.ZERO;
		return abacusCalc.convertToDecimal(abacus.getBase(), string);
	}

	/* Divide string into groups, size of group depends on root */
	static int[] rootGroup(Abacus abacus, String string, int root) {
		int decimal = getDecimalStringPosition(string);
		int length = string.length();
		int nIntegral = decimal;
		int nDecimal = length - decimal - 1;
		int i, j, k, b, n;

		if (nIntegral == 1 && string.charAt(0) == '0')
			nIntegral = 0;
		nIntegral = (nIntegral + root - 1) / root;
		nDecimal = (nDecimal + root - 1) / root;
		n = nIntegral + nDecimal + 1;
		int[] integerGroup = new int[n];

		for (i = 0; i < nIntegral; i++) {
			k = i * root + (decimal + root - 1) % root;
			integerGroup[i] = 0;
			b = 1;
			for (j = 0; j < root; j++) {
				if (k - j >= 0)
					integerGroup[i] += b *
			AbacusCalc.CHAR_TO_DIGIT(string.charAt(k - j));
				else
					break;
				b *= abacus.getBase();
			}
		}
		integerGroup[nIntegral] = -1;
		for (i = 0; i < nDecimal; i++) {
			k = i * root + 1 + decimal;
			integerGroup[i + nIntegral + 1] = 0;
			b = 1;
			for (j = 0; j < root; j++) {
				if (k + root - 1 - j < length)
					integerGroup[i + nIntegral + 1] += b *
			AbacusCalc.CHAR_TO_DIGIT(string.charAt(k + root - 1 - j));
				b *= abacus.getBase();
			}
		}
		return integerGroup;
	}

	/* Implement an iteration of Newton's Method or Halley's Method */
	public static double rootAdvance(double x, double p, int n) {
		int i = 0;
		double y = x;

		if (n < 1)
			return 0;
		if (n == 1)
			return x;
		/* Halley's Method, converges faster */
		if (n == 2) {
			y = x * x;
			if (debug)
				System.out.println("p " + p + ", y " + y + ", n " + n + ", x " + x);
			return x * (y + p * 3) / (y * 3 + p);
		}
		if (n == 3) {
			y = x * x * x;
			if (debug)
				System.out.println("p " + p + ", y " + y + ", n " + n + ", x " + x);
			return x * (y + p * 2) / (y * 2 + p);
		}
		/* Newton's Method */
		for (i = 0; i < n - 2; i++)
			y *= x;
		if (debug)
			System.out.println("p " + p + ", y " + y + ", n " + n + ", x " + x);
		return (p / y + x * (n - 1)) / n;
	}

	/* Calculate root, loop a number of times and try for certain approximation */
	public static double rootApprox(double guess, double power, int n) {
		int i;
		double xi = guess;
		final double EPSILON = 0.0000001;
		final int iterations = 8;

		for (i = 0; i < iterations; i++) {
			double xn = rootAdvance(xi, power, n);
			double diff = xn - xi;

			if (debug)
				System.out.println(i + ": " + xn + " " + xi + " " + diff);
			if (diff < 0.0)
				diff = -diff;
			if (diff < EPSILON)
				break;
			xi = xn;
		}
		return xi;
	}

	/* This calculates first digit in a root */
	void testRootKojima(Abacus abacus, double guess, int position, int root) {
		AbacusCalc abacusCalc = new AbacusCalc(abacus);
		double c = abacusCalc.convertToDecimal(abacus.getBase(),
			cString.toString()).doubleValue();
		int aPlace = position;
		double start = guess;

		while (aPlace > 1) {
			start *= abacus.getBase();
			aPlace--;
		}
		while (aPlace < 0 && start != 0.0) {
			start = abacus.getBase() / start;
			aPlace++;
		}
		c = rootApprox(start, c, root);
		if (debug) {
			System.out.println("incr root = " + c);
		}
	}

	/* Figure out next intGroup value */
	static int intGroupValue(int position) {
		int i, newPosition = position;

		if (position < 0)
			return 0;
		if (position >= intGroup.length)
			return 0;
		for (i = 0; i <= position; i++) {
			if (intGroup[i] == -1) { // only one decimal point
				newPosition = position + 1;
				break;
			}
		}
		if (newPosition >= intGroup.length)
			return 0;
		return intGroup[newPosition];
	}

	/* This will test Lee's method for calculating cbrt via Newton's Method */
	static int testRootAdvance(int power, int root) {
		int digitAdd = 1;
		int i = 0;
		int doubleOrder = 0;

		if (debug) {
			System.out.println("remainder power = " + power);
		}
		while (groupSum + ((root == 2) ? groupInc : orderInc) <= power) {
			if (root == 2) {
				groupInc += digitAdd;
			} else {
				orderInc += digitAdd;
				if (i == 0)
					doubleOrder = digitAdd;
				else
					doubleOrder += orderInc;
				groupInc += doubleOrder;
			}
			if (debug) {
				System.out.print((i + 1) + ": left A-0 field " +
					digitAdd);
				if (root == 3)
					System.out.print(", right A-0 field " +
						orderInc);
				System.out.println(", P-0 field " + groupInc);
			}
			i++;
			digitSum += digitAdd;
			groupSum += groupInc;
			if (root == 3) {
				orderSum += orderInc;
				digitAdd = 1;
				orderInc += digitAdd;
				doubleOrder = orderInc;
				if (debug) {
					System.out.println((i + 1) +
						": left A-0 field " + digitAdd +
						", right A-0 field " + orderInc);
				}
				i++;
				digitSum += digitAdd;
				orderSum += orderInc;
			}
			digitAdd = 2;
		}
		if (debug) {
			System.out.print("sum " + i + " : " + digitSum);
			if (root == 3)
				System.out.print(" : " + orderInc + " " + orderSum);
			System.out.println(" : " + groupInc + " " + groupSum);
		}
		return i;
	}

	/* This will test Lee's method for calculating roots via Newton's Method */
	static void testRootLee(Abacus abacus, int position, int root) {
		int i, power = 0, j;
		int basePow = abacus.getBase() * abacus.getBase() * ((root == 2) ? 1 : abacus.getBase());
		int magnitude = abacus.getBase();
		int allDigitSum = 0, groupVal = 0;
		double result = 0.0;

		digitSum = 0;
		groupInc = 0;
		groupSum = 0;
		for (i = 0; i <= position + abacus.getDecimalPosition(); i++) {
			groupVal = intGroupValue(i);
			power = (power - groupSum) * basePow + groupVal;
			digitSum = 0;
			groupSum = 0;
			if (debug) {
				System.out.println("left A-0 digit " + (i + 1) +
					": inc power = " + power +
					" at position " + position +
					", intGroup " + groupVal);
			}
			digitSum *= abacus.getBase();
			if (root == 3)
				orderSum *= abacus.getBase();
			groupSum *= basePow;
			j = testRootAdvance(power, root);
			if (j == 0) {
				digitSum = 0;
				if (root == 2)
					groupInc = groupInc * abacus.getBase();
				else
					orderInc = orderInc * abacus.getBase();
			} else {
				digitSum++;
				if (root == 2)
					groupInc = (groupInc + 1) * abacus.getBase();
				else
					orderInc = (orderInc + 1) * abacus.getBase();
			}
			magnitude *= abacus.getBase();
			groupInc = orderSum * magnitude + orderInc;
			if (debug) {
				System.out.print("left A-0 digit " + (i + 1) +
					": digitSum = " + digitSum);
				System.out.print(", orderSum = " + orderSum);
				System.out.println(", groupSum = " + groupSum +
					", power = " + power + ", j = " + j);
			}
			allDigitSum = allDigitSum * abacus.getBase() + digitSum;
		}
		allDigitSum--;
		result = (double) (allDigitSum + root - 1) / root;
		if (debug) {
			System.out.println("allDigitSum " + allDigitSum +
				", result = " + result);
		}
		for (i = 0; i < abacus.getDecimalPosition(); i++) {
			result /= abacus.getBase();
		}
		if (debug) {
			System.out.println("result = " + result);
		}
	}

	void highlightRail(int aux, int rail, boolean highlight) {
		Abacus abacus = ((AbacusApplet)applet).getAbacus(aux);

		if (!test) {
			if (rail + abacus.getDecimalPosition() < 0)
				System.out.println("Error: drawing rail " + rail);
			else
				abacus.drawBeadRail(rail + abacus.getDecimalPosition(), highlight);
		}
	}

	void highlightRails(int aux) {
		Abacus abacus = ((AbacusApplet)applet).getAbacus(aux);
		int rail;
		final int delay = 10;

		if (!test) {
			for (rail = -abacus.getDecimalPosition(); rail < abacus.getRails() - abacus.getDecimalPosition(); rail++) {
				highlightRail(aux, rail, true);
				try {
					Thread.sleep(delay);
				} catch (Exception e) {
					//e.printStackTrace();
				}
				highlightRail(aux, rail, false);
			}
			try {
				Thread.sleep(delay);
			} catch (Exception e) {
				//e.printStackTrace();
			}
			for (rail = abacus.getRails() - abacus.getDecimalPosition() - 1; rail >= -abacus.getDecimalPosition(); rail--) {
				highlightRail(aux, rail, true);
				try {
					Thread.sleep(delay);
				} catch (Exception e) {
					//e.printStackTrace();
				}
				highlightRail(aux, rail, false);
			}
		}
	}

	/* Tell about what is going to happen */
	boolean pendingUpdate(int line, int aux, int position,
			int base, int bottomNumber) {
		boolean done;
		StringBuffer buffer = new StringBuffer("For rail " + position);

		if (lower == 0 && upper == 0 && carry[state] == 0) {
			buffer.append(", do nothing");
			step++; /* or else two do nothings */
			done = true;
		} else {
			highlightRail(aux, position, true);
			if (lower != 0) {
				if (lower < 0)
					buffer.append(", take off " + (-lower));
				else
					buffer.append(", put on " + (lower));
				if (bottomNumber <= base / 2) {
					if (lower < 0)
						buffer.append(" on lower deck");
					else
						buffer.append(" from lower deck");
				}
			}
			if (upper != 0) {
				if (upper < 0)
					buffer.append(", take off " +
						(-upper) + " on upper deck");
				else
					buffer.append(", put on " +
						upper + " from upper deck");
			}
			if (carry[state] != 0) {
				if (carry[state] > 0) {
					buffer.append(", carry " +
						carry[state] + " (on next move)");
				} else {
					buffer.append(", borrow " +
						(-carry[state]) + " (on next move)");
				}
			}
			done = false;
		}
		buffer.append(".");
		drawLineText(buffer.toString(), line);
		return done;
	}

	/* Handle addition and subtraction one step at a time */
	boolean nextPositionSum(Abacus abacus, char operation) {
		int n = (step - 2) / 2; /* 2 step display */
		int max = addSteps(aString.toString()); /* number of steps with original */
		int topFactor = abacus.getTopFactor();
		int bottomNumber = abacus.getBottomNumber();
		boolean rightToLeft = abacus.getRightToLeftAdd();
		int rPos, bPos, place, rDigit;

		/* rString can expand with carries. */
		/* bString does not change, so bPos will be predictable. */
		bPos = nextCharPosition(n, carryStep, 0,
			rightToLeft, bString.toString());
		place = decimalPlaceString(bString.toString(), bPos);
		rPos = decimalOffsetString(rString.toString(), place);
		rDigit = nextChar(rString.toString(), rPos);
		if (carryStep == 0)
			bDigit = nextChar(bString.toString(), bPos);
		else
			bDigit = 1;
		if (!rightToLeft)
			carry[state] = 0;
		if (operation == '+')
			digitAdd(rDigit, bDigit, carry[state],
				abacus.getBase(), topFactor, bottomNumber);
		else /* operation == '-' */
			digitSubtract(rDigit, bDigit, carry[state],
				abacus.getBase(), topFactor, bottomNumber);
		rPosition = nextRail(n, 0, rightToLeft, bString.toString());
		aDigit = rDigit;
		return (n >= max - 1);
	}

	/* Handle multiplication one step at a time */
	boolean nextPositionProduct(Abacus abacus) {
		int n = (step - 2) / 2; /* 2 step display */
		int max = addMultSteps(aString.toString(), bString.toString());
		int topFactor = abacus.getTopFactor();
		int bottomNumber = abacus.getBottomNumber();
		int bCount = bString.length() - 1;
		boolean rightToLeft = abacus.getRightToLeftAdd();
		int aOffset, bOffset, rOffset;
		int aPlace, bPlace, rPlace, rDigit;

		aOffset = nextCharPosition(
			(n / 2) / bCount, /* 2 digits result for each multiplication */
			0, /* not place for carry */
			1, /* aux 1 */
			true, /* "a" side always starts on right */
			aString.toString());
		aPlace = decimalPlaceString(aString.toString(), aOffset);
		aDigit = nextChar(aString.toString(), aOffset);
		bOffset = nextCharPosition(
			(n / 2) % bCount, /* 2 digits result for each multiplication */
			0, /* not place for carry */
			2, /* aux 2 */
			abacus.getRightToLeftMult(), /* this can vary */
			bString.toString());
		bPlace = decimalPlaceString(bString.toString(), bOffset);
		bDigit = nextChar(bString.toString(), bOffset);
		rPlace = ((((n & 1) == 0) && !rightToLeft) ||
			(((n & 1) == 1) && rightToLeft)) ? 1 : 0;
		if (carryStep != 0) {
			rPlace += carryStep / 2;
			bDigit = 1;
		}
		rPlace += aPlace + bPlace;
		expandStringBuffer(rString, rPlace);
		rOffset = decimalOffsetString(rString.toString(), rPlace);
		rDigit = nextChar(rString.toString(), rOffset);
		if (carryStep == 0) {
			/* 2 digits * 2 step display */
			if ((((n & 1) == 0) && rightToLeft) ||
					(((n & 1) == 1) && !rightToLeft)) {
				bValue = (aDigit * bDigit) % abacus.getBase();
			} else {
				bValue = (aDigit * bDigit) / abacus.getBase();
			}
		} else {
			aDigit = 0;
			bDigit = 0;
			bValue = 1;
		}
		if (!rightToLeft)
			carry[state] = 0;
		digitAdd(rDigit, bValue, carry[state],
			abacus.getBase(), topFactor, bottomNumber);
		rPosition = rPlace;
		if (carry[state] != 0 && ((n & 1) == 1) && rightToLeft)
			return true; /* Do not want to forget carry */
		return (n >= max - 1);
	}

	/* Handle division one step at a time */
	boolean nextPositionDivision(Abacus abacus) {
		int n = (step - 2) / 2; /* 2 step display */
		int max = subMultDivSteps(abacus, aString.toString(),
			bString.toString());
		int topFactor = abacus.getTopFactor();
		int bottomNumber = abacus.getBottomNumber();
		int bCount = bString.length() - 1;
		boolean rightToLeft = abacus.getRightToLeftAdd();
		int bOffset, rOffset;
		int aPlace, bPlace, rPlace, rDigit;
		BigDecimal dividend;

		if (n / (2 * bCount) == regCount / 2) {
			reg = regCount;
		} else {
			reg = -1;
		}
		if (reg >= 0) {
			if (reg > 1)
				cString = new StringBuffer(rString);
			regCount++;
			aPlace = divSteps(abacus, cString.toString(),
				bString.toString(),
				((AbacusApplet)applet).getAbacus(2).getDecimalPosition()) - 1;
			if (debug) {
				int aOffset = decimalPlaceString(cString.toString(), aPlace);
				System.out.println("aPlace = " + aPlace +
					", aOffset = " + aOffset);
			}
			dividend = headDividend(abacus, cString.toString(),
				aPlace);
			divisor = headDivisor(abacus, bString.toString());
			if (debug)
				System.out.println("dividend = " + dividend +
					", divisor = " + divisor);
			if (divisor.compareTo(BigDecimal.ZERO) == 0)
				qDigit = 0;
			else
				qDigit = dividend.divide(divisor,
					0, BigDecimal.ROUND_DOWN).intValue();
			if (debug)
				System.out.println("cString = " + cString +
					", qDigit = " + qDigit);
			qPosition = aPlace;
			digitAdd(0, qDigit, carry[state], abacus.getBase(),
				((AbacusApplet)applet).getAbacus(2).getTopFactor(),
				((AbacusApplet)applet).getAbacus(2).getBottomNumber());
			return (n >= 2 * max + 2 * abacus.getDecimalPosition() - 1);
		}
		bOffset = nextCharPosition(
			(n / 2) % bCount, /* 2 digits result for each multiplication */
			0, /* not place for carry */
			1, /* aux 1 */
			abacus.getRightToLeftMult(), /* this can vary */
			bString.toString());
		bPlace = decimalPlaceString(bString.toString(), bOffset);
		bDigit = nextChar(bString.toString(), bOffset);
		rPlace = ((((n & 1) == 0) && !rightToLeft) ||
			(((n & 1) == 1) && rightToLeft)) ? 1 : 0;
		if (carryStep != 0) {
			rPlace += carryStep / 2;
			bDigit = 1;
		}
		rPlace += qPosition + bPlace;
		expandStringBuffer(rString, rPlace);
		rOffset = decimalOffsetString(rString.toString(), rPlace);
		rDigit = nextChar(rString.toString(), rOffset);
		if (oldDebug) {
			System.out.println("bOffset " + bOffset +
				", bPlace " + bPlace +
				", bDigit " + bDigit);
		}

		if (carryStep == 0) {
			/* 2 digits * 2 step display */
			if ((((n & 1) == 0) && rightToLeft) ||
					(((n & 1) == 1) && !rightToLeft)) {
				bValue = (qDigit * bDigit) % abacus.getBase();
			} else {
				bValue = (qDigit * bDigit) / abacus.getBase();
			}
		} else {
			aDigit = 0;
			bDigit = 0;
			bValue = 1;
		}
		if (!rightToLeft)
			carry[state] = 0;
		digitSubtract(rDigit, bValue, carry[state],
			abacus.getBase(), topFactor, bottomNumber);
		rPosition = rPlace;
		return (n >= 2 * max + 2 * abacus.getDecimalPosition() - 1);
	}

	/* Handle root one step at a time, Kojima */
	boolean nextPositionRoot(Abacus abacus, int root) {
		int n = (step - 2) / 2; /* 2 step display */
		int max = subMultDivSteps(abacus, aString.toString(),
			bString.toString());
		int aOffset, aPlace;
		int power;

		if (reg >= 0) {
			if (reg > 1)
				cString = new StringBuffer(rString);
			regCount++;
			aOffset = 0;
			if (intGroup.length > 1 && intGroup[aOffset] == -1)
				aOffset++;
			aPlace = decimalPlaceArray(intGroup, aOffset);
			if (debug)
				System.out.println("aPlace = " + aPlace +
					", aOffset = " + aOffset);
			if (intGroup[aOffset] == -1)
				power = 0;
			else
				power = intGroup[aOffset];
			qDigit = AbacusMath.rootInt(power, root);
			if (debug) {
				System.out.println("power = " + power);
				System.out.println("cString = " + cString +
					", qDigit = " + qDigit);
			}
			qPosition = aPlace;
			if (debug) {
				testRootKojima(abacus, qDigit, aPlace, root);
				testRootLee(abacus, aPlace, root);
			}
			digitAdd(0, qDigit, carry[state], abacus.getBase(),
				((AbacusApplet)applet).getAbacus(1).getTopFactor(),
				((AbacusApplet)applet).getAbacus(1).getBottomNumber());
			return (n >= 2 * max + 2 * abacus.getDecimalPosition() - 1);
		}
		/* Only figures out first digit so far */
		/* May want to fix with (a+b)(a+b) = a^2+2ab+b^2 for 2 digits */
		/* and (a+b+c)(a+b+c) = a^2+2ab+b^2+2bc+c^2+2ac for 3 digits */
		/* see Advanced Abacus by Takashi Kojima */
		lower = 0;
		upper = 0;
		return true;
	}

	/* Handle Left Auxiliary Operations Field table one step at a time */
	void nextPositionLAOField(Abacus abacus, int count) {
		int topFactor = ((AbacusApplet)applet).getAbacus(1).getTopFactor();
		int bottomNumber = ((AbacusApplet)applet).getAbacus(1).getBottomNumber();
		int root = (op == 'v') ? 2 : 3;
		int oFPos, oFDigit;

		rPosition = regCount;
		if (carryStep != 0)
			rPosition += carryStep / 2;
		oFPos = decimalOffsetString(bString.toString(), rPosition);
		oFDigit = nextChar(bString.toString(), oFPos);
		if (carryStep == 0) {
			if (root == 2)
				bDigit = 1 + ((count > 0) ? 1 : 0);
			else
				bDigit = 1 + ((count > 0 &&
					((count &1) == 0)) ? 1 : 0);
		} else {
			bDigit = 1;
		}
		carry[state] = 0;
		digitAdd(oFDigit, bDigit, carry[state],
			abacus.getBase(), topFactor, bottomNumber);
		if (debug)
			System.out.println("LAOF: bs " + bString +
				", bDigit " + bDigit +
				", oFPos " + oFPos +
				", oFDigit " + oFDigit +
				", rPosition " + rPosition +
				", carryStep " + carryStep);
	}

	/* Handle Right Auxiliary Operations Field table one step at a time */
	/* Only used for cube root */
	void nextPositionRAOField(Abacus abacus, int count, int steps) {
		int topFactor = ((AbacusApplet)applet).getAbacus(1).getTopFactor();
		int bottomNumber = ((AbacusApplet)applet).getAbacus(1).getBottomNumber();
		boolean rightToLeft = abacus.getRightToLeftAdd(); /* only adding on 1 digit */
		int oFPos, oFDigit;
		int charPos, place;
		StringBuffer buffer;

		/* buffer is bString or shorter */
		buffer = new StringBuffer(bString);
		stripStringBuffer(buffer, regCount, steps);
		rPosition = 2 * regCount;
/*System.out.printf("BEFORE rPosition %d, regCount %d, buffer %s, bString %s, %d %d\n", rPosition, regCount, buffer, bString, buffer.length(), bString.length());*/
		charPos = nextCharPosition(count, carryStep, 0,
			rightToLeft, buffer.toString());
		place = decimalPlaceString(buffer.toString(), charPos);
		rPosition += place;
/*System.out.printf("AFTER rPostition %d, place %d, string %s, charPos%d\n", rPosition, place, buffer, charPos);*/
		oFPos = decimalOffsetString(cString.toString(), rPosition);
		oFDigit = nextChar(cString.toString(), oFPos);
		if (carryStep == 0) {
			bDigit = nextChar(buffer.toString(), charPos);
		} else {
			bDigit = 1;
		}
		if (!rightToLeft)
			carry[state] = 0;
		digitAdd(oFDigit, bDigit, carry[state],
			abacus.getBase(), topFactor, bottomNumber);
		if (debug)
			System.out.println("RAOF: buffer " + buffer +
				", bs " + bString +
				", cs " + cString +
				", bDigit " + bDigit +
				", oFPos " + oFPos +
				", oFDigit " + oFDigit +
				", rPosition " + rPosition +
				", carryStep " + carryStep +
				", place " + place +
				", charPos " + charPos +
				", regCount " + regCount +
				", count " + count);
	}

	/* Handle Primary Operations Field table one step at a time */
	void nextPositionPOField(Abacus abacus, int count) {
		int topFactor = abacus.getTopFactor();
		int bottomNumber = abacus.getBottomNumber();
		boolean rightToLeft = abacus.getRightToLeftAdd();
		int oFPos, oFDigit;
		int root = (op == 'v') ? 2 : 3;
		int charPos, place;
		StringBuffer buffer;

		/* buffer is string or shorter */
		if (root == 2)
			buffer = new StringBuffer(bString);
		else
			buffer = new StringBuffer(cString);
		stripStringBuffer(buffer, (root - 1) * regCount, 0);
		rPosition = root * regCount;
		charPos = nextCharPosition(count, carryStep, 0,
			rightToLeft, buffer.toString());
		place = decimalPlaceString(buffer.toString(), charPos);
		rPosition += place;
		/* rString can expand with carries. */
		oFPos = decimalOffsetString(rString.toString(), rPosition);
		oFDigit = nextChar(rString.toString(), oFPos);
		if (carryStep == 0) {
			bDigit = nextChar(buffer.toString(), charPos);
		} else {
			bDigit = 1;
		}
		if (!rightToLeft)
			carry[state] = 0;
		digitSubtract(oFDigit, bDigit, carry[state],
			abacus.getBase(), topFactor, bottomNumber);
		if (debug)
			System.out.println("POF: rs " + rString +
				", buffer " + buffer +
				", oFPos " + oFPos +
				", oFDigit " + oFDigit +
				", rPosition " + rPosition +
				", bValue " + bValue +
				", bDigit " + bDigit +
				", count " + count +
				", place " + place +
				", carryStep " + carryStep +
				", reg " + reg +
				", regCount " + regCount +
				", charPos " + charPos);
	}

	/* A little tricky as this is reentrant */
	void teachStepRoot(Abacus abacus) {
		int rValue, cValue = 0;
		int rStep = step - 2;
		int bSteps = 0, nSteps; /* number of steps in interation */
		int root = (op == 'v') ? 2 : 3;
		boolean rightToLeft = abacus.getRightToLeftAdd();
		AbacusCalc abacusCalc = new AbacusCalc(abacus);
		BigDecimal r = abacusCalc.convertToDecimal(abacus.getBase(), rString.toString());
		BigDecimal a = abacusCalc.convertToDecimal(abacus.getBase(), aString.toString());
		int futureOffset = 1;
		int decimalPosition = abacus.getDecimalPosition();
		int decimalPosition1 =
			((AbacusApplet)applet).getAbacus(1).getDecimalPosition();
		int decimalPosition2 =
			((AbacusApplet)applet).getAbacus(2).getDecimalPosition();
		int aux;

		if (reg == -1) {
			regCount = placeGroup(intGroup) - 1;
			reg = 0;
			primarySteps = 2 * andAbovePlaces(rString.toString(),
				root * regCount);
		}
		rValue = andAbove(rString.toString(), root * regCount,
			abacus.getBase());
		bValue = andAbove(bString.toString(), regCount,
			abacus.getBase());
		nSteps = 4 + primarySteps;
		if (root == 3) {
			cValue = andAbove(cString.toString(), 2 * regCount,
				abacus.getBase());
			bSteps = 2 * andAbovePlaces(bString.toString(),
				regCount);
		}
		if (rStep / nSteps == 0)
			futureOffset = 0;
		if (debug)
			System.out.println("teachStepRoot: rString" + rString +
				", bString" + bString +
				", cString" + cString +
				", rValue" + rValue +
				", bValue" + bValue +
				", cValue" + cValue +
				", reg" + reg +
				", regCount" + regCount +
				", auxCount" + auxCount +
				", carryStep" + carryStep +
				", rStep" + rStep +
				", bSteps" + bSteps +
				", nSteps" + nSteps +
				", r " + r +
				", a " + a);
		if (rStep % nSteps == 0) {
			if ((r.compareTo(BigDecimal.ZERO) == 0 &&
					r.compareTo(a) < 0) ||
					((root == 2) && bValue + futureOffset >= rValue) ||
					((root == 3) && cValue + (2 * bValue + 3) * futureOffset >= rValue &&
					(((rStep / nSteps) % 2) != 0) &&
					(auxCount >= bSteps || auxCount == 0)) &&
					reg < 1) {
					reg = 1; /* need for carry as bValue changes */
			}
			if (reg >= 1) {
				if (futureOffset == 0) {
					/* Have not started with this position and
					   nothing here */
					step = 2;
					regCount--;
					reg = 0;
					primarySteps = 2 * andAbovePlaces(rString.toString(),
						root * regCount);
					if ((root == 3 && regCount < -decimalPosition2 / (root - 1)) ||
							regCount < -decimalPosition1 ||
							regCount < -decimalPosition / root) {
						step = 0;
						drawLineText("Answer (divide by " + root + "): " +
							bString, 2);
						drawLineText("Done", 1);
						highlightRails(1);
					} else {
						drawLineText("Now try smaller value", 1);
					}
				} else {
					drawLineText("Almost done with position, need to add 1 to place working on", 1);
					step += 2;
				}
			} else {
				drawLineText(((root == 2) ? "Left" : "Right") +
					" A-O field value " +
					((root == 2) ? bValue : cValue) +
					((futureOffset != 0) ? " + 1" : "") +
					" < P-O field group value " +
					rValue, 1);
				step++;
			}
		} else if (rStep % nSteps == 1) {
			if (((root == 2) ? bValue : cValue) + futureOffset < rValue) {
				drawLineText("Yes, ok to iterate", 1);
				step++;
			} else {
				if (rValue == 0) {
					step = 0;
					drawLineText("Answer (divide by " + root + "): " +
						bString, 2);
					drawLineText("Done", 1);
					highlightRails(1);
				} else {
					drawLineText("Try smaller value", 1);
					step = 2;
					reg = 0;
					regCount--;
					primarySteps = 2 * andAbovePlaces(rString.toString(),
						root * regCount);
				}
			}
			carryStep = 0;
			carry[0] = 0;
			carry[1] = 0;
		} else if (rStep % nSteps == 2 ||
				(rStep % nSteps == 3 &&
				carryStep != 0 &&
				carryStep % 2 == 0)) {
			aux = 1;
			state = 0;
			nextPositionLAOField(abacus,
				((reg >= 1) ? root - 2 : rStep / nSteps));
			highlightRail(aux, rPosition, true);
			if (carryStep != 0)
				drawLineText("Carrying, add " + bDigit +
					" to left A-O field, offset digit at position " + rPosition, 1); /* should only need carry once */
			else
				drawLineText("Add " + bDigit +
					" to left A-O field, offset digit by corresponding group (or position) " + rPosition +
					((carry[state] != 0) ? ", with carry" : ""), 1);
			if (carryStep == 0) {
				step++;
			} else {
				carryStep++;
			}
		} else if (rStep % nSteps == 3) {
			aux = 1;
			carry[1] = carry[0];
			state = 1;
			nextPositionLAOField(abacus,
				((reg >= 1) ? root - 2 : rStep / nSteps));
			setStringBuffer(abacus, bString, aux,
				rPosition, lower, upper);
			if (lower != 0)
				abacus.abacusMove(aux, 0, rPosition, lower);
			if (upper != 0)
				abacus.abacusMove(aux, 1, rPosition, upper);
			highlightRail(aux, rPosition, false);
			if (carry[state] == 0 && carryStep != 0) {
				carryStep = 0;
			}
			contractStringBuffer(bString);
			if (reg == root - 1) {
				if (carry[state] != 0) {
					if (carryStep == 0) {
						carryStep = 2;
					} else {
						carryStep++;
					}
					drawLineText("Current answer (divide by " + root + "): " + bString, 2);
				} else {
					step = 2;
					reg = 0;
					regCount--;
					primarySteps = 2 * andAbovePlaces(rString.toString(),
						root * regCount);
					if ((root == 3 && regCount < -decimalPosition2 / (root - 1)) ||
							regCount < -decimalPosition1 ||
							regCount < -decimalPosition / root) {
						step = 0;
						drawLineText("Answer (divide by " + root + "): " + bString, 2);
						highlightRails(1);
					} else {
						drawLineText("Current answer (divide by " + root + "): " + bString, 2);
					}
				}
			} else {
				drawLineText("Current answer (divide by " + root + "): " + bString, 2);
				if (carry[state] != 0) {
					if (carryStep == 0) {
						carryStep = 2;
						if (!rightToLeft)
							carry[1] = carry[0] = 0;
					} else {
						carryStep++;
					}
				} else {
					carryStep = 0;
				}
				if (carryStep == 0) {
					step++;
					if (root == 3)
						auxCount = 0;
				}
			}
		} else if (root == 3 && auxCount < bSteps &&
				(auxCount % 2 == 0 ||
				(carryStep != 0 && carryStep % 2 == 0))) {
			aux = 2;
			state = 0;
			nextPositionRAOField(abacus,
				auxCount / 2, bSteps / 2);
			highlightRail(aux, rPosition, true);
			if (carryStep != 0) {
				drawLineText("Carrying in right A-O field", 1);
			} else {
				if (bDigit != 0 && bDigit != bValue)
					drawLineText("Add " + bDigit +
						" (part of " + bValue +
						") to right A-O field, offset digit by corresponding position " + rPosition +
						" (in group " + regCount +
						")" + ((carry[state] != 0) ? ", with carry" : ""), 1);
				else
					drawLineText("Subtract " + bDigit +
						" from primary field, offset digit by corresponding position " + rPosition +
						" (in group " + regCount +
						")" + ((carry[state] != 0) ? ", with borrow" : ""), 1);
			}
			if (carryStep == 0) {
				auxCount++;
			} else {
				carryStep++;
			}
		} else if (root == 3 && auxCount < bSteps) {
			aux = 2;
			if (!rightToLeft)
				carry[1] = carry[0];
			state = 1;
			nextPositionRAOField(abacus,
				auxCount / 2, bSteps / 2);
			setStringBuffer(abacus, cString, aux,
				rPosition, lower, upper);
			if (lower != 0)
				abacus.abacusMove(aux, 0, rPosition, lower);
			if (upper != 0)
				abacus.abacusMove(aux, 1, rPosition, upper);
			highlightRail(aux, rPosition, false);
			if (carry[state] == 0 && carryStep != 0) {
				carryStep = 0;
			}
			if (auxCount % bSteps != bSteps - 1 &&
					carry[state] == 0 &&
					logInt(cValue, abacus.getBase()) <= auxCount / 2) {
				auxCount++;
			} else {
				if (carry[state] != 0) {
					if (carryStep == 0) {
						carryStep = 2;
					} else {
						carryStep++;
					}
				}
				if (abacus.getRightToLeftAdd() &&
						carry[state] != 0) {
					carry[1] = carry[0] = 0;
				}
				if (carryStep == 0) {
					auxCount++;
					if (auxCount == bSteps) {
						if (reg >= 1) {
							auxCount = 0;
							step = (step / nSteps + 1) * nSteps + 4;
							reg = 2;
						} else if ((rStep / nSteps) % 2 != 0) {
							/* want skip primary if odd */
							step = ((step - 2) / nSteps + 1) * nSteps + 2;
						}
					}
				}
			}
		} else if (rStep % 2 == 0 || (carryStep != 0 &&
				carryStep % 2 == 0)) {
			aux = 0;
			state = 0;
			nextPositionPOField(abacus,
				(rStep % nSteps - 4) / 2);
			highlightRail(aux, rPosition, true);
			if (carryStep != 0) {
				drawLineText("Borrowing", 1);
			} else {
				if (bDigit != 0 && bDigit != ((root == 2) ? bValue : cValue))
					drawLineText("Subtract " + bDigit +
						" (part of " + ((root == 2) ? bValue : cValue) +
						") from primary field, offset digit by corresponding position " + rPosition +
						" (in group " + regCount +
						")" + ((carry[state] != 0) ? ", with borrow" : ""), 1);
				else
					drawLineText("Subtract " + bDigit +
						" from primary field, offset digit by corresponding position " + rPosition +
						" (in group " + regCount +
						")" + ((carry[state] != 0) ? ", with borrow" : ""), 1);
			}
			if (carryStep == 0) {
				step++;
			} else {
				carryStep++;
			}
		} else {
			aux = 0;
			if (!abacus.getRightToLeftAdd()) {
				carry[1] = carry[0];
			}
			state = 1;
			nextPositionPOField(abacus,
				(rStep % nSteps - 4) / 2);
			setStringBuffer(abacus, rString, aux,
				rPosition, lower, upper);
			if (lower != 0)
				abacus.abacusMove(aux, 0, rPosition, lower);
			if (upper != 0)
				abacus.abacusMove(aux, 1, rPosition, upper);
			highlightRail(aux, rPosition, false);
			if (carry[state] == 0 && carryStep != 0) {
				carryStep = 0;
			}
			if (rStep % nSteps != nSteps - 1 &&
					carry[state] == 0 &&
					logInt(((root == 2) ? bValue : cValue),
					abacus.getBase()) <= (rStep % nSteps - 4) / 2) {
				step = (step / nSteps + 1) * nSteps + 2;
			} else {
				if (!abacus.getRightToLeftAdd() &&
						carry[state] != 0) {
					if (carryStep == 0) {
						carryStep = 2;
					} else {
						carryStep++;
					}
				}
				if (abacus.getRightToLeftAdd() ||
						carryStep == 0) {
					step++;
				}
			}
		}
	}

	/* A little tricky as this is reentrant */
	public void teachStep(Abacus abacus, String buffer, int aux) {
		StringBuffer buffer1 = null;
		int base = abacus.getBase();
		int bottomNumber = abacus.getBottomNumber();
		BigDecimal a, b;
		int newAux = aux;

		if (test) {
			if (newAux == 0) {
				//testTeachRoot(abacus, 0, 10102, 10000);
				testTeachRoot(abacus, 0, 1002, 10000);
			} else {
				newAux = 0;
			}
		}
		if (step == 0) {
			/*testTable();*/
			drawLineText(AbacusInterface.TEACH_STRING0, 0);
			drawLineText("", 1);
			drawLineText(AbacusInterface.TEACH_STRING1, 2);
			reg = -1;
			regCount = 0;
			carryStep = 0;
			carry[0] = 0;
			carry[1] = 0;
			step++;
		} else if (step == 1) {
			simpleParser(abacus, buffer);
			if (debug)
				System.out.println("buffer = " + aString +
					" " + op + " " + bString);
			if (op == 'q' || op == 'Q') {
				return;
			}
			AbacusCalc abacusCalc = new AbacusCalc(abacus);

			if (abacus.getAnomaly() != 0) {
				abacusCalc.addBackAnomaly(aString,
					abacus.getAnomaly(),
					abacus.getShiftAnomaly(),
					base);
			}
			if (abacus.getAnomalySq() != 0) {
				abacusCalc.addBackAnomaly(aString,
					abacus.getAnomalySq(),
					abacus.getShiftAnomaly() + abacus.getShiftAnomalySq(),
					base);
			}
			if (abacus.checkSubdeck(3)) {
				AbacusCalc.zeroFractionalPart(aString);
				AbacusCalc.zeroFractionalPart(bString);
			}
			contractStringBuffer(aString);
			contractStringBuffer(bString);
			/*testShift(aString.toString);*/
			drawLineText("", 1);
			if (op == '+' || op == '-') {
				abacusCalc.convertStringToAbacus(aString.toString(),
					newAux);
				if (((AbacusApplet)applet).getLee())
					abacusCalc.convertStringToAbacus("0.0", 1);
				if (((AbacusApplet)applet).getLee())
					abacusCalc.convertStringToAbacus("0.0", 2);
				a = abacusCalc.convertToDecimal(base,
					aString.toString());
				b = abacusCalc.convertToDecimal(base,
					bString.toString());
				if (op == '-' && b.compareTo(a) > 0) {
					/* Revisit this, it should be allowed, but
					  rails probably need to be respected.
					  Goes along with complement feature. */
					drawLineText("Subtraction underflow " +
						aString + op + bString, 0);
					step = 0;
					return;
				}
				buffer1 = new StringBuffer(
					((op == '+') ? "Add" : "Subtract") +
					"ing " + aString + " " + op +
					" " + bString);
				drawLineText(buffer1.toString(), 0);
				decimalSafe(aString);
				decimalSafe(bString);
				addSafe(aString, bString);
				rString = new StringBuffer(aString);
				drawLineText("Current answer: " + rString, 2);
				step++;
			} else if (op == '*') {
				if (aString.length() == 0)
					return;
				abacusCalc.convertStringToAbacus("0.0", 0);
				if (((AbacusApplet)applet).getLee())
					abacusCalc.convertStringToAbacus(aString.toString(), 1);
				a = abacusCalc.convertToDecimal(base,
					aString.toString());
				if (((AbacusApplet)applet).getLee())
					abacusCalc.convertStringToAbacus(bString.toString(), 2);
				b = abacusCalc.convertToDecimal(base,
					bString.toString());
				if (a.compareTo(BigDecimal.ZERO) < 0 ||
						b.compareTo(BigDecimal.ZERO) < 0) {
					drawLineText("Multiplication underflow " +
						aString + op + bString, 0);
					step = 0;
					return;
				}
				buffer1 = new StringBuffer("Multiplying " +
					aString + " " + op + " " + bString);
				if (!((AbacusApplet)applet).getLee()) {
					buffer1.append(" ... works best with lee option");
				}
				drawLineText(buffer1.toString(), 0);
				decimalSafe(aString);
				decimalSafe(bString);
				rString = new StringBuffer("0.");
				drawLineText("Current answer: " + rString, 2);
				step++;
			} else if (op == '/') {
				if (aString.length() == 0)
					return;
				if (((AbacusApplet)applet).getLee())
					abacusCalc.convertStringToAbacus("0.0", 2);
				abacusCalc.convertStringToAbacus(aString.toString(), 0);
				a = abacusCalc.convertToDecimal(base,
					aString.toString());
				if (((AbacusApplet)applet).getLee())
					abacusCalc.convertStringToAbacus(bString.toString(), 1);
				b = abacusCalc.convertToDecimal(base,
					bString.toString());
				if (a.compareTo(BigDecimal.ZERO) < 0 ||
						b.compareTo(BigDecimal.ZERO) < 0) {
					drawLineText("Division underflow " +
						aString + op + bString, 0);
					step = 0;
					return;
				}
				if (b.compareTo(BigDecimal.ZERO) == 0) {
					drawLineText("Division overflow " +
						aString + op + bString, 0);
					step = 0;
					return;
				}
				contractStringBuffer(aString);
				buffer1 = new StringBuffer("Dividing " +
					aString + " " + op + " " + bString);
				if (!((AbacusApplet)applet).getLee()) {
					buffer1.append(" ... works best with lee option");
				}
				drawLineText(buffer1.toString(), 0);
				decimalSafe(aString); /* quotient */
				decimalSafe(bString);
				rString = new StringBuffer(aString);
				sString = new StringBuffer("0.");
				drawLineText("Current answer: " + sString, 2);
				step++;
				reg = 0;
				cString = new StringBuffer(aString); /* remainder */
			} else if (op == 'v' || op == 'u') {
				StringBuffer group = new StringBuffer();

				intGroup = rootGroup(abacus, aString.toString(),
					(op == 'v') ? 2 : 3);
				stringGroup(group, intGroup, op);
				abacusCalc.convertStringToAbacus(aString.toString(),
					newAux);
				if (((AbacusApplet)applet).getLee())
					abacusCalc.convertStringToAbacus("0.0", 1);
				if (((AbacusApplet)applet).getLee())
					abacusCalc.convertStringToAbacus("0.0", 2);
				a = abacusCalc.convertToDecimal(base,
					aString.toString());
				if (a.compareTo(BigDecimal.ZERO) < 0) {
					drawLineText("Root underflow " +
						aString + op, 0);
					step = 0;
					return;
				}
				buffer1 = new StringBuffer(
					((op == 'v') ? "Square" : "Cube") +
					" root of " + aString +
					", grouping digits yields (" + group +
					")");
				if (!((AbacusApplet)applet).getLee()) {
					buffer1.append(" ... works best with lee option");
				}
				drawLineText(buffer1.toString(), 0);
				rString = new StringBuffer(aString);
				sString = new StringBuffer("0.");
				drawLineText("Current answer: " + sString, 2);
				step++;
				reg = -1; /* LEE root stuff else should be 0 */
				bString = new StringBuffer("0.");
				cString = new StringBuffer("0.");
			}
			if (debug)
				System.out.println("op buffer = " + aString +
					" " + op + " " + bString + " " +
					abacus.getRightToLeftAdd() + " " +
					abacus.getRightToLeftMult());
			carry[0] = 0;
			carry[1] = 0;
		} else if (op == 'v' || op == 'u') {
			teachStepRoot(abacus);
			return;
		} else if (reg == 0 || (reg < 0 && ((carryStep != 0 &&
				carryStep % 2 == 0 && carryStep >= 2) ||
				(carryStep == 0 && step % 2 == 0 &&
				step >= 2)))) {
			/* Tell user what is going to happen */
			boolean done = false;

			state = 0;
			if (op == '+' || op == '-') {
				done = nextPositionSum(abacus, op);
				buffer1 = new StringBuffer(
					((op == '+') ? "Add" : "Subtract") +
					"ing " + aString + " " + op +
					" " + bString);
				if (carryStep == 0) {
					StringBuffer buf;
					AbacusCalc abacusCalc = new AbacusCalc(abacus);

					buf = new StringBuffer();
					abacusCalc.convertFromDecimal(buf, abacus.getBase(),
						new BigDecimal(aDigit), false);
					trimStringBuffer(buf);
					buffer1.append(" ... " + buf);
					buf = new StringBuffer();
					abacusCalc.convertFromDecimal(buf, abacus.getBase(),
						new BigDecimal(bDigit), false);
					trimStringBuffer(buf);
					buffer1.append(" " + op + " " + buf);
					buf = new StringBuffer();
					if (op == '+')
						abacusCalc.convertFromDecimal(buf, abacus.getBase(),
							new BigDecimal(aDigit + bDigit), false);
					else
						abacusCalc.convertFromDecimal(buf, abacus.getBase(),
							new BigDecimal(aDigit - bDigit), false);
					trimStringBuffer(buf);
					buffer1.append(" = " + buf);
				} else {
					buffer1.append(" ... carrying " + 1);
				}
				drawLineText(buffer1.toString(), 0);
			} else if (op == '*') {
				done = nextPositionProduct(abacus);
				buffer1 = new StringBuffer("Multiplying " +
					aString + " " + op + " " + bString);
				if (carryStep == 0) {
					StringBuffer buf;
					AbacusCalc abacusCalc = new AbacusCalc(abacus);

					buf = new StringBuffer();
					abacusCalc.convertFromDecimal(buf, abacus.getBase(),
						new BigDecimal(aDigit), false);
					trimStringBuffer(buf);
					buffer1.append(" ... " + buf);
					buf = new StringBuffer();
					abacusCalc.convertFromDecimal(buf, abacus.getBase(),
						new BigDecimal(bDigit), false);
					trimStringBuffer(buf);
					buffer1.append(" " + op + " " + buf);
					buf = new StringBuffer();
					abacusCalc.convertFromDecimal(buf, abacus.getBase(),
						new BigDecimal(aDigit * bDigit), false);
					trimStringBuffer(buf);
					buffer1.append(" = " + buf);
					buf = new StringBuffer();
					abacusCalc.convertFromDecimal(buf, abacus.getBase(),
						new BigDecimal(bValue), false);
					trimStringBuffer(buf);
					buffer1.append(", adding " + buf +
						" digit");
				} else {
					buffer1.append(" ... carrying " + 1);
				}
				drawLineText(buffer1.toString(), 0);
			} else if (op == '/') {
				done = nextPositionDivision(abacus);
				contractStringBuffer(cString);
				buffer1 = new StringBuffer("Dividing " +
					cString + " " + op + " " + bString);
				if (carryStep == 0 && divisor.compareTo(BigDecimal.ZERO) != 0) {
					StringBuffer buf;
					AbacusCalc abacusCalc = new AbacusCalc(abacus);

					buf = new StringBuffer();
					abacusCalc.convertFromDecimal(buf, abacus.getBase(),
						new BigDecimal(qDigit), false);
					trimStringBuffer(buf);
					buffer1.append(" ... " + buf);
					buf = new StringBuffer();
					abacusCalc.convertFromDecimal(buf, abacus.getBase(),
						divisor, false);
					trimStringBuffer(buf);
					buffer1.append(" * " + buf);
					buf = new StringBuffer();
					abacusCalc.convertFromDecimal(buf, abacus.getBase(),
						divisor.multiply(new BigDecimal(qDigit)), false);
					trimStringBuffer(buf);
					buffer1.append(" = " + buf);
					if (reg >= 0) {
						buf = new StringBuffer();
						abacusCalc.convertFromDecimal(buf, abacus.getBase(),
							new BigDecimal(qDigit), false);
						trimStringBuffer(buf);
						buffer1.append(", register " +
							buf + " on second auxiliary, rail " +
							qPosition);
						drawLineText(buffer1.toString(), 0);
						reg++;
						pendingUpdate(1, 2, qPosition, base,
							((AbacusApplet)applet).getAbacus(2).getBottomNumber());
						return;
					}
					if (divisor.compareTo(new BigDecimal(bDigit)) != 0) {
					    buf = new StringBuffer();
					    abacusCalc.convertFromDecimal(buf, abacus.getBase(),
						new BigDecimal(qDigit), false);
					    trimStringBuffer(buf);
					    buffer1.append(" ... " + buf);
					    buf = new StringBuffer();
					    abacusCalc.convertFromDecimal(buf, abacus.getBase(),
						new BigDecimal(bDigit), false);
					    trimStringBuffer(buf);
					    buffer1.append(" * " + buf);
					    buf = new StringBuffer();
					    abacusCalc.convertFromDecimal(buf, abacus.getBase(),
						new BigDecimal(qDigit * bDigit), false);
					    trimStringBuffer(buf);
					    buffer1.append(" = " + buf);
					}
					buf = new StringBuffer();
					abacusCalc.convertFromDecimal(buf, abacus.getBase(),
						new BigDecimal(bValue), false);
					trimStringBuffer(buf);
					buffer1.append(", subtracting " + buf +
						" digit");
					drawLineText(buffer1.toString(), 0);
				} else {
					buffer1.append(" ... borrowing " + 1);
				}
				drawLineText(buffer1.toString(), 0);
			} else if (op == 'v' || op == 'u') {
				done = nextPositionRoot(abacus, (op == 'v') ? 2 : 3);
				contractStringBuffer(cString);
				buffer1 = new StringBuffer("Taking " +
					((op == 'v') ? "Square" : "Cube") +
					" Root of " + cString);
				if (carryStep == 0) {
					buffer1.append(" ... " + qDigit);
					if (op == 'u')
						buffer1.append(" * " + qDigit);
					buffer1.append(" * " + qDigit + " = " +
						(qDigit * qDigit * ((op == 'v') ? 1 : qDigit)));
					if (reg >= 0) {
						buffer1.append(", register " +
							qDigit + " on first auxiliary, rail " +
							qPosition);
						drawLineText(buffer1.toString(), 0);
						reg++;
						pendingUpdate(1, 1, qPosition, base,
							((AbacusApplet)applet).getAbacus(1).getBottomNumber());
						return;
					}
					buffer1.append(" ... subtracting " + bValue +
						" digit");
					drawLineText(buffer1.toString(), 0);
				} else {
					buffer1.append(" ... borrowing " + 1);
				}
				drawLineText(buffer1.toString(), 0);
			}
			if (!pendingUpdate(1, 0, rPosition, base, bottomNumber))
				done = false;
			if (carry[state] == 0 && carryStep == 0 && done) {
				contractStringBuffer(rString);
				if (op == 'v' || op == 'u') {
					contractStringBuffer(sString);
					drawLineText("Final answer: " + sString, 2);
					highlightRails(1);
				} else if (op == '/') {
					contractStringBuffer(sString);
					drawLineText("Final answer: " + sString, 2);
					highlightRails(2);
				} else {
					drawLineText("Final answer: " + rString, 2);
					highlightRails(0);
				}
				step = 0;
			} else if (carryStep == 0) {
				step++;
			} else {
				carryStep++;
			}
		} else {
			/* Actually carry out what was told would happen */
			boolean done = false;

			if (!abacus.getRightToLeftAdd())
				carry[1] = carry[0];
			state = 1;
			if (op == '+' || op == '-') {
				done = nextPositionSum(abacus, op);
				setStringBuffer(abacus, rString, 0, rPosition, lower, upper);
			} else if (op == '*') {
				done = nextPositionProduct(abacus);
				setStringBuffer(abacus, rString, 0, rPosition, lower, upper);
			} else if (op == '/') {
				done = nextPositionDivision(abacus);
				if (reg > 0) {
					if (((AbacusApplet)applet).getLee()) {
						if (lower != 0)
							abacus.abacusMove(2, 0, qPosition, lower);
						if (upper != 0)
							abacus.abacusMove(2, 1, qPosition, upper);
						highlightRail(2, qPosition, false);
					}
					setStringBuffer(abacus, sString, 2, qPosition, lower, upper);
					reg = -1;
					contractStringBuffer(sString);
					drawLineText("Current answer: " + sString, 2);
					return;
				}
				setStringBuffer(abacus, rString, 0, rPosition, lower, upper);
				AbacusCalc abacusCalc = new AbacusCalc(abacus);
				a = abacusCalc.convertToDecimal(base, rString.toString());
				if (a.compareTo(BigDecimal.ZERO) == 0) {
					done = true;
				}
			} else if (op == 'v' || op == 'u') {
				done = nextPositionRoot(abacus, (op == 'v') ? 2 : 3);
				if (reg > 0) {
					if (((AbacusApplet)applet).getLee()) {
						if (lower != 0)
							abacus.abacusMove(1, 0, qPosition, lower);
						if (upper != 0)
							abacus.abacusMove(1, 1, qPosition, upper);
						highlightRail(1, qPosition, false);
					}
					setStringBuffer(abacus, sString, 1, qPosition, lower, upper);
					reg = -1;
					contractStringBuffer(sString);
					drawLineText("Current answer: " + sString, 2);
					return;
				}
				setStringBuffer(abacus, rString, 0, rPosition, lower, upper);
				AbacusCalc abacusCalc = new AbacusCalc(abacus);
				a = abacusCalc.convertToDecimal(base, rString.toString());
				if (a.compareTo(BigDecimal.ZERO) == 0) {
					done = true;
				}
			}
			if (lower != 0)
				abacus.abacusMove(0, 0, rPosition, lower);
			if (upper != 0)
				abacus.abacusMove(0, 1, rPosition, upper);
			highlightRail(0, rPosition, false);
			/*if (carry[state] != 0) {
				carry[state] = 0;
				abacus.abacusMove(0, 0, rPosition + 1, carry[state]);
			}*/
			if (carry[state] == 0 && carryStep != 0) {
				carryStep = 0;
			}
			contractStringBuffer(rString);
			if (carry[state] == 0 && carryStep == 0 && done) {
				step = 0;
				if (op == 'v' || op == 'u') {
					contractStringBuffer(sString);
					drawLineText("Final answer: " + sString, 2);
					highlightRails(1);
				} else if (op == '/') {
					contractStringBuffer(sString);
					drawLineText("Final answer: " + sString, 2);
					highlightRails(2);
				} else {
					drawLineText("Final answer: " + rString, 2);
					highlightRails(0);
				}
			} else {
				if (op == '/' || op == 'v' || op == 'u') {
					contractStringBuffer(sString);
					drawLineText("Current answer: " + sString, 2);
				} else {
					drawLineText("Current answer: " + rString, 2);
				}
				if ((done && abacus.getRightToLeftAdd()) ||
					!abacus.getRightToLeftAdd()) {
					if (carry[state] != 0) {
						if (carryStep == 0) {
							carryStep = 2;
							if (abacus.getRightToLeftAdd())
								carry[1] = carry[0] = 0;
						} else {
							carryStep++;
						}
					}
					if (carryStep == 0) {
						step++;
					}
				} else if (abacus.getRightToLeftAdd()) {
					step++;
				}
			}
		}
	}

	void testTeachRoot(Abacus abacus, int startX, int finishX, int max) {
		StringBuffer buffer = null;
		int i, x, side;
		double ans, rem, extra, diff, diff2, temp;
		char rootOp;
		double factor, epsilon, epsilon2;
		int root, places;

		if (!abacus.getRightToLeftAdd())
			abacus.toggleRightToLeftAbacusAdd();
		abacus.setDelay(0);
		for (x = startX; x < finishX; x++) {
		  for (root = 2; root <= 3; root++) {
		    rootOp = (root == 2) ? 'v' : 'u';
		    for (places = 0; places <= 1; places++) {
		      factor = expFloat(places, abacus.getBase());
		      epsilon = expFloat(root * places + 1, abacus.getBase());
		      epsilon2 = expFloat((root - 1) * places + 1, abacus.getBase());
		      for (side = 0; side <= 1; side++) {
		        abacus.toggleRightToLeftAbacusAdd();
				DecimalFormat formatter = new DecimalFormat("0.######");
				temp = 1.0;
				if (root == 3)
					temp *= factor * x;
				buffer = new StringBuffer(formatter.format(factor * factor * x * x * temp) + rootOp);
				System.out.print(formatter.format(factor * x) + ": " +
					buffer + " = ");
				for (i = 0; i < max; i++) {
					teachStep(abacus, buffer.toString(), -1);
					if (step == 0) {
						break;
					}
				}
				trimStringBuffer(rString);
				trimStringBuffer(bString);
				if (root == 3)
					trimStringBuffer(cString);
				ans = Double.valueOf(bString.toString());
				rem = Double.valueOf(rString.toString());
				extra = Double.valueOf(cString.toString());
				diff = Math.abs(x * root * factor - ans);
				diff2 = 0;
				if (root == 3)
					diff2 = Math.abs(x * x * factor * factor * root - extra);
				System.out.println(bString + " / " +
					root + " (remainder " + rString + ")" +
					((root == 3) ? ", (order " : "") +
					((root == 3) ? cString : "") +
					((root == 3) ? ")" : "") + ", (" +
					((abacus.getRightToLeftAdd()) ? "r-l" : "l-r") + ") " +
					((step != 0) ? "s" : "") +
					((rem != 0) ? "r" : "") +
					((diff >= epsilon) ? "a" : "") +
					((diff2 >= epsilon2) ? "e" : ""));
				if ((step != 0) ||
						(rem != 0) ||
						(diff >= epsilon) ||
						(diff2 >= epsilon2)) {
					System.out.println("Error! " + diff2);
					System.exit(-1);
				}
/*
			} else {
				for (y = startY; y < finishY; y++) {
					buffer = new StringBuffer("" + x + op + y);
					System.out.printf("%s = ", buffer);
					for (i = 0; i < max; i++) {
						teachStep(abacus, buffer.toString(), -1);
						if (step == 0)
							break;
					}
					if (op =='/') {
						System.out.printf("%s\n", sString);
					} else {
						System.out.printf("%s\n", rString);
					}
				}
*/
		      }
		    }
		  }
		}
	}
}
