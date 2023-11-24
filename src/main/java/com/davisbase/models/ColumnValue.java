package com.davisbase.models;

import javax.xml.crypto.Data;

import com.davisbase.utils.Utils;

public class ColumnValue {
    private final DataType dataType;
    private final Object value;

    public ColumnValue(DataType dataType, Object value) {
        this.dataType = dataType;
        this.value = value;
    }

    public DataType getDataType() {
        return dataType;
    }

    public Object getValue() {
        return value;
    }

    public byte[] getBytes() {
        if (DataType.INT.equals(dataType)) {
            return Utils.integerToByteArray((Integer) value);
        }
        if (DataType.TEXT.equals(dataType)) {
            return Utils.stringToByteArray((String) value);
        }
        return new byte[1];
    }

    public byte getRecordHeader() {
        int code = dataType.getTypeCode();
        if (DataType.TEXT.equals(dataType)) {
            code += ((String) value).length();
        }
        return (byte)code;
    }
}
