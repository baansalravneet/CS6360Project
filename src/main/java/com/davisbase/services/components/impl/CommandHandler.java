package com.davisbase.services.components.impl;

import com.davisbase.commands.Command;
import com.davisbase.output.CommandOutput;
import com.davisbase.services.Mediator;
import com.davisbase.services.components.Component;

public class CommandHandler extends Component {
    
    public CommandHandler(Mediator mediator) {
        super(mediator);
    }

    public void handle(Command command) {
        CommandOutput output = command.execute();
        mediator.notify(this, output);
    }

}
