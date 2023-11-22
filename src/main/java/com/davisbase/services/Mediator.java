package com.davisbase.services;

import com.davisbase.commands.Command;
import com.davisbase.services.components.Component;
import com.davisbase.services.components.impl.CommandHandler;
import com.davisbase.services.components.impl.DisplayManager;
import com.davisbase.services.components.impl.QueryParser;

// orchestrates multiple services 
public class Mediator {

    private Component prompt;
    private Component queryParser;
    private Component commandHandler;
    private Component displayManager;

    public void notify(Component sender, Object event) {
        if (sender == prompt) {
            ((QueryParser) queryParser).parseQuery((String) event);
        } else if (sender == queryParser) {
            ((CommandHandler) commandHandler).handle((Command) event);
        } else if (sender == commandHandler) {
            ((DisplayManager) displayManager).displayOutput((String) event);
        }
    }

    public void addPromptComponent(Component component) {
        this.prompt = component;
    }

    public void addQueryParserComponent(Component component) {
        this.queryParser = component;
    }

    public void addCommandHandlerComponent(Component component) {
        this.commandHandler = component;
    }

    public void addDisplayManagerComponent(Component component) {
        this.displayManager = component;
    }
}
