package com.davisbase.output.impl;

import com.davisbase.output.DDLOutput;

public class DropTableOutput extends DDLOutput {

    public DropTableOutput(boolean status) {
        super(status);
    }

    @Override
    public void display() {
        if (this.getStatus()) {
            System.out.println("Drop Table is executed successfully");
        } else {
            System.out.println("Drop Table operation failed");
        }
    }

}
