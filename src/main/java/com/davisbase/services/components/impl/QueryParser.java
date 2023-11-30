package com.davisbase.services.components.impl;

import com.davisbase.commands.Command;
import com.davisbase.commands.impl.CreateTableCommand;
import com.davisbase.commands.impl.ExitCommand;
import com.davisbase.commands.impl.HelpCommand;
import com.davisbase.commands.impl.InvalidCommand;
import com.davisbase.services.Mediator;
import com.davisbase.services.components.Component;

public class QueryParser extends Component {

    private static final String EXIT = "^exit\\s?.*";
    private static final String HELP = "^help\\s?.*";
    private static final String CREATE_TABLE = "^create table\\s?.*";

    public QueryParser(Mediator mediator) {
        super(mediator);
    }

    public void parseQuery(String input) {
        input = cleanUp(input);
        mediator.notify(this, generateCommand(input));
    }

    private String cleanUp(String input) {
        return input.strip().toLowerCase();
    }

    private Command generateCommand(String input) {
        if (input.matches(HELP)) return new HelpCommand(null);
        if (input.matches(EXIT)) return new ExitCommand(null);
        if (input.matches(CREATE_TABLE)) return generateCreateTableCommand(input);
        return new InvalidCommand(null);
    }
    
    // TODO: complete this
    private Command generateCreateTableCommand(String input) {
        return new CreateTableCommand(null);
    }
}