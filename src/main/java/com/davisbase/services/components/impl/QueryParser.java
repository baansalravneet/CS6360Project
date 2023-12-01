package com.davisbase.services.components.impl;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.commands.CommandType;
import com.davisbase.commands.impl.CreateTableCommand;
import com.davisbase.commands.impl.ExitCommand;
import com.davisbase.commands.impl.HelpCommand;
import com.davisbase.services.Mediator;
import com.davisbase.services.components.Component;

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

	private CommandContext getCommandContext() {
		CommandContext context = null;

		return context;
	}

	// TODO: complete this
	private Command generateCreateTableCommand(String input) {
		return new CreateTableCommand(null);
	}

}