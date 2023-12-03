package com.davisbase.services.components.impl;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.commands.CommandType;
import com.davisbase.commands.impl.CreateTableCommand;
import com.davisbase.commands.impl.ExitCommand;
import com.davisbase.commands.impl.HelpCommand;
import com.davisbase.commands.impl.InsertCommand;
import com.davisbase.commands.impl.InvalidCommand;
import com.davisbase.models.ColumnDefinition;
import com.davisbase.models.ColumnValue;
import com.davisbase.models.DataType;
import com.davisbase.services.Mediator;
import com.davisbase.services.components.Component;
import com.davisbase.utils.Utils;

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
                return generateCreateTableCommand();
            case SHOW_TABLE:
                return null;
            case DROP_TABLE:
                return null;
            case CREATE_INDEX:
                return null;
            case INSERT:
                return generateInsertCommand(input);
            case DELETE:
                return null;
            case UPDATE:
                return null;
            case SELECT:
                return null;
            default:
                return new InvalidCommand(null);
        }

    }

    private Command generateInsertCommand(String input) {
        System.out.print("\nEnter table name.");
        String tableName = mediator.getInput();
        CommandContext context = new CommandContext();
        context.setTableName(tableName);
        ColumnValue columnValue = null;
        do {
            columnValue = null;
            System.out.print("\nEnter \"DONE\" if done");
            System.out.print("\nEnter column info (<name> <datatype> <value>)");
            String columnString = mediator.getInput();
            if (columnString.equalsIgnoreCase("DONE")) {
                break;
            }
            String[] words = columnString.split(" ");
            if (words.length != 3) {
                return new InvalidCommand(null);
            }
            String name = words[0];
            DataType dataType = DataType.getEnum(words[1]);
            if (dataType == null) {
                return new InvalidCommand(null);
            }
            String value = words[2];
            Object parsedValue = DataType.parseData(value, dataType);
            if (parsedValue == null) {
                return new InvalidCommand(context);
            }
            columnValue = new ColumnValue(name, dataType, parsedValue);
            context.addColumnValues(columnValue);
        } while (columnValue != null);
        return new InsertCommand(context);
    }

    private Command generateCreateTableCommand() {
        System.out.print("\nEnter table name.");
        String tableName = mediator.getInput();
        CommandContext context = new CommandContext();
        context.setTableName(tableName);
        ColumnDefinition definition = null;
        do {
            definition = null;
            System.out.print("\nEnter \"DONE\" if done");
            System.out.print(
                    "\nEnter column info (<name> <datatype> <nullable(YES/NO)> <unique(YES/NO)> <primary key(YES/NO)>)");
            String columnString = mediator.getInput();
            if (columnString.equalsIgnoreCase("DONE")) {
                break;
            }
            String[] words = columnString.split(" ");
            if (words.length != 5)
                return new InvalidCommand(null);
            String name = words[0];
            DataType dataType = DataType.getEnum(words[1]);
            if (dataType == null) {
                return new InvalidCommand(null); // invalid query
            }
            Boolean nullable = Utils.getBoolean(words[2]);
            if (nullable == null) {
                return new InvalidCommand(null);
            }
            Boolean unique = Utils.getBoolean(words[3]);
            if (unique == null) {
                return new InvalidCommand(null);
            }
            Boolean primaryKey = Utils.getBoolean(words[4]);
            if (primaryKey == null) {
                return new InvalidCommand(null);
            }
            definition = new ColumnDefinition(name, dataType, nullable, unique, primaryKey);
            context.addColumnContext(definition);

        } while (definition != null);
        return context.getColumnContext() != null && !context.getColumnContext().isEmpty()
                ? new CreateTableCommand(context)
                : new InvalidCommand(null);
    }
}