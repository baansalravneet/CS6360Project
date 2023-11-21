package com.davisbase.commands.impl;

import com.davisbase.commands.Command;
import com.davisbase.config.Settings;

public class ExitCommand implements Command {

    @Override
    public void execute() {
        Settings.setExit(true);
    }
    
}
