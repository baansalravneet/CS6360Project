package com.davisbase.services;

import com.davisbase.commands.impl.ExitCommand;

public class QueryParser extends Component {

    public QueryParser(Mediator mediator) {
        super(mediator);
    }

    public void parseQuery(String input) {
        if (input.equals("EXIT")) {
            mediator.notify(this, new ExitCommand());
        }
    }
}