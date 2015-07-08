package org.tux.bagleyd.abacus.model;

/*
 * @(#)AbacusDeck.java
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
 * The <code>AbacusDeck</code> class part of an actual abacus for
 * <code>Abacus</code> class.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */


public class AbacusDeck {
	private int number;
	private boolean orientation;
	private int factor;
	private int spaces;
	private int[] position = null;
	private int piece = 0, piecePercent = 0;

	public AbacusDeck(int rails, int number, boolean orientation,
			int factor, int spaces) {
		this.number = number;
		this.orientation = orientation;
		this.factor = factor;
		this.spaces = spaces;
		this.position = new int[rails];
	}

	public AbacusDeck(int rails, int number, boolean orientation,
			int factor, int spaces,
			int piece) {
		this.number = number;
		this.orientation = orientation;
		this.factor = factor;
		this.spaces = spaces;
		this.position = new int[rails];
		this.piece = piece;
	}

	public AbacusDeck(int rails, int number, boolean orientation,
			int factor, int spaces,
			int piece, int piecePercent) {
		this.number = number;
		this.orientation = orientation;
		this.factor = factor;
		this.spaces = spaces;
		this.position = new int[rails];
		this.piece = piece;
		this.piecePercent = piecePercent;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int value) {
		number = value;
	}

	public boolean getOrientation() {
		return orientation;
	}

	public void setOrientation(boolean value) {
		orientation = value;
	}

	public int getFactor() {
		return factor;
	}

	public void setFactor(int value) {
		factor = value;
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

	public void setRailSize(int size) {
		position = new int[size];
	}
	
	public int getRailSize() {
		return position.length;
	}

	public int getPosition(int index) {
		if (index >= 0 && index < position.length)
			return position[index];
		System.out.println("deck getPosition: " + index + ", "
			+ position.length);
		return -1;
	}

	public void setPosition(int index, int value) {
		if (index >= 0 && index < position.length && value >= 0) {
			position[index] = value;
			return;
		}
		System.out.println("deck setPosition: " + index + ", "
			+ position.length + ", " + value);
	}

	public int getPiece() {
		return piece;
	}

	public void setPiece(int value) {
		piece = value;
	}

	public int getPiecePercent() {
		return piecePercent;
	}
	
	public void setPiecePercent(int value) {
		piecePercent = value;
	}
}
