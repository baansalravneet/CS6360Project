package com.davisbase.utils;

import com.davisbase.config.Settings;

public class Utils {

	// Display the splash 
	public static void splashScreen() {
		System.out.println(printSeparator("-", 80));
		System.out.println("Welcome to DavisBaseLite"); // Display the string.
		System.out.println("DavisBaseLite Version " + Settings.VERSION);
		System.out.println(Settings.COPYRIGHT);
		System.out.println("\nType \"help;\" to display supported commands.");
		System.out.println(printSeparator("-", 80));
	}

	public static String printSeparator(String s, int len) {
		String bar = "";
		for (int i = 0; i < len; i++) {
			bar += s;
		}
		return bar;
	}

}