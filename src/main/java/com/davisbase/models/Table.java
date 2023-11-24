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

    private static final byte FILE_HEADER_PAGE_TYPE = 0x07;
    private static final long FILE_HEADER_PAGE_TYPE_OFFSET = 0x00;
    private static final long ROOT_PAGE_NUMBER_OFFSET = 0x01; // Page numbers are 1 indexed
    private static final int ROW_ID_OFFSET = 0x09;
    private static final long PAGE_HEADER_CONTENT_START_OFFSET = 0x04;
    private static final byte TABLE_TREE_LEAF_PAGE = 0x0D;
    private static final long RIGHT_SIBLING_OFFSET = 0x06;
    private static final int NULL_RIGHT_SIBLING = -1;
    private static final long PARENT_PAGE_POINTER_OFFSET = 0x0A;
    private static final int NULL_PARENT = -1;
    private static final long PAGE_HEADER_NUMBER_OF_ROWS_OFFSET = 0x02;

    // TODO: add page header with the constructor 
    // TODO: handle exceptions
    public Table(String name) throws FileNotFoundException, IOException {
        super(new File(name), "rw");
        writeFileHeaderPage();
    }
    
    private void writeFileHeaderPage() throws IOException {
        this.setLength(Settings.PAGE_SIZE);

        this.seek(FILE_HEADER_PAGE_TYPE_OFFSET);
        this.writeByte(FILE_HEADER_PAGE_TYPE);


        int rootPageNumber = addPage();
        setPageAsRoot(rootPageNumber);
    }

    private void setPageAsRoot(int pageNumber) throws IOException {
        this.seek((pageNumber - 1) * Settings.PAGE_SIZE + PARENT_PAGE_POINTER_OFFSET);
        this.writeInt(NULL_PARENT);
        setRootPageInFileHeader(pageNumber);
    }

    private void setRootPageInFileHeader(int pageNumber) throws IOException {
        this.seek(ROOT_PAGE_NUMBER_OFFSET);
        this.writeInt(pageNumber);
    }

    private int addPage() throws IOException {
        int pages = (int) (this.length() / Settings.PAGE_SIZE) + 1;
        this.setLength(Settings.PAGE_SIZE * pages);
        this.seek((pages-1) * Settings.PAGE_SIZE);
        this.writeByte(TABLE_TREE_LEAF_PAGE);

        this.seek((pages-1) * Settings.PAGE_SIZE + PAGE_HEADER_CONTENT_START_OFFSET);
        this.writeShort(Settings.PAGE_SIZE);

        this.seek((pages-1) * Settings.PAGE_SIZE + RIGHT_SIBLING_OFFSET);
        this.writeInt(NULL_RIGHT_SIBLING);

        return pages;
    }

    /*
    * 1. get next row id
    * 2. increment row id in the meta page
    * 3. add this row id to the cell
    * 4. find the rightmost leaf page
    * 4.1. go to the root page
    * 4.2. keep going towards the right until you find null
    * 5. check if this page would overflow
    * 5.1. if yes, then split it.
    * 6. find where the cell content would go and put it there
    * 7. change the related header info.
    */
    // TODO
    public void addRow(TableRow row) throws IOException {
        int nextRowId = getNextRowId();
        incrementFileHeaderRowId(nextRowId);

        byte[] payload = row.getRowBytesWithRecordHeader();
        short payloadSize = (short)payload.length;

        byte[] cell = Utils.prepend(payload, nextRowId);
        cell = Utils.prepend(cell, payloadSize);

        int rightmostLeafPageNumber = getRightMostLeafPageNumber(getRootPageNumber());
        // TODO: check overflow
        writeCellInPage(cell, rightmostLeafPageNumber);


    }

    private void writeCellInPage(byte[] cell, int pageNumber) throws IOException {
        long fileOffset = Utils.getFileOffsetFromPageNumber(pageNumber);
        int pageOffsetForCell = getPageOffsetForNewCell(fileOffset, cell.length);
        this.seek(fileOffset + pageOffsetForCell);
        this.write(cell);

        this.seek(fileOffset + PAGE_HEADER_CONTENT_START_OFFSET);
        this.writeShort(pageOffsetForCell);

        this.seek(fileOffset + PAGE_HEADER_NUMBER_OF_ROWS_OFFSET);
        short numberOfRows = this.readShort();
        this.seek(fileOffset + PAGE_HEADER_NUMBER_OF_ROWS_OFFSET);
        this.writeShort(numberOfRows + 1);
    }

    private int getPageOffsetForNewCell(long fileOffset, int cellLength) throws IOException {
        long contentStartOffset = fileOffset + PAGE_HEADER_CONTENT_START_OFFSET;
        this.seek(contentStartOffset);
        short contentStart = this.readShort();
        return contentStart - cellLength;
    }

    private int getRightMostLeafPageNumber(int pageNumber) throws IOException {
        long fileOffset = Utils.getFileOffsetFromPageNumber(pageNumber);
        this.seek(fileOffset + RIGHT_SIBLING_OFFSET);
        int next = this.readInt();
        if (next != -1) return getRightMostLeafPageNumber(next);
        return pageNumber;
    }

    private int getRootPageNumber() throws IOException {
        this.seek(ROOT_PAGE_NUMBER_OFFSET);
        return this.readInt();
    }
    
    private void incrementFileHeaderRowId(int rowId) throws IOException {
        this.seek(ROW_ID_OFFSET);
        this.writeInt(rowId);
    }

    private int getNextRowId() throws IOException {
        this.seek(ROW_ID_OFFSET);
        int rows = this.readInt();
        return rows + 1;
    }

    // TODO: B+Tree traversal including creating new page, extending file, and tree rebalancing
    private long getFileOffsetPage() throws IOException {
        this.setLength(1024);
        return 512;
    }

}
