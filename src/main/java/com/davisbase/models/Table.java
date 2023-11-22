package com.davisbase.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class Table extends RandomAccessFile {

    // TODO: add other settings like setPageSize and write table header
    public Table(String name) throws FileNotFoundException {
        super(new File(name), "rw");
    }
    
}
