package com.davisbase.commands.output.impl;

import java.util.List;

import com.davisbase.commands.output.DQLOutput;
import com.davisbase.models.OutputRow;

public class ShowTablesOutput extends DQLOutput {

    public ShowTablesOutput(boolean status, List<OutputRow> outputRows) {
        super(status, outputRows);
    }

    @Override
    public void display() {
        if (this.getStatus()) {
            for (OutputRow row : getOutputRows()) {
                System.out.println(row.toString());
            }
        } else {
            System.out.println("Show tables operation failed");
        }
    }
}
