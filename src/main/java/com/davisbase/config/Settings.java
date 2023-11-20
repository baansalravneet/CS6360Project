package com.davisbase.config;

public class Settings {
	public static final String PROMPT = "davisql> ";
	public static final String VERSION = "v1.2";
	public static final String COPYRIGHT = "Â©2020 Chris Irwin Davis";
	public static final int PAGE_SIZE = 512;
	
	private static boolean exit;

	public static boolean isExit() {
		return exit;
	}

	public static void setExit(boolean exit) {
		Settings.exit = exit;
	}

}	