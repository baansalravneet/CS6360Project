package com.davisbase.models;

import java.util.ArrayList;
import java.util.List;

public class OutputTable {
	private String tableName;
	private List<String> columnNames;
	private List<List<Object>> records;
	private List<String> columnsToPrint;

	public OutputTable(String tableName, List<String> columnNames) {
		this.tableName = tableName;
		this.columnNames = columnNames;
		this.records = new ArrayList<>();
	}

	public OutputTable(String tableName, List<String> columnNames, List<List<Object>> records) {
		this.tableName = tableName;
		this.columnNames = columnNames;
		this.records = records;
	}

	public void setRecords(List<List<Object>> records) {
		this.setRecords(records);
	}

	public void addRecord(List<Object> record) {
		records.add(record);
	}

	public void setColumnsToPrint(List<String> columnsToPrint) {
		this.columnsToPrint = columnsToPrint;
	}

	public void print() {
		if (columnsToPrint == null) {
			columnsToPrint = columnNames;
		}
		this.printColumns(columnsToPrint);
	}

	private void printColumns(List<String> columnsToPrint) {
		System.out.println(tableName);

		// Print specified columns as headers
		for (String column : columnsToPrint) {
			System.out.print(column + "\t");
		}
		System.out.println();

		// Print records with specified columns
		for (List<Object> record : records) {
			for (String column : columnsToPrint) {
				int columnIndex = columnNames.indexOf(column);
				if (columnIndex != -1) {
					System.out.print(record.get(columnIndex) + "\t");
				} else {
					// Handle the case where the specified column is not found
					System.out.print("N/A\t");
				}
			}
			System.out.println();
		}
	}
}