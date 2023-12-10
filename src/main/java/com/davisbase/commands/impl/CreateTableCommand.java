package com.davisbase.commands.impl;

import java.io.IOException;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.config.Database;
import com.davisbase.models.ColumnValue;
import com.davisbase.models.DataType;
import com.davisbase.models.Table;
import com.davisbase.models.TableRow;
import com.davisbase.output.CommandOutput;
import com.davisbase.output.impl.CreateTableOutput;

public class CreateTableCommand extends Command {

    public CreateTableCommand(CommandContext context) {
        super(context);
    }

    @Override
    public CommandOutput execute() { 
        try (Table newTable = new Table(context.getTableName())) {
            TableRow tablesTableRow = new TableRow();
            tablesTableRow.appendValue(new ColumnValue(DataType.TEXT, context.getTableName()));
            Database.getTablesTable().addRow(tablesTableRow);
            
            for (TableRow row : context.getColumnsTableRows()) {
                Database.getColumnsTable().addRow(row);
            }
        } catch (IOException e) {
            // TODO: fix this
            e.printStackTrace();
        }

        return new CreateTableOutput(true);
    }

}
