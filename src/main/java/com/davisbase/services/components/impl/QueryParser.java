package com.davisbase.services.components.impl;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.commands.CommandType;
import com.davisbase.commands.impl.CreateTableCommand;
import com.davisbase.commands.impl.ExitCommand;
import com.davisbase.commands.impl.HelpCommand;
import com.davisbase.models.ColumnDefinition;
import com.davisbase.models.DataType;
import com.davisbase.services.Mediator;
import com.davisbase.services.components.Component;

import java.util.ArrayList;
import java.util.List;

public class QueryParser extends Component {

	public QueryParser(Mediator mediator) {
		super(mediator);
	}

	public void parseQuery(String input) {
		input = cleanUp(input);
		mediator.notify(this, generateCommand(input));
	}

	private String cleanUp(String input) {
		return input.strip();
	}

	private Command generateCommand(String input) {
		switch (CommandType.getCommandType(input)) {
		case EXIT:
			return new ExitCommand(null);
		case HELP:
			return new HelpCommand(null);
		case CREATE_TABLE:

			return generateCreateTableCommand(input);
		case SHOW_TABLE:
			return null;
		case DROP_TABLE:
			return null;
		case CREATE_INDEX:
			return null;
		case INSERT:
			return null;
		case DELETE:
			return null;
		case UPDATE:
			return null;
		case SELECT:
			return null;
		default:
			System.out.println("Unknown command type");
			return null;
		}

	}

//	private CommandContext getCommandContext() {
//		CommandContext context = null;
//
//		return context;
//	}
private CommandContext getCreateTableCommandContext(String input) {
	CommandContext context = new CommandContext();
	List<ColumnDefinition> columnDefinitions = new ArrayList<>();

	System.out.println(input);

	// Extract the part of the string inside the parentheses
	int startIndex = input.indexOf("(") + 1;
	int endIndex = input.lastIndexOf(")");
	if (startIndex < 0 || endIndex < 0 || endIndex <= startIndex) {
		// Handle error - invalid input format
		return null;
	}
	String columnsPart = input.substring(startIndex, endIndex).trim();

	// Split the extracted part into individual column definitions
	String[] cols = columnsPart.split(",\\s*");

	for (String col : cols) {
		// Split each column definition into its parts
		String[] parts = col.split("\\s+");
		String columnName = parts[0];
		DataType dataType = DataType.valueOf(parts[1].toUpperCase());

		boolean isNotNull = col.contains("NO");
		boolean isPrimaryKey = col.contains("PRI");
		boolean isUnique = col.contains("UNI") && !isPrimaryKey;

		ColumnDefinition colDef = new ColumnDefinition(columnName, dataType, isNotNull, isUnique, isPrimaryKey);
		columnDefinitions.add(colDef);
	}

	// Set the column definitions in the context
	context.setColumnContext(columnDefinitions);

	// Extract and set the table name
	String tableName = extractTableName(input);
	System.out.println(tableName);
	context.setTableName(tableName);

	return context;
}

	private String extractTableName(String input) {
		String[] parts = input.split("\\s+");
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].equalsIgnoreCase("TABLE") && (i + 1) < parts.length) {
				// Extracting table name and removing anything after '('
				String tableName = parts[i + 1].trim();
				int parenIndex = tableName.indexOf("(");
				if (parenIndex != -1) {
					tableName = tableName.substring(0, parenIndex);
				}
				return tableName;
			}
		}
		return null; // Table name not found in the input
	}

	// TODO: complete this
	private Command generateCreateTableCommand(String input) {

		return new CreateTableCommand(getCreateTableCommandContext(input));
	}

}