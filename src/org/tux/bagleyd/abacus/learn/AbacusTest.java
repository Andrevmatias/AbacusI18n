package org.tux.bagleyd.abacus.learn;

/*
 * @(#)AbacusTest.java
 *
 * Copyright 2011 - 2014  David A. Bagley, bagleyd@tux.org
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.tux.bagleyd.abacus.Abacus;
import org.tux.bagleyd.abacus.AbacusApplet;
import org.tux.bagleyd.abacus.AbacusCalc;
import org.tux.bagleyd.abacus.AbacusInterface;
/**
 * The <code>AbacusTest</code> class is shows the test window of
 * the <code>AbacusApplet</code> class to test a student on the
 * understanding of the abacus.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author      http:/www.tux.org/~bagleyd/abacus.html
 */

public class AbacusTest {
	Applet applet;
	Abacus abacus;

	AbacusExam exam;
	String currentProblem, currentQuestion, answer;
	boolean started = false;
	int testCount = 0, testLine = 0;
	int numberQuestions = 0;
	AbacusInterface.TestState state = AbacusInterface.TestState.start;

	public static final String nl = System.getProperty("line.separator");

	String examText[];
	final String[] demoTests = {
		"Grade0-Add",
	};
	final String[] demoProblems = {
		//"Add Test",
		"1) 2 + 4 + 6 + 3",
		"2) 12 + 56 + 43 + 72",
		"3) 256 + 489 + 311 + 723",
	};


	public AbacusTest(Applet applet, Abacus abacus) {
		this.applet = applet;
		this.abacus = abacus;
	}

	//Format 36 q in 12 min, max 4 dig add, max 3 dig sub, no decimal pt

	public int calculateNumberQuestions() {
		int line = 0;
		int count = 0;

		while (line < examText.length) {
			if (examText[line].contains(")"))
				count++;
			line++;
		}
		return count;
	}

	public void doTest() {
		while (examText.length >= testLine - 1) {
			if (examText[testLine].contains(")"))
				break;
			testLine++;
		}
		if (testLine >= examText.length) {
			return;
		}
		String[] strings = examText[testLine].split(")");
		currentProblem = strings[0];
		((AbacusApplet)applet).setTestLabel(currentProblem + ")");
		currentQuestion = strings[1];
		((AbacusApplet)applet).setTestQuestion(currentQuestion);
		AbacusCalc abacusCalc = new AbacusCalc(abacus);
		abacusCalc.calculate(abacus, currentQuestion);
		answer = abacusCalc.getDisplayBuf();
	}

	public void gradeTest() {
		int num = examText.length;
		int numAnswered = exam.getNumberQuestions();
		int numCorrect = exam.getNumberCorrect();
		int grade = exam.percentCorrect(num);
		String gradeString = AbacusExam.evalGrade(grade).toString();

		if (AbacusApplet.getApplication()) {
			StringBuffer fileName = new StringBuffer("./results");
			new File(fileName.toString()).mkdir();
			fileName = fileName.append("/" + exam.getTestName());
			new File(fileName.toString()).mkdir();
			fileName = fileName.append("/" + exam.getStudentName() + ".txt");
			try (PrintWriter pr = new PrintWriter(new FileWriter(fileName.toString(), false))) {
				pr.println("Number Correct: " + numCorrect);
				pr.println("Number Answered: " + numAnswered);
				pr.println("Number Questions: " + num);
				pr.println("Percent Correct: " + grade);
				pr.println("Grade: " + gradeString);
				pr.println("");
				for (int i = 0; i < numAnswered; i++) {
					if (!exam.getCorrect(i)) {
						pr.println(exam.getProblem(i) + ")" + exam.getQuestion(i) + " = " + exam.getAnswer(i));
						pr.println(" Answer given: " + exam.getAttempt(i));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Can not write to " + fileName);
			}
		}
		((AbacusApplet)applet).setGrade(numCorrect, num, grade, gradeString);
	}

	public void returnStrings(String string1, String string2) {
		if (state == AbacusInterface.TestState.start) {
			// assume test = 0 for now
			testCount = 0;
			testLine = 0;
			exam = new AbacusExam(string1, string2);
			if (AbacusApplet.getApplication()) {
				examText = readTest("./tests/" + string1 + ".txt");
			} else {
				examText = demoProblems;
			}
			numberQuestions = calculateNumberQuestions();
			doTest();
			state = AbacusInterface.TestState.exam;
			abacus.clearAbacus(); // started moving beads before start
		} else if (state == AbacusInterface.TestState.exam) {
			exam.add(currentProblem, currentQuestion, answer, string1);
			testLine++;
			((AbacusApplet)applet).setTestPercentValue((testLine * 100 / numberQuestions) + "%");
			abacus.clearAbacus();
			// do not have to press button for last question
			if ("00:00".equals(string2) ||
					testLine >= numberQuestions)
				gradeTest();
			else
				doTest();
		} else if (state == AbacusInterface.TestState.finish) {
			System.exit(0);
		}
	}

	public void getTest() {
		started = false;
		if (AbacusApplet.getApplication()) {
			((AbacusApplet)applet).setTest(readTestNames(), this);
		} else {
			((AbacusApplet)applet).setTest(demoTests, this);
		}
	}

	public static String[] readTestNames() {
		final String directory = "./tests";

		File file = new File(directory);
		String[] fileNames = file.list();
		ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i < fileNames.length; i++) {
			if (fileNames[i].indexOf(".txt") !=
					fileNames[i].length() - 4)
				continue;
			list.add(fileNames[i].substring(0, fileNames[i].length() - 4));
		}
		list.trimToSize();
		if (list.isEmpty()) {
			System.err.println("No tests found... no test today!");
			System.exit(-1);
		}
		String[] tests = new String[list.size()];
		list.toArray(tests);
		return tests;
	}

	public static String[] readTest(String fileName) {
		 try (BufferedReader br = new BufferedReader(
				new FileReader(fileName))) {
			ArrayList<String> list = new ArrayList<>();
			String lineData;

			while ((lineData = br.readLine()) != null)
				list.add(lineData);
			list.trimToSize();
			if (list.isEmpty()) {
				System.err.println("No test data!");
				System.exit(-1);
			}
			String[] problems = new String[list.size()];
			list.toArray(problems);
			return problems;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Can not read from " + fileName);
			System.exit(-1);
		}
		return new String[0];
	}
}
