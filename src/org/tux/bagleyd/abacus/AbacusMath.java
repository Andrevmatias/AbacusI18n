package org.tux.bagleyd.abacus;

/*
 * @(#)AbacusMath.java
 *
 * Copyright 1994 - 2009  David A. Bagley, bagleyd@tux.org
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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * The <code>AbacusMath</code> class handles the string mathematics
 * for the <code>AbacusApplet</code> class.  Whenever possible the
 * mathematics is done using strings.  (Roman math is also done here
 * and is limited by definition.)  I use long when I can not figure
 * out how to do it by strings, as in Display Base.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */

public class AbacusMath {
	/* LONGEST_ROMANNUMERAL mmmdccclxxxvMMMDCCCLXXXVIII 3,888,888 */
	static final int MAX_ROMAN = 3999999;
	static final int MAX_ROMAN_DIGIT = 1000000;
	static final int MAX_ROMANFRACT = 36 /* 9+18+strlen(HALF_UNCIA) */;
	static final int ROMANFRACTIONBASE = 12;
	static final int ALTROMANFRACTIONBASE = 8;
	static final int sizeRoman = 13;
	char roman[] = {
		'I', 'V', 'X', 'L', 'C', 'D', 'M',
		     'v', 'x', 'l', 'c', 'd', 'm'
	};
	static final String[] oldRoman = {
		"I", "V",     "X",    "L",       "(",    "I)", "(I)",
		   "I))", "((I))", "I)))", "(((I)))", "I))))", "|x|"
	};

	/*
	Here we have lower case to represent the letters with bars on top.
	Pardon the non-standard notation (case historically was ignored).
        Think of it as room to add the line by hand... :)
	_
	V = v,
	_
	X = x, etc
	It has been suggested to put more bars on top for bigger numbers
	but there is no recorded usage of a larger Roman numeral in
	Roman times.
	An older notation for Roman numerals was represented thus:
	( = C, I) = D, (I) = M, I)) = v, ((I)) = x, I))) = l, (((I))) = c,
	 _
	|X| = m (here for simplicity of display, just displayed as |x| ).
	Fractions
	12 ounces (uncia) in a as
	S = 1/2 ounce
	) = 1/4 ounce
	Z = 1/12 ounce
	*/

	/* Fractions of twelfths had these names 0/12 - 12/12 */
	static final String[] twelfthStrings = {
		"", "uncia", "sextans", "quadrans",
		"triens", "quincunx", "semis", "septunx",
		"bes", "dodrans", "dextans", "deunix",
		"as"};
	static final String[] twelfthGlyphs = {
		"", "-", "=", "=-",
		"==", "=-=", "S", "S-",
		"S=", "S=-", "S==", "S=-=",
		"|"};

	static final String HALF_UNCIA = "semuncia"; /* E, (actually a Greek
		letter sigma) */
	static final String ONEANDAHALF_UNCIA = "sescuncia"; /* E- */
	static final String[] halftwelfthStrings = {
		HALF_UNCIA, ONEANDAHALF_UNCIA};
	static final String[] halftwelfthGlyphs = {
		"E", "E-"};

	/* Fractions of Uncia had these names, took shortest variant */
	static final String TWELFTH_UNCIA = "semisextula"; /* z (actually,
		a "Z" with a "-" through the middle) AKA dimidia sextula,
		dimidio sextula */
	static final String SIXTH_UNCIA = "sextula"; /* Z */
	static final String QUARTER_UNCIA = "sicilicus"; /* Q (actually a
		backwards C but confusing with ancient Roman numerals) */
	static final String THIRD_UNCIA = "duella"; /* u (actually a Greek
		letter mu), AKA binae sextulae */
	static final String FIVETWELFTHS_UNCIA = "sicilicus sextula"; /* not
		sure if this is best representation */
	static final String EIGHTH_UNCIA = "drachma";
	static final String THREEEIGHTHS_UNCIA = "sicilicus drachma"; /* not
		sure if this is best representation */
	/* Combining fractions (not sure how this was done in practice
	   but this seems reasonable). Combine with the representation
	   for HALF_UNCIA or ONEANDAHALF_UNCIA as required. */
	static final String[] subtwelfthStrings = {
		"", TWELFTH_UNCIA, SIXTH_UNCIA,
		QUARTER_UNCIA, THIRD_UNCIA, FIVETWELFTHS_UNCIA};
	static final String[] subeighthStrings = {
		"", EIGHTH_UNCIA, QUARTER_UNCIA, THREEEIGHTHS_UNCIA};
	static final String[] subtwelfthGlyphs = {
		"", "z", "Z",
		"Q", "u", "QZ"};
	static final String[] subeighthGlyphs = {
		"", "t", "Q", "Qt"};

	static final boolean debug = false;

	public static int char2Int(char character) {
		int charValue = character;

	//	System.out.print("char"  + charValue +  " ");
		if (charValue >= '0' && charValue <= '9') {
			charValue += 0 - '0';
		/* ASCII or EBCDIC */
		} else if (charValue >= 'a' && charValue <= 'i') {
			charValue += 10 - 'a';
		} else if (charValue >= 'j' && charValue <= 'r') {
			charValue += 19 - 'j';
		} else if (charValue >= 's' && charValue <= 'z') {
			charValue += 28 - 's';
		} else if (charValue >= 'A' && charValue <= 'I') {
			charValue += 10 - 'A';
		} else if (charValue >= 'J' && charValue <= 'R') {
			charValue += 19 - 'J';
		} else if (charValue >= 'S' && charValue <= 'Z') {
			charValue += 28 - 'S';
		} else
			charValue = 36;
	//	System.out.println("int"  + (int) charValue);
		return charValue;
	}

	public static char int2Char(int digit) {
		char charValue = (char) digit;

		charValue += '0';
		if (charValue > '9' || charValue < '0') {
			charValue += ('A' - '9' - 1);
			/* ASCII or EBCDIC */
			/*if (charValue > 'I')
				charValue += ('J' - 'I' - 1);
			if (charValue > 'R')
				charValue += ('S' - 'R' - 1);*/
		}
		return charValue;
	}

	public static int int2String(StringBuffer buffer, BigInteger number, int base,
			boolean negative) {
		StringBuffer buf = buffer;
		int digit, last, position = 0, i;
		BigInteger mult = BigInteger.ONE, remain = number;

		last = 1;
		buf.setLength(0);
		while (mult.multiply(BigInteger.valueOf(base)).compareTo(remain) <= 0) {
			mult = mult.multiply(BigInteger.valueOf(base));
			last++;
			if (mult.multiply(BigInteger.valueOf(base)).compareTo(mult) < 0) {
				buf = buf.insert(0, 0);
				buf.setLength(1);
				return 2;
			}
		}
		if (negative) {
			buf = buf.insert(position, '-');
			position++;
		}
		for (i = 0; i < last; i++) {
			digit = (remain.divide(mult)).intValue();
			remain = remain.subtract(mult.multiply(BigInteger.valueOf(digit)));
			buf = buf.insert(position, int2Char(digit));
			mult = mult.divide(BigInteger.valueOf(base));
			if (mult.compareTo(BigInteger.ZERO) == 0 &&
					i != last - 1) {
				buf = buf.insert(0, 0);
				buf.setLength(1);
				return 2;
			}
			position++;
		}
		return last;
	}

	public static int flt2String(StringBuffer buffer, BigInteger number, int abacusBase,
			int base, int places, int decimalPosition) {
		StringBuffer buf = buffer;
		int position = 0, digit;
		BigInteger divisor = BigInteger.valueOf(base);
		BigDecimal mult = BigDecimal.ONE.divide(BigDecimal.valueOf(base),
			2 * decimalPosition, BigDecimal.ROUND_DOWN);
		BigDecimal fraction = new BigDecimal(number);

		for (position = 0; position < places; position++) {
			fraction = fraction.divide(BigDecimal.valueOf(abacusBase),
				2 * decimalPosition, BigDecimal.ROUND_DOWN);
		}
		buf.setLength(0);
		for (position = 0; position < decimalPosition; position++) {
			digit = (fraction.multiply(new BigDecimal(divisor))).intValue();
			fraction = fraction.subtract(mult.multiply(BigDecimal.valueOf(digit)));
			buf = buf.insert(position, int2Char(digit));
			mult = mult.divide(BigDecimal.valueOf(base),
				2 * decimalPosition, BigDecimal.ROUND_DOWN);
			divisor = divisor.multiply(BigInteger.valueOf(base));
			if (mult.compareTo(BigDecimal.ZERO) == 0 &&
					position != places - 1) {
				buf.setLength(0);
				return 0;
			}
		}
		for (digit = position - 1; digit > 0; digit--) {
			if (buf.charAt(digit) == '0')
				buf.setLength(buf.length() - 1);
			else
				break;
		}
		return places;
	}

	/* not used */
	public static int string2Int(String buf, int base, char decimalPoint) {
		int digit, position = 0, value = 0, last;

		if (buf.charAt(position) == '-') {
			return 0;
		}
		last = buf.length();
		for (; position < last; position++) {
			if (buf.charAt(position) == decimalPoint) {
				break;
			}
			digit = char2Int(buf.charAt(position));
			value = value * base + digit;
			if (value >= ((base >= 8) ? base / 2 - 1 : base) *
					base * base * base * base * base * base)
				return 0;
		}
		return value;
	}

	/* Try not to use since this restricts the size of abacus. */
	public static BigInteger multPower(int m, int x, int n) {
		/* raise x to the nth power where m, x, n >= 0 */
		int i;
		BigInteger p = BigInteger.valueOf(1);

		if (m == 0 || x == 0)
			return BigInteger.ZERO;
		for (i = 1; i <= n; ++i) {
			p = p.multiply(BigInteger.valueOf(x));
		}
		return p.multiply(BigInteger.valueOf(m));
	}

	/* This is fast for small i. */
	public static int rootInt(int i, int n) {
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

	public static void dividePieces(StringBuffer buffer, int base,
			int pieces, int mult, int places, char decimalPoint) {
		StringBuffer buf = buffer;
		int position, digit, inter;

		if (debug)
			System.out.println("dividePieces: base " + base +
				", pieces " + pieces + ", mult " + mult +
				", places " + places);
		digit = mult / pieces;
		inter = mult % pieces;
		/* This works because digit should never be greater than base here */
		buf.setLength(0);
		buf = buf.insert(0, digit);
		buf = buf.insert(1, decimalPoint);
		for (position = 2; position < places;) {
			digit = (inter * base) / pieces;
			inter = (inter * base) % pieces;
			buf = buf.insert(position, int2Char(digit));
			position++;
			if (inter == 0)
				break;
		}
		if (debug)
			System.out.println("dividePieces: buf " + buf);
	}

	public static void shiftDecimal(StringBuffer buf, String aBuf,
			int shift, int place, char decimalPoint) {
		int size = aBuf.trim().length();
		int loc = 0, i = 0, integerDigits;

		if (debug)
			System.out.println("shiftDecimal: aBuf " + aBuf +
				", shift " + shift + ", place " + place);
		buf.setLength(0);
		while (aBuf.charAt(i) != decimalPoint && i < size) {
			buf.append(aBuf.charAt(i));
			i++;
			buf.setLength(i);
		}
		integerDigits = i;
		buf.append(aBuf.charAt(i));
		i++;
		buf.setLength(i);
		while (i - 1 < place + integerDigits && i < size) {
			buf.append(aBuf.charAt(i));
			i++;
			buf.setLength(i);
		}
		loc = i - 1;
		if (shift > 0) {
			/* shift right */
			for (i = place + 2; i < shift + place + 2; i++) {
				buf.append('0');
				buf.setLength(i + 1);
			}
		} else {
			/* shift left */
			loc -= shift;
			buf.setLength(loc);
		}
		while (loc < size - 1) {
			loc++;
			buf.append(aBuf.charAt(loc));
			i++;
			buf.setLength(i);
		}
		if (debug) {
			System.out.println("shiftDecimal: buf " + buf + " " +
				buf.length());
			/*for (int j = 0; j < buf.length(); j++)
				System.out.println(buf.charAt(j));*/
		}
	}

	/* 2nd grade math made hard */
	/* May disregard sign if different */
	public static void addStrings(StringBuffer buf, String aBuf, String bBuf,
			int base, char decimalPoint) {
		int aEnd = aBuf.trim().length();
		int bEnd = bBuf.trim().length(); /* zeros included */
		int aDecimal = aEnd, bDecimal = bEnd;
		int aSign = ((aBuf.charAt(0) == '-') ? 1 : 0);
		int bSign = ((bBuf.charAt(0) == '-') ? 1 : 0);
		int i, carry = 0, digit, sum;
		StringBuffer aMut = new StringBuffer(aBuf.trim());
		StringBuffer bMut = new StringBuffer(bBuf.trim());

		if (debug)
			System.out.println("addStrings: aBuf " + aBuf +
				", bBuf " + bBuf + ", base " + base +
				", aEnd " + aEnd + ", bEnd " + bEnd);
		for (i = 0; i < aEnd; i++) {
			if (aMut.charAt(i) == decimalPoint &&
					aDecimal == aEnd) {
				aDecimal = i;
				break;
			}
		}
		for (i = 0; i < bEnd; i++) {
			if (bMut.charAt(i) == decimalPoint &&
					bDecimal == bEnd) {
				bDecimal = i;
				break;
			}
		}
		sum = aEnd - bEnd - aDecimal + bDecimal;
		if (sum > 0) {
			for (i = 0; i < sum; i++) {
				bMut.append("0");
				bEnd++;
				bMut.setLength(bEnd);
			}
		}
		if (-sum > 0) {
			for (i = 0; i < -sum; i++) {
				aMut.append("0");
				aEnd++;
				aMut.setLength(aEnd);
			}
		}
		buf.setLength(0);
		if (aDecimal + aSign >= bDecimal + bSign) {
			for (i = aEnd - 1; i > aDecimal; i--) {
				digit = char2Int(aMut.charAt(i)) +
					char2Int(bMut.charAt(
					i - aEnd + bEnd)) + carry;
				aMut.setCharAt(i, int2Char(digit % base));
				carry = digit / base;
			}
			for (i = aDecimal - 1; i >= aSign; i--) {
				digit = char2Int(aMut.charAt(i)) +
					((i - aEnd + bEnd >= bSign) ?
					char2Int(bMut.charAt(
					i - aEnd + bEnd)) : 0) + carry;
				aMut.setCharAt(i, int2Char(digit % base));
				carry = digit / base;
			}
			if (carry > 0) {
				buf.insert(aSign, (char) (carry + '0'));
				buf.append(aMut.toString().substring(aSign));
			} else {
				buf.setLength(aMut.toString().trim().length());
				buf.append(aMut.toString().trim());
			}
		} else {
			for (i = bEnd - 1; i > bDecimal; i--) {
				digit = char2Int(bMut.charAt(i)) +
					char2Int(aMut.charAt(
					i - bEnd + aEnd)) + carry;
				bMut.setCharAt(i, int2Char(digit % base));
				carry = digit / base;
			}
			for (i = bDecimal - 1; i >= bSign; i--) {
				digit = char2Int(bMut.charAt(i)) +
					((i - bEnd + aEnd >= aSign) ?
					char2Int(aMut.charAt(
					i - bEnd + aEnd)) : 0) + carry;
				bMut.setCharAt(i, int2Char(digit % base));
				carry = digit / base;
			}
			if (carry > 0) {
				buf.insert(bSign, (char) (carry + '0'));
				buf.append(bMut.toString().substring(bSign));
			} else {
				buf.setLength(bMut.toString().trim().length());
				buf.append(bMut.toString().trim());
			}
		}
		i = buf.length() - 1;
		while (buf.charAt(i) == '0' &&
				buf.charAt(i - 1) != decimalPoint)
			i--;
		buf.setLength(i + 1);
		if (debug)
			System.out.println("addStrings: buf " + buf);
	}

	public static void convertString(StringBuffer buf, String inbuf, int base,
			int displayBase, int decimalPosition,
			int anomaly, int shiftAnomaly, boolean carryAnomaly,
			int anomalySq, int shiftAnomalySq,
			boolean carryAnomalySq, char decimalPoint) {
		StringBuffer fltbuf = new StringBuffer("");
		int i, last, place = -1, decimalPlace, decimalPointPlace, mult;
		boolean negative, gotDecimal = false;
		BigInteger intPart = BigInteger.ZERO;
		BigInteger floatPart = BigInteger.ZERO, tmpPower;

		if (debug)
			System.out.println("convertString: inbuf " + inbuf);
		last = inbuf.length();
		negative = (inbuf.charAt(0) == '-');
		for (i = (negative) ? 1 : 0; i < last; i++) {
			if (inbuf.charAt(i) == decimalPoint) {
				place = i - 1 - ((negative) ? 1 : 0);
				break;
			}
		}
		if (place == -1)
			place = last - 1 - ((negative) ? 1 : 0);
		decimalPlace = place;
		decimalPointPlace = last - 2 - ((negative) ? 1 : 0) - place;
		for (i = (negative) ? 1 : 0; i < last; i++) {
			if (inbuf.charAt(i) == decimalPoint) {
				gotDecimal = true;
				place = last - decimalPlace - 2 -
					((negative) ? 1 : 0);
				floatPart = BigInteger.ZERO;
				place--;
			} else if (gotDecimal) {
				mult = char2Int(inbuf.charAt(i));
				tmpPower = multPower(mult, base, place);
				if (tmpPower.compareTo(BigInteger.ZERO) < 0) {
					floatPart = BigInteger.ZERO;
					break;
				}
				floatPart = floatPart.add(tmpPower);
				place--;
			} else {
				mult = char2Int(inbuf.charAt(i));
				if (place >= shiftAnomaly && anomaly != 0) {
					if (place >= shiftAnomalySq + shiftAnomaly && anomalySq != 0) {
						tmpPower = multPower(mult * (base - anomaly) * (base - anomalySq), base, place - 2);
						if (tmpPower.compareTo(BigInteger.ZERO) < 0) {
							intPart = BigInteger.ZERO;
							floatPart = BigInteger.ZERO;
							break;
						}
						intPart = intPart.add(tmpPower);
						if (carryAnomalySq &&
							place == shiftAnomaly + shiftAnomalySq) {
							tmpPower = multPower((base - anomaly) * anomalySq, base,
								shiftAnomaly + shiftAnomalySq - 2);
							if (tmpPower.compareTo(BigInteger.ZERO) < 0) {
								intPart = BigInteger.ZERO;
								floatPart = BigInteger.ZERO;
								break;
							}
							intPart = intPart.add(tmpPower);
						}
					} else {
						tmpPower = multPower(mult * (base - anomaly), base, place - 1);
						if (tmpPower.compareTo(BigInteger.ZERO) < 0) {
							intPart = BigInteger.ZERO;
							floatPart = BigInteger.ZERO;
							break;
						}
						intPart = intPart.add(tmpPower);
						if (carryAnomaly && place == shiftAnomaly) {
							tmpPower = multPower(anomaly, base, shiftAnomaly - 1);
							if (tmpPower.compareTo(BigInteger.ZERO) < 0) {
								intPart = BigInteger.ZERO;
								floatPart = BigInteger.ZERO;
								break;
							}
							intPart = intPart.add(tmpPower);
						}
					}
				} else {
					tmpPower = multPower(mult, base, place);
					if (tmpPower.compareTo(BigInteger.ZERO) < 0) {
						intPart = BigInteger.ZERO;
						floatPart = BigInteger.ZERO;
						break;
					}
					intPart = intPart.add(tmpPower);
				}
				place--;
			}
		}
		if (debug)
			System.out.println("convertString: intPart " +
				intPart + ", floatPart " + floatPart);
		int2String(buf, intPart, displayBase, negative);
		flt2String(fltbuf, floatPart, base, displayBase,
			decimalPointPlace, decimalPosition);
		buf.append(".");
		buf.append(fltbuf);
		if (debug)
			System.out.println("convertString: outbuf " + buf);
	}

	public static int sizeofRoman(int base, boolean romanNumerals,
			boolean ancientRoman) {
		int romanWidth;

		if (!romanNumerals)
			return 0;
		romanWidth = ((base < 8) ? base : base / 2 - 1) *
			((sizeRoman + 1) / 2);
		romanWidth *= ((ancientRoman) ? 6 : 1);
		return romanWidth + 12 + MAX_ROMANFRACT;
	}

	public static void romanFraction(StringBuffer buf, int base, int number,
			int subnumber, int subbase, boolean latin) {
		boolean gotFraction = false;
		int halfBase = subbase / 2;
		int fraction = number, subfraction = subnumber;

		buf.setLength(0);
		fraction %= base;
		if (fraction == 1 && subfraction >= halfBase) {
			subfraction -= halfBase;
			if (latin) {
				buf.append(halftwelfthStrings[1]);
				gotFraction = true;
			} else {
				buf.append(halftwelfthGlyphs[1]);
			}
		} else if (fraction > 0) {
			if (latin) {
				buf.append(twelfthStrings[fraction *
					ROMANFRACTIONBASE / base]);
				gotFraction = true;
			} else {
				buf.append(twelfthGlyphs[fraction *
					ROMANFRACTIONBASE / base]);
			}
		}
		if (subfraction >= halfBase) {
			subfraction -= halfBase;
			if (latin) {
				if (gotFraction)
					buf.append(" ");
				buf.append(halftwelfthStrings[0]);
				gotFraction = true;
			} else {
				buf.append(halftwelfthGlyphs[0]);
			}
		}
		if (subfraction != 0) {
			if (latin) {
				if (gotFraction)
					buf.append(" ");
				if (subbase == 8)
					buf.append(subeighthStrings[subfraction]);
				else
					buf.append(subtwelfthStrings[subfraction]);
			} else {
				if (subbase == 8)
					buf.append(subeighthGlyphs[subfraction]);
				else
					buf.append(subtwelfthGlyphs[subfraction]);
			}
		}
	}

	int append(StringBuffer buf, int position, int place, boolean ancient) {
		if (ancient) {
			buf.insert(position, oldRoman[place]);
			return oldRoman[place].length();
		}
		buf.insert(position, roman[place]);
		return 1;
	}

	public int string2Roman(StringBuffer buf, String inbuf, int base,
			int pieces, int number, int subnumber,
			int subbase, char decimalPoint,
			boolean ancientRoman, boolean latin) {
		int i = 0, position = 0, digit, last, j;
		int loga = (sizeRoman + 1) / 2;

		buf.insert(position, '[');
		position++;
		if (inbuf.charAt(i) == '-') {
			buf.insert(position, ']');
			return 0;
		}
		last = inbuf.length();
		for (i = 0; i < last; i++) {
			if (inbuf.charAt(i) == decimalPoint) {
				break;
			}
		}
		last = i;
		i = 0;
		digit = char2Int(inbuf.charAt(i));
		if (last > loga || (last == loga && digit >=
				((base >= 8) ? base / 2 - 1 : base))) {
			buf.append("?]");
			return 0;
		}
		for (i = 0; i < last; i++) {
			int romanChar = 2 * (last - i) - 1;

			digit = char2Int(inbuf.charAt(i));
			/* IX */
			if (!ancientRoman && digit >= base + 1 - base / 4 &&
					base >= 8) {
				for (j = 0; j < base - digit; j++) {
					position += append(buf, position,
						romanChar - 1, ancientRoman);
				}
				position += append(buf, position,
					romanChar + 1, ancientRoman);
			/* VI */
			} else if (digit >= (base + 1) / 2 && base >= 4) {
				position += append(buf, position,
					romanChar, ancientRoman);
				digit = digit - (base + 1) / 2;
				while (digit > 0) {
					position += append(buf, position,
						romanChar - 1, ancientRoman);
					digit--;
				}
			/* IV */
			} else if (!ancientRoman && digit >= (base + 1) / 2 + 1 - base / 4 &&
					base >= 8) {
				for (j = 0; j < (base + 1) / 2 - digit; j++) {
					position += append(buf, position,
						romanChar - 1, ancientRoman);
				}
				position += append(buf, position,
					romanChar, ancientRoman);
			/* I */
			} else {
				while (digit > 0) {
					position += append(buf, position,
						romanChar - 1, ancientRoman);
					digit--;
				}
			}
		}
		if (pieces > 0 && ROMANFRACTIONBASE % pieces == 0) {
			StringBuffer fractbuf = new StringBuffer();
			int subfraction = subnumber;

			if (pieces != ROMANFRACTIONBASE)
				subfraction = 0; /* words not scalable */
			if (latin && position > 1 &&
					(number != 0 || subfraction != 0))
				buf.append(" ");
			romanFraction(fractbuf, pieces, number, subfraction,
				subbase, latin);
			buf.append(fractbuf);
		}
		buf.append("]");
		return 0;
	}

	/* not used */
	public int int2Roman(StringBuffer buf, int arabic) {
		int i = 0, position = 0, digit;
		int loga = (sizeRoman + 1) / 2;
		int place = MAX_ROMAN_DIGIT;
		int base = AbacusInterface.DEFAULT_BASE;
		int base2 = (base + 1) / 2;
		int remain = arabic;

		buf.setLength(0);
		buf.setCharAt(position, '[');
		position++;
		if (remain < 0 || remain > MAX_ROMAN) {
			buf.setCharAt(position, ']');
			return 0;
		}
		for (i = 0; i < loga; i++) {
			int romanChar = 2 * (loga - i) - 1;

			digit = remain / place;
			remain -= digit * place;
			if (digit >= base - 1) {
				buf.setCharAt(position, roman[romanChar - 1]);
				position++;
			} else if (digit >= base2) {
				buf.setCharAt(position, roman[romanChar]);
				position++;
				digit = digit - base2;
				while (digit > 0) {
					buf.setCharAt(position,
						roman[romanChar - 1]);
					position++;
					digit--;
				}
			} else if (digit == base2 - 1) {
				buf.setCharAt(position, roman[romanChar - 1]);
				position++;
				buf.setCharAt(position, roman[romanChar]);
				position++;
			} else {
				while (digit > 0) {
					buf.setCharAt(position,
						roman[romanChar - 1]);
					position++;
					digit--;
				}
			}
			place /= base;
		}
		buf.setCharAt(position, ']');
		return 0;
	}

	public static String string2Group(String inbuf, int groupSize,
			char decimalPoint, char groupSeparator) {
		StringBuffer buf = new StringBuffer();
		int i, j, len;

		for (i = 0; i < inbuf.length(); i++) {
			if (inbuf.charAt(i) == ' ' ||
					inbuf.charAt(i) == decimalPoint) {
				break;
			}
		}
		len = i;
		j = 0;
		buf.setLength(3 * inbuf.length() / 2);
		if (inbuf.charAt(j) == '-' || inbuf.charAt(j) == '+') {
			buf.setCharAt(j, inbuf.charAt(0));
			j = 1;
		}
		for (i = j; i < len - 1; i++) {
			buf.setCharAt(j, inbuf.charAt(i));
			if ((len - i + groupSize - 1) % groupSize == 0) {
				j++;
				buf.setCharAt(j, groupSeparator);
			}
			j++;
		}
		buf.setLength(j);
		buf.append(inbuf.substring(len - 1, inbuf.length()));
		return buf.toString();
	}
}
