package com.davisbase.commands.output.impl;

import com.davisbase.commands.output.CommandOutput;
import com.davisbase.models.OutputTable;

public class SelectOutput extends CommandOutput {
    private boolean status;
    private OutputTable table;

    public SelectOutput(boolean status) {
        super(status);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void display() {
        if (this.getStatus()) {
            table.print();
        } else {
            System.out.println("Select Operation failed");
        }
    }

    public boolean getStatus() {
        return status;
    }
}
