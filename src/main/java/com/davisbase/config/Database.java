package com.davisbase.config;

import com.davisbase.models.Table;

public class Database {
    private static Table tablesTable;
    private static Table columnsTable;

    public static Table getColumnsTable() {
        return columnsTable;
    }

    public static void setColumnsTable(Table columnsTable) {
        Database.columnsTable = columnsTable;
    }

    public static Table getTablesTable() {
        return tablesTable;
    }

    public static void setTablesTable(Table tablesTable) {
        Database.tablesTable = tablesTable;
    }

}
