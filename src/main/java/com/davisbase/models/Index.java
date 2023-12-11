package com.davisbase.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

import com.davisbase.config.Settings;
import com.davisbase.utils.Utils;

// TODO text data type is not handled
public class Index extends DatabaseFile {
    private static final byte LEAF_PAGE_TYPE = 0x0A;
    private static final byte INTERIOR_PAGE_TYPE = 0x0D;

    public Index(String name) throws FileNotFoundException, IOException {
        super(name + Settings.INDEX_FILE_EXTENSION);
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
    public Index(File file) throws FileNotFoundException {
        super(file);
    }

    public void addIndex(DataType datatype, int rowId, Object value) throws IOException {
        // TODO check if the datatype is correct for this index file
        short rootPage = getRootPageNumber();
        short pageToInsert = getPageToInsertIndex(rootPage, value, datatype);
        // inserts only happen in the leaf
        byte[] cell = getIndexCellForLeaf(rowId, value, datatype);
        writeCellInPage(cell, pageToInsert, value, datatype);
    }

    private byte[] getIndexCellForLeaf(int rowId, Object value, DataType dataType) {
        int size = 2 + 1 + 1 + 4 + 4; // payload size + number of row Ids + data type + index value + row IDs
        byte[] cell = new byte[0];
        cell = Utils.prepend(cell, rowId);
        cell = Utils.prepend(cell, value);
        cell = Utils.prepend(cell, dataType.getTypeCode());
        cell = Utils.prepend(cell, (byte) 1);
        cell = Utils.prepend(cell, (short) size);
        return cell;
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

    // TODO implement this for other values
    private byte[] getLeafCellByCellNumber(short pageNumber, int cellNumber) throws IOException {
        short cellOffset = getCellStartOffsetInPage(cellNumber, pageNumber);
        short cellSize = 10; // TODO fix this for other data types. Assuming 10 for now
        byte[] cell = new byte[cellSize];
        this.seek(Utils.getFileOffsetFromPageNumber(pageNumber) + cellOffset);
        this.read(cell);
        return cell;
    }

    private void removeLeafCellByCellNumber(short pageNumber, int cellNumber) throws IOException {
        long pageOffset = Utils.getFileOffsetFromPageNumber(pageNumber);
        long cellOffset = getCellStartOffsetInPage(cellNumber, pageNumber);
        long cellOffsetOffset = pageOffset + PAGE_HEADER_SIZE + cellNumber * 2;
        // TODO currently assuming size 10 for 1 row and int value index. Fix this
        byte[] result = new byte[10];
        // removing the record
        this.seek(pageOffset + cellOffset);
        this.write(result);
        // removing the pointer to this record
        this.seek(pageOffset + cellOffsetOffset);
        this.writeShort(0);
        decrementNumberOfCellsOnPage(pageNumber);
        this.seek(pageOffset + getCellStartOffsetInPage(cellNumber - 1, pageNumber));
        short newContentOffset = this.readShort();
        setContentStartOffset(pageNumber, newContentOffset);
    }

    private Object splitLeaf(short pageA, short pageB, short parent, Object value, DataType dataType)
            throws IOException {
        int numberOfCells = getNumberOfCellsInPage(pageA);
        Deque<byte[]> cells = new ArrayDeque<>();

        // take out half the cells
        for (int cellNumber = numberOfCells - 1; cellNumber >= numberOfCells / 2; cellNumber--) {
            byte[] cell = getLeafCellByCellNumber(pageA, cellNumber);
            cells.addFirst(cell);
            removeLeafCellByCellNumber(pageA, cellNumber);
        }
        // middle cell goes to the parent
        byte[] middleCell = cells.pollLast();
        Object midValue = ByteBuffer.wrap(middleCell).getInt(2); // TODO implement this for other data types
        middleCell = Utils.prepend(middleCell, pageA);
        writeCellInPage(middleCell, parent, value, dataType);
        // rest goes to the other page
        while (!cells.isEmpty()) {
            writeCellInPage(cells.pollFirst(), pageB, value, dataType);
        }
        return midValue;
    }

    private void writeCellInPage(byte[] cell, short pageNumber, Object value, DataType dataType) throws IOException {
        if (checkOverflow(pageNumber, cell.length)) {
            if (getPageType(pageNumber) == LEAF_PAGE_TYPE) {
                short parent = addInteriorPage();
                short rightSibling = addLeafPage();
                Object midValue = splitLeaf(pageNumber, rightSibling, parent, value, dataType);
                if (getRootPageNumber() == pageNumber) {
                    setPageAsRoot(parent);
                }
                setRightSibling(pageNumber, rightSibling);
                setParentOfPage(pageNumber, parent);
                setParentOfPage(rightSibling, parent);
                setRightSibling(parent, rightSibling);
                if (Utils.compare(value, midValue, dataType) > 0) {
                    pageNumber = rightSibling;
                }
            } else {
                // TODO implement this
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

    private short addInteriorPage() throws IOException {
        short newPageNumber = extendFileByOnePage();
        setPageType(newPageNumber, INTERIOR_PAGE_TYPE);
        setEmptyPageStartContent(newPageNumber);
        setRightmostChildNull(newPageNumber);
        setParentOfPage(newPageNumber, NULL_PARENT);
        return newPageNumber;
    }

    private short getPageToInsertIndex(short page, Object value, DataType type) throws IOException {
        if (getPageType(page) == LEAF_PAGE_TYPE) {
            return page;
        }
        short numberOfCells = getNumberOfCellsInPage(page);
        for (int cellNumber = 0; cellNumber < numberOfCells; cellNumber++) {
            byte[] cell = getInteriorCellByCellNumber(page, cellNumber);
            short leftChildPageNumber = (short) getLeftChildPageNumberFromCell(cell);
            Object indexValueInCell = getIndexValueInCell(cell, type);
            if (Utils.compare(indexValueInCell, value, type) > 0) {
                return leftChildPageNumber;
            }
        }
        short rightSibling = getRightSibling(page);
        if (rightSibling == -1) {
            return page;
        }
        return getPageToInsertIndex(rightSibling, value, type);
    }

    private byte[] getInteriorCellByCellNumber(short page, int cellNumber) throws IOException {
        long fileOffset = Utils.getFileOffsetFromPageNumber(page);
        short pageOffset = getPageOffsetByCellNumber(page, cellNumber);
        this.seek(fileOffset + pageOffset + 4);
        short payloadSize = this.readShort();
        byte[] cell = new byte[4 + 2 + payloadSize];
        this.seek(fileOffset + pageOffset);
        this.read(cell);
        return cell;
    }

    private short getPageOffsetByCellNumber(short page, int cellNumber) throws IOException {
        this.seek(Utils.getFileOffsetFromPageNumber(page) + PAGE_HEADER_SIZE + 2 * cellNumber);
        return this.readShort();
    }

    private static Object getIndexValueInCell(byte[] cell, DataType type) {
        switch (type) {
            case INT:
                return ByteBuffer.wrap(cell).getInt(8);
            case TINYINT:
                return ByteBuffer.wrap(cell).get(8);
            case FLOAT:
                return ByteBuffer.wrap(cell).getFloat(8);
            case BIGINT:
                return ByteBuffer.wrap(cell).getLong(8);
            case SMALLINT:
                return ByteBuffer.wrap(cell).getShort(8);
            case TEXT:
                // TODO
            case DOUBLE:
                return ByteBuffer.wrap(cell).getDouble(8);
            default:
                throw new UnsupportedOperationException("Unimplemented");
        }
    }

    private int getLeftChildPageNumberFromCell(byte[] cell) {
        return ByteBuffer.wrap(cell).getInt(0);
    }
}
