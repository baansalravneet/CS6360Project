package com.davisbase.services.components;

import com.davisbase.services.Mediator;

public class Component {
    public Mediator mediator;

    public Component(Mediator mediator) {
        this.mediator = mediator;
    }
}
