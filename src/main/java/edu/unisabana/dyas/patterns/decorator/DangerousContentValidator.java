package edu.unisabana.dyas.patterns.decorator;

import edu.unisabana.dyas.patterns.util.MessageSender;

import java.util.regex.Pattern;

// Bloquea mensajes que contengan el patrón ##{...}
public class DangerousContentValidator extends MessageSenderDecorator {

    private static final Pattern DANGEROUS_PATTERN = Pattern.compile("##\\{.*?\\}");

    public DangerousContentValidator(MessageSender wrapped) {
        super(wrapped);
    }

    @Override
    public void sendMessage(String message) {
        if (DANGEROUS_PATTERN.matcher(message).find()) {
            System.out.println("Mensaje bloqueado debido a contenido peligroso");
            return;
        }
        wrapped.sendMessage(message);
    }
}