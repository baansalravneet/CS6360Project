package com.davisbase.commands;

import java.util.ArrayList;
import java.util.List;

import com.davisbase.models.ColumnDefinition;
import com.davisbase.models.ColumnValue;
import com.davisbase.models.DataType;
import com.davisbase.models.TableRow;

public class CommandContext {
    private String tableName;
    private List<ColumnDefinition> columnContext;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnDefinition> getColumnContext() {
        return columnContext;
    }

    public void setColumnContext(List<ColumnDefinition> columnContext) {
        this.columnContext = columnContext;
    }

    public TableRow getTablesTableRow() {
        TableRow row = new TableRow();
        row.appendValue(new ColumnValue(DataType.TEXT, this.getTableName()));
        return row;
    }

    public List<TableRow> getColumnsTableRows() {
        List<TableRow> result = new ArrayList<>();
        for (int i = 0; i < this.getColumnContext().size(); i++) {
            ColumnDefinition column = this.getColumnContext().get(i);
            TableRow row = new TableRow();
            row.appendValue(new ColumnValue(DataType.TEXT, this.getTableName()));
            row.appendValue(new ColumnValue(DataType.TEXT, column.getName()));
            row.appendValue(new ColumnValue(DataType.TEXT, column.getDataType().toString()));
            row.appendValue(new ColumnValue(DataType.TINYINT, (byte)(i+1)));
            row.appendValue(new ColumnValue(DataType.TEXT, column.isNullable() ? "YES" : "NO"));
            result.add(row);
        }
        return result;
    }
}
