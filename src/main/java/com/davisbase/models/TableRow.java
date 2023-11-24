package com.davisbase.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TableRow {

    private List<ColumnValue> values;

    public TableRow() {
        values = new LinkedList<>();
    }

    public void appendValue(ColumnValue value) {
        values.add(value);
    }

    // TODO: find a better way to do this
    public byte[] getRowBytesWithRecordHeader() {
        List<Byte> result = new ArrayList<>();
        result.add((byte)values.size());
        for (ColumnValue v : values) result.add(v.getRecordHeader());
        for (ColumnValue v : values) for (byte b : v.getBytes()) result.add(b);
        byte[] arr = new byte[result.size()];
        int index = 0;
        for (byte b : result) arr[index++] = b;
        return arr;
    }
}
