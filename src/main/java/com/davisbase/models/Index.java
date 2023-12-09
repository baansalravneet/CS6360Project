package com.davisbase.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.davisbase.config.Settings;
import com.davisbase.utils.Utils;

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
        byte[] cell;
        if (getPageType(pageToInsert) == LEAF_PAGE_TYPE) {
            cell = getIndexCellForLeaf(rowId, value, datatype);
        } else {
            // TODO implement for interior page, how will you insert in interior page?
            cell = new byte[0];
        }
        writeCellInPage(cell, pageToInsert);
    }
    
    private byte[] getIndexCellForLeaf(int rowId, Object value, DataType dataType) {
        int size = 2 + 1 + 1 + 4 + 4; // payload size + number of row Ids + data type + index value + row IDs
        byte[] cell = new byte[0];
        cell = Utils.prepend(cell, rowId);
        cell = Utils.prepend(cell, value);
        cell = Utils.prepend(cell, DataType.INT.getTypeCode());
        cell = Utils.prepend(cell, (byte) 1);
        cell = Utils.prepend(cell, (short) size);
        return cell;
    }

    private void writeCellInPage(byte[] cell, short pageNumber) throws IOException {
        // TODO check overflow
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
