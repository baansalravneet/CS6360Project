package com.davisbase.models;

import com.davisbase.utils.Utils;

public class ColumnValue {
    private String columnName;
    private DataType dataType;
    private Object value;

    public ColumnValue(String columnName, DataType dataType, Object value) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.value = value;
    }

    public ColumnValue(DataType dataType, Object value) {
        this.dataType = dataType;
        this.value = value;
    }

    public ColumnValue(String name, Object value) {
        this.columnName = name;
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public Object getValue() {
        return value;
    }

    public byte[] getBytes() {
        switch(dataType) {
            case INT:
                return Utils.integerToByteArray((Integer) value);
            case TEXT:
                return Utils.stringToByteArray((String) value);
            case TINYINT:
                return Utils.tinyintToByteArray((Byte) value);
            case SMALLINT:
                return Utils.shortToByteArray((Short) value);
            case BIGINT:
                return Utils.bigintToByteArray((Long) value);
            case FLOAT:
                return Utils.floatToByteArray((Float) value);
            case DOUBLE:
                return Utils.doubleToByteArray((Double) value);
            case YEAR:
            case TIME:
            case DATETIME:
            case DATE:
            default:
                // TODO: throw some error here
                return new byte[0];
        }
    }

    public byte getRecordHeader() {
        int code = dataType.getTypeCode();
        if (DataType.TEXT.equals(dataType)) {
            code += ((String) value).length();
        }
        return (byte)code;
    }
}
