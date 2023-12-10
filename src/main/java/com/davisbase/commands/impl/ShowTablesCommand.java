package com.davisbase.commands.impl;

import java.io.IOException;
import java.util.List;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.config.Database;
import com.davisbase.models.OutputRow;
import com.davisbase.models.Table;
import com.davisbase.output.CommandOutput;
import com.davisbase.output.impl.InvalidCommandOutput;
import com.davisbase.output.impl.ShowTablesOutput;

public class ShowTablesCommand extends Command {

    public ShowTablesCommand(CommandContext context) {
        super(context);
    }

    @Override
    public CommandOutput execute() {
        try {
            Table tablesTable = Database.getTablesTable();
            List<byte[]> cells = tablesTable.getAllCells();
            List<OutputRow> outputRows = Table.getOutputRows(cells);
            return new ShowTablesOutput(true, outputRows);
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }

        return new InvalidCommandOutput();
    }

}
