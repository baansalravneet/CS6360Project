package com.davisbase.commands.output.impl;

import com.davisbase.commands.output.DDLOutput;

public class ExitOutput extends DDLOutput {
    public ExitOutput(boolean status) {
		super(status);
	}

	@Override
    public void display() {
		if(this.getStatus()) {
			System.out.println("Exit Operation Performed Successfully");
		}else {
			System.out.println("Exit Operation failed");			
		}
    }
}
