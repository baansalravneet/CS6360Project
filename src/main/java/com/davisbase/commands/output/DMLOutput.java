package com.davisbase.commands.output;

public abstract class DMLOutput extends CommandOutput{
	private boolean status;
	private int numberOfRecords;
	
	public DMLOutput(boolean status, int numberOfRecords) {
		this.setStatus(status);
		this.setNumberOfRecords(numberOfRecords);
	}
	public boolean getStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public int getNumberOfRecords() {
		return numberOfRecords;
	}
	public void setNumberOfRecords(int numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

}
