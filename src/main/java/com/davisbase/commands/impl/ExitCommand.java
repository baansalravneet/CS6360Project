package com.davisbase.commands.impl;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.commands.output.CommandOutput;
import com.davisbase.commands.output.impl.ExitOutput;
import com.davisbase.config.Settings;

public class ExitCommand extends Command {

    public ExitCommand(CommandContext context) {
        super(context);
    }

    @Override
    public CommandOutput execute() {
        Settings.setExit(true);
        return new ExitOutput();
    }
}
