package com.davisbase.commands.output.impl;

import com.davisbase.commands.output.DMLOutput;

// TODO complete this
public class InsertOutput extends DMLOutput {

    public InsertOutput(boolean status, int numberOfRecords) {
		super(status, numberOfRecords);
	}

	@Override
	public void display() {
		if(this.getStatus()) {
			System.out.printf("Successfully Inserted %d records",this.getNumberOfRecords());
		}else {
			System.out.println("Insert Operation failed");
		}		
	}
    
}