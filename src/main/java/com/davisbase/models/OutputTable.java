package com.davisbase.models;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

	public static List<List<Object>> getRecordsFromCells(List<byte[]> cells) {
		List<List<Object>> records = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		for (byte[] cell : cells) {
			ByteBuffer buffer = ByteBuffer.wrap(cell);

			// Extracting payload length
			int payloadLength = buffer.getShort();

			// Extracting row ID
			int rowId = buffer.getInt();

			// Extracting number of columns
			int numColumns = buffer.get();

			// Extracting data types of columns
			byte[] columnDataTypes = new byte[numColumns];
			buffer.get(columnDataTypes);

			// Extracting column values
			List<Object> columnValues = new ArrayList<>();

			for (byte columnDataType : columnDataTypes) {
				switch (columnDataType) {
				case 0x00:
					// NULL type
					columnValues.add(null);
					break;
				case 0x01:
					// TINYINT
					columnValues.add(buffer.get());
					break;
				case 0x02:
					// SMALLINT
					columnValues.add(buffer.getShort());
					break;
				case 0x03:
					// INT
					columnValues.add(buffer.getInt());
					break;
				case 0x04:
					// BIGINT
					columnValues.add(buffer.getLong());
					break;
				case 0x05:
					// FLOAT
					columnValues.add(buffer.getFloat());
					break;
				case 0x06:
					// DOUBLE
					columnValues.add(buffer.getDouble());
					break;
				case 0x08:
					// YEAR
					columnValues.add(buffer.get());
					break;
				case 0x09:
					// TIME
					columnValues.add(buffer.getInt());
					break;
				case 0x0A:
					// DATETIME
					columnValues.add(sdf.format(new Date(buffer.getLong())));
					break;
				case 0x0B:
					// DATE
					Date date = new Date(buffer.getLong());
//					date.setHours(0);
//					date.setMinutes(0);
//					date.setSeconds(0);
					columnValues.add(sdf.format(date));
					break;
                case 0x0C:
					columnValues.add(sdf.format(""));                	
				default:
                    int stringLength = (columnDataType & 0xFF )-0x0C;
                    byte[] stringBytes = new byte[stringLength];
                    buffer.get(stringBytes);
                    // ASCII String
                    String asciiString = new String(stringBytes, StandardCharsets.US_ASCII);
                    columnValues.add(asciiString);
				}
			}
			records.add(columnValues);
		}
		return records;
	}
}