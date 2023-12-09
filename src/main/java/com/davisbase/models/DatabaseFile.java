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
    protected static final long NUMBER_OF_CELLS_OFFSET = 0x02;
    
    // TODO: handle exceptions
    public DatabaseFile(String name) throws FileNotFoundException, IOException {
        super(new File(name), "rw");
    }

    public DatabaseFile(File file) throws FileNotFoundException {
        super(file, "rw");
    }

    protected void setEmptyPageStartContent(short pageNumber) throws IOException {
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

    protected short getRightSibling(short page) throws IOException {
        long fileOffset = Utils.getFileOffsetFromPageNumber(page);
        this.seek(fileOffset + RIGHT_SIBLING_OFFSET);
        return this.readShort();
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

    protected short getNumberOfCellsInPage(int pageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + NUMBER_OF_CELLS_OFFSET);
        return this.readShort();
    }

    protected short extendFileByOnePage() throws IOException {
        long newLength = this.length() + Settings.PAGE_SIZE;
        this.setLength(newLength);
        return (short) ((newLength / Settings.PAGE_SIZE) - 1);
    }

    protected void setPageType(short pageNumber, byte pageType) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber));
        this.writeByte(pageType);
    }

    protected byte getPageType(short pageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber));
        return this.readByte();
    }

    protected short getPageOffsetForNewCell(short pageNumber, short cellLength) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + CONTENT_START_OFFSET);
        short contentStart = this.readShort();
        return (short) (contentStart - cellLength);
    }

    protected void setContentStartOffset(int pageNumber, short offset) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + CONTENT_START_OFFSET);
        this.writeShort(offset);
    }

    protected short getCellStartOffsetInPage(int cellNumber, short page) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(page) + PAGE_HEADER_SIZE + cellNumber * 2);
        return this.readShort();
    }

    protected boolean checkOverflow(short pageNumber, int cellLength) throws IOException {
        short contentStart = getContentStartOffset(pageNumber);
        short numberOfRows = getNumberOfCellsInPage(pageNumber);
        int emptySpace = contentStart - numberOfRows * 2 - PAGE_HEADER_SIZE;
        return emptySpace < cellLength + 2; // 2 bytes for the cell offset
    }

}
