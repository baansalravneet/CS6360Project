package com.davisbase.services.components.impl;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.commands.impl.ExitCommand;
import com.davisbase.commands.impl.HelpCommand;
import com.davisbase.commands.impl.InvalidCommand;
import com.davisbase.services.Mediator;
import com.davisbase.services.components.Component;

public class QueryParser extends Component {

    private static final String EXIT = "exit";
    private static final String HELP = "help";

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
        String[] keywords = input.split(" ");
        if (HELP.equals(keywords[0])) return new HelpCommand(null);
        if (EXIT.equals(keywords[0])) return new ExitCommand(null);
        return new InvalidCommand(new CommandContext());
    }
}