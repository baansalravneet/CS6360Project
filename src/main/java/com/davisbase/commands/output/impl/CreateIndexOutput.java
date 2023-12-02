package com.davisbase.commands.output.impl;

import com.davisbase.commands.output.DDLOutput;

public class CreateIndexOutput extends DDLOutput{

	public CreateIndexOutput(boolean status) {
		super(status);
	}

	@Override
	public void display() {
		if(this.getStatus()) {
			System.out.println("Create Index is executed successfully");
		}else {
			System.out.println("Create Index operation failed");
		}
	}

}
