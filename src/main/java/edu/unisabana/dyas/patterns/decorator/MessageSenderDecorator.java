package edu.unisabana.dyas.patterns.decorator;

import edu.unisabana.dyas.patterns.util.MessageSender;

// Clase base para todas las validaciones (decoradores).
// Implementa MessageSender y envuelve a otro MessageSender.
public abstract class MessageSenderDecorator implements MessageSender {

    protected final MessageSender wrapped;

    public MessageSenderDecorator(MessageSender wrapped) {
        this.wrapped = wrapped;
    }
}