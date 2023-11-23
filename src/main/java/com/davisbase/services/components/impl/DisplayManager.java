package com.davisbase.services.components.impl;

import com.davisbase.commands.output.CommandOutput;
import com.davisbase.services.Mediator;
import com.davisbase.services.components.Component;

public class DisplayManager extends Component {
    
    public DisplayManager(Mediator mediator) {
        super(mediator);
    }

    // TODO: Revisit this. The input for the method will depend
    // on how the Command is implemented.
    public void displayOutput(CommandOutput output) {
        output.display();

    }
}
