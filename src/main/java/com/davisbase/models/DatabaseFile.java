package com.davisbase.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.davisbase.config.Settings;
import com.davisbase.utils.Utils;

public abstract class DatabaseFile extends RandomAccessFile {

	private static final long FILE_HEADER_PAGE_TYPE_OFFSET = 0x00;
    private static final byte FILE_HEADER_PAGE_TYPE = 0x07;
    private static final int NULL_PARENT = -1;
    private static final byte TABLE_TREE_LEAF_PAGE = 0x0D;
    private static final long PARENT_PAGE_POINTER_OFFSET = 0x0A;
    private static final int NULL_RIGHT_SIBLING = -1;
    private static final long ROOT_PAGE_NUMBER_OFFSET = 0x01;
    protected static final long PAGE_HEADER_CONTENT_START_OFFSET = 0x04;
    private static final long RIGHT_SIBLING_OFFSET = 0x06;
    
    // TODO: handle exceptions
    public DatabaseFile(String name) throws FileNotFoundException, IOException {
        super(new File(name), "rw");
        writeFileHeaderPage();
    }

    public DatabaseFile(File file) throws FileNotFoundException {
        super(file, "rw");
    }

    private void writeFileHeaderPage() throws IOException {
        this.setLength(Settings.PAGE_SIZE);

        this.seek(FILE_HEADER_PAGE_TYPE_OFFSET);
        this.writeByte(FILE_HEADER_PAGE_TYPE);

        int rootPageNumber = addLeafPage();
        setPageAsRoot(rootPageNumber);
    }
    
    protected void setPageAsRoot(int pageNumber) throws IOException {
        this.seek((pageNumber - 1) * Settings.PAGE_SIZE + PARENT_PAGE_POINTER_OFFSET);
        this.writeInt(NULL_PARENT);
        setRootPageInFileHeader(pageNumber);
    }

    private void setRootPageInFileHeader(int pageNumber) throws IOException {
        this.seek(ROOT_PAGE_NUMBER_OFFSET);
        this.writeInt(pageNumber);
    }

    protected int addLeafPage() throws IOException {
        int pages = (int) (this.length() / Settings.PAGE_SIZE) + 1;

        // increase the length of the file
        this.setLength(Settings.PAGE_SIZE * pages);

        // add the page type
        this.seek((pages - 1) * Settings.PAGE_SIZE);
        this.writeByte(TABLE_TREE_LEAF_PAGE);

        // set the content start offset
        this.seek((pages - 1) * Settings.PAGE_SIZE + PAGE_HEADER_CONTENT_START_OFFSET);
        this.writeShort(Settings.PAGE_SIZE);

        // set right sibling offset
        this.seek((pages - 1) * Settings.PAGE_SIZE + RIGHT_SIBLING_OFFSET);
        this.writeInt(NULL_RIGHT_SIBLING);

        return pages;
    }

    protected void setRightmostChildNull(int pageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + RIGHT_SIBLING_OFFSET);
        this.writeInt(NULL_RIGHT_SIBLING);
    }

    protected int getRootPageNumber() throws IOException {
        this.seek(ROOT_PAGE_NUMBER_OFFSET);
        return this.readInt();
    }

    protected int getRightmostLeafPageNumber(int pageNumber) throws IOException {
        long fileOffset = Utils.getFileOffsetFromPageNumber(pageNumber);
        this.seek(fileOffset + RIGHT_SIBLING_OFFSET);
        int next = this.readInt();
        if (next != -1)
            return getRightmostLeafPageNumber(next);
        return pageNumber;
    }

    protected int getParentPage(int pageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + PARENT_PAGE_POINTER_OFFSET);
        return this.readInt();
    }

    protected void setRightSibling(int pageNumber, int siblingPageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + RIGHT_SIBLING_OFFSET);
        this.writeInt(siblingPageNumber);
    }

    protected void setParentOfPage(int pageNumber, int parentPageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + PARENT_PAGE_POINTER_OFFSET);
        this.writeInt(parentPageNumber);
    }
}
