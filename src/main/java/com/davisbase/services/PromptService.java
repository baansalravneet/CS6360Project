package com.davisbase.services;

public class PromptService {

    private static final String PROMPT = "davisql>";

    public PromptService() {
        showPrompt();
    }

    private static void showPrompt() {
        System.out.printf("\n%s", PROMPT);
    }

}
