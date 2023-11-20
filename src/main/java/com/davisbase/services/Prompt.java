package com.davisbase.services;

import java.util.Scanner;

import com.davisbase.config.Settings;

public class Prompt extends Component {
    
    private Scanner scanner;

    public Prompt(Mediator mediator) {
        super(mediator);
        this.scanner = new Scanner(System.in);
    }

    public void showPrompt() {
        System.out.printf("\n%s", Settings.PROMPT);
        String commandString = scanner.nextLine();
        mediator.notify(this, commandString);
    }

}
