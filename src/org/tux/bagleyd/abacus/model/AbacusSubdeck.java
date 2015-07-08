package org.tux.bagleyd.abacus.model;

/*
 * @(#)AbacusSubdeck.java
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
 * The <code>AbacusDeck</code> class part of an actual abacus (Roman
 * style) <code>Abacus</code> class.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */

public class AbacusSubdeck {
	private int number;
	private int spaces;
	private int position;

	public AbacusSubdeck(int number, int spaces) {
		this.number = number;
		this.spaces = spaces;
		this.position = 0;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int value) {
		number = value;
	}

	public int getSpaces() {
		return spaces;
	}

	public void setSpaces(int value) {
		spaces = value;
	}

	public int getRoom() {
		return number + spaces;
	}

	/*public void setNumberAndSpaces(int numberBeads, int emptySpaces) {
		number = numberBeads;
		spaces = emptySpaces;
	}*/

	public int getPosition() {
		return position; 
	}

	public void setPosition(int value) {
		position = value;
	}
}
