package com.davisbase.commands;

import java.util.List;

import com.davisbase.models.ColumnDefinition;

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
}
