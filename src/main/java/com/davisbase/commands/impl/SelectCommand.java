package com.davisbase.commands.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.commands.output.CommandOutput;
import com.davisbase.commands.output.impl.InvalidCommandOutput;
import com.davisbase.commands.output.impl.SelectOutput;
import com.davisbase.config.Settings;
import com.davisbase.models.OutputTable;
import com.davisbase.models.Table;

public class SelectCommand extends Command {

    public SelectCommand(CommandContext context) {
        super(context);
    }

    @Override
    public CommandOutput execute() {
        // TODO only happy case working right now
        String fileName = context.getTableName() + Settings.TABLE_FILE_EXTENSION;
        try {
            File tableFile = new File(fileName);
            if (!tableFile.exists()) {
                throw new FileNotFoundException();
            }
            Table t = new Table(tableFile);
            List<byte[]> cells = t.getAllCells();
            t.close();
            OutputTable output = new OutputTable();
            
            return new SelectOutput(true);
        } catch (Exception e) {
            // TODO fix this
            System.out.println(e);
            return new InvalidCommandOutput();
        }
    }

}
