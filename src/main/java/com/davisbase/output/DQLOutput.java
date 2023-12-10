package com.davisbase.output;

import java.util.List;

import com.davisbase.models.OutputRow;

public abstract class DQLOutput extends CommandOutput {

    private List<OutputRow> outputRows;

    public DQLOutput(boolean status, List<OutputRow> outputRows) {
        super(status);
        this.outputRows = outputRows;
    }

    public List<OutputRow> getOutputRows() {
        return outputRows;
    }

}
