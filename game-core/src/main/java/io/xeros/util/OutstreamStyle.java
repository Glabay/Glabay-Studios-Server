package io.xeros.util;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author nick
 */
public class OutstreamStyle extends PrintStream {

	private final DateFormat dateFormat = new SimpleDateFormat();

	public OutstreamStyle(PrintStream out) {
		super(out);
	}

	@Override
	public void print(String str) {
		if (str == null || str.length() <= 0) {
			return;
		}
		if (str.startsWith("debug:"))
			super.print("[" + getPrefix() + "] DEBUG: " + str.substring(6));
		else
			super.print("[" + getPrefix() + "]: " + str);
	}

	private String getPrefix() {
		return dateFormat.format(new Date());
	}
}
