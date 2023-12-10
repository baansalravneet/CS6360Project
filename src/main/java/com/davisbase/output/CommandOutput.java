package com.davisbase.output;

public abstract class CommandOutput {
    private final boolean status;
    public abstract void display();

    public CommandOutput(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }
}
