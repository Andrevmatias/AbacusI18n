package org.tux.bagleyd.util;

/*
 * @(#)ArgumentParser.java
 *
 * From Tip of the Day http://www.devx.com/tips/Tip/13004
 * Simplify Command-Line Argument Parsing
 * May 28, 1999  Whit Stockwell
 */

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The <code>ArgumentParser</code> class simplifies command-line
 * argument processing by separating arguments into positional
 * parameters and options (any argument starting with "-" or "/" is
 * considered an option, which may specify a value).
 *
 * @author	Whit Stockwell
 */

public class ArgumentParser {
	public ArgumentParser(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-") ||
					args[i].startsWith("/")) {
				int loc = args[i].indexOf('=');
				String key = (loc > 0) ?
					args[i].substring(1, loc) :
					args[i].substring(1);
				String value = (loc > 0) ?
					args[i].substring(loc + 1) : "";

				this.options.put(key.toLowerCase(), value);
			} else {
				this.params.add(args[i]);
			}
		}
	}

	public boolean hasOption(String opt) {
		return this.options.containsKey(opt.toLowerCase());
	}

	public String getOption(String opt) {
		return this.options.get(opt.toLowerCase());
	}

	public String nextParam() {
		if (this.paramIndex < this.params.size()) {
			return this.params.get(this.paramIndex++);
		}
		return null;
	}

	private final ArrayList<String> params = new ArrayList<>();
	private final HashMap<String, String> options = new HashMap<>();

	private int paramIndex = 0;
}
