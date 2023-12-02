package com.davisbase.models;

public class WhereClause {
    private final String columnName;
    private final Condition condition;
    private final Object value;

    public Condition getCondition() {
        return condition;
    }

    public Object getValue() {
        return value;
    }

    public String getColumnName() {
        return columnName;
    }

    public WhereClause(String columnName, Condition condition, Object value) {
        this.columnName = columnName;
        this.condition = condition;
        this.value = value;
    }

}

enum Condition {
    NOT,
    EQUAL,
    GREATER_THAN,
    GREATER_THAN_EQUAL,
    SMALLER_THAN,
    SMALLER_THAN_EQUAL
} 

