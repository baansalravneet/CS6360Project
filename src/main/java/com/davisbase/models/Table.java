package com.davisbase.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Table extends RandomAccessFile {

    // TODO: add page header with the constructor 
    public Table(String name) throws FileNotFoundException, IOException {
        super(new File(name), "rw");
    }

}
