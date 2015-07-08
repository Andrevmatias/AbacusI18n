package org.tux.bagleyd.abacus;

/*
 * @(#)AbacusCalc.java
 *
 * Copyright 1992 - 2014  David A. Bagley, bagleyd@tux.org
 * Taken from a C++ group project where I was a lead contributer
 * OOP Group4!
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

import java.util.Stack;
import java.math.BigDecimal;

import org.tux.bagleyd.util.Functions;

/**
 * The <code>AbacusCalc</code> class is the infix calculator for
 * <code>AbacusApplet</code> class.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */

/* proposed, implemented only a small portion
Letter	Meaning
------	-------
<Interrupt (^C etx 03)> kill program, or turn off calculator [quit]
<Back space (^H bs 010)> delete last digit [numeric]
<Line feed (^J lf 012), Carriage return (^M cr 015), Space (040)> [ignore]
!	factorial
"
#	number of values for statistics
$	summation
%	mod e.g. 7%2=1 [medium]
&	bitwise and [medium]
'
(	( [equate]
)	) [equate]
*	multipication [medium]
+	addition [low]
,	[numeric]
-	subtraction (not sign change) [low]
.	decimal point (or octal pt if in octal, etc.) [numeric]
/	divide e.g. 1/2=0.5 [medium]
0	0 [numeric]
1	1 [numeric]
2	2 [numeric]
3	3 [numeric]
4	4 [numeric]
5	5 [numeric]
6	6 [numeric]
7	7 [numeric]
8	8 [numeric]
9	9 [numeric]
:
;
<	bitwise shift left [high]
=	= [equate]
>	bitwise shift right [high]
?	data input for statistics
@	average
A	hexadecimal 10 [numeric]
B	hexadecimal 11 [numeric]
C	hexadecimal 12 [numeric]
D	hexadecimal 13 [numeric]
E	hexadecimal 14 [numeric]
F	hexadecimal 15 [numeric]
G	16 [numeric]
H	17 [numeric]
I	18 [numeric]
J	19 [numeric]
K
L	logarithm base 2
M	memory recall ('M', ('0'-'9' | 'A'-'F'))
N	natural logarithm
O	bitwise xor [high]
P	permutation [low]
Q	quit [quit]
R	"to the root of" e.g. 8R3=2 [high]
S	sample variance (s^2)
T	square summation
U	x^3
V	x^2
W	convert from MinSec
X	e^x (unfortunately 'e' & 'E' are used already)
Y	hyperbolic
Z	inverse
[
\	Pascal's div i.e 28\8=3 [medium]
]
^	"power of" e.g. 2^3=8 [high]
_	reset statistics
`
a	clear almost all [clear]
b	base mode ('b', (['2'-'9'] | '1',['0'-'6']))
c	combination [low]
d	decimal hot key (also 'b', '1', '0')
e	used for exponents e.g. 6.02*10^22 = 6.02e22(input) = 6.02 22(output)
f
g	gradient mode
h	hexadecimal hot key (also 'b', '1', '6')
i	invert 1/x
j	variance (sigma^2)
k	cosine (kosine)
l	logarithm base 10
m	memory ('m', ('0'-'9' | 'A'-'F'))
n	bitwise negation
o	degree mode
p	pi
q	clear everything (quit all calulations) [clear]
r	radian mode
s	sine
t	tangent
u	cube root
v	unary operation square root (v-) UNIX's dc & bc use this also
w	convert to MinSec
x	complex mode (base 10 only)
y
z	clear (zero) [clear]
{
|	bitwise or [low]
}
~	negate +/- [numeric (and also unary)]
*/

public class AbacusCalc {

	static final int notused = 0;
	static final int low = 1;
	static final int medium = 2;
	static final int high = 3;
	static final int unary = 4;
	static final int mode = 5;
	static final int constant = 6;
	static final int equate = 7;

	int orderASCII[] = {
	   notused,  notused,  notused,  notused,
	   notused,  notused,  notused,  notused,
	   notused,  notused,  notused,  notused,
	   notused,  notused,  notused,  notused,
	   notused,  notused,  notused,  notused,
	   notused,  notused,  notused,  notused,
	   notused,  notused,  notused,  notused,
	   notused,  notused,  notused,  notused,

	   notused,    unary,  notused, constant, /* Space ! " # */
	  constant,   medium,   medium,  notused, /* $ % & ' */
	    equate,   equate,   medium,      low, /* ( ) * + */
	   notused,      low,  notused,   medium, /* , - . / */
	   notused,  notused,  notused,  notused, /* 0 1 2 3 */
	   notused,  notused,  notused,  notused, /* 4 5 6 7 */
	   notused,  notused,  notused,  notused, /* 8 9 : ; */
	      high,   equate,     high,    unary, /* < = > ? */


	  constant,  notused,  notused,  notused, /* @ A B C */
	   notused,  notused,  notused,  notused, /* D E F G */
	   notused,  notused,  notused,  notused, /* H I J K */
	     unary, constant,    unary,     high, /* L M N O */
	       low,  notused,     high, constant, /* P Q R S */
	  constant,    unary,    unary,    unary, /* T U V W */
	     unary,     mode,     mode,  notused, /* X Y Z [ */
	    medium,  notused,     high,     mode, /* \ ] ^ _ */

	   notused,  notused,     mode,      low, /* ` a b c */
	      mode, constant,  notused,     mode, /* d e f g */
	      mode,    unary, constant,    unary, /* h i j k */
	     unary,     mode,    unary,     mode, /* l m n o */
	  constant,  notused,     mode,    unary, /* p q r s */
	     unary,    unary,    unary,    unary, /* t u v w */
	      mode,  notused,  notused,  notused, /* x y z { */
	       low,  notused,    unary,  notused  /* | } ~ Delete */
	};

	static final int undefined = -1;
	static final int ignore = 0;
	static final int numeric = 1;
	static final int operation = 2;
	static final int clear = 3;
	static final int quit = 4;
	int operationASCII[] = {
	  undefined, undefined, undefined,      quit, /* Null SOH STX Interrupt */
	  undefined, undefined, undefined, undefined, /* EOT ENQ ACK Bell */
	    numeric, undefined,    ignore, undefined, /* Backspace Tab Linefeed VT */
	  undefined,    ignore, undefined, undefined, /* Formfeed Carriagereturn SO SI */
	  undefined, undefined, undefined, undefined, /* DLE DC1 DC2 DC3 */
	  undefined, undefined, undefined, undefined, /* DC4 NAK SYN ETB */
	  undefined, undefined, undefined, undefined, /* CAN EM SUB Escape */
	  undefined, undefined, undefined, undefined, /* FS GS RS US */

	     ignore, operation, undefined, operation, /* Space ! " # */
	  operation, operation, operation, undefined, /* $ % & ' */
	  operation, operation, operation, operation, /* ( ) * + */
	    numeric, operation,   numeric, operation, /* , - . / */
	    numeric,   numeric,   numeric,   numeric, /* 0 1 2 3 */
	    numeric,   numeric,   numeric,   numeric, /* 4 5 6 7 */
	    numeric,   numeric, undefined, undefined, /* 8 9 : ; */
	  operation, operation, operation, operation, /* < = > ? */

	  operation,   numeric,   numeric,   numeric, /* @ A B C */
	    numeric,   numeric,   numeric,   numeric, /* D E F G */
	    numeric,   numeric,   numeric, undefined, /* H I J K */
	  operation, operation, operation, operation, /* L M N O */
	  operation,      quit, operation, operation, /* P Q R S */
	  operation, operation, operation, operation, /* T U V W */
	  operation, undefined, undefined, undefined, /* X Y Z [ */
	  operation, undefined, operation, operation, /* \ ] ^ _ */

	  undefined,     clear, operation, operation, /* ` a b c */
	  operation, operation, undefined, operation, /* d e f g */
	  operation, operation, operation, operation, /* h i j k */
	  operation, operation, operation, operation, /* l m n o */
	  operation,     clear, operation, operation, /* p q r s */
	  operation, operation, operation, operation, /* t u v w */
	  operation, undefined,     clear, undefined, /* x y z { */
	  operation, undefined,   numeric, undefined  /* | } ~ Delete */
	};

	public static int CHAR_TO_DIGIT(char c) {
		return ((c >= 'A') ? c - 'A' + 10 : c - '0');
	}
	public static char DIGIT_TO_CHAR(int d) {
		return ((d >= 10) ? (char) ('A' + d - 10) : (char) ('0' + d));
	}

	/* Calculation support up to base 20, which historically,
	   was the maximum base used.  Babylonians used 60, but was a
	   conglomeration of base 10 and 6. */
	public static boolean IS_DIGIT(char c) {
		return ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'J'));
	}

	public static boolean IS_NUMBER(char c) {
		return (IS_DIGIT(c) || c == decimalPoint);
	}

	/* true; for period, +/-, or operator */
	public static boolean IS_VALID(char v, int b) {
		return ((IS_DIGIT(v)) ? (CHAR_TO_DIGIT(v) < b) : true);
	}
	static final int MAX_VALUE_LENGTH = 64;

	static final boolean paren = false;
	static final boolean order = true;

	static final int DEFAULT_BASE = 10;
	static final int DEFAULT_DECIMAL_DIGITS = 16;
	static final boolean debug = false;
	//static final boolean debug = true;

	Abacus myAbacus;

	CalcTerm term = new CalcTerm();
	CalcExpression expression = new CalcExpression();

	/* Inputter & Parser & Paren Manager */
	boolean period = false;
	boolean gotFirstDigit = false; /* handles special case of just a '0' */
	boolean negateNext = false; /* handles unary '-' */
	BigDecimal left, right; /* sides of a binary operation */
	char pendingOperation; /* operation to be performed on left (& right if not unary) */
	boolean hub; /* intermediate result? usually a ')' or pi pressed */
	StringBuffer memoryBuf = new StringBuffer(64);
	StringBuffer displayBuf = new StringBuffer(64);
	int digits = 1;
	int nestingLevel = 0;
	static char decimalPoint = '.';
	static char groupSymbol = ',';

	/* expression manipulation */
	BigDecimal myLeft;
	char binaryOperation;

	public AbacusCalc(Abacus abacus) {
		myAbacus = abacus;
	}

	/* -inf < x < inf */
	/*private static double cbrt(double x) {
		return ((x < 0.0) ? -pow(-x, 1.0 / 3.0) : pow(x, 1.0 / 3.0));
	}*/

	private static boolean isIntValue(BigDecimal arg) {
		try {
			arg.intValueExact();
			return true;
		} catch (ArithmeticException e) {
			return false;
		}
	}

	private BigDecimal powInt(int base, int y) {
		int i;
		BigDecimal z = BigDecimal.valueOf(1);

		if (y == 0)
			return z;
		if (y > 0)
			for (i = 0; i < y; i++) { // *=
				z = z.multiply(BigDecimal.valueOf(base));
			}
		else /* if (y < 0) */
			for (i = 0; i > y; i--) // /=
				z = z.divide(BigDecimal.valueOf(base),
					myAbacus.getDecimalPosition(),
					BigDecimal.ROUND_DOWN);
		return z;
	}

	/* More exact (int) (log(x) / log(base)) */
	private int logInt(BigDecimal x, int base) {
		BigDecimal quotient = x;
		int i = 0;

		if (quotient.compareTo(BigDecimal.valueOf(base)) >= 0)
			while (quotient.compareTo(BigDecimal.valueOf(base)) >= 0) {
				quotient = quotient.divide(BigDecimal.valueOf(base),
					myAbacus.getDecimalPosition(),
					BigDecimal.ROUND_DOWN);
				i++;
			}
		else if (quotient.compareTo(BigDecimal.ZERO) > 0)
			while (quotient.compareTo(BigDecimal.ONE) < 0) {
				quotient = quotient.multiply(BigDecimal.valueOf(base));
				i--;
			}
		return i;
	}

	/* private static double rootInt(double x, int y) {
		// y != 0 && (x >= 0 || (y odd))
		if (x < 0.0 && 2 * (y / 2) != y)
			return -Math.pow(-x, 1.0 / y);
		else if (x <= 0.0)
			return 0.0;
		else
			return Math.pow(x, 1.0 / y);
	} */

	public BigDecimal convertToDecimal(int base, String inputString) {
		int k = 0;
		boolean negative = false;
		int digit;
		int length = 0;
		BigDecimal number = BigDecimal.ZERO;
		BigDecimal factor;

		/* Convert Integer Part */
		k = 0;
		if (inputString.charAt(k) == '-') {
			negative = true;
			k++;
		}
		while (k + length < inputString.length() &&
				IS_DIGIT(inputString.charAt(k + length))) {
			length++;
		}
		factor = powInt(base, length);
		for (; length > 0; length--, k++) {
			digit = CHAR_TO_DIGIT(inputString.charAt(k));
			factor = factor.divide(BigDecimal.valueOf(base),
				myAbacus.getDecimalPosition(),
				BigDecimal.ROUND_DOWN);
			number = number.add(factor.multiply(BigDecimal.valueOf(digit)));
		}

		/* Convert Fractional Part */
		if (k < inputString.length() &&
				inputString.charAt(k) == decimalPoint) {
			k++;
			while (k < inputString.length() &&
					IS_DIGIT(inputString.charAt(k))) {
				digit = CHAR_TO_DIGIT(inputString.charAt(k));
				factor = factor.divide(BigDecimal.valueOf(base),
					myAbacus.getDecimalPosition(),
					BigDecimal.ROUND_DOWN);
				number = number.add(factor.multiply(BigDecimal.valueOf(digit)));
				k++;
			}
		}

		if (negative)
			number = number.negate();
		negative = false;
		return number;
	}

	public void convertFromDecimal(StringBuffer outputString,
			int base, BigDecimal x, boolean fraction) {
		String string; /* [MAX_VALUE_LENGTH] */
		boolean localPeriod = false;
		int placesBase;
		int l = 0, i = 0;
		int digit;
		int fractDigits = logInt(powInt(DEFAULT_BASE,
			DEFAULT_DECIMAL_DIGITS), base) +
			myAbacus.getDecimalPosition();
		BigDecimal number = x;
		BigDecimal factor;

		if (debug)
			System.out.println("x =" + x);
		outputString.setLength(0);
		string = String.valueOf(number);
		if (string.charAt(i) == '-') {
			outputString.append('-');
			l++;
			number = number.negate();
		}
		while (i < string.length()) {
			if (string.charAt(i) == '.') /* DECIMAL_POINT LOCALE C */
				localPeriod = true;
			i++;
		}
		{
			/* Chicken and egg problem:
			   rounding might increase placesBase */
			placesBase = logInt(number, base);
			fractDigits -= (placesBase + 1);
			/* rounder */
			//number = number.add((BigDecimal.valueOf(base)).pow(-fractDigits).divide(BigDecimal.valueOf(2),
			//	BigDecimal.ROUND_DOWN)); /* Convert Integer Part */
			number = number.add(BigDecimal.valueOf(Math.pow(base, -fractDigits)));
			if (number.compareTo(BigDecimal.ONE) < 0) {
				outputString.append('0');
				l++;
				factor = BigDecimal.ONE.divide(BigDecimal.valueOf(base),
					fractDigits,
					BigDecimal.ROUND_DOWN);
			} else {
				placesBase = logInt(number, base);
				factor = BigDecimal.valueOf(Math.pow(base, placesBase));
				placesBase++; /* allow one more for possible */
				factor = factor.multiply(BigDecimal.valueOf(base)); /* rounding error in logInt */
				//if (factor.compareTo(BigDecimal.ZERO) != 0) {
				try {
				    for (; placesBase >= 0; placesBase--) {
					digit = (number.divide(factor,
						fractDigits,
						BigDecimal.ROUND_DOWN)).intValue();
					outputString.append(
						DIGIT_TO_CHAR(digit));
					l++;
					number = number.subtract(factor.multiply(BigDecimal.valueOf(digit)));
					factor = factor.divide(BigDecimal.valueOf(base),
						fractDigits,
						BigDecimal.ROUND_DOWN);
				    }
				} catch (ArithmeticException e) {
					//outputString = new StringBuffer("0.0");
				}
			}
			/* Convert Fractional Part */
			if (localPeriod && fraction) {
				outputString.append(decimalPoint);
				l++;
				try {
				    for (placesBase = 1;
						placesBase <= fractDigits;
						placesBase++) {
					digit = (number.divide(factor,
						fractDigits,
						BigDecimal.ROUND_DOWN)).intValue();
					outputString.append(
						DIGIT_TO_CHAR(digit));
					l++;
					number = number.subtract(factor.multiply(BigDecimal.valueOf(digit)));
					factor = factor.divide(BigDecimal.valueOf(base),
						fractDigits,
						BigDecimal.ROUND_DOWN);
				    }
				} catch (ArithmeticException e) {
					//outputString = new StringBuffer("0.0");
				}
				while (outputString.charAt(l - 1) == '0')
					l--;
			}
		}
		if (!localPeriod) {
			outputString.append('.');
			l++;
		}
		outputString.setLength(l);
		if (debug)
			System.out.println("outputString =" + outputString);
	}

	private BigDecimal formatFromDisplay(int base, String string) {
		StringBuffer floatString = new StringBuffer(MAX_VALUE_LENGTH);
		boolean got1Digit = false;
		int periods = 0;
		int s = 0, k = 0;

		while (s < string.length()) { /* Look at each input character */
			if (IS_DIGIT(string.charAt(s))) {
				got1Digit = true;
				floatString.append(string.charAt(s));
				k++;
			} else if (string.charAt(s) == decimalPoint) {
				if (periods == 1)
					System.out.println(
						string.charAt(s) +
						" handler not implemented in Format_From_Display");
				periods++;
				if (!got1Digit) {
					got1Digit = true;
					floatString.append('0');
					k++;
				}
				floatString.append(decimalPoint);
				k++;
			} else if (string.charAt(s) == '-') {
				floatString.append('-');
				k++;
			} else {
				System.out.println(
					string.charAt(s) +
					" handler not implemented in Format_From_Display");
			}
			s++;
		}
		if (!got1Digit) {
			got1Digit = true;
			floatString.append('0');
			k++;
		}
		floatString.setLength(k);
		return convertToDecimal(base, floatString.toString());
	}

	private void formatToDisplay(StringBuffer string, BigDecimal z,
			int base) {
		int i = 0, j, length;
		int periods = 0;

		convertFromDecimal(string, base, z, true);
		length = string.length();
		while (i <= length) {
			if (i == string.length() && periods == 0) {
				j = length;
				if (j >= i) {
					string.append(string.charAt(j));
					j--;
				}
				while (j >= i) {
					string.setCharAt(j + 1,
						string.charAt(j));
					j--;
				}
				length++;
				string.setCharAt(i++, decimalPoint);
				periods++;
			}
			if (i < string.length() &&
					string.charAt(i) == decimalPoint)
				periods++;
			if (i < string.length() && string.charAt(i) == '+') {
				j = i;
				while (j < length) {
					string.setCharAt(j,
						string.charAt(j + 1));
					j++;
				}
				length--;
			} else
				i++;
		}
		string.setLength(length);
	}

	private void reset() {
		period = false;
		gotFirstDigit = false;
		digits = 1;
		memoryBuf = new StringBuffer("0");
	}

	private static void setDecimalComma(boolean dpc) {
		if (dpc) {
			decimalPoint = ',';
			groupSymbol = '.';
		} else {
			decimalPoint = '.';
			groupSymbol = ',';
		}
	}

	/* private int getNestingLevel() {
		return nestingLevel;
	} */

	private void incNestingLevel() {
		nestingLevel++;
	}

	private void decNestingLevel() {
		nestingLevel--;
		if (nestingLevel < 0) /* Ignore extra right parentheses */
			nestingLevel = 0;
	}

	private void resetNestingLevel() {
		nestingLevel = 0;
	}

	private BigDecimal evaluateSingle(BigDecimal arg,
			char unaryOperation) {
		switch(unaryOperation) {
		case 'i':
			if (arg.compareTo(BigDecimal.ZERO) == 0) {
				return BigDecimal.ZERO;
			}
			return BigDecimal.ONE.divide(arg,
				myAbacus.getDecimalPosition(),
				BigDecimal.ROUND_DOWN);
		case '!':
			if ((arg.compareTo(BigDecimal.ZERO) < 0 &&
					isIntValue(arg)) ||
					(arg.compareTo(BigDecimal.valueOf(20)) > 0)) {
				/* this does not catch all the errors */
				return BigDecimal.ZERO;
			} else if (isIntValue(arg)) {
				int i;
				BigDecimal j;

				if (arg.compareTo(BigDecimal.ZERO) < 0 ||
						arg.compareTo(BigDecimal.valueOf(20)) > 0) {
					return BigDecimal.ZERO;
				}
				j = BigDecimal.ONE;
				for (i = 2; i <= arg.intValue(); i++) {
					j = j.multiply(BigDecimal.valueOf(i));
				}
				return j;
			} else {
				return BigDecimal.valueOf(Functions.fgamma(arg.doubleValue() + 1.0));
			}
		case 'v':
			if (arg.compareTo(BigDecimal.ZERO) <= 0) {
				return BigDecimal.ZERO;
			}
			return BigDecimal.valueOf(Math.sqrt(arg.doubleValue()));
		case 'u':
			// Java 1.5
			return BigDecimal.valueOf(Math.cbrt(arg.doubleValue()));
			//return ((arg < 0.0) ? -Math.pow(-arg, 1.0 / 3.0) :
			//	Math.pow(arg, 1.0 / 3.0));
		default:
			return BigDecimal.ZERO;
		}
	}

	private BigDecimal evaluateBigDecimal(BigDecimal arg1, char binaryOp,
			BigDecimal arg2) {
		BigDecimal comp = BigDecimal.ZERO;

		switch(binaryOp) {
		case '+':
			comp = arg1.add(arg2);
			break;
		case '-':
			comp = arg1.subtract(arg2);
			break;
		case '*':
			comp = arg1.multiply(arg2);
			break;
		case '/':
			if (arg2.compareTo(BigDecimal.valueOf(0)) == 0)
				return BigDecimal.ZERO;
			comp = arg1.divide(arg2,
				myAbacus.getDecimalPosition(),
				BigDecimal.ROUND_DOWN);
			break;
		case '^':
			if (isIntValue(arg2)) {
				comp = arg1.pow(arg2.intValue());
			} else {
				comp = BigDecimal.valueOf(Math.pow(arg1.doubleValue(),
					arg2.doubleValue()));
			}
			break;
		default:
			return BigDecimal.ZERO;
		}
		return comp;
	}

	private boolean previousOrder() {
		return (!expression.madeExpressions() &&
			term.readOrderUsage() == order);
	}

	private boolean canReduceExpression(char binaryOp) {
		return (previousOrder() &&
			orderASCII[term.readOperation()] >=
			orderASCII[binaryOp]);
	}

	private void getPreviousExpressionPart() {
		CalcTerm tempTerm = new CalcTerm();

		tempTerm.getTerm();
		myLeft = tempTerm.readVariable();
		binaryOperation = tempTerm.readOperation();
	}

	private boolean storeExpressionParen() {
		CalcTerm tempTerm = new CalcTerm();

		tempTerm.setTerm(myLeft, binaryOperation, paren);
		return true;
	}

	private void evaluateExpressionPart(BigDecimal myRight) {
		if (binaryOperation == '\0')
			myLeft = myRight;
		else
			myLeft = evaluateBigDecimal(myLeft, binaryOperation,
				myRight);
	}

	private boolean evaluateExpressionOrder(char newOperation) {
		if (binaryOperation == '\0') {
			left = right;
		} else {
			if (orderASCII[binaryOperation] >=
					orderASCII[newOperation]) {
				left = evaluateBigDecimal(left,
						binaryOperation, right);
				while (canReduceExpression(newOperation)) {
					right = left;
					getPreviousExpressionPart();
					evaluateExpressionPart(right);
				}
			} else {
				CalcTerm tempTerm = new CalcTerm();

				tempTerm.setTerm(myLeft, binaryOperation, order);
				left = right;
				return true;
			}
		}
		return true;
	}

	private void evaluateExpressionParen() {
		BigDecimal myRight;

		while (expression.madeExpressions() &&
				term.readOrderUsage() == order) {
			myRight = myLeft;
			getPreviousExpressionPart();
			evaluateExpressionPart(myRight);
		}
	}

	private void evaluateExpression() {
		BigDecimal myRight;

		while (expression.madeExpressions()) {
			myRight = myLeft;
			getPreviousExpressionPart();
			if (binaryOperation == '\0')
				myLeft = myRight;
			else
				myLeft = evaluateBigDecimal(myLeft,
					binaryOperation, myRight);
		} /* ignore uneven "(()" */
		binaryOperation = '\0';
	}

	/* private void initParser() {
		left = BigDecimal.ZERO;
		right = BigDecimal.ZERO;
		pendingOperation = '\0';
		hub = false;
	} */

	/* private void resetRight() {
		right = BigDecimal.ZERO;
		hub = true;
	} */

	private void resetExpression() {
		right = left = BigDecimal.ZERO;
		pendingOperation = '\0';
		hub = false;
		expression.flushExpressions();
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

	/* Contract StringBuffer to remove leading and trailing 0's */
	static void contractStringBuffer(StringBuffer string, int decimalPosition) {
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
				if (string.charAt(i) == '0' || i > decimalPosition + offset)
					string.deleteCharAt(i);
				else
					break;
			}
		if (string.length() > 0 && string.charAt(0) == '.') /* normalize */
			string.insert(0, '0');
		length = string.length();
		if (string.charAt(length - 1) == '.')
			string.deleteCharAt(length - 1);
	}

	private void parse(BigDecimal input, boolean gotNumeral,
			char newOperation) {
		boolean numeral = gotNumeral;

		if (numeral) {
			if (negateNext) {
				negateNext = false;
				right = input.negate();
				pendingOperation = '\0';
			} else {
				right = input;
			}
		} else if (hub) {
			numeral = true;
			hub = false;
		}
		if (orderASCII[newOperation] == equate) {
			switch (newOperation) {
			case '(':
				incNestingLevel();
				myLeft = left;
				binaryOperation = pendingOperation;
				storeExpressionParen();
				left = BigDecimal.ZERO;
				pendingOperation = '\0';
				right = left; /* "#(" # is lost forever */
				break;
			case ')': /* Evaluate last term and restore variable and operation. */
				decNestingLevel();
				if (pendingOperation != '\0' && numeral) /* this stops 4+) = 8 */
					left = evaluateBigDecimal(left, pendingOperation, right);
				if (pendingOperation == '\0' && numeral)
					left = right;
				else {
					myLeft = left;
					binaryOperation = pendingOperation;
					evaluateExpressionParen();
					pendingOperation = '\0';
					right = left;
					if (expression.madeExpressions()) {
						/* ignore uneven "())" */
						myLeft = left;
						binaryOperation = pendingOperation;
						getPreviousExpressionPart();
						hub = true;
					}
				}
				break;
			case '=':
				/* Evaluate term and restore variable and operation. */
				resetNestingLevel();
				if (pendingOperation != '\0' && numeral) {
					/* this stops 4+= = 8 (2+2) */
					left = evaluateBigDecimal(left,
						pendingOperation, right);
				}
				if (pendingOperation == '\0' && numeral) {
					left = right;
				} else {
					myLeft = left;
					binaryOperation = pendingOperation;
					evaluateExpression();
					right = left;
				}
				break;
			default:
				resetExpression();
			}
		} else if (orderASCII[newOperation] == constant) {
			switch (newOperation) {
			case 'e':	/* natural logarithm */
				right = BigDecimal.valueOf(Math.E);
				break;
			case 'p':	/* pi */
				right = BigDecimal.valueOf(Math.PI);
				break;
			default:
				resetExpression();
			}
			hub = true;
		} else if (orderASCII[newOperation] == unary) {
			right = evaluateSingle(right, newOperation);
			if (pendingOperation == '\0')
				left = right;
			hub = true;
		} else if (orderASCII[newOperation] == notused) {
			resetExpression();
		} else {
			/* binary operation */
			if (numeral) {
				myLeft = left;
				binaryOperation = pendingOperation;
				evaluateExpressionOrder(newOperation);
				right = left;
			} else {
				if (previousOrder()) /* this stops 2*+ = 6 (2*2+2) */ {
					myLeft = left;
					binaryOperation = pendingOperation;
					getPreviousExpressionPart();
					evaluateExpressionOrder(newOperation);
					right = left;
				} else if (pendingOperation != '\0') {
					resetExpression();
				} else if (newOperation == '-') {
					negateNext = true;
				}
			}
			pendingOperation = newOperation;
		}
		formatToDisplay(memoryBuf, right, myAbacus.getBase());
		contractStringBuffer(memoryBuf, myAbacus.getDecimalPosition());
		displayBuf = new StringBuffer(memoryBuf.toString());
		/* update display */
	}

	private void inputOperator(char input) {
		if (gotFirstDigit) {
			parse(formatFromDisplay(myAbacus.getBase(),
				memoryBuf.toString()), true, input);
		} else {
			parse(BigDecimal.ZERO, false, input);
		}
		reset();
	}

	private boolean inputNumeric(char input) {
		int i = digits - 1, j;

		if (!IS_VALID(input, myAbacus.getBase())) {
			reset();
			return false;
		}
		if (IS_DIGIT(input)) {
			gotFirstDigit = true;
			if (memoryBuf.charAt(0) == '0' &&
					memoryBuf.length() == 1) {
				memoryBuf.setCharAt(0, input);
			} else {
				memoryBuf.append(input);
				digits++;
			}
		} else if (input == decimalPoint) {
			if (period) {
				reset(); /* Handle multiple decimalPoint */
			}
			period = true;
			memoryBuf.append(input);
			digits++;
		} else if (input == '~') { /* Handle +/- */
			/* Note: if `memoryBuf == "0"' then "+/-" should act
			   as a unary operation, that is, if no number is entered,
			   it should act on the result. */
			if (!gotFirstDigit) {
				inputOperator('~');
				return false;
			}
			if (memoryBuf.charAt(0) == '0' &&
					memoryBuf.length() == 1)
				return false;
			while (memoryBuf.charAt(i) != ' ' &&
					memoryBuf.charAt(i) != '-' &&
				memoryBuf.charAt(i) != ',' && i != 0)
				i--;
			if (memoryBuf.charAt(i) == '-') {
				/* Take a '-' from front of a negative number */
				while (i < memoryBuf.length()) {
					memoryBuf.setCharAt(i,
						memoryBuf.charAt(i + 1));
					i++;
				}
				digits--;
			} else {
				/* Put the '-' in the front of a positive number */
				if (digits == MAX_VALUE_LENGTH - 1)
					return false;
				j = digits;
				while (j >= i) {
					memoryBuf.setCharAt(j + 1,
						memoryBuf.charAt(j));
					j--;
				}
				memoryBuf.setCharAt(i, '-');
				digits++;
			}
		} else {
			reset();
			return false;
		}
		return true;
	}

	public void convertStringToAbacus(String string, int aux) {
		int decimal = 0;
		int num, factor, i, sign = 0, len = string.length();
		Abacus abacus =	myAbacus.getAbacus(aux);

		if (debug) {
			String prefix;
			if (aux == 1)
				prefix = new String("Left Auxiliary> ");
			else if (aux == 2)
				prefix = new String("Right Auxiliary> ");
			else
				prefix = new String("Primary> ");
			System.out.println("convertStringToAbacus: " + prefix + string);
		}
		abacus.clearRails();
		while (decimal < string.length() &&
				string.charAt(decimal) != decimalPoint)
			decimal++;
		if (string.charAt(0) == '-' || string.charAt(0) == '+') {
			sign = 1;
		}
		for (i = 0; i < decimal - sign; i++) {
			/* abacus.getDisplayBase() == abacus.getBase() and all that ... */
			num = AbacusMath.char2Int(string.charAt(
				decimal - 1 - i));
			if (abacus.getBottomNumber() <= abacus.getBase() / 2) {
				factor = num / abacus.getAbacus(aux).getTopFactor();
				if (factor > 0) {
					myAbacus.abacusMove(aux, 1, i, factor);
					num = num - factor * abacus.getTopFactor();
				}
			}
			factor = num / abacus.getBottomFactor();
			if (factor > 0) {
				myAbacus.abacusMove(aux, 0, i, factor);
			}
		}
		if (abacus.getSign() && string.charAt(0) == '-') {
			myAbacus.abacusMove(aux, 0,
				abacus.getRails() - abacus.getDecimalPosition() - 1, 1);
		}
		for (i = 0; i < len - decimal - 1 &&
				i < abacus.getDecimalPosition(); i++) {
			int offset = 0;

			num = AbacusMath.char2Int(string.charAt(
				decimal + i + 1));
			if (abacus.getBottomPiece() != 0)
				offset++;
			if (abacus.getBottomPiecePercent() != 0 &&
					i >= abacus.getShiftPercent())
				offset++;
			if (abacus.getBottomNumber() <= abacus.getBase() / 2) {
				factor = num / abacus.getTopFactor();
				if (factor > 0) {
					myAbacus.abacusMove(aux, 1, -i - 1 - offset, factor);
					num = num - factor * abacus.getTopFactor();
				}
			}
			factor = num / abacus.getBottomFactor();
			if (factor > 0) {
				myAbacus.abacusMove(aux, 0, -i - 1 - offset, factor);
			}
		}
	}

	public void addBackAnomaly(StringBuffer buf, int anomaly, int shift,
			int base) {
		BigDecimal anom;
		BigDecimal factor;
		int shiftValue = (AbacusMath.multPower(1, base, shift)).intValue();
		int anomalyValue = anomaly * shiftValue / base;

		anom = convertToDecimal(base, buf.toString());
		factor = anom.divide(BigDecimal.valueOf(shiftValue - anomalyValue));
		anom = anom.add(factor.multiply(BigDecimal.valueOf(anomalyValue)));
		convertFromDecimal(displayBuf, base, anom, true);
	}

	public static void zeroFractionalPart(StringBuffer buf) {
		int i;

		for (i = 0; i < buf.length(); i++) {
			if (buf.charAt(i) == '.') {
				buf.setLength(i + 1);
				return;
			}
		}
	}

	private StringBuffer convertStringBases(String buf,
			int displayBase, int base) {
		StringBuffer sb = new StringBuffer("");
		int i;

		for (i = 0; i < buf.length(); i++) {
			if (IS_NUMBER(buf.charAt(i))) {
				StringBuffer numberString =
					new StringBuffer("");
				BigDecimal num;

				while (i < buf.length() &&
						IS_NUMBER(buf.charAt(i))) {
					numberString.append(buf.charAt(i));
					i++;
				}
				i--;
				num = convertToDecimal(displayBase,
					numberString.toString());
				convertFromDecimal(numberString, base, num, true);
				sb.append(numberString);
				if (debug)
					System.out.println("ns =" +
						numberString);
			} else {
				sb.append(buf.charAt(i));
			}
		}
		return sb;
	}

	public void calculate(Abacus abacus, String buffer) {
		int i, base = abacus.getBase();
		int displayBase = abacus.getDisplayBase();
		String stringBuf = buffer;

		this.myAbacus = abacus;
		setDecimalComma(abacus.getDecimalComma());
		if (debug)
			System.out.println("buffer =" + buffer);
		if (displayBase != base) {
			stringBuf = new String(convertStringBases(buffer,
				displayBase, base));
			if (debug)
				System.out.println("conv buffer =" + stringBuf);
		}
		resetExpression();
		memoryBuf = new StringBuffer("0");
		memoryBuf.setLength(1);
		displayBuf = new StringBuffer("0");
		displayBuf.setLength(1);
		for (i = 0; i < stringBuf.length(); i++) {
			switch (operationASCII[stringBuf.charAt(i)]) {
			case undefined:
				if (debug)
					System.out.println("undefined");
				break;
			case ignore:
				if (debug)
					System.out.println("ignore");
				break;
			case numeric:
				if (debug) {
					if (stringBuf.charAt(i) == groupSymbol)
						System.out.println("ignore");
					else
						System.out.println("number " +
							stringBuf.charAt(i) + " " +
							memoryBuf);
				}
				if (stringBuf.charAt(i) != groupSymbol)
					inputNumeric(stringBuf.charAt(i));
				break;
			case operation:
				if (debug)
					System.out.println("operate " +
						stringBuf.charAt(i) + " " +
						memoryBuf);
				inputOperator(stringBuf.charAt(i));
				break;
			case clear:
				if (debug)
					System.out.println("clear");
				break;
			default:
				if (debug)
					System.out.println("QUIT");
				break;
			}
		}
		inputOperator('=');
		if (debug)
			System.out.println("display =" + displayBuf);
		if (myAbacus.checkAnomaly()) {
			addBackAnomaly(displayBuf, myAbacus.getAnomaly(),
				myAbacus.getShiftAnomaly(), base);
		}
		if (myAbacus.checkAnomalySq()) {
			addBackAnomaly(displayBuf, myAbacus.getAnomalySq(),
				myAbacus.getShiftAnomaly() +
				myAbacus.getShiftAnomalySq(), base);
		}
		if (myAbacus.getSubdeck() != 0) {
			zeroFractionalPart(displayBuf);
		}
	}

	public String getDisplayBuf() {
		return displayBuf.toString();
	}

	public void calculateAndDisplay(Abacus abacus, String buffer, int aux) {
		calculate(abacus, buffer);
		convertStringToAbacus(displayBuf.toString(), aux);
	}
}

class CalcTerm {
	BigDecimal variable;
	int packed; /* This makes assumptions on the data. */
	//char operation;
	boolean orderUsage;
	int count;
	Stack<String> stack = new Stack<>();
	//Stack stack = new Stack();

	void writeVariable(BigDecimal localVariable) {
		this.variable = localVariable;
	}

	void writeOperation(char op) {
		//this.operation = op;
		packed += ((op & 0xF) << 4);
	}

	void writeOrderUsage(boolean order) {
		//this.orderUsage = order;
		packed += ((order) ? 1 : 0) & 0xF;
	}

	BigDecimal readVariable() {
		return this.variable;
	}

	char readOperation() {
		//return this.operation;
		return (char) ((packed >> 4) & 0xF);
	}

	boolean readOrderUsage() {
		//return this.orderUsage;
		return ((packed & 0xF) != 0);
	}

	void setTerm(BigDecimal variable, char operation, boolean orderUsage) {
		packed = 0;
		writeVariable(variable);
		writeOperation(operation);
		writeOrderUsage(orderUsage);
		stack.push(variable.toPlainString());
		stack.push(Integer.toString(packed));
		count++;
	}

	void getTerm() {
		packed = Integer.parseInt(stack.pop());
		variable = new BigDecimal(stack.pop());
		count--;
	}

	boolean madeTerms() {
		return !stack.empty();
	}

	void flushTerms() {
		while (madeTerms())
			getTerm();
		count = 0; // probably do not need
	}

	int numTerms() {
		return count;
	}
}

class CalcExpression {
	int variable;
	int packed; /* This makes assumptions on the data. */
	//char operation;
	int count;
	Stack<String> stack = new Stack<>();
	//Stack stack = new Stack();

	void writeVariable(int localVariable) {
		this.variable = localVariable;
	}

	void writeOperation(char op) {
		//this.operation = op;
		packed += op & 0xF;
	}

	int readVariable() {
		return this.variable;
	}

	char readOperation() {
		//return this.operation;
		return (char) (packed & 0xF);
	}

	void setExpression(int variable, char operation) {
		packed = 0;
		writeVariable(variable);
		writeOperation(operation);
		stack.push(Integer.toString(variable));
		stack.push(Integer.toString(packed));
		count++;
	}

	void getExpression() {
		packed = Integer.parseInt(stack.pop());
		variable = Integer.parseInt(stack.pop());
		count--;
	}

	boolean madeExpressions() {
		return !stack.empty();
	}

	void flushExpressions() {
		while (madeExpressions())
			getExpression();
		count = 0; // probably do not need
	}

	int numExpressions() {
		return count;
	}
}
