package com.davisbase.config;

import com.davisbase.models.Table;

public class Database {
    private static Table tableTable;
    private static Table columnsTable;

    public static Table getColumnsTable() {
        return columnsTable;
    }

    public static void setColumnsTable(Table columnsTable) {
        Database.columnsTable = columnsTable;
    }

    public static Table getTableTable() {
        return tableTable;
    }

    public static void setTableTable(Table tablesTable) {
        Database.tableTable = tablesTable;
    }

}
