package com.davisbase.models;

public class ColumnDefinition {
    private final String name;
    private final DataType dataType;
    private final boolean nullable;
    private final boolean unique;
    private final boolean primaryKey;
    
    public ColumnDefinition(String name, DataType dataType, boolean nullable, boolean unique, boolean primaryKey) {
        this.name = name;
        this.dataType = dataType;
        this.nullable = nullable;
        this.unique = unique;
        this.primaryKey = primaryKey;
    }

    public String getName() {
        return name;
    }

    public DataType getDataType() {
        return dataType;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    

}
