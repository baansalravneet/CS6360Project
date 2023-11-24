package com.davisbase.commands.impl;

import java.io.IOException;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.commands.output.CommandOutput;
import com.davisbase.commands.output.impl.InvalidCommandOutput;
import com.davisbase.config.Database;
import com.davisbase.models.Table;
import com.davisbase.models.TableRow;

public class CreateTableCommand extends Command {

    public CreateTableCommand(CommandContext context) {
        super(context);
    }

    // TODO: implement this
    @Override
    public CommandOutput execute() { 
        return new InvalidCommandOutput();
    }

}
