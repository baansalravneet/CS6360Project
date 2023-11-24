package com.davisbase.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.davisbase.config.Settings;

public class Table extends RandomAccessFile {

    private static final byte META_DATA_PAGE_TYPE = 0x07;
    private static final long PAGE_TYPE_OFFSET = 0x00;
    private static final long ROOT_PAGE_OFFSET = 0x1;
    private static final long NUMBER_OF_ROWS_OFFSET = 0x5;

    // TODO: add page header with the constructor 
    // TODO: handle exceptions
    public Table(String name) throws FileNotFoundException, IOException {
        super(new File(name), "rw");
        this.setLength(Settings.PAGE_SIZE);
        writeMetadataPage();
    }
    
    private void writeMetadataPage() throws IOException {
        this.seek(PAGE_TYPE_OFFSET);
        this.writeByte(META_DATA_PAGE_TYPE);
    }

}
