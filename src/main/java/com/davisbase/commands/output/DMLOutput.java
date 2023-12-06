package com.davisbase.commands.output;

public abstract class DMLOutput extends CommandOutput {
    private final int numberOfRecords;

    public DMLOutput(boolean status, int numberOfRecords) {
        super(status);
        this.numberOfRecords = numberOfRecords;
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

}
