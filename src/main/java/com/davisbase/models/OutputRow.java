package com.davisbase.models;

import java.util.ArrayList;
import java.util.List;

public class OutputRow {
    
    private final List<String> outputValues;

    public OutputRow() {
        this.outputValues = new ArrayList<>();
    }

    public List<String> getOutputValues() {
        return this.outputValues;
    }

    public void addOutputValue(String value) {
        this.outputValues.add(value);
    }

    public void prependOutputValue(String value) {
        this.outputValues.add(0, value);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String value : outputValues) {
            sb.append(value + "\t");
        }
        return sb.toString();
    }

}
