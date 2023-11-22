package com.davisbase.services.components.impl;

import com.davisbase.commands.impl.ExitCommand;
import com.davisbase.commands.impl.HelpCommand;
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
        switch (input) {
            case EXIT:
                mediator.notify(this, new ExitCommand());
                break;
            case HELP:
                mediator.notify(this, new HelpCommand());
                break;
            default:
                System.out.println("Invalid query. Type \"help\" to display supported commands.");
        }
    }

    private String cleanUp(String input) {
        return input.strip().toLowerCase();
    }
}