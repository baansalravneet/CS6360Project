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
		return new byte[] {
				(byte) (i >> 24),
				(byte) (i >> 16),
				(byte) (i >> 8),
				(byte) i
		};
	}

	public static byte[] bigintToByteArray(long i) {
		return new byte[] {
				(byte) (i >> 64),
				(byte) (i >> 56),
				(byte) (i >> 48),
				(byte) (i >> 40),
				(byte) (i >> 32),
				(byte) (i >> 24),
				(byte) (i >> 16),
				(byte) (i >> 8),
				(byte) i
		};
	}

	public static byte[] stringToByteArray(String s) {
		return s.getBytes();
	}

	public static byte[] shortToByteArray(short i) {
		return new byte[] {
				(byte) (i >> 8),
				(byte) i
		};
	}

	public static byte[] tinyintToByteArray(byte i) {
		return new byte[] { i };
	}

	// TODO
	public static byte[] doubleToByteArray(double i) {
		return new byte[] {};
	}

	// TODO
	public static byte[] floatToByteArray(double i) {
		return new byte[] {};
	}

	public static byte[] prepend(byte[] arr, int i) {
		byte[] byteArray = new byte[arr.length + 4];
		byteArray[0] = (byte) (i >> 24);
		byteArray[1] = (byte) (i >> 16);
		byteArray[2] = (byte) (i >> 8);
		byteArray[3] = (byte) i;
		int index = 4;
		for (byte b : arr) {
			byteArray[index++] = b;
		}
		return byteArray;
	}

	public static byte[] prepend(byte[] arr, short i) {
		byte[] byteArray = new byte[arr.length + 2];
		byteArray[0] = (byte) (i >> 8);
		byteArray[1] = (byte) i;
		int index = 2;
		for (byte b : arr) {
			byteArray[index++] = b;
		}
		return byteArray;
	}

	public static long getFileOffsetFromPageNumber(int pageNumber) {
		return (long) Settings.PAGE_SIZE * (pageNumber - 1);
	}

}