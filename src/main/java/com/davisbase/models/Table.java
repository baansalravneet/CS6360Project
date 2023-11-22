package com.davisbase.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Table extends RandomAccessFile {

    // TODO: add page header with the constructor 
    public Table(String name) throws FileNotFoundException, IOException {
        super(new File(name), "rw");
        writePageHeader();
    }

    private void writePageHeader() throws IOException {
        super.writeByte(13); // new leaf page
        
        super.seek(0x04); // page offset of the start of the first row
        super.writeByte(65536);
        
        super.seek(0x0A); // parent page file offset
        super.writeInt(0xFFFFFFFF); // since this is the root, special value of 0xFFFFFFFF is used
    }
    
}
