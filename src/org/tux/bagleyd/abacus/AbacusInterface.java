package org.tux.bagleyd.abacus;

/*
 * @(#)AbacusInterface.java
 *
 * Copyright 1994 - 2015  David A. Bagley, bagleyd@tux.org
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
 * The <code>AbacusInterface</code> class contains most of the
 * constants used by the <code>AbacusApplet</code> class.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */

public class AbacusInterface {

	public static final int ACTION_HIDE = 101;
	public static final int ACTION_BASE_DEFAULT = 102;
	public static final int ACTION_DEMO_DEFAULT = 103;
	public static final int ACTION_CLEAR_QUERY = 104;
	public static final int ACTION_CALC = 105;
	public static final int ACTION_SCRIPT = 106;
	public static final int ACTION_MOVE = 107;
	public static final int ACTION_PLACE = 108;
	public static final int ACTION_CLEAR = 200;
	public static final int ACTION_COMPLEMENT = 201;
	public static final int ACTION_INCREMENT = 202;
	public static final int ACTION_DECREMENT = 203;
	public static final int ACTION_FORMAT = 204;
	public static final int ACTION_ROMAN_NUMERALS = 206;
	public static final int ACTION_GROUP = 207;
	public static final int ACTION_SIGN = 208;
	public static final int ACTION_QUARTER = 209;
	public static final int ACTION_TWELFTH = 210;
	public static final int ACTION_QUARTER_PERCENT = 211;
	public static final int ACTION_SUBDECK = 212;
	public static final int ACTION_EIGHTH = 213;
	public static final int ACTION_MUSEUM = 214;
	public static final int ACTION_ANOMALY = 216;
	public static final int ACTION_WATCH = 217;
	public static final int ACTION_VERTICAL = 218;
	public static final int ACTION_TEACH = 219;
	public static final int ACTION_RIGHT_TO_LEFT_ADD = 220;
	public static final int ACTION_RIGHT_TO_LEFT_MULT = 221;
	public static final int ACTION_SPEED_UP = 223;
	public static final int ACTION_SLOW_DOWN = 224;
	public static final int ACTION_DEMO = 300;
	public static final int ACTION_NEXT = 301;
	public static final int ACTION_REPEAT = 302;
	public static final int ACTION_JUMP = 303;
	public static final int ACTION_MORE = 304;
	public static final int ACTION_IGNORE = 999;

	public static final int MIN_RAILS = 1;
	public static final int MIN_DEMO_RAILS = 3;
	public static final int DEFAULT_RAILS = 13;
	public static final int DEFAULT_LEFT_AUX_RAILS = 7;
	public static final int DEFAULT_RIGHT_AUX_RAILS = 13;
	public static final int DEFAULT_TOP_SPACES = 2;
	public static final int DEFAULT_BOTTOM_SPACES = 2;
	public static final int DEFAULT_TOP_NUMBER = 2;
	public static final int DEFAULT_BOTTOM_NUMBER = 5;
	public static final int DEFAULT_TOP_FACTOR = 5;
	public static final int DEFAULT_BOTTOM_FACTOR = 1;
	public static final boolean DEFAULT_TOP_ORIENT = true;
	public static final boolean DEFAULT_BOTTOM_ORIENT = false;
	public static final int MIN_BASE = 2; // Base 1 is rediculous :)
	public static final int MAX_BASE = 36; // 10 numbers + 26 letters (ASCII)
	public static final int DEFAULT_BASE = 10;
	public static final int DEFAULT_SUBDECKS = 3;
	public static final int DEFAULT_SUBBEADS = 4;
	public static final int DEFAULT_SHIFT_PERCENT = 2;
	public static final int DEFAULT_SHIFT_ANOMALY = 2;
	public static final int DEFAULT_GROUP_SIZE = 3;
	public static final int SUBDECK_SPACE = 1;
	public static final int MAX_MUSEUMS = 3;
	public static final int IT = 0;
	public static final int UK = 1;
	public static final int FR = 2;
	public static final int COLOR_MIDDLE = 1;
	public static final int COLOR_FIRST = 2;
	public static final int COLOR_HALF = 4;
	public static final int PRIMARY = 0;
	public static final int LEFT_AUX = 1;
	public static final int RIGHT_AUX = 2;
	public static final String TEACH_STRING0 = "Enter calculation X+Y, X-Y, X*Y, X/Y, Xv, or Xu where X positive and result positive.";
	public static final String TEACH_STRING1 = "Press enter to go through calculation steps.";

	public static final int NORMAL = 1;
	public static final int DOUBLE = 2;
	public static final int INSTANT = 3;

	public enum Modes {Chinese, Japanese, Korean, Roman, Russian, Danish, Medieval, Generic}
	public static final String[] formatStrings = {"Saun-pan", "Soroban", "Supan",
		"Hand-abacus", "Schoty", "Abacus", "Counter", "Abacus"};
	public static final String[] museumStrings = {"it", "uk", "fr"};
	public static final String[] museumCountry = {"Italian", "British", "French"};
	public static final String TITLE = "Abacus";
	public static final String[] ICONS_16x16 = {"icons/16x16/abacus.png",
		"icons/16x16/abacus.png", "icons/16x16/abacus.png",
		"icons/16x16/abacusro.png", "icons/16x16/abacusru.png",
		"icons/16x16/abacusdk.png", "icons/16x16/abacusme.png",
		"icons/16x16/abacus.png"};
	public static final String[] ICONS_32x32 = {"icons/32x32/abacus.png",
		"icons/32x32/abacus.png", "icons/32x32/abacus.png",
		"icons/32x32/abacusro.png", "icons/32x32/abacusru.png",
		"icons/32x32/abacusdk.png", "icons/32x32/abacusme.png",
		"icons/32x32/abacus.png"};
	public static final String[] ICONS_48x48 = {"icons/48x48/abacus.png",
		"icons/48x48/abacusjp.png", "icons/48x48/abacusko.png",
		"icons/48x48/abacusro.png", "icons/48x48/abacusru.png",
		"icons/48x48/abacusdk.png", "icons/48x48/abacusme.png",
		"icons/48x48/abacus.png"};
	private static final String SOUNDEXT = ".au";
	//private static final String SOUNDEXT = ".wav";
	public static final String BUMP_SOUND = "sounds/bump" + SOUNDEXT;
	public static final String MOVE_SOUND = "sounds/move" + SOUNDEXT;
	public static final String DRIP_SOUND = "sounds/drip" + SOUNDEXT;

	public enum TestState {start, exam, finish}

	public static int setModeFromFormat(String string) {
		for (Modes mode : Modes.values()) {
			if (string.equalsIgnoreCase(mode.toString())) {
				return mode.ordinal();
			}
		}
		if (string.equalsIgnoreCase("Italian")) {
			return Modes.Roman.ordinal();
		} else if (string.equalsIgnoreCase("cn")) {
			return Modes.Chinese.ordinal();
		} else if (string.equalsIgnoreCase("zh")) {
			return Modes.Chinese.ordinal();
		} else if (string.equalsIgnoreCase("ja")) {
			return Modes.Japanese.ordinal();
		} else if (string.equalsIgnoreCase("jp")) {
			return Modes.Japanese.ordinal();
		} else if (string.equalsIgnoreCase("ko")) {
			return Modes.Korean.ordinal();
		} else if (string.equalsIgnoreCase("ro")) {
			return Modes.Roman.ordinal();
		} else if (string.equalsIgnoreCase("it")) {
			return Modes.Roman.ordinal();
		} else if (string.equalsIgnoreCase("ru")) {
			return Modes.Russian.ordinal();
		} else if (string.equalsIgnoreCase("dk")) {
			return Modes.Danish.ordinal();
		} else if (string.equalsIgnoreCase("da")) {
			return Modes.Danish.ordinal();
		} else if (string.equalsIgnoreCase("de")) {
			return Modes.Medieval.ordinal();
		} else if (string.equalsIgnoreCase("uk")) {
			return Modes.Medieval.ordinal();
		} else {
			return Modes.Generic.ordinal();
		}
	}

	public static int setMuseumFromFormat(String string) {
		if (string.equalsIgnoreCase("it")) {
			return IT;
		} else if (string.equalsIgnoreCase("uk")) {
			return UK;
		} else if (string.equalsIgnoreCase("fr")) {
			return FR;
		}
		return MAX_MUSEUMS;
	}
}
