package com.davisbase.services;

import com.davisbase.commands.Command;
import com.davisbase.output.CommandOutput;
import com.davisbase.services.components.Component;
import com.davisbase.services.components.impl.CommandHandler;
import com.davisbase.services.components.impl.DisplayManager;
import com.davisbase.services.components.impl.Prompt;
import com.davisbase.services.components.impl.QueryParser;

// orchestrates multiple services 
public class Mediator {

    private Prompt prompt;
    private QueryParser queryParser;
    private CommandHandler commandHandler;
    private DisplayManager displayManager;

    public void notify(Component sender, Object event) {
        if (sender == prompt) {
            queryParser.parseQuery((String) event);
        } else if (sender == queryParser) {
            commandHandler.handle((Command) event);
        } else if (sender == commandHandler) {
            displayManager.displayOutput((CommandOutput) event);
        }
    }

    public void addPromptComponent(Prompt component) {
        this.prompt = component;
    }

    public void addQueryParserComponent(QueryParser component) {
        this.queryParser = component;
    }

    public void addCommandHandlerComponent(CommandHandler component) {
        this.commandHandler = component;
    }

    public void addDisplayManagerComponent(DisplayManager component) {
        this.displayManager = component;
    }

    public String getInput() {
        return prompt.getInput();
    }
}
