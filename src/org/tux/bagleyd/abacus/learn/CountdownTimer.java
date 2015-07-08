package org.tux.bagleyd.abacus.learn;

//http://www.java.happycodings.com/Java_Swing/code16.html

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import javax.swing.Timer;

import org.tux.bagleyd.abacus.learn.TestDialog;

class CountdownTimer {
	Timer timer;
	String string;
	SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");

	public CountdownTimer(final TestDialog dialog, int min) {
		final long finishTime = System.currentTimeMillis() +
			(long) 1000 * 60 * min;
		timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				long remainder = finishTime - System.currentTimeMillis();
				if (remainder < 0)
					remainder = 0;
				string = timeFormat.format(remainder);
				dialog.updateTimer(string);
			}
		});
		timer.start();
	}

	String getString() {
		return string;
	}
}
