package com.davisbase.models;

public enum DataType {
    NULL((byte) 0x00),
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
    EMPTYTEXT((byte)0x0C),
    TEXT((byte) 0x0D);

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
        return NULL; // NULL is returned if nothing matches
    }

    public static DataType getEnum(byte dataType) {
        for (DataType d : DataType.values()) {
            if (d.getTypeCode() == dataType) {
                return d;
            }
        }
        return TEXT;
    }

    // TODO implement this for other data types
    public static Object parseData(String value, DataType dataType) {
        try {
            switch (dataType) {
                case INT:
                    return Integer.parseInt(value);
                case TINYINT:
                    return Byte.parseByte(value);
                case SMALLINT:
                    return Short.parseShort(value);
                case BIGINT:
                    return Long.parseLong(value);
                case FLOAT:
                    return Float.parseFloat(value);
                case DOUBLE:
                    return Double.parseDouble(value);
                case TEXT:
                    return value;
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

}
