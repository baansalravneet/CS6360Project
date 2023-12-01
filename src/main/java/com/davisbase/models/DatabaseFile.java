package com.davisbase.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.davisbase.config.Settings;
import com.davisbase.utils.Utils;

public abstract class DatabaseFile extends RandomAccessFile {

    private static final long PARENT_PAGE_OFFSET = 0x0A;
    private static final long ROOT_PAGE_OFFSET = 0x06;
    private static final long RIGHT_SIBLING_OFFSET = 0x08;

    protected static final short PAGE_HEADER_SIZE = 16;
    protected static final short NULL_PARENT = -1;
    protected static final short NULL_RIGHT_SIBLING = -1;
    protected static final long PAGE_TYPE_OFFSET = 0x00;
    protected static final long CONTENT_START_OFFSET = 0x04;
    protected static final long NUMBER_OF_ROWS_OFFSET = 0x02;
    
    // TODO: handle exceptions
    public DatabaseFile(String name) throws FileNotFoundException, IOException {
        super(new File(name), "rw");
    }

    public DatabaseFile(File file) throws FileNotFoundException {
        super(file, "rw");
    }

    protected void setEmptyPageStartContent(int pageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + CONTENT_START_OFFSET);
        this.writeShort(Settings.PAGE_SIZE);
    }

    protected void setPageAsRoot(short pageNumber) throws IOException {
        // set this page number as root in each page of the file
        for (short page = 0; page * Settings.PAGE_SIZE < this.length(); page++) {
            setRootInPage(page, pageNumber);
        }
    }

    private void setRootInPage(short pageNumber, short rootPageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + ROOT_PAGE_OFFSET);
        this.writeShort(rootPageNumber);
    }

    protected short addLeafPage() throws IOException {
        short newPage = extendFileByOnePage();

        // set the content start offset
        setEmptyPageStartContent(newPage);

        // set right sibling offset
        setRightSibling(newPage, NULL_RIGHT_SIBLING);

        return newPage;
    }

    protected short getContentStartOffset(short pageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + CONTENT_START_OFFSET);
        return this.readShort();
    }

    protected void setRightmostChildNull(int pageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + RIGHT_SIBLING_OFFSET);
        this.writeShort(NULL_RIGHT_SIBLING);
    }

    protected short getRootPageNumber() throws IOException {
        this.seek(ROOT_PAGE_OFFSET);
        return this.readShort();
    }

    // recursive method to find the rightmost leaf page
    protected short getRightmostLeafPage(short rootPage) throws IOException {
        long fileOffset = Utils.getFileOffsetFromPageNumber(rootPage);
        this.seek(fileOffset + RIGHT_SIBLING_OFFSET);
        short next = this.readShort();
        if (next != -1)
            return getRightmostLeafPage(next);
        return rootPage;
    }

    protected short getParentPage(short pageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + PARENT_PAGE_OFFSET);
        return this.readShort();
    }

    protected void setRightSibling(short pageNumber, short siblingPageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + RIGHT_SIBLING_OFFSET);
        this.writeShort(siblingPageNumber);
    }

    protected void setParentOfPage(short pageNumber, short parentPageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + PARENT_PAGE_OFFSET);
        this.writeShort(parentPageNumber);
    }

    protected short getNumberOfRowsInPage(int pageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + NUMBER_OF_ROWS_OFFSET);
        return this.readShort();
    }

    protected void writeCellInPage(byte[] cell, short pageNumber) throws IOException {
        long fileOffset = Utils.getFileOffsetFromPageNumber(pageNumber);
        short pageOffsetForCell = getPageOffsetForNewCell(pageNumber, (short) cell.length);

        // write the cell
        this.seek(fileOffset + pageOffsetForCell);
        this.write(cell);

        // update the content start offset
        setContentStartOffset(pageNumber, pageOffsetForCell);

        // update the number of rows in the page header
        this.seek(fileOffset + NUMBER_OF_ROWS_OFFSET);
        short numberOfRows = this.readShort();
        this.seek(fileOffset + NUMBER_OF_ROWS_OFFSET);
        this.writeShort(numberOfRows + 1);

        // write cell start offset
        this.seek(fileOffset + getCellStartOffset(numberOfRows));
        this.writeShort(pageOffsetForCell);
    }
    
    protected short extendFileByOnePage() throws IOException {
        long newLength = this.length() + Settings.PAGE_SIZE;
        this.setLength(newLength);
        return (short) ((newLength / Settings.PAGE_SIZE) - 1);
    }

    private short getPageOffsetForNewCell(short pageNumber, short cellLength) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + CONTENT_START_OFFSET);
        short contentStart = this.readShort();
        return (short) (contentStart - cellLength);
    }

    private long getCellStartOffset(int numberOfRows) {
        return PAGE_HEADER_SIZE + numberOfRows * 2;
    }

    private void setContentStartOffset(int pageNumber, short offset) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + CONTENT_START_OFFSET);
        this.writeShort(offset);
    }
}
