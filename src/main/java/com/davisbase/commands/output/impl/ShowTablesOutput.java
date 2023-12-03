package com.davisbase.commands.output.impl;

import java.util.List;

import com.davisbase.commands.output.DQLOutput;

public class ShowTablesOutput extends DQLOutput {
    private List<String> tableNames;

    public ShowTablesOutput(boolean status, List<String> tableNames) {
        super(status);
        this.tableNames = tableNames;
    }

    @Override
    public void display() {
        if (this.getStatus()) {

            // Find the maximum length of the table names
            int maxLength = 0;
            for (String tableName : tableNames) {
                if (tableName.length() > maxLength) {
                    maxLength = tableName.length();
                }
            }

            // Print spaces before table header
            for (int i = 0; i < (maxLength - 5) / 2; i++) {
                System.out.print(" ");
            }

            // Print header
            System.out.println("TABLE NAME");

            // Print underscores
            for (int i = 0; i < maxLength + 5; i++) {
                System.out.print("_");
            }
            System.out.println();
            for (String tableName : tableNames) {
                int spacesBefore = (maxLength + 5 - tableName.length()) / 2;
                // Print spaces before table name
                for (int i = 0; i < spacesBefore; i++) {
                    System.out.print(" ");
                }
                // Print table name
                System.out.println(tableName);
            }

        } else {
            System.out.println("Show Table operation failed");
        }
    }
}
