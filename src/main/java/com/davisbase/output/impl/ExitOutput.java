package com.davisbase.output.impl;

import com.davisbase.output.CommandOutput;

public class ExitOutput extends CommandOutput {
    public ExitOutput(boolean status) {
        super(status);
    }

    @Override
    public void display() {
        if (this.getStatus()) {
            System.out.println("Exit Operation Performed Successfully");
        } else {
            System.out.println("Exit Operation failed");
        }
    }
}
