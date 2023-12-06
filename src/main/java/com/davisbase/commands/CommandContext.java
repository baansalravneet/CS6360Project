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
    private List<ColumnValue> columnValues;

    public void addColumnContext(ColumnDefinition columnDefinition) {
        if (columnContext == null) {
            columnContext = new ArrayList<>();
        }
        columnContext.add(columnDefinition);
    }

    public void addColumnValues(ColumnValue value) {
        if (columnValues == null) {
            columnValues = new ArrayList<>();
        }
        columnValues.add(value);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnDefinition> getColumnContext() {
        return columnContext;
    }

    public List<ColumnValue> getColumnValues() {
        return columnValues;
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
            row.appendValue(new ColumnValue(DataType.TEXT, this.getTableName())); // table name
            row.appendValue(new ColumnValue(DataType.TEXT, column.getName())); // column name
            row.appendValue(new ColumnValue(DataType.TEXT, column.getDataType().toString())); // data type
            row.appendValue(new ColumnValue(DataType.TINYINT, (byte) (i + 1))); // position
            row.appendValue(new ColumnValue(DataType.TEXT, column.isNullable() ? "YES" : "NO")); // nullable
            result.add(row);
        }
        return result;
    }

    public TableRow getInsertRow() {
        TableRow row = new TableRow();
        for (ColumnValue v : this.columnValues) {
            row.appendValue(v);
        }
        return row;
    }
}
