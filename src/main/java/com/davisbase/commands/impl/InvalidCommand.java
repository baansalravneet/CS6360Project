package com.davisbase.commands.impl;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.output.CommandOutput;
import com.davisbase.output.impl.InvalidCommandOutput;

public class InvalidCommand extends Command {

    public InvalidCommand(CommandContext context) {
        super(context);
    }

    @Override
    public CommandOutput execute() {
        return new InvalidCommandOutput();
    }

}
