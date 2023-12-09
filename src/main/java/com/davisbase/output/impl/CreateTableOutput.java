package com.davisbase.output.impl;

import com.davisbase.output.DDLOutput;

public class CreateTableOutput extends DDLOutput {

    public CreateTableOutput(boolean status) {
        super(status);
    }

    @Override
    public void display() {
        if (this.getStatus()) {
            System.out.println("Create Table is executed successfully");
        } else {
            System.out.println("Create Table operation failed");
        }
    }

}
