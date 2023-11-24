package com.davisbase.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.davisbase.config.Settings;
import com.davisbase.utils.Utils;

public class Table extends RandomAccessFile {

    private static final byte META_DATA_PAGE_TYPE = 0x07;
    private static final long META_DATA_PAGE_TYPE_OFFSET = 0x00;
    private static final long ROOT_PAGE_OFFSET_OFFSET = 0x01;
    private static final long ROOT_PAGE_OFFSET = 512;
    private static final int ROW_NUMBERS_OFFSET = 0x09;
    private static final long START_CONTENT_OFFSET = 0x04;

    // TODO: add page header with the constructor 
    // TODO: handle exceptions
    public Table(String name) throws FileNotFoundException, IOException {
        super(new File(name), "rw");
        this.setLength(Settings.PAGE_SIZE);
        writeMetadataPage();
    }
    
    private void writeMetadataPage() throws IOException {
        this.seek(META_DATA_PAGE_TYPE_OFFSET);
        this.writeByte(META_DATA_PAGE_TYPE);

        this.seek(ROOT_PAGE_OFFSET_OFFSET);
        this.writeLong(ROOT_PAGE_OFFSET);
    }

    public void addRow(TableRow row) throws IOException {
        List<Byte> cellBytes = new ArrayList<>();
        byte[] payload = row.getRowBytesWithRecordHeader();

        for (byte b : Utils.shortToByteArray((short) payload.length)) cellBytes.add(b);
        
        int rowId = getNextRowId();
        for (byte b : Utils.integerToByteArray(rowId)) cellBytes.add(b);

        for (byte b : payload) cellBytes.add(b);

        long offset = getFileOffsetPage();
        this.seek(offset);
        for (byte b : cellBytes) this.write(b);
    }

    private int getNextRowId() throws IOException {
        this.seek(ROW_NUMBERS_OFFSET);
        int rows = this.readInt();
        return rows + 1;
    }

    // TODO: B+Tree traversal including creating new page, extending file, and tree rebalancing
    private long getFileOffsetPage() throws IOException {
        this.setLength(1024);
        return 512;
    }

    private void addRow(long offset, TableRow row) throws IOException {
        long startOfContent = getStartContentOffset(offset);
        byte[] payload = row.getRowBytesWithRecordHeader();
    }

    private long getStartContentOffset(long offset) throws IOException {
        this.seek(offset + START_CONTENT_OFFSET);
        return offset + this.readShort();
    }

}
