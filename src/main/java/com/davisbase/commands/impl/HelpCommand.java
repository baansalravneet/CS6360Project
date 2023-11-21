package com.davisbase.commands.impl;

import com.davisbase.commands.Command;

public class HelpCommand implements Command {

    @Override
    public void execute() {
        printHelp();
    }

    // TODO: Complete this. Ideally, maintain a txt file somewhere
    // in the repo and dump that file on screen.
    public void printHelp() {
        System.out.println("WIP");
    }
    
}
