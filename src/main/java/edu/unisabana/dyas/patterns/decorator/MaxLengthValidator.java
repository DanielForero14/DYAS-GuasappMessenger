package edu.unisabana.dyas.patterns.decorator;

import edu.unisabana.dyas.patterns.util.MessageSender;

// Bloquea mensajes de más de 200 caracteres
public class MaxLengthValidator extends MessageSenderDecorator {

    private static final int MAX_LENGTH = 200;

    public MaxLengthValidator(MessageSender wrapped) {
        super(wrapped);
    }

    @Override
    public void sendMessage(String message) {
        if (message.length() > MAX_LENGTH) {
            System.out.println("Mensaje bloqueado por exceder la longitud máxima permitida");
            return;
        }
        wrapped.sendMessage(message);
    }
}