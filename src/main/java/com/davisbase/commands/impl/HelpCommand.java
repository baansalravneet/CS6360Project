package com.davisbase.commands.impl;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.commands.output.CommandOutput;
import com.davisbase.commands.output.impl.HelpOutput;

public class HelpCommand extends Command {

    public HelpCommand(CommandContext context) {
        super(context);
    }

    @Override
    public CommandOutput execute() {
        return printHelp();
    }

    // TODO: Complete this. Ideally, maintain a txt file somewhere
    // in the repo and dump that file on screen.
    public CommandOutput printHelp() {
        return new HelpOutput("WIP");
    }
    
}
