package org.tux.bagleyd.util;

/*
 * @(#)ComponentUtil.java
 *
 * Mostly from Java Sourcebook
 * by Ed Anuff, pp 287-289
 */

import java.awt.Component;
import javax.swing.JFrame;

/**
 * The <code>ComponentUtil</code> class is used in
 * <code>OKDialog</code>.
 *
 * @author	Ed Anuff
 */

public class ComponentUtil {
	public static JFrame findJFrame(Component myComponent) {
		Component currentParent = myComponent;
		JFrame myFrame = null;

		while (currentParent != null) {
			if (currentParent instanceof JFrame) {
				myFrame = (JFrame) currentParent;
				break;
			}
			currentParent = currentParent.getParent();
		}
		return myFrame;
	}
}
