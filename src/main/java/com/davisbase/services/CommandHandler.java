package com.davisbase.services;

import com.davisbase.commands.Command;

public class CommandHandler extends Component {
    
    public CommandHandler(Mediator mediator) {
        super(mediator);
    }

    public void handle(Command command) {
        command.execute();
    }

}
