package com.davisbase.commands;

import com.davisbase.commands.output.CommandOutput;

public abstract class Command {
    private CommandContext context;
    public Command(CommandContext context) {
        this.context = context;
    }
    public abstract CommandOutput execute();
}

/*
 * SHOW TABLES
 * CREATE TABLE
 * DROP TABLE
 * CREATE INDEX
 * 
 * EXIT
 * HELP
 * 
 * INSERT
 * DELETE
 * UPDATE
 * SELECT-FORM-WHERE
 */