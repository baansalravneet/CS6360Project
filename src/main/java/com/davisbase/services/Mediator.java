package com.davisbase.services;

// orchestrates multiple services 
public class Mediator {

    private Component prompt;
    private Component queryParser;

    public void notify(Component sender, String event) {
        if (sender == prompt) {
            ((QueryParser)queryParser).parseQuery(event);
        }
    }

    public void addPromptComponent(Component component) {
        this.prompt = component;
    }

    public void addQueryParserComponent(Component component) {
        this.queryParser = component;
    }
}
