package com.davisbase.commands.impl;

import java.io.File;
import java.io.FileNotFoundException;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.config.Settings;
import com.davisbase.models.Table;
import com.davisbase.output.CommandOutput;
import com.davisbase.output.impl.InsertOutput;
import com.davisbase.output.impl.InvalidCommandOutput;

public class InsertCommand extends Command {

    public InsertCommand(CommandContext context) {
        super(context);
    }

    @Override
    public CommandOutput execute() {
        // TODO verify the values (data types, null values etc)
        String fileName = context.getTableName() + Settings.TABLE_FILE_EXTENSION;
        try {
            File tableFile = new File(fileName);
            if (!tableFile.exists()) {
                throw new FileNotFoundException();
            }
            Table t = new Table(tableFile);
            t.addRow(context.getInsertRow());
            t.close();
            return new InsertOutput(true, 1);
        } catch (Exception e) {
            // TODO fix this
            System.out.println(e);
            return new InvalidCommandOutput();
        }
    }

}
