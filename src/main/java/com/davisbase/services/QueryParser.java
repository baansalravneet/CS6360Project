package com.davisbase.services;

public class QueryParser extends Component {

    public QueryParser(Mediator mediator) {
        super(mediator);
    }

    public void parseQuery(String input) {
        System.out.println(input);
    }
}