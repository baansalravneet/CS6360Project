package com.davisbase.output.impl;

import com.davisbase.output.CommandOutput;

public class HelpOutput extends CommandOutput {
    private String outputString;

    public String getOutputString() {
        return outputString;
    }

    public HelpOutput(String outputString) {
        super(true);
        this.outputString = outputString;
    }

    @Override
    public void display() {
        System.out.println(outputString);
    }
}
