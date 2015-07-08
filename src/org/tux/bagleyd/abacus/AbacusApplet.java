package org.tux.bagleyd.abacus;

/*
 * @(#)AbacusApplet.java
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

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.EnumSet;
import java.util.Random;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
/*import static java.awt.GraphicsDevice.WindowTranslucency.*;*/

import org.tux.bagleyd.abacus.learn.AbacusTest;
import org.tux.bagleyd.abacus.learn.DemoDialog;
import org.tux.bagleyd.abacus.learn.TeachDialog;
import org.tux.bagleyd.abacus.learn.TestDialog;
import org.tux.bagleyd.util.ArgumentParser;
import org.tux.bagleyd.util.Colour;
import org.tux.bagleyd.util.ColumnLayout;
import org.tux.bagleyd.util.ComponentUtil;
import org.tux.bagleyd.util.Icon;
import org.tux.bagleyd.util.PartialDisableComboBox;
import org.tux.bagleyd.util.OKDialog;
import org.tux.bagleyd.util.Slider;
import org.tux.bagleyd.util.TextPrompt;

/**
 * The <code>AbacusApplet</code> class is to be embedded in a Web page
 * or run as an application.
 *
 * @author	David A. Bagley
 * @author	bagleyd@tux.org
 * @author	http:/www.tux.org/~bagleyd/abacus.html
 */

/* http://www.codemiles.com/java/jframe-on-applet-t385.html */

public class AbacusApplet extends JApplet implements ActionListener {
	private static final long serialVersionUID = 42L;
	public static final String nl = System.getProperty("line.separator");
	static final int MAX_RAILS = 24;
	static final String[] commandString = new String[] {
		"Clear", "Complement ~",
		"Increment", "Decrement", "Format",
		"Roman Nvmerals", /*"Ancient Roman Numerals (", */"Group",
		"Sign", "Quarter", "Twelfth",
		"Quarter Percent", "Subdeck", "Eighth", "Museum",
		/*"Modern Roman Numerals )", */"Anomaly", "Watch",
		"Right To Left Add +", "Right To Left Mult *",
		"Sound @", "Speed Up >", "Slow Down <",
		"Demo", "Teach $",
		"Description ?", "Features !", "References ^", "About",
		"Detach", "Exit"};
	static final char[] commandChar = new char[] {
		'c', '~',
		'i', 'd', 'f',
		'v', /*'(', */'g',
		's', 'u', 't',
		'p', 'b', 'e', 'm',
		/*')', */'l', 'w',
		'+', '*',
		'@', '>', '<',
		'o', '$',
		'?', '!', '^', 'a',
		'h', 'x'};

	static ArgumentParser argumentParser = null;
	Icon mediumIcon = null;
	Icon largeIcon = null;
	int widthValue = 400;
	int heightValue = 400;
	static int xValue = 0;
	static int yValue = 0;
	Color fgColor = Colour.tan;
	Color bgColor = Colour.steelBlue;
	Color borderColor = Colour.gray25;
	Color primaryBeadColor = Colour.darkRed;
	Color leftAuxBeadColor = Colour.limeGreen;
	Color rightAuxBeadColor = Colour.gainsboro;
	Color secondaryBeadColor = Colour.brown;
	Color highlightBeadColor = Colour.magenta;
	Color primaryRailColor = Colour.gold;
	Color secondaryRailColor = Colour.silver;
	Color highlightRailColor = Colour.purple;
	Color lineRailColor = Colour.black;
	boolean sound = false;
	int delayValue = 50; // msec
	int inc = 10;
	int testValue = 0; // minutes
	boolean script = false;
	boolean controlValue = true;
	boolean demoValue = false;
	boolean rightToLeftAddValue = false, rightToLeftMultValue = false;
	boolean leeValue = false;
	int railsValue = AbacusInterface.DEFAULT_RAILS;
	int leftAuxRailsValue = AbacusInterface.DEFAULT_LEFT_AUX_RAILS;
	int rightAuxRailsValue = AbacusInterface.DEFAULT_RIGHT_AUX_RAILS;
	int totalAuxRailsValue = leftAuxRailsValue + rightAuxRailsValue;
	boolean verticalValue = false;
	int colorSchemeValue = 0;
	boolean slotValue = false;
	boolean diamondValue = false;
	int railIndexValue = 0;
	boolean topOrientValue = AbacusInterface.DEFAULT_TOP_ORIENT;
	boolean bottomOrientValue = AbacusInterface.DEFAULT_BOTTOM_ORIENT;
	int topNumberValue = AbacusInterface.DEFAULT_TOP_NUMBER;
	int bottomNumberValue = AbacusInterface.DEFAULT_BOTTOM_NUMBER;
	int topFactorValue = AbacusInterface.DEFAULT_TOP_FACTOR;
	int bottomFactorValue = AbacusInterface.DEFAULT_BOTTOM_FACTOR;
	int topSpacesValue = AbacusInterface.DEFAULT_TOP_SPACES;
	int bottomSpacesValue = AbacusInterface.DEFAULT_BOTTOM_SPACES;
	int topPieceValue = 0;
	int bottomPieceValue = 0;
	int topPiecePercentValue = 0;
	int bottomPiecePercentValue = 0;
	int shiftPercentValue = 2;
	int subdeckValue = 0;
	int subbeadValue = 4;
	boolean signValue = false;
	int decimalPositionValue = 2; /* Good default for most currencies */
	boolean groupValue = false;
	int groupSizeValue = AbacusInterface.DEFAULT_GROUP_SIZE;
	boolean decimalCommaValue = false;
	int baseValue = AbacusInterface.DEFAULT_BASE;
	int subbaseValue = 12;
	int anomalyValue = 0;
	int shiftAnomalyValue = 2;
	int anomalySqValue = 0;
	int shiftAnomalySqValue = 2;
	int displayBaseValue = AbacusInterface.DEFAULT_BASE;
	boolean pressOffsetValue = false;
	boolean romanNumeralsValue = false;
	boolean latinValue = false;
	boolean ancientRomanValue = false;
	boolean modernRomanValue = false;
	int modeValue = AbacusInterface.Modes.Chinese.ordinal();
	int museumValue = -1;
	Random generator;
	int valueTextLength = 42 - 14; /* Roman Numerals fit */
	boolean debug = false;
	JPanel topPanel = new JPanel();
	Slider railSlider = null;
	PartialDisableComboBox modeComboBox = new PartialDisableComboBox();
	Abacus abacus = null, leftAuxAbacus = null, rightAuxAbacus = null;
	AbacusCalc abacusCalc = new AbacusCalc(abacus);
	AbacusCalc leftAuxAbacusCalc = new AbacusCalc(leftAuxAbacus);
	AbacusCalc rightAuxAbacusCalc = new AbacusCalc(rightAuxAbacus);
	JTextField leftAuxValueTextField = new JTextField("");
	JTextField rightAuxValueTextField = new JTextField("");
	JTextField valueTextField = new JTextField("");
	TextPrompt leftAuxValueTextPrompt = null;
	TextPrompt rightAuxValueTextPrompt = null;
	TextPrompt valueTextPrompt = null;
	AudioClip bumpAudioClip = null;
	AudioClip moveAudioClip = null;
	AudioClip dripAudioClip = null;
	OKDialog aDialog = null;
	DemoDialog demoDialog = null;
	TestDialog testDialog = null;
	TeachDialog teachDialog = null;
	StringBuffer stringBufferSave;
	boolean mathDone = true;
	JMenuBar menuBar;
	JMenuItem detachMenuItem;
	JPanel mainPanel = new JPanel();
	JPanel secondaryPanel = new JPanel();
	JFrame frame;
	boolean isPopped = false;
	Font font = new Font("Verdana", Font.PLAIN, 12);
	Slider displaySlider = null, baseSlider = null;
 	JMenu secondaryRailsMenu, museumMenu;
	JCheckBoxMenuItem romanNumeralsMenuItem = null;
	/*JCheckBoxMenuItem ancientRomanNumeralsMenuItem = null;*/
	JCheckBoxMenuItem groupMenuItem = null;
	JCheckBoxMenuItem signMenuItem = null;
	JCheckBoxMenuItem quarterMenuItem = null;
	JCheckBoxMenuItem twelfthMenuItem = null;
	JCheckBoxMenuItem quarterPercentMenuItem = null;
	JCheckBoxMenuItem subdeckMenuItem = null;
	JCheckBoxMenuItem eighthMenuItem = null;
	JMenuItem[] museumMenuItem = null;
	/*JCheckBoxMenuItem modernRomanNumeralsMenuItem = null;*/
	JCheckBoxMenuItem anomalyMenuItem = null;
	JCheckBoxMenuItem watchMenuItem = null;
	JCheckBoxMenuItem rightToLeftAddMenuItem = null;
	JCheckBoxMenuItem rightToLeftMultMenuItem = null;
	Slider delaySlider = null;

	/* Thanks to Brian Pipa 08Feb2004
	 * http://www.javakb.com/Uwe/Forum.aspx/java-gui/1533/Detaching-Applet-to-a-Frame-the-Frame-is-iconized */

	private void popoutApplet() {
		// Comment out next line to DEBUG
		if (getApplication()) {
			return;
		}
		if (frame == null) {
			frame = new JFrame(AbacusInterface.TITLE);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					popinApplet();
				}
			});
			// possible if debugging
			if (!getApplication()) {
				String icon = (nl.compareTo("\n") == 0) ?
					AbacusInterface.ICONS_48x48[0] :
					AbacusInterface.ICONS_16x16[0];

				frame.setIconImage(getImage(getCodeBase(),
					icon));
			}
		}
		getContentPane().remove(mainPanel);
		frame.getContentPane().add(mainPanel);
		int ht = heightValue + topPanel.getSize().height;

		if (controlValue)
			ht += menuBar.getSize().height;
		Insets inset = frame.getInsets();
		int titleBarHeight = (controlValue) ?
			menuBar.getSize().height : // a good guess for titleHeight
			16; // a bad guess for titleHeight
		frame.setSize(widthValue + inset.left + inset.right,
			ht + inset.top + inset.bottom +
			titleBarHeight);
		frame.setLocation(0, 0);
		isPopped = true;
		invalidate();
		validate();
		/* Thanks to Alex, move if null in popinApplet removed
		 * http://stackoverflow.com/questions/2106367/listen-to-jframe-resize-events-as-the-user-drags-their-mouse */
		mainPanel.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				int topOffset = topPanel.getSize().height;

				if (controlValue)
					topOffset += menuBar.getSize().height;
				setSize(mainPanel.getSize().width,
					mainPanel.getSize().height - topOffset);
				//setShape(new Rectangle2D.Double(0, 0, getWidth(), getHeight());
			}
		});
		frame.setVisible(true);
		repaint();
		detachMenuItem.setText("Attach");
	}

	void popinApplet() {
		frame.setVisible(false);
		frame.getContentPane().remove(mainPanel);
		frame = null; // not my original idea, but this gets rid of
			// a drawing error on second detachment.
		getContentPane().add(mainPanel);
		isPopped = false;
		invalidate();
		validate();
		repaint();
		detachMenuItem.setText("Detach");
	}

	public boolean popApplet() {
		if (isPopped) {
			popinApplet();
		} else {
			popoutApplet();
		}
		return isPopped;
	}

	public String getDemoString() {
		return this.stringBufferSave.toString();
	}

	public boolean getDemo() {
		return demoValue;
	}

	public boolean getTest() {
		return (testValue != 0);
	}

	public boolean getTeach() {
		return (teachDialog != null);
	}

	public boolean getRightToLeftAdd() {
		return rightToLeftAddValue;
	}

	public void setRightToLeftAdd(boolean rightToLeftAdd) {
		rightToLeftAddValue = rightToLeftAdd;
	}

	public boolean getRightToLeftMult() {
		return rightToLeftMultValue;
	}

	public void setRightToLeftMult(boolean rightToLeftMult) {
		rightToLeftMultValue = rightToLeftMult;
	}

	public boolean getLee() {
		return leeValue;
	}

	public Abacus getAbacus(int aux) {
		if (leeValue && aux == AbacusInterface.LEFT_AUX)
			return leftAuxAbacus;
		else if (leeValue && aux == AbacusInterface.RIGHT_AUX)
			return rightAuxAbacus;
		else /*if (aux == AbacusInterface.PRIMARY)*/
			return abacus;
	}

	public static boolean getApplication() {
		return (argumentParser != null);
	}

	public boolean mainAbacus(AbacusCanvas ab) {
		return abacus.equals(ab);
	}

	public boolean getToggleSound() {
		return sound;
	}

	public void initSound() {
		/* There is a noticeable delay on initializing, so maybe it should
		 *  be done first.  On the other hand, it may hang which would
		 *  be worse.
		 */
	}

	public void toggleSound() {
		sound = !sound;
	}

	public boolean getScript() {
		return script;
	}

	public String getOpt(String string) {
		if (getApplication()) {
			return argumentParser.getOption(string);
		}
		return getParameter(string);
	}

	public int getOpt(String param, int value) {
		String string = getOpt(param);

		if (string == null)
			return value;
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return value; // ignore garbage
		}
	}

	public boolean getOpt(String param, boolean value) {
		String string = getOpt(param);

		if (string == null)
			return value;
		try {
			if (string.equalsIgnoreCase("true") ||
					string.equalsIgnoreCase("yes") ||
					string.equalsIgnoreCase("on"))
				return true;
			else if (string.equalsIgnoreCase("false") ||
					string.equalsIgnoreCase("no") ||
					string.equalsIgnoreCase("off"))
				return false;
			return (Integer.parseInt(string) != 0);
		} catch (NumberFormatException e) {
			return value; // ignore garbage
		}
	}

	public Color getOpt(String param, Color value) {
		String string = getOpt(param);

		if (string == null)
			return value;
		try {
			return Color.decode(string);
		} catch (NumberFormatException e) {
			return value; // ignore garbage
		}
	}

	public void getOpts() {
		String string;

		widthValue = getOpt("windowWidth", widthValue);
		heightValue = getOpt("windowHeight", heightValue);
		xValue = getOpt("windowX", xValue);
		yValue = getOpt("windowY", yValue);
		fgColor = getOpt("fgColor", fgColor);
		bgColor = getOpt("bgColor", bgColor);
		borderColor = getOpt("borderColor", borderColor);
		primaryBeadColor = getOpt("primaryBeadColor", primaryBeadColor);
		leftAuxBeadColor = getOpt("leftAuxBeadColor",
			leftAuxBeadColor);
		rightAuxBeadColor = getOpt("rightAuxBeadColor",
			rightAuxBeadColor);
		secondaryBeadColor = getOpt("secondaryBeadColor",
			secondaryBeadColor);
		highlightBeadColor = getOpt("highlightBeadColor",
			highlightBeadColor);
		primaryRailColor = getOpt("primaryRailColor",
			primaryRailColor);
		secondaryRailColor = getOpt("secondaryRailColor",
			secondaryRailColor);
		highlightRailColor = getOpt("highlightRailColor",
			highlightRailColor);
		lineRailColor = getOpt("lineRailColor",
			lineRailColor);
		sound = getOpt("sound", sound);
		script = getOpt("script", script);
		controlValue = getOpt("control", controlValue);
		rightToLeftAddValue = getOpt("rightToLeftAdd",
			rightToLeftAddValue);
		rightToLeftMultValue = getOpt("rightToLeftMult",
			rightToLeftMultValue);
		leeValue = getOpt("lee", leeValue);
		railsValue = getOpt("rails", railsValue);
		if (railsValue < AbacusInterface.MIN_RAILS)
			railsValue = AbacusInterface.DEFAULT_RAILS;
		leftAuxRailsValue = getOpt("leftAuxRails", leftAuxRailsValue);
		if (leftAuxRailsValue < AbacusInterface.MIN_RAILS)
			leftAuxRailsValue =
				AbacusInterface.DEFAULT_LEFT_AUX_RAILS;
		rightAuxRailsValue = getOpt("rightAuxRails",
			rightAuxRailsValue);
		if (rightAuxRailsValue < AbacusInterface.MIN_RAILS)
			rightAuxRailsValue =
				AbacusInterface.DEFAULT_RIGHT_AUX_RAILS;
		totalAuxRailsValue = leftAuxRailsValue + rightAuxRailsValue;
		verticalValue = getOpt("vertical", verticalValue);
		colorSchemeValue = getOpt("colorScheme", colorSchemeValue);
		slotValue = getOpt("slot", slotValue);
		diamondValue = getOpt("diamond", diamondValue);
		railIndexValue = getOpt("railIndex", railIndexValue);
		topOrientValue = getOpt("topOrient", topOrientValue);
		bottomOrientValue = getOpt("bottomOrient", bottomOrientValue);
		topNumberValue = getOpt("topNumber", topNumberValue);
		bottomNumberValue = getOpt("bottomNumber", bottomNumberValue);
		topFactorValue = getOpt("topFactor", topFactorValue);
		bottomFactorValue = getOpt("bottomFactor", bottomFactorValue);
		topSpacesValue = getOpt("topSpaces", topSpacesValue);
		bottomSpacesValue = getOpt("bottomSpaces", bottomSpacesValue);
		topPieceValue = getOpt("topPiece", topPieceValue);
		bottomPieceValue = getOpt("bottomPiece", bottomPieceValue);
		topPiecePercentValue = getOpt("topPiecePercent",
			topPiecePercentValue);
		bottomPiecePercentValue = getOpt("bottomPiecePercent",
			bottomPiecePercentValue);
		shiftPercentValue = getOpt("shiftPercent", shiftPercentValue);
		subdeckValue = getOpt("subdeck", subdeckValue);
		subbeadValue = getOpt("subbead", subbeadValue);
		signValue = getOpt("sign", signValue);
		decimalPositionValue = getOpt("decimalPosition",
			decimalPositionValue);
		groupValue = getOpt("group", groupValue);
		groupSizeValue = getOpt("groupSize", groupSizeValue);
		decimalCommaValue = getOpt("decimalComma", decimalCommaValue);
		baseValue = getOpt("base", baseValue);
		if (baseValue < AbacusInterface.MIN_BASE ||
				baseValue > AbacusInterface.MAX_BASE)
			baseValue = AbacusInterface.DEFAULT_BASE;
		subbaseValue = (getOpt("eighth", subbaseValue == 8)) ? 8 : 12;
		anomalyValue = getOpt("anomaly", anomalyValue);
		shiftAnomalyValue = getOpt("shiftAnomaly", shiftAnomalyValue);
		anomalySqValue = getOpt("anomalySq", anomalySqValue);
		shiftAnomalySqValue = getOpt("shiftAnomalySq",
			shiftAnomalySqValue);
		displayBaseValue = getOpt("displayBase", displayBaseValue);
		if (displayBaseValue < AbacusInterface.MIN_BASE ||
				displayBaseValue > AbacusInterface.MAX_BASE)
			displayBaseValue = AbacusInterface.DEFAULT_BASE;
		pressOffsetValue = getOpt("pressOffset", pressOffsetValue);
		romanNumeralsValue = getOpt("romanNumerals",
			romanNumeralsValue);
		latinValue = getOpt("latin", latinValue);
		ancientRomanValue = getOpt("ancientRoman", ancientRomanValue);
		modernRomanValue = getOpt("modernRoman", modernRomanValue);
		string = getOpt("format");
		if (string != null) {
			modeValue = AbacusInterface.setModeFromFormat(string);
		}
		modeValue = getOpt("mode", modeValue);
		if (modeValue < AbacusInterface.Modes.Chinese.ordinal() ||
				modeValue > AbacusInterface.Modes.Generic.ordinal())
			modeValue = AbacusInterface.Modes.Chinese.ordinal();
		string = getOpt("museum");
		if (string != null) {
			museumValue = AbacusInterface.setMuseumFromFormat(string);
		}
		museumValue = getOpt("submode", museumValue);
		if (museumValue < 0 ||
				museumValue >= AbacusInterface.MAX_MUSEUMS)
			museumValue = generator.nextInt(AbacusInterface.MAX_MUSEUMS);
		delayValue = getOpt("delay", delayValue);
		testValue = getOpt("test", testValue);
	}

	public void showDemoMessage(String msg) {
		if (demoDialog == null) {
			createDemoDialog(msg);
		} else {
			demoDialog.showDemoMessage(msg);
		}
	}

	public void createDemoDialog(String msg) {
		if (demoDialog == null) {
//			FontMetrics fm = getFontMetrics(this.getFont());
			FontMetrics fm = getFontMetrics(font);

			demoDialog = new DemoDialog(ComponentUtil.findJFrame(this),
				this, AbacusInterface.TITLE + ": Demo",
				msg + "                               ",
				largeIcon);
			demoDialog.setBounds(this.getSize().width / 2,
				this.getSize().height / 2,
				425, (4 + ((getApplication()) ? 4 : 6)) *
				fm.getHeight());
			demoDialog.setBackground(Color.white);
			demoDialog.setVisible(true);
		}
	}

	public void killDemoDialog() {
		if (demoDialog != null) {
			demoDialog.dispose();
			demoDialog = null;
		}
	}

	public void setDemoMsg(String m1, String m2, String m3, String m4) {
		showDemoMessage(m1 + nl + m2 + nl + m3 + nl + m4);
	}

	public void setDemoMsg(String m1) {
		showDemoMessage(m1 + nl + nl + nl);
	}

	public static String readUserDemo() {
		String demoString = "";

		return demoString;
	}

	public void setTest(String[] testNames, AbacusTest test) {
		if (testDialog == null) {
			createTestDialog(testNames, test);
		}
	}

	public void setTestLabel(String msg) {
		testDialog.showTestLabel(msg);
	}

	public void setTestQuestion(String msg) {
		testDialog.showTestQuestion(msg);
	}

	public void setTestPercentValue(String msg) {
		testDialog.showTestPercentValue(msg);
	}

	public void setGrade(int numberCorrect, int numberQuestions, int grade, String gradeString) {
		testDialog.showTestGrade(numberCorrect, numberQuestions, grade, gradeString);
	}

	public void createTestDialog(String[] testNames, AbacusTest test) {
		if (testDialog == null) {
//			FontMetrics fm = getFontMetrics(this.getFont());
			FontMetrics fm = getFontMetrics(font);

			testDialog = new TestDialog(ComponentUtil.findJFrame(this),
				this, test, AbacusInterface.TITLE + ": Test",
				testNames, largeIcon, testValue);
			testDialog.setBounds(this.getSize().width / 2,
				this.getSize().height / 2,
				425, 13 * fm.getHeight());
			testDialog.setBackground(Color.white);
			testDialog.setVisible(true);
		}
	}

	public void killTestDialog() {
		if (testDialog != null) {
			testDialog.dispose();
			testDialog = null;
		}
	}

	public void createTeachDialog() {
		if (teachDialog == null) {
//			FontMetrics fm = getFontMetrics(this.getFont());
			FontMetrics fm = getFontMetrics(font);

			teachDialog = new TeachDialog(ComponentUtil.findJFrame(this),
				this, abacus, AbacusInterface.TITLE + ": Teach",
				AbacusInterface.TEACH_STRING0, 0, largeIcon);
			teachDialog.setBounds(this.getSize().width / 2,
				this.getSize().height / 2,
				600, (4 + ((getApplication()) ? 4 : 6)) *
				fm.getHeight());
			teachDialog.setBackground(Color.white);
			teachDialog.setVisible(true);
		}
	}

	public void showTeachMessage(String msg, int line) {
		teachDialog.showTeachMessage(msg, line);
	}

	public void killTeachDialog() {
		if (teachDialog != null) {
			teachDialog.dispose();
			teachDialog = null;
		}
	}

	public void setTeachMsg(String m, int line) {
		showTeachMessage(m, line);
	}

	public void showMessage(String msg) {
		if (aDialog == null || !OKDialog.getActive()) {
			aDialog = new OKDialog(ComponentUtil.findJFrame(this),
				AbacusInterface.TITLE, msg, "OK",
				mediumIcon, false);
			aDialog.setBounds(this.getSize().width / 2,
				this.getSize().height / 2, 350, 120);
			aDialog.setVisible(true);
		}
	}

	public void descriptionHelp() {
		if (aDialog == null || !OKDialog.getActive()) {
			aDialog = new OKDialog(ComponentUtil.findJFrame(this),
				AbacusInterface.TITLE + ": Description",
"This is an implementation of the classic Chinese Abacus " +
"(Saun-pan) which has its origins in the 12th century." + nl +
" " + nl +
"The device has two decks. Each deck, separated by a " +
"partition, normally has 13 rails on which are mounted beads." + nl +
"Each rail on the top deck contains 1 or 2 beads, and each " +
"rod on the bottom deck contains 4 or 5 beads.  Each bead on" + nl +
"the upper deck has a value of five, while each bead on the " +
"lower deck has value of one. Beads are considered counted," + nl +
"when moved towards the partition separating the decks, i.e. " +
"to add a value of one, a bead in the bottom deck is moved" + nl +
"up, and to add a value of 5, a bead in the top deck is moved " +
"down." + nl +
" " + nl +
"The basic operations of the abacus are addition and subtraction. " +
" Multiplication can be done by mentally multiplying the" + nl +
"digits and adding up the intermediate results on the abacus. " +
" Division would be similar where the intermediate results" + nl +
"are subtracted.  There are techniques like using your thumb " +
"with forefinger which does not apply with mouse entry." + nl +
"Also with multiplication, one can carry out calculations on " +
"different parts of the abacus for scratch work, here it is" + nl +
"nice to have a long abacus." + nl +
" " + nl +
"The pre-WWII Japanese Abacus (Soroban) (or Korean Supan) " +
"is similar to the Chinese Abacus but has only one bead" + nl +
"per rail on the top deck.  The later Japanese Abacus was " +
"further simplified to have only 4 beads per rail on the" + nl +
"bottom deck." + nl +
" " + nl +
"The Roman Hand-Abacus predates the Chinese Abacus and is " +
"very similar to the later Japanese Abacus, but seems to" + nl +
"have fallen out of use with the Fall of the Roman Empire " +
"(at least 3 are in existence).  The Roman Abaci are brass" + nl +
"plates where the beads move in slots.  In addition to the " +
"normal 7 columns of beads, they generally have 2 special " + nl +
"columns on the right side.  In two examples: the first " +
"special column was for 12ths (12 uncia (ounces) = 1 as) and" + nl +
"had one extra bead in the bottom deck.  Also the last column " +
"was a combination of halves, quarters, and twelfths of an" + nl +
"ounce and had no beads in the top deck and 4 beads at the " +
"bottom (beads did not have to come to the top to be" + nl +
"counted but at one of 3 marked points, where the top bead " +
"was for halves, the next bead for quarters, and the last two" + nl +
"beads for twelfths).  In another surviving example: the 2 " +
"special columns were switched and the combination column" + nl +
"was broken into 3 separate slots.  If available, decimal " +
"input is ignored." + nl +
" " + nl +
"The Russian Abacus was invented in the 17th century, here " +
"the beads are moved from right to left.  It has colored" + nl +
"beads in the middle for ease of use.  Quarters represent " +
"1/4 Rubles and are only present historically on the Russian" + nl +
"Abacus (Schoty).  Some of the older Schoty have a extra place " +
"for the 1/4 Kopek (quarter percent) as well as the 1/4" + nl +
"Ruble (quarter)." + nl +
" " + nl +
"The Danish Abacus was used in the early 20th century in " +
"elementary schools as a teaching aid." + nl +
" " + nl +
"The Medieval Counter is a primitive form of the abacus and " +
"was used in Europe as late as the 1600s.  It was useful" + nl +
"considering they were using it with Roman Numerals.   This " +
"is similar to the Salamis Greek Tablet from 4th or 5th" + nl +
"Century BCE." + nl +
" " + nl +
"The Mesoamerican Nepohualtzintzin is a Japanese Abacus " +
"base 20.  The Mesoamericans had base 20 with the" + nl +
"exception of the 3rd decimal place where instead of " +
"20 * 20 = 400 the third place marked 360 and the 4th place was" + nl +
"20 * 360, etc.  They independently created their own zero " +
"(only Babylon (base 60) and India (base 10) have done this)" + nl +
"but the anomaly took away its true power." + nl +
" " + nl +
"An easy way of figuring out time in seconds given hours, " +
"minutes, and seconds, can be done on the abacus with" + nl +
"special Japanese Abacus with special anomaly \"watch\" settings." + nl +
" " + nl +
"The Chinese Solid-and-Broken-Bar System is a base 12 " +
"numbering system and not really an abacus.  When the" + nl +
"abacus is setup in this way though (topFactor 3, topNumber 3, " +
"bottomNumber 2, base 12, displayBase 12), it is easy to" + nl +
"relate the two." + nl +
" " + nl +
"The signed bead is an invention of the author and is not " +
"present on any historical abacus (to his knowledge) and is" + nl +
"used to represent negatives.  \"New & Improved\" abacus " +
"models have two auxiliary decks stacked above the principal" + nl +
"deck that enable multiplication, division, square-root, and " +
"cube-root computations to be performed with equal ease as" + nl +
"addition and subtraction (well, so I have read).",
				"OK", largeIcon, true);
			aDialog.setBackground(Color.white);
			aDialog.setVisible(true);
		}
	}

	public void featuresHelp() {
		if (aDialog == null || !OKDialog.getActive()) {
			aDialog = new OKDialog(ComponentUtil.findJFrame(this),
				AbacusInterface.TITLE + ": Features",
"Click \"mouse-left\" button on a bead you want to move.  The " +
"beads will shift themselves to vacate the area of the" + nl +
"column that was clicked." + nl +
" " + nl +
"Click \"mouse-right\" button, or press \"C\" or \"c\" keys, " +
"to clear the abacus." + nl +
" " + nl +
"Press \"O\" or \"o\" keys to toggle the demo mode." + nl +
" " + nl +
"Press \"$\" key to toggle the teach mode." + nl +
" " + nl +
"In teach mode, \"+\" key toggles starting side to sum, " +
"\"*\" key toggles for starting side for multiplicand." + nl +
" " + nl +
"Press \"~\" or \"`\" keys to complement the beads on the rails." + nl +
" " + nl +
"Press \"I\" or \"i\" keys to increment the number of rails.  " +
"Press \"D\" or \"d\" keys to decrement the number of rails." + nl +
" " + nl +
"Press \"F\" or \"f\" keys to switch between Chinese, Japanese, " +
"Japanese, Korean, Roman, Russian, and Danish formats." + nl +
"There is an extra \"Generic\" format, this allows one to " +
"break some rules binding the other formats (for example," + nl +
"if one wanted more beads on top deck than on bottom deck you " +
"would use this, in addition to resource option changes)." + nl +
" " + nl +
"Press \"V\" or \"v\" keys to toggle the Roman Nvmerals.  " +
"(Pardon humor/typo but ran out of letters)." + nl +
" " + nl +
"Press \"S\" or \"s\" keys to toggle the sign sign bead." + nl +
" " + nl +
"Press \"U\" or \"u\" keys to toggle the availability of " +
"quarter beads.  (Mutually exclusive to twelfth beads)." + nl +
" " + nl +
"Intended for the Russian Abacus." + nl +
"Press \"T\" or \"t\" keys to toggle the availability of " +
"twelfth beads.  (Mutually exclusive to quarter beads)." + nl +
"Intended for the Roman Abacus." + nl +
" " + nl +
"Press \"P\" or \"p\" keys to toggle the availability of " +
"quarter percent beads.   (Dependent on quarter beads (or" + nl +
"twelfth beads)).  Intended for the older Russian Abacus." + nl +
" " + nl +
"Press \"B\" or \"b\" keys to toggle the availability of " +
"subdecks.  (Dependent on twelfth beads (or quarter beads)" + nl +
"and Roman format).  Intended for the Roman Abacus, where " +
"the lowest value of the two at bottom of the rightmost" + nl +
"column of beads are a twelfth of the column, second from " +
"right." + nl +
" " + nl +
"Press \"E\" or \"e\" keys to toggle the availability of" +
"subdecks.  (Dependent on twelfth beads (or quarter beads)" + nl +
"and Roman format).  Intended for the Roman Abacus, where " +
"the lowest value of the three at bottom of the rightmost" + nl +
"column of beads are an eighth of the column, second from " +
"right." + nl +
" " + nl +
"Press \"M\" or \"m\" keys to switch between it, uk, and fr " +
"museum formats." + nl +
" " + nl +
"Press \"L\" or \"l\" keys to toggle the availability of " +
"anomaly bars.  Intended to used with Japanese Abacus and" + nl +
"base 20 for the Mesoamerican Abacus. (Mutually exclusive " +
"to watch bars)." + nl +
"Press \"W\" or \"w\" keys to toggle the availability of " +
"watch bars.  Intended to represent seconds where hours" + nl +
"and minutes can be set.  (Mutually exclusive to anomaly " +
"bars)." + nl +
" " + nl +
"Press \">\" or \".\" keys to speed up the movement of beads." + nl +
"Press \"<\" or \",\" keys to slow down the movement of beads." + nl +
" " + nl +
"Press \"@\" key to toggle the sound." + nl +
" " + nl +
"Press \"Esc\" key to hide program." + nl +
" " + nl +
"Press \"Q\", \"q\", or \"CTRL-C\" keys to kill program if an application." + nl +
" " + nl +
"The abacus may be resized.  Beads will reshape depending " +
"on the room they have." + nl +
" " + nl +
"Demo Mode: in this mode, the abacus is controlled by the " +
"program.  When started with the demo option, a second" + nl +
"window is presented that should be placed directly below the " +
"abacus-window. Descriptive text, and user prompts are" + nl +
"displayed in this window.  Pressing 'q' during the demo " +
"will quit it.  Clicking the left mouse-button with the" + nl +
"pointer in the window will restart the demo (beginning " +
"of current lesson).",
				"OK", largeIcon, true);
			aDialog.setBackground(Color.white);
			aDialog.setVisible(true);
		}
	}

	public void referencesHelp() {
		if (aDialog == null || !OKDialog.getActive()) {
			aDialog = new OKDialog(ComponentUtil.findJFrame(this),
				AbacusInterface.TITLE + ": References",
"Luis Fernandes  http://www.ee.ryerson.ca/~elf/abacus/" + nl +
"Lee Kai-chen, How to Learn Lee's Abacus, 1958, 58 pages." + nl +
"Abacus Guide Book, 57 pages." + nl +
"Georges Ifrah, The Universal history of Numbers, Wiley Press " +
"2000, pp 209-211, 288-294." + nl +
"Review of above: http://www.ams.org/notices/200201/rev-dauben.pdf" + nl +
"David Eugene Smith, History of Mathematics Volume II, " +
"Dover Publications, Inc 1958, pp 156-195." + nl,
				"OK", mediumIcon, false);
			aDialog.setVisible(true);
		}
	}

	public void aboutHelp() {
		if (aDialog == null || !OKDialog.getActive()) {
			aDialog = new OKDialog(ComponentUtil.findJFrame(this),
				AbacusInterface.TITLE + ": About",
"Java AbacusApplet Version 8.0.3" + nl +
"Send bugs (reports or fixes) to the author: " +
"David Bagley <bagleyd@tux.org>" + nl +
"The latest version is at: " +
"http://www.tux.org/~bagleyd/abacus.html" + nl,
				"OK", mediumIcon, false);
			aDialog.setVisible(true);
		}
	}

	public void processKey(char key) {
		abacus.processKey(key, false);
	}

	public void setRailChangeListenerValue(int n) {
		if (railSlider != null)
			railSlider.setValue(n);
	}

	public void setDelayChangeListenerValue(int n) {
		if (delaySlider != null)
			delaySlider.setValue(n);
	}

	public String getValue() {
		//replace with spaces then trim!!
		return valueTextField.getText().replace(',', ' ').trim();
	}

	public Icon getIcon(String string, int sx, int sy) {
		if (getApplication()) {
			URL url = AbacusApplet.class.getResource("/" + string);

			if (url == null)
				return null;
			return new Icon(Toolkit.getDefaultToolkit().
				getImage(url), sx, sy);
		}
		return new Icon(getImage(getCodeBase(), string),
			sx, sy);
	}

	public AudioClip getAudioClip(String string) {
		if (getApplication()) {
			URL url = AbacusApplet.class.getResource("/" + string);

			if (url == null)
				return null;
			return Applet.newAudioClip(url);
		}
		return getAudioClip(getCodeBase(), string);
	}

	public void playBumpAudio() {
		if (bumpAudioClip == null)
			bumpAudioClip = getAudioClip(AbacusInterface.BUMP_SOUND);
		if (bumpAudioClip != null)
			bumpAudioClip.play();
	}

	public void playMoveAudio() {
		if (moveAudioClip == null)
			moveAudioClip = getAudioClip(AbacusInterface.MOVE_SOUND);
		if (moveAudioClip != null)
			moveAudioClip.play();
	}

	public void playDripAudio() {
		if (dripAudioClip == null)
			dripAudioClip = getAudioClip(AbacusInterface.DRIP_SOUND);
		if (dripAudioClip != null)
			dripAudioClip.play();
	}

	public void setSize(int width, int height) {
		if (abacus == null)
			return;
		//if (getApplication())
		{
			if (leeValue) {
				int newWidth = leftAuxRailsValue * width /
					totalAuxRailsValue;
				int newHeight = height / 4;

				leftAuxAbacus.setBounds(0, 0,
					newWidth, newHeight);
				rightAuxAbacus.setBounds(0, 0,
					width - newWidth, newHeight);
				abacus.setBounds(0, 0,
					width, height - newHeight);
				validate();
			} else {
				abacus.setBounds(0, 0, width, height);
			}
			//repaint(); // causes flashing
		}
		/* else {
			int topOffset = topPanel.getSize().height;
			int ht = height - topOffset;

			//if (controlValue)
			//	topOffset += menuBar.getSize().height;
			if (leeValue) {
				int newWidth = leftAuxRailsValue * width /
					totalAuxRailsValue;
				int newHeight = ht / 4;

				leftAuxAbacus.setBounds(0, 0,
					newWidth, newHeight);
				rightAuxAbacus.setBounds(0, 0,
					width - newWidth, newHeight);
				abacus.setBounds(0, topOffset,
					width, ht - newHeight);
			} else {
				abacus.setBounds(0, topOffset,
					width, ht);

			}
			super.setSize(width, height);
			validate();
		}*/
	}

	public void paint(Graphics g) {
		//if (getApplication())
		{
			int topOffset = topPanel.getSize().height;

			if (controlValue)
				topOffset += menuBar.getSize().height;
			setSize(getSize().width, getSize().height - topOffset);
		}
		super.paint(g);
	}

	public void writeDemoCookie(StringBuffer sb) {
		stringBufferSave = sb;

		try {
			if (!getApplication()) {
				netscape.javascript.JSObject win =
					netscape.javascript.JSObject.getWindow(
					this);
				win.call("writeDemoCookie", null);
			}
		} catch (Exception e) {
			//e.printStackTrace();
			showMessage("no Cookies or JavaScript");
		}
	}

	public void readDemoCookie() {
		try {
			if (!getApplication()) {
				netscape.javascript.JSObject win =
					netscape.javascript.JSObject.getWindow(
					this);
				win.call("readDemoCookie", null);
			}
		} catch (Exception e) {
			//e.printStackTrace();
			showMessage("no Cookies or JavaScript");
		}
	}

	public void shuffleDown() {
		try {
			if (getApplication()) {
				ComponentUtil.findJFrame(this).toBack();
			} else {
				netscape.javascript.JSObject win =
					netscape.javascript.JSObject.getWindow(
					this);
				win.call("shuffleDown", null);
			}
		} catch (Exception e) {
			//e.printStackTrace();
			return;
		}
	}

	public void quit() {
		try {
			if (getApplication()) {
				System.exit(0);
			} else {
				netscape.javascript.JSObject win =
					netscape.javascript.JSObject.getWindow(
					this);
				win.call("quit", null);
			}
		} catch (Exception e) {
			//e.printStackTrace();
			return;
		}
	}

	class RailChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent event) {
			int oldValue = railsValue, min;

			railsValue = railSlider.getValue();
			min = ((signValue) ? 1 : 0) +
				((bottomPieceValue == 0) ? 0 : 1) +
				((bottomPiecePercentValue == 0) ? 0 :
				1 + shiftPercentValue) +
				((demoValue) ? AbacusInterface.MIN_DEMO_RAILS :
				AbacusInterface.MIN_RAILS);
			if (railsValue < min) {
				/* Prohibit possible endless recursion */
				if (oldValue >= min)
					railsValue = oldValue;
				else
					railsValue = 2 * shiftPercentValue + 1;
				railSlider.setValue(railsValue);
				return;
			}
			if (railsValue != oldValue) {
				abacus.shiftRails(oldValue, railsValue,
					false, false, false);
			}
		}
	}


	class ComboBoxActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			int oldValue = modeValue;
			String modeString = (String) ((PartialDisableComboBox)event.getSource()).getSelectedItem();

			modeValue = AbacusInterface.Modes.Generic.ordinal();
			for (AbacusInterface.Modes mode : EnumSet.range(AbacusInterface.Modes.Chinese, AbacusInterface.Modes.Medieval)) {
				if (modeString.indexOf(mode.toString()) >= 0) {
					modeValue = mode.ordinal();
					try {
						mediumIcon = getIcon(AbacusInterface.ICONS_32x32[modeValue], 32, 32);
						largeIcon = getIcon(AbacusInterface.ICONS_48x48[modeValue], 48, 48);
					} catch (Exception e) {
						break;
					}
					break;
				}
			}
			if (modeValue != AbacusInterface.Modes.Generic.ordinal() &&
					oldValue != modeValue) {
				boolean allow;

				abacus.setMode(modeValue);
				abacus.formatRails(0);
				allow = bottomPieceValue != 0 && bottomPiecePercentValue == 0 && abacus.getDecimalPosition() == 3 && abacus.getSlot();
				subdeckMenuItem.setEnabled(allow);
				eighthMenuItem.setEnabled(allow);
				museumMenu.setEnabled(allow && abacus.getSubdeck() > 0);
				museumValue = abacus.getMuseum();
				museumMenuItem[museumValue].setSelected(true);
				//make sure digits are not lost when
				//chinese -> japanese
			}
		}
	}

	class DisplayBaseChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent event) {
			int oldValue = displayBaseValue;

			displayBaseValue = displaySlider.getValue();
			if (oldValue != displayBaseValue)
				return;
			abacus.setDisplayBaseAbacus(displayBaseValue);
			if (leeValue && leftAuxAbacus != null) {
				leftAuxAbacus.setDisplayBaseAbacus(displayBaseValue);
			}
			if (leeValue && rightAuxAbacus != null) {
				rightAuxAbacus.setDisplayBaseAbacus(displayBaseValue);
			}
		}
	}

	class AbacusBaseChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent event) {
			int oldValue = baseValue;

			baseValue = baseSlider.getValue();
			if (oldValue == baseValue)
				return;
			if (bottomNumberValue == oldValue && topSpacesValue == 0) {
				topFactorValue = AbacusCanvas.convertBaseToBottom(baseValue);
				bottomNumberValue = baseValue;
			} else if (topNumberValue * topFactorValue == oldValue) {
				topFactorValue = AbacusCanvas.convertBaseToBottom(baseValue);
				bottomNumberValue = topFactorValue;
				topNumberValue = baseValue / topFactorValue;
			} else if ((topNumberValue + 1) * topFactorValue == oldValue) {
				topFactorValue = AbacusCanvas.convertBaseToBottom(baseValue);
				bottomNumberValue = topFactorValue - 1;
				topNumberValue = baseValue / topFactorValue - 1;
			} else {
				System.err.println("Base adjustment, strange format: topNumber " +
					topNumberValue +
					", topFactor " + topFactorValue +
					", newBase " + baseValue +
					", oldBase " + oldValue);
			}
			abacus.setBaseAbacus(baseValue);
			if (leeValue && leftAuxAbacus != null) {
				leftAuxAbacus.setBaseAbacus(baseValue);
			}
			if (leeValue && rightAuxAbacus != null) {
				rightAuxAbacus.setBaseAbacus(baseValue);
			}
		}
	}

	class DelayChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent event) {
			int oldValue = delayValue;

			delayValue = inc * delaySlider.getValue();
			if (delayValue < 0) {
				/* Prohibit possible endless recursion */
				if (oldValue > 0)
					delayValue = 0;
				delaySlider.setValue(delayValue / inc);
				return;
			}
			if (delayValue != oldValue) {
				abacus.setDelay(delayValue);
			}
		}
	}

	class MuseumListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			int oldValue = abacus.getMuseum();

			try {
				int newValue = Integer.parseInt(event.getActionCommand());
				
				if (newValue != oldValue) {
					museumValue = newValue;
					abacus.setMuseum(museumValue);
				}
			} catch (NumberFormatException e) {
				return;
			}
		}
	}

	private void forceDemoParams() {
		if (modeValue == AbacusInterface.Modes.Generic.ordinal()) {
			topNumberValue = AbacusInterface.DEFAULT_TOP_NUMBER;
			bottomNumberValue = AbacusInterface.DEFAULT_BOTTOM_NUMBER;
			topFactorValue = AbacusInterface.DEFAULT_TOP_FACTOR;
			bottomOrientValue = AbacusInterface.DEFAULT_BOTTOM_ORIENT;
			topSpacesValue = AbacusInterface.DEFAULT_TOP_SPACES;
			modeValue = AbacusInterface.Modes.Chinese.ordinal();
			baseValue = AbacusInterface.DEFAULT_BASE;
			displayBaseValue = AbacusInterface.DEFAULT_BASE;
			abacus.setValuesModeAbacus(
				topOrientValue, bottomOrientValue,
				topNumberValue, bottomNumberValue,
				topFactorValue, bottomFactorValue,
				topSpacesValue, bottomSpacesValue,
				baseValue, displayBaseValue, modeValue);
			modeComboBox.setSelectedItem(Integer.valueOf(modeValue));
			displaySlider.setValue(displayBaseValue);
			baseSlider.setValue(baseValue);
			if (leeValue && leftAuxAbacus != null) {
				leftAuxAbacus.setValuesBaseAbacus(baseValue, displayBaseValue);
			}
			if (leeValue && rightAuxAbacus != null) {
				rightAuxAbacus.setValuesBaseAbacus(baseValue, displayBaseValue);
			}
			try {
				mediumIcon = getIcon(AbacusInterface.ICONS_32x32[modeValue], 32, 32);
				largeIcon = getIcon(AbacusInterface.ICONS_48x48[modeValue], 48, 48);
			} catch (Exception e) {
				return;
			}
		} else if (baseValue != AbacusInterface.DEFAULT_BASE ||
				displayBaseValue != AbacusInterface.DEFAULT_BASE) {
			baseValue = AbacusInterface.DEFAULT_BASE;
			displayBaseValue = AbacusInterface.DEFAULT_BASE;
			displaySlider.setValue(displayBaseValue);
			baseSlider.setValue(baseValue);
			abacus.setValuesBaseAbacus(baseValue, displayBaseValue);
			if (leeValue && leftAuxAbacus != null) {
				leftAuxAbacus.setValuesBaseAbacus(baseValue, displayBaseValue);
			}
			if (leeValue && rightAuxAbacus != null) {
				rightAuxAbacus.setValuesBaseAbacus(baseValue, displayBaseValue);
			}
		}
	}

	private void forceTeachParams() {
		if (displayBaseValue != baseValue || !signValue ||
				anomalyValue != 0 || anomalySqValue != 0) {
			displayBaseValue = baseValue;
			signValue = false;
			anomalyValue = 0;
			anomalySqValue = 0;
			if (displaySlider != null)
				displaySlider.setValue(displayBaseValue);
			abacus.setValuesTeachAbacus(displayBaseValue,
				signValue, anomalyValue, anomalySqValue);
			if (leeValue && leftAuxAbacus != null) {
				leftAuxAbacus.setValuesBaseAbacus(baseValue, displayBaseValue);
				leftAuxAbacus.setValuesTeachAbacus(displayBaseValue,
					signValue, anomalyValue, anomalySqValue);
			}
			if (leeValue && rightAuxAbacus != null) {
				rightAuxAbacus.setValuesBaseAbacus(baseValue, displayBaseValue);
				rightAuxAbacus.setValuesTeachAbacus(displayBaseValue,
					signValue, anomalyValue, anomalySqValue);
			}
		}
	}

	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();

		if ("Detach".equals(command)) {
			popoutApplet();
			return;
		} else if ("Attach".equals(command)) {
			popinApplet();
			return;
		}
		for (int opt = 0; opt < commandString.length; opt++)
			if (command.equals(commandString[opt]))
				processKey(commandChar[opt]);
	}

	public void callbackAbacusDemo(int aux, int deck, int rail, int number) {
		if (leeValue && aux == AbacusInterface.LEFT_AUX)
			leftAuxAbacus.directMove(deck, rail, number);
		else if (leeValue && aux == AbacusInterface.RIGHT_AUX)
			rightAuxAbacus.directMove(deck, rail, number);
		else if (aux == AbacusInterface.PRIMARY)
			abacus.directMove(deck, rail, number);
	}

	public void callbackAbacus(AbacusCanvas ab, String value) {
		if (leeValue && leftAuxAbacus.equals(ab)) {
			if ((value.equals("0.0") || value.equals("0")) &&
					leftAuxValueTextPrompt.isVisible())
				return;
			leftAuxValueTextField.setText(value);
		} else if (leeValue && rightAuxAbacus.equals(ab)) {
			if ((value.equals("0.0") || value.equals("0")) &&
					rightAuxValueTextPrompt.isVisible())
				return;
			rightAuxValueTextField.setText(value);
		} else {
			if (leeValue && (value.equals("0.0") || value.equals("0")) &&
					valueTextPrompt.isVisible())
				return;
			valueTextField.setText(value);
		}
	}

	public static void callbackAbacus(int aux, int deck, int rail, int number) {
		System.out.println(aux + " " + deck + " " + rail +
			" " + number + " 4");
		System.out.println("Lesson" + nl + nl + nl +
			"Press Space-bar");
	}

	static StringBuffer cleanString(StringBuffer dirty) {
		int i, j = 0;

		for (i = 0; i < dirty.length(); i++) {
			if (dirty.charAt(i) == '[' ||
					dirty.charAt(i) == ']') {
				dirty.setLength(j);
				return dirty;
			} else if (dirty.charAt(i) != ' ' &&
					dirty.charAt(i) != '\t') {
				dirty.setCharAt(j, dirty.charAt(i));
				j++;
			}
		}
		return dirty;
	}

	public void clearAllBeads(int aux) {
		if (aux == 0) {
			valueTextField.setText("0.0");
		} else if (leeValue && aux == AbacusInterface.LEFT_AUX) {
			leftAuxAbacus.clearAllBeads();
		} else if (leeValue && aux == AbacusInterface.RIGHT_AUX) {
			rightAuxAbacus.clearAllBeads();
		}
	}

	public void callbackAbacus(AbacusCanvas ab, int reason) {
		boolean allow;

		switch(reason) {
		/*case AbacusInterface.ACTION_SCRIPT:
			break;*/
		case AbacusInterface.ACTION_MOVE:
			break;
		case AbacusInterface.ACTION_CLEAR:
			if (leeValue) {
				leftAuxAbacus.clearAllBeads();
				rightAuxAbacus.clearAllBeads();
				//leftAuxValueTextField.setText("0.0");
				//rightAuxValueTextField.setText("0.0");
			}
			valueTextField.setText("0.0");
			break;
		case AbacusInterface.ACTION_INCREMENT:
			if (!abacus.equals(ab))
				return;
			railsValue++;
			if (railSlider != null)
				railSlider.setValue(railsValue);
			break;
		case AbacusInterface.ACTION_DECREMENT:
			if (!abacus.equals(ab))
				return;
			railsValue--;
			if (railSlider != null)
				railSlider.setValue(railsValue);
			break;
		case AbacusInterface.ACTION_DEMO:
			if (!abacus.equals(ab))
				return;
			if (getTest() || getTeach())
				return;
			forceDemoParams();
			demoValue = !demoValue;
			break;
		case AbacusInterface.ACTION_TEACH:
			if (!abacus.equals(ab))
				return;
			if (getTest() || demoValue)
				return;
			forceTeachParams();
			if (getTeach())
				killTeachDialog();
			else
				createTeachDialog();
			break;
		case AbacusInterface.ACTION_PLACE:
			allow = bottomPieceValue != 0 && abacus.getDecimalPosition() >= 3;
			secondaryRailsMenu.setEnabled(allow);
			quarterPercentMenuItem.setEnabled(allow);
			allow = bottomPieceValue != 0 && abacus.getDecimalPosition() == 3 && abacus.getSlot();
			subdeckMenuItem.setEnabled(allow);
			eighthMenuItem.setEnabled(allow);
			museumMenu.setEnabled(allow && abacus.getSubdeck() > 0);
			break;
		case AbacusInterface.ACTION_FORMAT:
			if (!abacus.equals(ab))
				return;
			modeValue = modeValue + 1;
			if (modeValue < 0 || modeValue >= AbacusInterface.Modes.values().length - 1)
				modeValue = AbacusInterface.Modes.Chinese.ordinal();
			modeComboBox.setSelectedIndex(modeValue);
			allow = bottomPieceValue != 0 && bottomPiecePercentValue == 0 && abacus.getDecimalPosition() == 3 && abacus.getSlot();
			subdeckMenuItem.setEnabled(allow);
			eighthMenuItem.setEnabled(allow);
			museumMenu.setEnabled(allow && abacus.getSubdeck() > 0);
			museumValue = abacus.getMuseum();
			museumMenuItem[museumValue].setSelected(true);
			try {
				mediumIcon = getIcon(AbacusInterface.ICONS_32x32[modeValue], 32, 32);
				largeIcon = getIcon(AbacusInterface.ICONS_48x48[modeValue], 48, 48);
			} catch (Exception e) {
				break;
			}
			break;
		case AbacusInterface.ACTION_ROMAN_NUMERALS:
			if (!abacus.equals(ab))
				return;
			romanNumeralsValue = !romanNumeralsValue;
			romanNumeralsMenuItem.setSelected(romanNumeralsValue);
			break;
		case AbacusInterface.ACTION_GROUP:
			if (!abacus.equals(ab))
				return;
			groupValue = !groupValue;
			groupMenuItem.setSelected(groupValue);
			break;
		case AbacusInterface.ACTION_SIGN:
			if (!abacus.equals(ab))
				return;
			signValue = !signValue;
			signMenuItem.setSelected(signValue);
			break;
		case AbacusInterface.ACTION_QUARTER:
			if (!abacus.equals(ab))
				return;
			topPieceValue = 0;
			if (bottomPieceValue == 0) {
				bottomPieceValue = 4;
			} else {
				bottomPieceValue = 0;
			}
			quarterMenuItem.setEnabled(true);
			quarterMenuItem.setSelected(bottomPieceValue != 0);
			twelfthMenuItem.setEnabled(bottomPieceValue == 0 && bottomPiecePercentValue == 0);
			allow = bottomPieceValue != 0 && abacus.getDecimalPosition() >= 3;
			secondaryRailsMenu.setEnabled(allow);
			quarterPercentMenuItem.setEnabled(allow);
			allow = bottomPieceValue != 0 && abacus.getDecimalPosition() == 3 && abacus.getSlot();
			subdeckMenuItem.setEnabled(allow);
			eighthMenuItem.setEnabled(allow);
			museumMenu.setEnabled(allow && abacus.getSubdeck() > 0);
			museumValue = abacus.getMuseum();
			museumMenuItem[museumValue].setSelected(true);
			break;
		case AbacusInterface.ACTION_TWELFTH:
			if (!abacus.equals(ab))
				return;
			if (bottomPieceValue == 0) {
				bottomPieceValue = 6;
				topPieceValue = 2;
			} else {
				bottomPieceValue = 0;
				topPieceValue = 0;
			}
			twelfthMenuItem.setEnabled(true);
			twelfthMenuItem.setSelected(bottomPieceValue != 0);
			quarterMenuItem.setEnabled(bottomPieceValue == 0 && bottomPiecePercentValue == 0);
			allow = bottomPieceValue != 0 && abacus.getDecimalPosition() >= 3;
			secondaryRailsMenu.setEnabled(allow);
			quarterPercentMenuItem.setEnabled(allow);
			allow = bottomPieceValue != 0 && abacus.getDecimalPosition() == 3 && abacus.getSlot();
			subdeckMenuItem.setEnabled(allow);
			eighthMenuItem.setEnabled(allow);
			museumMenu.setEnabled(allow && abacus.getSlot() && abacus.getSubdeck() > 0);
			museumValue = abacus.getMuseum();
			museumMenuItem[museumValue].setSelected(true);
			break;
		case AbacusInterface.ACTION_QUARTER_PERCENT:
			if (!abacus.equals(ab))
				return;
			topPiecePercentValue = 0;
			if (bottomPiecePercentValue == 0) {
				bottomPiecePercentValue = 4;
			} else {
				bottomPiecePercentValue = 0;
			}
			quarterPercentMenuItem.setEnabled(true);
			allow = bottomPieceValue != 0 && topPiecePercentValue == 0 && bottomPiecePercentValue == 4;
			quarterPercentMenuItem.setSelected(allow);
			allow = bottomPiecePercentValue == 0;
			quarterMenuItem.setEnabled(allow && bottomPieceValue == 4);
			twelfthMenuItem.setEnabled(allow && bottomPieceValue == 6);
			allow = bottomPieceValue != 0 && bottomPiecePercentValue == 0 && abacus.getDecimalPosition() == 3 && abacus.getSlot();
			subdeckMenuItem.setEnabled(allow);
			subdeckMenuItem.setSelected(false);
			eighthMenuItem.setEnabled(allow);
			eighthMenuItem.setSelected(false);
			museumMenu.setEnabled(allow && abacus.getSubdeck() > 0);
			museumValue = abacus.getMuseum();
			museumMenuItem[museumValue].setSelected(true);
			break;
		case AbacusInterface.ACTION_SUBDECK:
		case AbacusInterface.ACTION_EIGHTH:
			if (!abacus.equals(ab))
				return;
			subdeckValue = abacus.getSubdeck();
			allow = subdeckValue == 0;
			quarterMenuItem.setEnabled(allow && bottomPieceValue == 4);
			twelfthMenuItem.setEnabled(allow && bottomPieceValue == 6);
			quarterPercentMenuItem.setEnabled(allow);
			allow = bottomPieceValue != 0 && bottomPiecePercentValue == 0 && abacus.getDecimalPosition() == 3 && abacus.getSlot();
			subdeckMenuItem.setEnabled(allow && (subdeckValue == 0 || (subdeckValue > 0 && abacus.getSubbase() == 12)));
			subdeckMenuItem.setSelected(allow && subdeckValue > 0 && abacus.getSubbase() == 12);
			eighthMenuItem.setEnabled(allow && (subdeckValue == 0 || (subdeckValue > 0 && abacus.getSubbase() == 8)));
			eighthMenuItem.setSelected(allow && subdeckValue > 0 && abacus.getSubbase() == 8);
			museumMenu.setEnabled(allow && subdeckValue > 0);
			museumValue = abacus.getMuseum();
			museumMenuItem[museumValue].setSelected(true);
			break;
		case AbacusInterface.ACTION_MUSEUM:
			museumValue = abacus.getMuseum();
			museumMenuItem[museumValue].setSelected(true);
			break;
		case AbacusInterface.ACTION_ANOMALY:
			if (!abacus.equals(ab))
				return;
			if (anomalyValue == 0) {
				anomalyValue = 2;
				anomalySqValue = 0;
			} else {
				anomalyValue = 0;
				anomalySqValue = 0;
			}
			anomalyMenuItem.setEnabled(true);
			watchMenuItem.setEnabled(anomalyValue == 0);
			anomalyMenuItem.setSelected(anomalyValue != 0);
			watchMenuItem.setSelected(false);
			break;
		case AbacusInterface.ACTION_WATCH:
			if (!abacus.equals(ab))
				return;
			if (anomalyValue == 0) {
				anomalyValue = 4;
				anomalySqValue = 4;
			} else {
				anomalyValue = 0;
				anomalySqValue = 0;
			}
			anomalyMenuItem.setEnabled(anomalySqValue == 0);
			watchMenuItem.setEnabled(true);
			anomalyMenuItem.setSelected(false);
			watchMenuItem.setSelected(anomalySqValue != 0);
			break;
		case AbacusInterface.ACTION_RIGHT_TO_LEFT_ADD:
			if (!abacus.equals(ab))
				return;
			rightToLeftAddMenuItem.setSelected(rightToLeftAddValue);
			break;
		case AbacusInterface.ACTION_RIGHT_TO_LEFT_MULT:
			if (!abacus.equals(ab))
				return;
			rightToLeftMultMenuItem.setSelected(rightToLeftMultValue);
			break;
		case AbacusInterface.ACTION_SPEED_UP:
			delayValue -= inc;
			if (delayValue < 0)
				break;
			delayValue -= inc;
			if (delayValue < 0)
				delayValue = 0;
			if (delaySlider != null)
				delaySlider.setValue(delayValue / inc);
			break;
		case AbacusInterface.ACTION_SLOW_DOWN:
			if (delayValue > 500)
				break;
			delayValue += inc;
			if (delayValue > 500)
				delayValue = 500;
			if (delaySlider != null)
				delaySlider.setValue(delayValue / inc);
			break;
		default:
			System.out.println("Callback:" + reason);
		}
	}

	public void init() {
		/*JPanel info0Panel = new JPanel();
		JPanel leftPanel = new JPanel();
		JPanel rightPanel = new JPanel();*/
		JPanel startPanel = new JPanel();
		JPanel info1Panel = new JPanel();
		JPanel info2Panel = new JPanel();
		JPanel valuePanel = new JPanel();
		JPanel leftAuxValuePanel = new JPanel();
		JPanel rightAuxValuePanel = new JPanel();
		JPanel basicControlPanel = new JPanel();
		JPanel comboBoxPanel = new JPanel();
		JPanel canvasPanel = new JPanel();
		JPanel leftAuxCanvasPanel = new JPanel();
		JPanel rightAuxCanvasPanel = new JPanel();

		/*// Determine what the default GraphicsDevice can support.
		GraphicsEnvironment ge =
			GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();

		boolean isShapedWindowSupported =
			gd.isWindowTranslucencySupported(PERPIXEL_TRANSPARENT);*/

		generator = new Random(System.nanoTime());
		getOpts();
		try {
			mediumIcon = getIcon(AbacusInterface.ICONS_32x32[modeValue], 32, 32);
			largeIcon = getIcon(AbacusInterface.ICONS_48x48[modeValue], 48, 48);
		} catch (Exception e) {
			//
		}
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBackground(Color.white);
		if (controlValue) {
			menuBar = new JMenuBar();
			JMenu menu, submenu;
			JMenuItem menuItem;
			JCheckBoxMenuItem soundMenuItem;

			// Allows menus to be on top of canvas
			JPopupMenu.setDefaultLightWeightPopupEnabled(false);
			menu = new JMenu("File");
			// Comment out next line to DEBUG
			if (!getApplication()) {
				menu.add(detachMenuItem = new JMenuItem("Detach"));
				detachMenuItem.setMnemonic(KeyEvent.VK_H);
				detachMenuItem.addActionListener(this);
				menu.addSeparator();
			}
			menu.add(menuItem = new JMenuItem("Exit"));
			menuItem.setMnemonic(KeyEvent.VK_X);
			menuItem.addActionListener(this);
			menuBar.add(menu);
			menu = new JMenu("Controls");
			submenu = new JMenu("Bead Control");
			submenu.add(menuItem = new JMenuItem("Clear"));
			menuItem.setMnemonic(KeyEvent.VK_C);
			menuItem.addActionListener(this);
			submenu.add(menuItem = new JMenuItem("Complement ~"));
			menuItem.addActionListener(this);
			menu.add(submenu);
			/* redundant */
			/*submenu = new JMenu("Basic Format");
			submenu.add(menuItem = new JMenuItem("Format"));
			menuItem.setMnemonic(KeyEvent.VK_F);
			menuItem.addActionListener(this);
			submenu.add(menuItem = new JMenuItem("Increment"));
			menuItem.setMnemonic(KeyEvent.VK_I);
			menuItem.addActionListener(this);
			submenu.add(menuItem = new JMenuItem("Decrement"));
			menuItem.setMnemonic(KeyEvent.VK_D);
			menuItem.addActionListener(this);
			menu.add(submenu);*/
			submenu = new JMenu("Base Settings");
			submenu.add(displaySlider = new Slider("Display Base",
				displayBaseValue,
				AbacusInterface.MIN_BASE, AbacusInterface.MAX_BASE,
				126));
			submenu.add(baseSlider = new Slider("Abacus Base",
				baseValue,
				AbacusInterface.MIN_BASE, AbacusInterface.MAX_BASE,
				126));
			menu.add(submenu);
			submenu = new JMenu("Display Format");
			submenu.add(romanNumeralsMenuItem = new JCheckBoxMenuItem(
				"Roman Nvmerals", romanNumeralsValue));
			romanNumeralsMenuItem.setMnemonic(KeyEvent.VK_V);
			romanNumeralsMenuItem.addActionListener(this);
			/*submenu.add(oldRomanNumeralsMenuItem = new JCheckBoxMenuItem(
				"Ancient Roman Numerals )", ancientRomanNumeralsValue));
			ancientRomanNumeralsMenuItem.addActionListener(this);*/
			submenu.add(groupMenuItem = new JCheckBoxMenuItem(
				"Group", groupValue));
			groupMenuItem.setMnemonic(KeyEvent.VK_G);
			groupMenuItem.addActionListener(this);
			menu.add(submenu);
			submenu = new JMenu("Special Rails");
			submenu.add(signMenuItem = new JCheckBoxMenuItem(
				"Sign", signValue));
			signMenuItem.setMnemonic(KeyEvent.VK_S);
			signMenuItem.addActionListener(this);
			submenu.add(quarterMenuItem = new JCheckBoxMenuItem(
				"Quarter", (bottomPieceValue == 4)));
			quarterMenuItem.setMnemonic(KeyEvent.VK_U);
			quarterMenuItem.addActionListener(this);
			submenu.add(twelfthMenuItem = new JCheckBoxMenuItem(
				"Twelfth", (bottomPieceValue == 6)));
			twelfthMenuItem.setMnemonic(KeyEvent.VK_T);
			twelfthMenuItem.addActionListener(this);
			secondaryRailsMenu = new JMenu("Secondary Rails");
			secondaryRailsMenu.add(quarterPercentMenuItem = new JCheckBoxMenuItem(
				"Quarter Percent", (bottomPiecePercentValue == 4)));
			quarterPercentMenuItem.setMnemonic(KeyEvent.VK_P);
			quarterPercentMenuItem.addActionListener(this);
			quarterMenuItem.setEnabled(bottomPieceValue != 6 && bottomPiecePercentValue == 0);
			twelfthMenuItem.setEnabled(bottomPieceValue != 4 && bottomPiecePercentValue == 0);
			secondaryRailsMenu.add(subdeckMenuItem = new JCheckBoxMenuItem("Subdeck"));
			subdeckMenuItem.setMnemonic(KeyEvent.VK_B);
			subdeckMenuItem.addActionListener(this);
			secondaryRailsMenu.add(eighthMenuItem = new JCheckBoxMenuItem("Eighth"));
			eighthMenuItem.setMnemonic(KeyEvent.VK_E);
			eighthMenuItem.addActionListener(this);
			//museumMenuItem.setMnemonic(KeyEvent.VK_M);
			museumMenu = new JMenu("Museum");
			ButtonGroup group = new ButtonGroup();
			museumMenuItem = new JRadioButtonMenuItem[AbacusInterface.MAX_MUSEUMS];
			for (int i = 0; i < AbacusInterface.MAX_MUSEUMS; i++) {
				museumMenu.add(museumMenuItem[i] =
					new JRadioButtonMenuItem(AbacusInterface.museumCountry[i]));
				museumMenuItem[i].setActionCommand(Integer.toString(i));
				group.add(museumMenuItem[i]);
			}
			secondaryRailsMenu.add(museumMenu);
			submenu.add(secondaryRailsMenu);
			submenu.addSeparator();
			submenu.add(anomalyMenuItem = new JCheckBoxMenuItem(
				"Anomaly", anomalyValue != 0 && anomalySqValue == 0));
			anomalyMenuItem.setMnemonic(KeyEvent.VK_L);
			anomalyMenuItem.addActionListener(this);
			anomalyMenuItem.setEnabled(anomalySqValue == 0);
			submenu.add(watchMenuItem = new JCheckBoxMenuItem(
				"Watch", anomalyValue != 0 && anomalySqValue != 0));
			watchMenuItem.setMnemonic(KeyEvent.VK_W);
			watchMenuItem.addActionListener(this);
			watchMenuItem.setEnabled(anomalySqValue != 0 || anomalyValue == 0);
			menu.add(submenu);
			submenu = new JMenu("Teach Options");
			submenu.add(rightToLeftAddMenuItem = new JCheckBoxMenuItem(
				"Right To Left Add +", rightToLeftAddValue));
			rightToLeftAddMenuItem.addActionListener(this);
			submenu.add(rightToLeftMultMenuItem = new JCheckBoxMenuItem(
				"Right To Left Mult *", rightToLeftMultValue));
			rightToLeftMultMenuItem.addActionListener(this);
			menu.add(submenu);
			submenu = new JMenu("Effects");
			submenu.add(soundMenuItem = new JCheckBoxMenuItem(
				"Sound @", sound));
			delaySlider = new Slider("msec Delay",
				5, 0, 50, 126);
			delaySlider.setIncrement(inc);
			submenu.add(delaySlider);
			soundMenuItem.addActionListener(this);
			menu.add(submenu);
			menuBar.add(menu);
			menu = new JMenu("Learn");
			menu.add(menuItem = new JMenuItem("Demo"));
			menuItem.setMnemonic(KeyEvent.VK_O);
			menuItem.addActionListener(this);
			menu.add(menuItem = new JMenuItem("Teach $"));
			menuItem.addActionListener(this);
			menuBar.add(menu);
			menu = new JMenu("Help");
			menu.add(menuItem = new JMenuItem("Description ?"));
			menuItem.addActionListener(this);
			menu.add(menuItem = new JMenuItem("Features !"));
			menuItem.addActionListener(this);
			menu.add(menuItem = new JMenuItem("References ^"));
			menuItem.addActionListener(this);
			menu.add(menuItem = new JMenuItem("About"));
			menuItem.setMnemonic(KeyEvent.VK_A);
			menuItem.addActionListener(this);
			menuBar.add(menu);
			//setJMenuBar(menuBar); // needs to be in a panel
			mainPanel.add(menuBar, BorderLayout.PAGE_START);
			/*leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			leftPanel.add(new JLabel("Move bead"));
			rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			rightPanel.add(new JLabel("Clear"));
			leftPanel.add(getIcon("/icons/mouse-l.png", 16, 16));
			rightPanel.add(getIcon("/icons/mouse-r.png", 16, 16));
			info0Panel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));
			info0Panel.add(leftPanel);
			info0Panel.add(rightPanel);*/
			railSlider = new Slider("Abacus Size",
				railsValue, AbacusInterface.MIN_RAILS,
				((railsValue > MAX_RAILS) ? railsValue : MAX_RAILS),
				126);
			basicControlPanel.setLayout(new FlowLayout(FlowLayout.LEFT,
				15, 5));
			comboBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT,
				5, 5));
			comboBoxPanel.add(new JLabel("Format:"));
			for (AbacusInterface.Modes mode : AbacusInterface.Modes.values()) {
				modeComboBox.addItem(mode.toString() + " " +
					AbacusInterface.formatStrings[mode.ordinal()],
					(mode != AbacusInterface.Modes.Generic));
			}
			modeComboBox.setLightWeightPopupEnabled(false);
			modeComboBox.setSelectedItem(
				AbacusInterface.Modes.values()[modeValue].toString() +
					" " + AbacusInterface.formatStrings[modeValue]);
			comboBoxPanel.add(modeComboBox);
			basicControlPanel.add(railSlider);
			basicControlPanel.add(comboBoxPanel);
		}
		if (leeValue) {
			leftAuxValuePanel.setLayout(new FlowLayout(FlowLayout.LEFT,
				0, 0));
			rightAuxValuePanel.setLayout(new FlowLayout(FlowLayout.LEFT,
				0, 0));
			leftAuxValueTextField.setColumns(valueTextLength *
				leftAuxRailsValue / totalAuxRailsValue + 1);
			rightAuxValueTextField.setColumns(valueTextLength *
				rightAuxRailsValue / totalAuxRailsValue + 1);
			leftAuxValuePanel.add(leftAuxValueTextField);
			rightAuxValuePanel.add(rightAuxValueTextField);
			if (testValue == 0) {
				info1Panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
				info1Panel.add(leftAuxValuePanel);
				info1Panel.add(rightAuxValuePanel);
			}
			leftAuxValueTextPrompt = new TextPrompt("Left Auxiliary",
				leftAuxValueTextField);
			leftAuxValueTextPrompt.changeStyle(Font.ITALIC);
			leftAuxValueTextPrompt.setForeground(leftAuxBeadColor.darker().darker());
			rightAuxValueTextPrompt = new TextPrompt("Right Auxiliary",
				rightAuxValueTextField);
			rightAuxValueTextPrompt.changeStyle(Font.ITALIC);
			rightAuxValueTextPrompt.setForeground(rightAuxBeadColor.darker().darker());
			valueTextPrompt = new TextPrompt("Primary", valueTextField);
			valueTextPrompt.changeStyle(Font.ITALIC + Font.BOLD);
			valueTextPrompt.setForeground(primaryBeadColor.darker().darker());
		}
		if (!controlValue)
			info1Panel.setBackground(bgColor);
		if (testValue == 0) {
			valuePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			valueTextField.setColumns(valueTextLength);
			//valueTextField.setEditable(false);
			valuePanel.add(valueTextField);
			info2Panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
			if (!controlValue)
				info2Panel.setBackground(bgColor);
			info2Panel.add(valuePanel);
		}
		topPanel.setLayout(new ColumnLayout(0, 0, 0,
			ColumnLayout.CENTER));
		/*topPanel.add(info0Panel);*/
		if (controlValue) {
			topPanel.add(basicControlPanel);
		} else
			topPanel.setBackground(bgColor);
		if (testValue == 0 && leeValue) {
			topPanel.add(info1Panel);
		}
		if (testValue == 0)
			topPanel.add(info2Panel);
		abacus = new Abacus(this,
			fgColor, bgColor, borderColor,
			primaryBeadColor, secondaryBeadColor, highlightBeadColor,
			primaryRailColor, secondaryRailColor, highlightRailColor, lineRailColor,
			delayValue, railsValue, verticalValue,
			colorSchemeValue, slotValue,
			diamondValue, railIndexValue,
			topOrientValue, bottomOrientValue,
			topNumberValue, bottomNumberValue,
			topFactorValue, bottomFactorValue,
			topSpacesValue, bottomSpacesValue,
			topPieceValue, bottomPieceValue,
			topPiecePercentValue, bottomPiecePercentValue,
			shiftPercentValue, subdeckValue, subbeadValue,
			signValue, decimalPositionValue,
			groupValue, groupSizeValue,
			decimalCommaValue, baseValue, subbaseValue,
			anomalyValue, shiftAnomalyValue,
			anomalySqValue, shiftAnomalySqValue,
			displayBaseValue, (pressOffsetValue) ? 1 : 0,
			romanNumeralsValue, latinValue,
			ancientRomanValue, modernRomanValue,
			modeValue, museumValue);
		if (leeValue) {
		int auxModeValue = AbacusInterface.Modes.Japanese.ordinal();
		leftAuxAbacus = new Abacus(this,
			fgColor, bgColor, borderColor,
			leftAuxBeadColor, secondaryBeadColor, highlightBeadColor,
			primaryRailColor, secondaryRailColor, highlightRailColor, lineRailColor,
			delayValue, leftAuxRailsValue, verticalValue,
			colorSchemeValue, slotValue,
			diamondValue, railIndexValue,
			topOrientValue, bottomOrientValue,
			topNumberValue, bottomNumberValue,
			topFactorValue, bottomFactorValue,
			topSpacesValue, bottomSpacesValue,
			topPieceValue, bottomPieceValue,
			topPiecePercentValue, bottomPiecePercentValue,
			shiftPercentValue, subdeckValue, subbeadValue,
			signValue, decimalPositionValue,
			groupValue, groupSizeValue,
			decimalCommaValue, baseValue, subbaseValue,
			anomalyValue, shiftAnomalyValue,
			anomalySqValue, shiftAnomalySqValue,
			displayBaseValue, (pressOffsetValue) ? 1 : 0,
			romanNumeralsValue, latinValue,
			ancientRomanValue, modernRomanValue,
			auxModeValue, museumValue);
		rightAuxAbacus = new Abacus(this,
			fgColor, bgColor, borderColor,
			rightAuxBeadColor, secondaryBeadColor, highlightBeadColor,
			primaryRailColor, secondaryRailColor, highlightRailColor, lineRailColor,
			delayValue, rightAuxRailsValue, verticalValue,
			colorSchemeValue, slotValue,
			diamondValue,railIndexValue,
			topOrientValue, bottomOrientValue,
			topNumberValue, bottomNumberValue,
			topFactorValue, bottomFactorValue,
			topSpacesValue, bottomSpacesValue,
			topPieceValue, bottomPieceValue,
			topPiecePercentValue, bottomPiecePercentValue,
			shiftPercentValue, subdeckValue, subbeadValue,
			signValue, decimalPositionValue,
			groupValue, groupSizeValue,
			decimalCommaValue, baseValue, subbaseValue,
			anomalyValue, shiftAnomalyValue,
			anomalySqValue, shiftAnomalySqValue,
			displayBaseValue, (pressOffsetValue) ? 1 : 0,
			romanNumeralsValue, latinValue,
			ancientRomanValue, modernRomanValue,
			auxModeValue, museumValue);
		}
		startPanel.setLayout(new ColumnLayout(0, 0, 0,
			ColumnLayout.LEFT));
		if (!controlValue)
			startPanel.setBackground(bgColor);
		startPanel.add(topPanel);
		secondaryPanel.setLayout(new BorderLayout());
		secondaryPanel.add(startPanel, BorderLayout.PAGE_START);
		canvasPanel.setBackground(bgColor);
		if (leeValue) {
			leftAuxCanvasPanel.setLayout(new ColumnLayout(0, 0, 0,
				ColumnLayout.LEFT));
			rightAuxCanvasPanel.setLayout(new ColumnLayout(0, 0, 0,
				ColumnLayout.LEFT));
			canvasPanel.setLayout(new ColumnLayout(0, 0, 0,
				ColumnLayout.LEFT));
			leftAuxCanvasPanel.add(leftAuxAbacus);
			rightAuxCanvasPanel.add(rightAuxAbacus);
			canvasPanel.add(abacus);
			JPanel leePanel = new JPanel();
			JPanel extraPanel = new JPanel();
			leePanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.weightx = 0.5;
			c.gridx = 0;
			c.gridy = 0;
			leePanel.add(leftAuxCanvasPanel, c);
			c.anchor = GridBagConstraints.FIRST_LINE_END;
			c.gridx = 1;
			c.gridy = 0;
			leePanel.add(rightAuxCanvasPanel, c);
			c.weightx = 0.0;
			c.gridwidth = 2;
			c.anchor = GridBagConstraints.PAGE_START;
			c.gridx = 0;
			c.gridy = 1;
			leePanel.add(canvasPanel, c);
			extraPanel.setLayout(new ColumnLayout(0, 0, 0,
				ColumnLayout.LEFT));
			extraPanel.add(leePanel);
			secondaryPanel.add(extraPanel, BorderLayout.CENTER);
			extraPanel.setBackground(bgColor);
		} else {
			canvasPanel.setBackground(bgColor);
			canvasPanel.setLayout(new ColumnLayout(0, 0, 0,
				ColumnLayout.LEFT));
			canvasPanel.add(abacus);
			secondaryPanel.add(canvasPanel, BorderLayout.CENTER);
		}
		mainPanel.add(secondaryPanel, BorderLayout.CENTER);
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		if (controlValue) {
			railSlider.addChangeListener(new RailChangeListener());
			modeComboBox.addActionListener(new ComboBoxActionListener());
			displaySlider.addChangeListener(
				new DisplayBaseChangeListener());
			baseSlider.addChangeListener(
				new AbacusBaseChangeListener());
			delaySlider.addChangeListener( new DelayChangeListener());
			for (int i = 0; i < AbacusInterface.MAX_MUSEUMS; i++)
				museumMenuItem[i].addActionListener(new MuseumListener());
		}
		if (leeValue) {
			leftAuxValueTextField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					StringBuffer mathBuffer = new
						StringBuffer(leftAuxValueTextField.getText());
					if (mathBuffer.length() >= 0)
						leftAuxAbacusCalc.calculateAndDisplay(leftAuxAbacus,
							cleanString(mathBuffer).toString(),
							AbacusInterface.LEFT_AUX);
					else
						leftAuxAbacus.clearAllBeads();
				}
			});
			leftAuxCanvasPanel.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent event) {
					leftAuxAbacus.requestFocus();
				}
				public void mouseExited(MouseEvent event) {
					leftAuxAbacus.requestFocus();
				}
				public void mousePressed(MouseEvent event) {
					leftAuxAbacus.requestFocus();
				}
			});
			rightAuxValueTextField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					StringBuffer mathBuffer = new
						StringBuffer(rightAuxValueTextField.getText());

					if (mathBuffer.length() >= 0)
						rightAuxAbacusCalc.calculateAndDisplay(rightAuxAbacus,
							cleanString(mathBuffer).toString(),
							AbacusInterface.RIGHT_AUX);
					else
						rightAuxAbacus.clearAllBeads();
				}
			});
			rightAuxCanvasPanel.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent event) {
					rightAuxAbacus.requestFocus();
				}
				public void mouseExited(MouseEvent event) {
					rightAuxAbacus.requestFocus();
				}
				public void mousePressed(MouseEvent event) {
					rightAuxAbacus.requestFocus();
				}
			});
		}
		valueTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				StringBuffer mathBuffer = new
					StringBuffer(valueTextField.getText());

				if (mathBuffer.length() >= 0)
					abacusCalc.calculateAndDisplay(abacus,
						cleanString(mathBuffer).toString(),
						AbacusInterface.PRIMARY);
				else
					abacus.clearAllBeads();
			}
		});
		canvasPanel.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent event) {
				abacus.requestFocus();
			}
			public void mouseExited(MouseEvent event) {
				abacus.requestFocus();
			}
			public void mousePressed(MouseEvent event) {
				abacus.requestFocus();
			}
		});
		/* abacus.setSize(getSize().width,
			getSize().height - topPanel.getSize().height); */
		if (leeValue) {
			int newWidth = leftAuxRailsValue * widthValue /
				totalAuxRailsValue;
			int newHeight = heightValue / 4;

			leftAuxAbacus.setSize(newWidth, newHeight);
			rightAuxAbacus.setSize(widthValue - newWidth,
				newHeight);
			abacus.setSize(widthValue, heightValue - newHeight);
		} else {
			abacus.setSize(widthValue, heightValue);
		}
		Font boldFont = valueTextField.getFont().deriveFont(Font.BOLD);
		valueTextField.setFont(boldFont);
		if (!getApplication() && testValue != 0) {
			forceDemoParams();
			abacus.checkBeads();
			abacus.resetBeads();
			AbacusTest abacusTest = new AbacusTest(this, abacus);
			abacusTest.getTest();
		}
		if (controlValue) {
			boolean allow;
			// decimalPosition is hard to figure out beforehand
			allow = bottomPieceValue != 0 && (abacus.getDecimalPosition() >= 2 || bottomPiecePercentValue != 0 || abacus.getSubdeck() != 0);
			secondaryRailsMenu.setEnabled(allow);
			allow = bottomPieceValue != 0 && (abacus.getDecimalPosition() >= 2 || bottomPiecePercentValue != 0);
			quarterPercentMenuItem.setEnabled(allow);
			allow = bottomPieceValue != 0 && bottomPiecePercentValue == 0 && (abacus.getDecimalPosition() == 3 || abacus.getSubdeck() > 0 && abacus.getSubbase() == 12) && abacus.getSlot();
			subdeckMenuItem.setEnabled(allow);
			allow = bottomPieceValue != 0 && bottomPiecePercentValue == 0 && abacus.getSubdeck() > 0 && abacus.getSubbase() == 12 && abacus.getSlot();
			subdeckMenuItem.setSelected(allow);
			allow = bottomPieceValue != 0 && bottomPiecePercentValue == 0 && (abacus.getDecimalPosition() == 3 || abacus.getSubdeck() > 0 && abacus.getSubbase() == 8) && abacus.getSlot();
			eighthMenuItem.setEnabled(allow);
			allow = bottomPieceValue != 0 && bottomPiecePercentValue == 0 && abacus.getSubdeck() > 0 && abacus.getSubbase() == 8 && abacus.getSlot();
			eighthMenuItem.setSelected(allow);
			allow = bottomPieceValue != 0 && bottomPiecePercentValue == 0 && (abacus.getDecimalPosition() == 3 || abacus.getSubdeck() > 0) && abacus.getSlot();
			museumMenu.setEnabled(allow);
			//museumValue = generator.nextInt(AbacusInterface.MAX_MUSEUMS);
			museumMenuItem[museumValue].setSelected(true);
		}
	}

	public static void main(String args[]) {
		String laf = UIManager.getCrossPlatformLookAndFeelClassName();

		try {
			UIManager.setLookAndFeel(laf);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		String icon = (nl.compareTo("\n") == 0) ?
			AbacusInterface.ICONS_48x48[0] :
			AbacusInterface.ICONS_16x16[0];
		URL url = AbacusApplet.class.getResource("/" + icon);

		argumentParser = new ArgumentParser(args);
		if (argumentParser.hasOption("?")) {
			System.out.println("usage:" + nl +
"\t[-windowWidth={int}] [-windowHeight={int}]" + nl +
"\t[-windowX={int}] [-windowY={int}]" + nl +
"\t[-primaryBeadColor={int}] [-leftAuxBeadColor={int}]" + nl +
"\t[-rightAuxBeadColor={int}] [-secondaryBeadColor={int}]" + nl +
"\t[-highlightBeadColor={int}]" + nl +
"\t[-primaryRailColor={int}] [-secondaryRailColor={int}]" + nl +
"\t[-highlightRailColor={int}] [-lineRailColor={int}]" + nl +
"\t[-borderColor={int}] [-fgColor={int}] [-bgColor={int}]" + nl +
"\t[-sound={bool}] [-delay={int}] [-script={bool}]" + nl +
"\t[-rightToLeftAdd={bool}] [-rightToLeftMult={bool}]" + nl +
"\t[-control={bool}] [-lee={bool}] [-rails={int}]" + nl +
"\t[-leftAuxRails={int}] [-rightAuxRails={int}] [-vertical={bool}]" + nl +
"\t[-colorScheme={int}] [-slot={bool}] [-diamond={bool}]" + nl +
"\t[-railIndex={int}] [-topOrient={bool}] [-bottomOrient={bool}]" + nl +
"\t[-topNumber={int}] [-bottomNumber={int}] [-topFactor={int}]" + nl +
"\t[-bottomFactor={int}] [-topSpaces={int}] [-bottomSpaces={int}]" + nl +
"\t[-topPiece={int}] [-bottomPiece={int}] [-topPiecePercent={int}]" + nl +
"\t[-bottomPiecePercent={int}] [-shiftPercent={int}]" + nl +
"\t[-subdeck={int}] [-subbead={int}] [-sign={bool}]" + nl +
"\t[-decimalPosition={int}] [-groupSize={int}] [-group={bool}]" + nl +
"\t[-decimalComma={bool}] [-base={int}] [-eighth={bool}]" + nl +
"\t[-anomaly={int}] [-shiftAnomaly={bool}] [-anomalySq={int}]" + nl +
"\t[-shiftAnomalySq={bool}] [-displayBase={int}]" + nl +
"\t[-pressOffset={bool}] [-romanNumerals={bool}]" + nl +
"\t[-latin={bool}] [-ancientRoman={bool}] [-modernRoman={bool}]" + nl +
"\t[-format={chinese|japanese|korean|roman|russian|danish|generic}]" + nl +
"\t[-museum={it|uk|fr}]");
			return;
		}
		AbacusApplet abacusApplet = new AbacusApplet();
		JFrame frame = new JFrame(AbacusInterface.TITLE);
		abacusApplet.init();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		if (url != null) {
			frame.setIconImage(Toolkit.getDefaultToolkit().getImage(url));
		}
		frame.add(abacusApplet);
		frame.setBounds(0, 0,
			abacusApplet.getSize().width, abacusApplet.getSize().height);
		frame.pack();
		abacusApplet.abacus.requestFocusInWindow();
		frame.setLocation(AbacusApplet.xValue, AbacusApplet.yValue);
		frame.setVisible(true);
		if (abacusApplet.testValue != 0) {
			abacusApplet.forceDemoParams();
			abacusApplet.abacus.checkBeads();
			abacusApplet.abacus.resetBeads();
			AbacusTest abacusTest = new AbacusTest(abacusApplet, abacusApplet.abacus);
			abacusTest.getTest();
		}
	}
}
