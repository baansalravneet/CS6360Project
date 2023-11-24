package com.davisbase.models;

public enum DataType {
    TINYINT(0x01),
    SMALLINT(0x02),
    INT(0x03),
    BIGINT(0x04),
    FLOAT(0x05),
    DOUBLE(0x06),
    YEAR(0x08),
    TIME(0x09),
    DATETIME(0x0A),
    DATE(0x0B),
    TEXT(0x0C);

    private final int typeCode;

    private DataType(int typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeCode() {
        return typeCode;
    }
}
