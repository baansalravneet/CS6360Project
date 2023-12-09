package com.davisbase.commands.impl;

import java.io.BufferedReader;
import java.io.FileReader;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.output.CommandOutput;
import com.davisbase.output.impl.HelpOutput;

public class HelpCommand extends Command {

    public HelpCommand(CommandContext context) {
        super(context);
    }

    @Override
    public CommandOutput execute() {
        return new HelpOutput(getHelpString());
    }

    public String getHelpString() {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("resources/help.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error printing help";
    }
    
}
