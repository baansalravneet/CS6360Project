package com.davisbase.commands.impl;

import java.io.IOException;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.commands.output.CommandOutput;
import com.davisbase.commands.output.impl.InvalidCommandOutput;
import com.davisbase.config.Database;
import com.davisbase.config.Settings;
import com.davisbase.models.ColumnValue;
import com.davisbase.models.DataType;
import com.davisbase.models.Table;
import com.davisbase.models.TableRow;

public class CreateTableCommand extends Command {

    public CreateTableCommand(CommandContext context) {
        super(context);
    }

    // TODO: fix this
    @Override
    public CommandOutput execute() { 
        try (Table newTable = new Table(context.getTableName() + Settings.TABLE_FILE_EXTENSION)) {
            TableRow tablesTableRow = new TableRow();
            tablesTableRow.appendValue(new ColumnValue(DataType.TEXT, context.getTableName()));
            Database.getTablesTable().addRow(tablesTableRow);
        } catch (IOException e) {
            // TODO: fix this
            e.printStackTrace();
        }

        return new InvalidCommandOutput();
    }

}
