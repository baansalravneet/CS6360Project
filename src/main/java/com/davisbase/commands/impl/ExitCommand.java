package com.davisbase.commands.impl;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.config.Settings;
import com.davisbase.output.CommandOutput;
import com.davisbase.output.impl.ExitOutput;

public class ExitCommand extends Command {

	public ExitCommand(CommandContext context) {
		super(context);
	}

	@Override
	public CommandOutput execute() {
		CommandOutput output;
		try {
			Settings.setExit(true);
			output = new ExitOutput(true);
		} catch (Exception ex) {
			output = new ExitOutput(false);
		}
		return output;
	}
}
