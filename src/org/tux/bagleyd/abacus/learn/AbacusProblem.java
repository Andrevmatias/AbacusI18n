package org.tux.bagleyd.abacus.learn;

/*
 * @(#)AbacusProblem.java
 *
 * Copyright 2011  David A. Bagley, bagleyd@tux.org
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
 * The <code>AbacusProblem</code> class holds all information about
 * a single question used in an abacus test.
 *
 * @author      David A. Bagley
 * @author      bagleyd@tux.org
 * @author      http:/www.tux.org/~bagleyd/abacus.html
 */

class AbacusProblem {
	String problem; // similar to index, but string like "1a"
	String question; // math question to test
	String answer; // correct answer
	String attempt; // student's answer
	boolean correct;

	public AbacusProblem(String problem,
			String question,
			String answer,
			String attempt) {
		this.problem = problem;
		this.question = question;
		this.answer = answer;
		this.attempt = attempt;
		correct = check(answer, attempt);
	}

	static boolean check(String answer, String attempt) {
		if (answer.equals(attempt))
			return true;
		// This is assuming integers in test...
		if (Integer.parseInt(answer) == Integer.parseInt(attempt))
			return true;
		return false;
	}

	String getProblem() {
		return problem;
	}

	String getQuestion() {
		return question;
	}

	String getAnswer() {
		return answer;
	}

	String getAttempt() {
		return attempt;
	}

	boolean getCorrect() {
		return correct;
	}
}
