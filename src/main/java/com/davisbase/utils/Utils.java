package com.davisbase.utils;

import com.davisbase.config.Settings;

public class Utils {

	// Display the splash 
	public static void splashScreen() {
		System.out.println(printSeparator("-", 80));
		System.out.println("Welcome to DavisBaseLite"); // Display the string.
		System.out.println("DavisBaseLite Version " + Settings.VERSION);
		System.out.println(Settings.COPYRIGHT);
		System.out.println("\nType \"help\" to display supported commands.");
		System.out.println(printSeparator("-", 80));
	}

	public static String printSeparator(String s, int len) {
		String bar = "";
		for (int i = 0; i < len; i++) {
			bar += s;
		}
		return bar;
	}

	public static byte[] integerToByteArray(int i) {
		byte[] byteArray = new byte[4];
        byteArray[0] = (byte) (i >> 24);
        byteArray[1] = (byte) (i >> 16);
        byteArray[2] = (byte) (i >> 8);
        byteArray[3] = (byte) i;
        return byteArray;
	}

	public static byte[] stringToByteArray(String s) {
		return s.getBytes();
	}

	public static byte[] shortToByteArray(short i) {
		byte[] byteArray = new byte[2];
        byteArray[0] = (byte) (i >> 8);
        byteArray[1] = (byte) i;
		return byteArray;
	}

}