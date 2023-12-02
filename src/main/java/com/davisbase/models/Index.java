package com.davisbase.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.davisbase.config.Settings;

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

    
}
