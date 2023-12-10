package com.davisbase.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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
        super(name + Settings.TABLE_FILE_EXTENSION);
        writeFirstPage();
    }

    private void writeFirstPage() throws IOException {
        // set the file length
        this.setLength(Settings.PAGE_SIZE);
        // set page type
        setPageType((short) 0, LEAF_PAGE_TYPE);
        // set content start offset
        setEmptyPageStartContent((short) 0);
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

    // TODO
    public List<byte[]> getRowsMatchingClause(WhereClause whereClause) throws IOException {
        short rootPage = getRootPageNumber();
        short firstLeaf = getFirstLeafPage(rootPage);
        return new ArrayList<>();
    }

    public List<byte[]> getAllCells() throws IOException {
        short rootPage = getRootPageNumber();
        short leaf = getFirstLeafPage(rootPage);
        List<byte[]> result = new ArrayList<>();
        while (leaf != -1) {
            short nCells = getNumberOfCellsInPage(leaf);
            for (short cellNumber = 0; cellNumber < nCells; cellNumber++) {
                result.add(getLeafCellByCellNumber(leaf, cellNumber));
            }
            leaf = getRightSibling(leaf);
        }
        return result;
    }

    public static List<OutputRow> getOutputRows(List<byte[]> cells) {
        List<OutputRow> rows = new ArrayList<>();
        for (byte[] cell : cells) {
            OutputRow row = new OutputRow();
            byte[] record = getRecordFromLeafCell(cell);
            List<Object> values = split(record);
            for (Object value : values) {
                row.addOutputValue(String.valueOf(value));
            }
            rows.add(row);
        }
        return rows;
    }

    private static List<Object> split(byte[] record) {
        List<Object> result = new ArrayList<>();
        int numberOfColumns = (int) record[0];
        int typeIndex = 1;
        int valueIndex = 1 + numberOfColumns;
        while (numberOfColumns-- > 0) {
            byte type = record[typeIndex];
            switch (DataType.getEnum(type)) {
                case TINYINT:
                    result.add((byte) record[valueIndex]);
                    valueIndex += 1;
                    typeIndex += 1;
                    break;
                case INT:
                    result.add((int) ByteBuffer.wrap(record).getInt(valueIndex));
                    valueIndex += 4;
                    typeIndex += 1;
                    break;
                case TEXT:
                    int length = type - DataType.TEXT.getTypeCode();
                    StringBuilder sb = new StringBuilder();
                    while (length-- > 0) {
                        sb.append((char) record[valueIndex++]);
                    }
                    result.add(sb.toString());
                    typeIndex++;
                    break;
                case DOUBLE:
                    result.add((double) ByteBuffer.wrap(record).getDouble(valueIndex));
                    valueIndex += 8;
                    typeIndex++;
                    break;
                case BIGINT:
                    result.add((long) ByteBuffer.wrap(record).getLong(valueIndex));
                    valueIndex += 8;
                    typeIndex++;
                    break;
                case SMALLINT:
                    result.add((short) ByteBuffer.wrap(record).getShort(valueIndex));
                    valueIndex += 2;
                    typeIndex++;
                    break;
                case FLOAT:
                    result.add((float) ByteBuffer.wrap(record).getFloat(valueIndex));
                    valueIndex += 4;
                    typeIndex++;
                    break;
                // TODO: implement for other data types
                default:
                    break;
            }
        }
        return result;
    }

    private static byte[] getRecordFromLeafCell(byte[] cell) {
        byte[] record = new byte[cell.length - 6];
        int index = 6;
        while (index < cell.length) {
            record[index - 6] = cell[index];
            index++;
        }
        return record;
    }

    private byte[] getLeafCellByCellNumber(short pageNumber, short cellNumber) throws IOException {
        long pageOffset = Utils.getFileOffsetFromPageNumber(pageNumber);
        short cellOffset = getCellStartOffsetInPage(cellNumber, pageNumber);

        this.seek(pageOffset + cellOffset);
        short cellSize = this.readShort();
        byte[] cell = new byte[2 + 4 + cellSize]; // payload size, rowid, payload
        this.seek(pageOffset + cellOffset);
        this.read(cell);
        return cell;
    }

    private short getFirstLeafPage(short page) throws IOException {
        if (getPageType(page) == LEAF_PAGE_TYPE)
            return page;
        byte[] cell = getInteriorCellByCellNumber(page, (short) 0);
        int leftChild = getLeftChildPageNumberFromCell(cell);
        return getFirstLeafPage((short) leftChild);
    }

    // TODO change the return type
    // TODO handle cases where rowId is not found
    public byte[] getRowByRowId(int rowId) throws IOException {
        short rootPage = getRootPageNumber();
        return recursivelyTraverseForRowId(rootPage, rowId);
    }

    private byte[] recursivelyTraverseForRowId(short page, int rowId) throws IOException {
        if (getPageType(page) == LEAF_PAGE_TYPE) {
            short numberOfCells = getNumberOfCellsInPage(page);
            for (short cellNumber = 0; cellNumber < numberOfCells; cellNumber++) {
                byte[] cell = getInteriorCellByCellNumber(page, cellNumber);
                int thisRowId = getRowIdFromLeafCell(cell);
                if (thisRowId == rowId)
                    return cell;
            }
        }
        short numberOfCells = getNumberOfCellsInPage(page);
        for (short cellNumber = 0; cellNumber < numberOfCells; cellNumber++) {
            byte[] cell = getInteriorCellByCellNumber(page, cellNumber);
            int thisRowId = getRowIdFromInteriorCell(cell);
            if (thisRowId > rowId) {
                int leftChild = getLeftChildPageNumberFromCell(cell);
                return recursivelyTraverseForRowId((short) leftChild, rowId);
            }
        }
        short rightSibling = getRightSibling(page);
        if (rightSibling == -1)
            return null;
        return recursivelyTraverseForRowId(rightSibling, rowId);
    }

    private static int getLeftChildPageNumberFromCell(byte[] cell) {
        return ByteBuffer.wrap(cell).getInt(0);
    }

    private static int getRowIdFromInteriorCell(byte[] cell) {
        return ByteBuffer.wrap(cell).getInt(4);
    }

    private static int getRowIdFromLeafCell(byte[] cell) {
        return ByteBuffer.wrap(cell).getInt(2);
    }

    private byte[] getInteriorCellByCellNumber(short page, short cellNumber) throws IOException {
        short cellStartOffset = getCellStartOffsetInPage(cellNumber, page);
        byte[] cell = new byte[8]; // all interior page cells are 8 bytes
        this.seek(Utils.getFileOffsetFromPageNumber(page) + cellStartOffset);
        this.read(cell);
        return cell;
    }

    public void addRow(TableRow row) throws IOException {
        short rightmostLeafPage = getRightmostLeafPage(getRootPageNumber());
        int nextRowId = getLastRowIdInPage(rightmostLeafPage) + 1;

        byte[] payload = row.getRowBytesWithRecordHeader();
        short payloadSize = (short) payload.length;

        byte[] cell = Utils.prepend(payload, nextRowId);
        cell = Utils.prepend(cell, payloadSize);

        writeCellInPage(cell, rightmostLeafPage, nextRowId);
    }

    private void writeCellInPage(byte[] cell, short pageNumber, int rowId) throws IOException {
        if (checkOverflow(pageNumber, cell.length)) {
            if (getPageType(pageNumber) == LEAF_PAGE_TYPE) {
                pageNumber = appendNewLeaf(pageNumber, rowId);
            } else {
                // for interior page overflow
                // insert a interior page as sibling
                // insert a new page as parent
                // add this new cell to the parent page and the sibling page
                short siblingPage = addInteriorPage();
                short parentPage = addInteriorPage();
                short rightChildPageNumber = getRightSibling(pageNumber);
                setParentOfPage(pageNumber, parentPage);
                setParentOfPage(siblingPage, parentPage);
                setRightSibling(parentPage, siblingPage);
                setRightSibling(pageNumber, siblingPage);
                if (getRootPageNumber() == pageNumber) {
                    setPageAsRoot(parentPage);
                }
                writeCellInPage(cell, siblingPage, rowId);
                changeLeftChildInCell(cell, rightChildPageNumber);
                writeCellInPage(cell, parentPage, rowId);
            }
        }
        long fileOffset = Utils.getFileOffsetFromPageNumber(pageNumber);
        short pageOffsetForCell = getPageOffsetForNewCell(pageNumber, (short) cell.length);

        // write the cell
        this.seek(fileOffset + pageOffsetForCell);
        this.write(cell);

        // update the content start offset
        setContentStartOffset(pageNumber, pageOffsetForCell);

        // update the number of rows in the page header
        this.seek(fileOffset + NUMBER_OF_CELLS_OFFSET);
        short numberOfRows = this.readShort();
        this.seek(fileOffset + NUMBER_OF_CELLS_OFFSET);
        this.writeShort(numberOfRows + 1);

        // write cell start offset
        this.seek(fileOffset + PAGE_HEADER_SIZE + 2 * numberOfRows);
        this.writeShort(pageOffsetForCell);
    }

    private void changeLeftChildInCell(byte[] cell, int pageNumber) {
        ByteBuffer.wrap(cell).putInt(pageNumber);
    }

    private int getLastRowIdInPage(short pageNumber) throws IOException {
        short pageContentOffset = getContentStartOffset(pageNumber);
        if (pageContentOffset == Settings.PAGE_SIZE)
            return 0;
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + pageContentOffset + 2);
        return this.readInt();
    }

    private short appendNewLeaf(short pageNumber, int nextRowId) throws IOException {
        short newLeafPage = addLeafPage();

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
        // insert cell to make "pageNumber" as the left child
        writeCellInPage(getInteriorPageCell(pageNumber, nextRowId - 1), parentPage, -1); // dummy row ID
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
        setParentOfPage(newPageNumber, NULL_PARENT);
        return newPageNumber;
    }

    private short addLeafPage() throws IOException {
        short newPage = extendFileByOnePage();
        // set the content start offset
        setEmptyPageStartContent(newPage);
        // set right sibling offset
        setRightSibling(newPage, NULL_RIGHT_SIBLING);
        setPageType(newPage, LEAF_PAGE_TYPE);
        return newPage;
    }

    // recursive method to find the rightmost leaf page
    private short getRightmostLeafPage(short rootPage) throws IOException {
        short next = getRightSibling(rootPage);
        if (next != -1)
            return getRightmostLeafPage(next);
        return rootPage;
    }
}
