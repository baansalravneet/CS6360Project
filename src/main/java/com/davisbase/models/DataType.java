package com.davisbase.models;

public enum DataType {
    TINYINT((byte) 0x01),
    SMALLINT((byte) 0x02),
    INT((byte) 0x03),
    BIGINT((byte) 0x04),
    FLOAT((byte) 0x05),
    DOUBLE((byte) 0x06),
    YEAR((byte) 0x08),
    TIME((byte) 0x09),
    DATETIME((byte) 0x0A),
    DATE((byte) 0x0B),
    TEXT((byte) 0x0C);

    private final byte typeCode;

    private DataType(byte typeCode) {
        this.typeCode = typeCode;
    }

    public byte getTypeCode() {
        return typeCode;
    }

    public static DataType getEnum(String datatype) {
        for (DataType dataType : DataType.values()) {
            if (dataType.name().equalsIgnoreCase(datatype)) {
                return dataType;
            }
        }
        return null; // null is returned if nothing matches
    }
}
