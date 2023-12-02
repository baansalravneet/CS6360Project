package com.davisbase.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.davisbase.config.Settings;
import com.davisbase.utils.Utils;

/* 
 * PAGE HEADER
 * 0x00 - PAGE TYPE = 2 - index interior, 5 - table interior, 10 - index leaf, 13 - table leaf
 * 0x01 - unused
 * 0x02 - short | number of cells on the page
 * 0x04 - short | page offset of start of cell content area
 * 0x06 - short | root page number of the file. Same in every page.
 * 0x08 - short | interior - page number of rightmost child. leaf - page number of sibling to the right
 * 0x0A - short | parent page number
 * 0x0E - short unused
 * 
 * CELL HEADER
 * LEAF
 * short - payload size
 * int - row id
 * 
 * RECORD HEADER
 * byte - number of columns
 * list byte - column data types
 * data values
 */

// ALWAYS SEEK BEFORE YOU READ OR WRITE
public class Table extends DatabaseFile {

    private static final byte LEAF_PAGE_TYPE = 0x0D;
    private static final byte INTERIOR_PAGE_TYPE = 0x05;

    // Use this if you are initialising a new table
    // TODO: handle exceptions
    public Table(String name) throws FileNotFoundException, IOException {
        super(name);
        writeFirstPage();
    }

    private void writeFirstPage() throws IOException {
        // set the file length
        this.setLength(Settings.PAGE_SIZE);
        // set page type
        setPageType(0, LEAF_PAGE_TYPE);
        // set content start offset
        setEmptyPageStartContent(0);
        // set this page as root
        setPageAsRoot((short) 0);
        // set the right sibling null
        setRightSibling((short) 0, NULL_RIGHT_SIBLING);
        // set the parent as null
        setParentOfPage((short) 0, NULL_PARENT);
    }

    // Use this if there is already a file present for this table
    public Table(File file) throws FileNotFoundException {
        super(file);
    }

    public void addRow(TableRow row) throws IOException {
        short rightmostLeafPage = getRightmostLeafPage(getRootPageNumber());
        int nextRowId = getLastRowIdInPage(rightmostLeafPage) + 1;

        byte[] payload = row.getRowBytesWithRecordHeader();
        short payloadSize = (short) payload.length;

        byte[] cell = Utils.prepend(payload, nextRowId);
        cell = Utils.prepend(cell, payloadSize);

        if (checkOverflow(rightmostLeafPage, cell.length)) {
            rightmostLeafPage = appendNewLeaf(rightmostLeafPage, nextRowId);
        }
        writeCellInPage(cell, rightmostLeafPage);
    }

    private int getLastRowIdInPage(short pageNumber) throws IOException {
        short pageContentOffset = getContentStartOffset(pageNumber);
        if (pageContentOffset == Settings.PAGE_SIZE) return 0;
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + pageContentOffset + 2);
        return this.readInt();
    }

    private short appendNewLeaf(short pageNumber, int nextRowId) throws IOException {
        short newLeafPage = addLeafPage();
        setPageType(newLeafPage, LEAF_PAGE_TYPE);

        // set this new page as right sibling
        setRightSibling(pageNumber, newLeafPage);
    
        short parentPage = getParentPage(pageNumber);
        if (parentPage == -1) { // this is the root
            parentPage = addInteriorPage();
            setPageAsRoot(parentPage);
            setParentOfPage(pageNumber, parentPage);
        }
        setParentOfPage(newLeafPage, parentPage);
        setRightSibling(parentPage, newLeafPage);
        // TODO handle case for overflow in the interior page
        // insert cell to make "pageNumber" as the left child
        writeCellInPage(getInteriorPageCell(pageNumber, nextRowId - 1), parentPage);
        return newLeafPage;
    }

    private byte[] getInteriorPageCell(int leftChildPageNumber, int rowId) {
        byte[] cell = new byte[0];
        cell = Utils.prepend(cell, rowId);
        cell = Utils.prepend(cell, leftChildPageNumber);
        return cell;
    }

    private short addInteriorPage() throws IOException {
        short newPageNumber = extendFileByOnePage();
        setPageType(newPageNumber, INTERIOR_PAGE_TYPE);
        setEmptyPageStartContent(newPageNumber);
        setRightmostChildNull(newPageNumber);
        return newPageNumber;
    }

    private void setPageType(int pageNumber, byte pageType) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber));
        this.writeByte(pageType);
    }
    
    private boolean checkOverflow(short pageNumber, int cellLength) throws IOException {
        short contentStart = getContentStartOffset(pageNumber);
        short numberOfRows = getNumberOfRowsInPage(pageNumber);
        int emptySpace = contentStart - numberOfRows * 2 - PAGE_HEADER_SIZE;
        return emptySpace < cellLength + 2; // 2 bytes for the cell offset
    }

}
