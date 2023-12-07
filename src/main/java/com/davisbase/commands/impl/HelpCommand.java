package com.davisbase.commands.impl;

import java.io.BufferedReader;
import java.io.FileReader;

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
        return new HelpOutput(getHelpString());
    }

    // TODO: Complete this. Ideally, maintain a txt file somewhere
    // in the repo and dump that file on screen.
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
