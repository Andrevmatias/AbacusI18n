package org.tux.bagleyd.abacus.learn;

/*
 * @(#)AbacusExam.java
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
 * The <code>AbacusExam</code> class holds all information about
 * an abacus test.
 *
 * @author      David A. Bagley
 * @author      bagleyd@tux.org
 * @author      http:/www.tux.org/~bagleyd/abacus.html
 */

import java.util.ArrayList;

public class AbacusExam {
	String testName; // filename - ".txt"
	ArrayList<AbacusProblem> exam = null;
	String studentName;
	enum Eval {Fail, Pass, Perfect}

	AbacusExam(String testName, String studentName) {
		this.testName = testName;
		this.studentName = studentName;
		exam = new ArrayList<>();
	}

	void add(String problem, String question,
			String answer, String attempt) {
		exam.add(new AbacusProblem(problem, question,
			answer, attempt));
		//System.out.println(attempt + " is correct? " + getCorrect(getNumberQuestions() - 1));
	}

	int getNumberQuestions() {
		return exam.size();
	}

	int getNumberCorrect() {
		int count = 0;

		for (int i = 0; i < getNumberQuestions(); i++) {
			if (exam.get(i).getCorrect())
				count++;
		}
		return count;
	}

	static Eval evalGrade(int percentCorrect) {
		if (percentCorrect == 100/* ||
				studentName.equals("David Bagley")*/)
			return Eval.Perfect;
		else if (percentCorrect >= 65)
			return Eval.Pass;
		return Eval.Fail;
	}

	int percentCorrect(int num) {
		// careful may not have answered all the questions
		return 100 * getNumberCorrect() / num;
	}

	String getProblem(int i) {
		return exam.get(i).getProblem();
	}

	String getQuestion(int i) {
		return exam.get(i).getQuestion();
	}

	String getAnswer(int i) {
		return exam.get(i).getAnswer();
	}

	String getAttempt(int i) {
		return exam.get(i).getAttempt();
	}

	boolean getCorrect(int i) {
		return exam.get(i).getCorrect();
	}

	String getTestName() {
		return testName;
	}

	String getStudentName() {
		return studentName;
	}
}
