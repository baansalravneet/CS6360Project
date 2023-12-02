package com.davisbase.commands.output.impl;

import com.davisbase.commands.output.DDLOutput;

public class DropTableOutput extends DDLOutput {

	public DropTableOutput(boolean status) {
		super(status);
	}

	@Override
	public void display() {
		if (this.getStatus()) {
			System.out.println("Drop Table is executed successfully");
		} else {
			System.out.println("Drop Table operation failed");
		}
	}

}
