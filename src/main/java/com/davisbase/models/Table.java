package com.davisbase.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.davisbase.config.Settings;
import com.davisbase.utils.Utils;

/* 
 * FILE HEADER PAGE
 * 0x00 - PAGE TYPE = 0x07
 * 0x01 - ROOT PAGE NUMBER (1 indexed)
 * 0x05 - LAST ROW ID
 * 
 * PAGE HEADER
 * 0x00 - PAGE TYPE = 2 - index interior, 5 - table interior, 10 - index leaf, 13 - table leaf
 * 0x01 - unused
 * 0x02 - short number of cells on the page
 * 0x04 - short start of cell content area
 * 0x06 - int interior - page number of rightmost child, leaf - page number of sibling to the right
 * 0x0A - int parent page number
 * 0x0E - short unused
 * 
 * CELL HEADER
 * int - left child page number
 * short - cell payload size
 * int - row id
 * 
 * RECORD HEADER
 * byte - number of columns
 * list byte - column data types
 * data values
 */

// ALWAYS SEEK BEFORE YOU READ OR WRITE
public class Table extends RandomAccessFile {

    private static final byte FILE_HEADER_PAGE_TYPE = 0x07;
    private static final long FILE_HEADER_PAGE_TYPE_OFFSET = 0x00;
    private static final long ROOT_PAGE_NUMBER_OFFSET = 0x01;
    private static final int ROW_ID_OFFSET = 0x05;
    private static final long PAGE_HEADER_CONTENT_START_OFFSET = 0x04;
    private static final byte TABLE_TREE_LEAF_PAGE = 0x0D;
    private static final byte TABLE_TREE_INTERIOR_PAGE = 0x05;
    private static final long RIGHT_SIBLING_OFFSET = 0x06;
    private static final int NULL_RIGHT_SIBLING = -1;
    private static final long PARENT_PAGE_POINTER_OFFSET = 0x0A;
    private static final int NULL_PARENT = -1;
    private static final long PAGE_HEADER_NUMBER_OF_ROWS_OFFSET = 0x02;
    private static final int PAGE_HEADER_SIZE = 16;

    // TODO: handle exceptions
    public Table(String name) throws FileNotFoundException, IOException {
        super(new File(name), "rw");
        writeFileHeaderPage();
    }

    private void writeFileHeaderPage() throws IOException {
        this.setLength(Settings.PAGE_SIZE);

        this.seek(FILE_HEADER_PAGE_TYPE_OFFSET);
        this.writeByte(FILE_HEADER_PAGE_TYPE);

        int rootPageNumber = addLeafPage();
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

    private int addLeafPage() throws IOException {
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
        short payloadSize = (short) payload.length;

        byte[] cell = Utils.prepend(payload, nextRowId);
        cell = Utils.prepend(cell, payloadSize);

        int rightmostLeafPageNumber = getRightmostLeafPageNumber(getRootPageNumber());
        if (checkOverflow(rightmostLeafPageNumber, cell.length)) {
            // TODO check if the B+ tree also needs to be balanced
            // TODO more B+ tree stuff
            rightmostLeafPageNumber = appendNewLeaf(rightmostLeafPageNumber, nextRowId);
        }
        writeCellInPage(cell, rightmostLeafPageNumber);
    }

    private int appendNewLeaf(int pageNumber, int nextRowId) throws IOException {
        int newLeafPage = addLeafPage();
        setRightSibling(pageNumber, newLeafPage);
    
        int parentPage = getParentPage(pageNumber);
        if (parentPage == -1) {
            parentPage = addInteriorPage();
            setPageAsRoot(parentPage);
            setParentOfPage(pageNumber, parentPage);
            setParentOfPage(newLeafPage, parentPage);
            setRightSibling(parentPage, newLeafPage);
            getInteriorPageCell(pageNumber, nextRowId);
            writeCellInPage(getInteriorPageCell(pageNumber, nextRowId), parentPage);
            return newLeafPage;
        } else {
            // TODO
            return 0;
        }
    }

    private byte[] getInteriorPageCell(int leftChildPageNumber, int rowId) {
        byte[] cell = new byte[0];
        cell = Utils.prepend(cell, rowId);
        cell = Utils.prepend(cell, leftChildPageNumber);
        return cell;
    }

    private int getParentPage(int pageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + PARENT_PAGE_POINTER_OFFSET);
        return this.readInt();
    }

    private void setRightSibling(int pageNumber, int siblingPageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + RIGHT_SIBLING_OFFSET);
        this.writeInt(siblingPageNumber);
    }

    private void setParentOfPage(int pageNumber, int parentPageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + PARENT_PAGE_POINTER_OFFSET);
        this.writeInt(parentPageNumber);
    }
    
    private void splitInteriorPage() {
        // TODO
    }

    private int addInteriorPage() throws IOException {
        int newPageNumber = extendFileByOnePage();
        setPageType(newPageNumber, TABLE_TREE_INTERIOR_PAGE);
        setEmptyPageStartContent(newPageNumber);
        setRightmostChildNull(newPageNumber);
        return newPageNumber;
    }

    private void setRightmostChildNull(int pageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + RIGHT_SIBLING_OFFSET);
        this.writeInt(NULL_RIGHT_SIBLING);
    }

    private void setEmptyPageStartContent(int pageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + PAGE_HEADER_CONTENT_START_OFFSET);
        this.writeShort(Settings.PAGE_SIZE);
    }

    private void setPageType(int pageNumber, byte pageType) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber));
        this.writeByte(pageType);
    }
    
    private int extendFileByOnePage() throws IOException {
        long newLength = this.length() + Settings.PAGE_SIZE;
        this.setLength(newLength);
        return (int) (newLength / Settings.PAGE_SIZE);
    }

    private byte getPageType(int pageNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber));
        return this.readByte();
    }

    private boolean isRootPage(int pageNumber) throws IOException {
        long fileOffset = Utils.getFileOffsetFromPageNumber(pageNumber);
        this.seek(fileOffset + PARENT_PAGE_POINTER_OFFSET);
        return this.readInt() == -1;
    }

    private boolean checkOverflow(int pageNumber, int cellLength) throws IOException {
        long pageOffset = (pageNumber - 1) * Settings.PAGE_SIZE;
        this.seek(pageOffset + PAGE_HEADER_CONTENT_START_OFFSET);
        short contentStart = this.readShort();
        this.seek(pageOffset + PAGE_HEADER_NUMBER_OF_ROWS_OFFSET);
        short numberOfRows = this.readShort();
        int emptySpace = contentStart - numberOfRows * 2 - PAGE_HEADER_SIZE;
        return emptySpace < cellLength + 2; // 2 bytes for the cell offset
    }

    private void writeCellInPage(byte[] cell, int pageNumber) throws IOException {
        long fileOffset = Utils.getFileOffsetFromPageNumber(pageNumber);
        int pageOffsetForCell = getPageOffsetForNewCell(fileOffset, cell.length);

        // write the cell
        this.seek(fileOffset + pageOffsetForCell);
        this.write(cell);

        // update the content start offset
        this.seek(fileOffset + PAGE_HEADER_CONTENT_START_OFFSET);
        this.writeShort(pageOffsetForCell);

        // update the number of rows in the page header
        this.seek(fileOffset + PAGE_HEADER_NUMBER_OF_ROWS_OFFSET);
        short numberOfRows = this.readShort();
        this.seek(fileOffset + PAGE_HEADER_NUMBER_OF_ROWS_OFFSET);
        this.writeShort(numberOfRows + 1);

        // write cell start offset
        this.seek(fileOffset + getCellStartOffset(numberOfRows));
        this.writeShort(pageOffsetForCell);
    }

    private long getCellStartOffset(int numberOfRows) {
        return PAGE_HEADER_SIZE + numberOfRows * 2;
    }

    private int getPageOffsetForNewCell(long fileOffset, int cellLength) throws IOException {
        long contentStartOffset = fileOffset + PAGE_HEADER_CONTENT_START_OFFSET;
        this.seek(contentStartOffset);
        short contentStart = this.readShort();
        return contentStart - cellLength;
    }

    private int getRightmostLeafPageNumber(int pageNumber) throws IOException {
        long fileOffset = Utils.getFileOffsetFromPageNumber(pageNumber);
        this.seek(fileOffset + RIGHT_SIBLING_OFFSET);
        int next = this.readInt();
        if (next != -1)
            return getRightmostLeafPageNumber(next);
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

    // TODO: B+Tree traversal including creating new page, extending file, and tree
    // rebalancing
    private long getFileOffsetPage() throws IOException {
        this.setLength(1024);
        return 512;
    }

}
