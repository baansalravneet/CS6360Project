package com.davisbase.output.impl;

import com.davisbase.output.CommandOutput;

public class InvalidCommandOutput extends CommandOutput {

    public InvalidCommandOutput() {
        super(true);
    }

    @Override
    public void display() {
        System.out.println("Invalid query. Type \"help\" to display supported commands.");
    }

}
