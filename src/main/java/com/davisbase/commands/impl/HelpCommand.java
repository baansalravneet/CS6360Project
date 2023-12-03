package com.davisbase.commands.impl;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.commands.output.CommandOutput;
import com.davisbase.commands.output.impl.HelpOutput;
import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.commands.output.CommandOutput;
import com.davisbase.commands.output.impl.HelpOutput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class HelpCommand extends Command {

    public HelpCommand(CommandContext context) {
        super(context);
    }

    @Override
    public CommandOutput execute() {
        return printHelp();
    }

    public CommandOutput printHelp() {
        try {
            // Get the current working directory
            String currentDir = System.getProperty("user.dir");
            // Build the relative path to the help file
            Path helpFilePath = Paths.get(currentDir,  "src", "main", "java", "com", "davisbase", "help.txt");

            // Read the content of the help file
            String helpText = Files.lines(helpFilePath)
                    .collect(Collectors.joining("\n"));
            return new HelpOutput(helpText);
        } catch (IOException e) {
            System.err.println("Error reading help file: " + e.getMessage());
            return new HelpOutput("Help file not found.");
        }
    }
}
//public class HelpCommand extends Command {
//
//    public HelpCommand(CommandContext context) {
//        super(context);
//    }
//
//    @Override
//    public CommandOutput execute() {
//        return printHelp();
//    }
//
//    // TODO: Complete this. Ideally, maintain a txt file somewhere
//    // in the repo and dump that file on screen.
//    public CommandOutput printHelp() {
//        return new HelpOutput("WIP");
//    }
//
//}
