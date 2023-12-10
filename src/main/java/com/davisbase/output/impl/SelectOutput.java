package com.davisbase.output.impl;

import java.util.List;

import com.davisbase.models.OutputRow;
import com.davisbase.output.DQLOutput;

public class SelectOutput extends DQLOutput {
    public SelectOutput(boolean status, List<OutputRow> outputRows) {
        super(status, outputRows);
    }

    @Override
    public void display() {
        if (this.getStatus()) {
            for (OutputRow row : getOutputRows()) {
                System.out.println(row.toString());
            }
        } else {
            System.out.println("Select Operation failed");
        }
    }

}
