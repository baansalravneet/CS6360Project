package com.davisbase.models;

import java.util.ArrayList;
import java.util.List;

public class TableRow {

    private List<Object> values;

    public TableRow() {
        values = new ArrayList<>();
    }

    public void appendValue(Object value) {
        values.add(value);
    }
    
}
