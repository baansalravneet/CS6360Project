package com.davisbase.utils;

import java.nio.ByteBuffer;

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

    public static Boolean getBoolean(String s) {
        if ("YES".equalsIgnoreCase(s)) {
            return true;
        }
        if ("NO".equalsIgnoreCase(s)) {
            return false;
        }
        return null;
    }

    public static byte[] integerToByteArray(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
    }

    public static byte[] bigintToByteArray(long i) {
        return ByteBuffer.allocate(8).putLong(i).array();
    }

    public static byte[] stringToByteArray(String s) {
        return s.getBytes();
    }

    public static byte[] shortToByteArray(short i) {
        return ByteBuffer.allocate(2).putShort(i).array();
    }

    public static byte[] tinyintToByteArray(byte i) {
        return ByteBuffer.allocate(1).put(i).array();
    }

    public static byte[] doubleToByteArray(double i) {
        return ByteBuffer.allocate(8).putDouble(i).array();
    }

    public static byte[] floatToByteArray(float i) {
        return ByteBuffer.allocate(4).putFloat(i).array();
    }

    public static byte[] prepend(byte[] arr, int i) {
        byte[] byteArray = new byte[arr.length + 4];
        ByteBuffer.wrap(byteArray).putInt(0, i);
        int index = 4;
        for (byte b : arr) {
            byteArray[index++] = b;
        }
        return byteArray;
    }

    public static byte[] prepend(byte[] arr, byte i) {
        byte[] byteArray = new byte[arr.length + 1];
        byteArray[0] = i;
        int index = 1;
        for (byte b : arr) {
            byteArray[index++] = b;
        }
        return byteArray;
    }

    public static byte[] prepend(byte[] arr, short i) {
        byte[] byteArray = new byte[arr.length + 2];
        ByteBuffer.wrap(byteArray).putShort(0, i);
        int index = 2;
        for (byte b : arr) {
            byteArray[index++] = b;
        }
        return byteArray;
    }

    public static long getFileOffsetFromPageNumber(int pageNumber) {
        return (long) Settings.PAGE_SIZE * pageNumber;
    }

}