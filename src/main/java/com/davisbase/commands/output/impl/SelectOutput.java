package com.davisbase.commands.output.impl;

import java.util.List;

import com.davisbase.commands.output.CommandOutput;
import com.davisbase.models.OutputRow;
import com.davisbase.models.OutputTable;

public class SelectOutput extends CommandOutput {
    private OutputTable table;
    private List<OutputRow> outputRows;

    public SelectOutput(boolean status, List<OutputRow> outputRows) {
        super(status);
        this.outputRows = outputRows;
        //TODO Auto-generated constructor stub
    }

    @Override
    public void display() {
        if (this.getStatus()) {
            for (OutputRow row : outputRows) {
                System.out.println(row.toString());
            }
        } else {
            System.out.println("Select Operation failed");
        }
    }

    public List<OutputRow> getOutputRows() {
        return outputRows;
    }

}
