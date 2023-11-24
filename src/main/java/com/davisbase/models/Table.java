package com.davisbase.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.davisbase.config.Settings;
import com.davisbase.utils.Utils;

public class Table extends RandomAccessFile {

    private static final byte META_DATA_PAGE_TYPE = 0x07;
    private static final long META_DATA_PAGE_TYPE_OFFSET = 0x00;
    private static final long ROOT_PAGE_NUMBER_OFFSET = 0x01;
    private static final int ROW_NUMBERS_OFFSET = 0x09;
    private static final long START_CONTENT_OFFSET = 0x04;
    private static final byte TABLE_TREE_LEAF_PAGE = 0x0D;
    private static final long RIGHT_SIBLING_OFFSET = 0x06;
    private static final int NULL_RIGHT_SIBLING = -1;
    private static final long PARENT_PAGE_POINTER_OFFSET = 0x0A;
    private static final int NULL_PARENT = -1;

    // TODO: add page header with the constructor 
    // TODO: handle exceptions
    public Table(String name) throws FileNotFoundException, IOException {
        super(new File(name), "rw");
        writeMetadataPage();
    }
    
    private void writeMetadataPage() throws IOException {
        this.setLength(Settings.PAGE_SIZE);

        this.seek(META_DATA_PAGE_TYPE_OFFSET);
        this.writeByte(META_DATA_PAGE_TYPE);


        int rootPageNumber = addPage();
        setPageAsRoot(rootPageNumber);
    }

    private void setPageAsRoot(int pageNumber) throws IOException {
        this.seek((pageNumber - 1) * Settings.PAGE_SIZE + PARENT_PAGE_POINTER_OFFSET);
        this.writeInt(NULL_PARENT);
        setRootPageInMetaData(pageNumber);
    }

    private void setRootPageInMetaData(int pageNumber) throws IOException {
        this.seek(ROOT_PAGE_NUMBER_OFFSET);
        this.writeInt(pageNumber);
    }

    private int addPage() throws IOException {
        int pages = (int) (this.length() / Settings.PAGE_SIZE + 1);
        this.setLength(Settings.PAGE_SIZE * pages);
        this.seek((pages-1) * Settings.PAGE_SIZE);
        this.writeByte(TABLE_TREE_LEAF_PAGE);

        this.seek((pages - 1) * Settings.PAGE_SIZE + RIGHT_SIBLING_OFFSET);
        this.writeInt(NULL_RIGHT_SIBLING);

        return pages;
    }

    // TODO
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

    private long getStartContentOffset(long offset) throws IOException {
        this.seek(offset + START_CONTENT_OFFSET);
        return offset + this.readShort();
    }

}
