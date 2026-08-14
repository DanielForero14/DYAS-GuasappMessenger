package edu.unisabana.dyas.patterns.decorator;

import edu.unisabana.dyas.patterns.util.MessageSender;

import java.util.LinkedList;
import java.util.Deque;

// Bloquea a partir del 4º mensaje enviado en menos de 1 segundo
public class FrequencyValidator extends MessageSenderDecorator {

    private static final int MAX_MESSAGES = 3;
    private static final long WINDOW_MILLIS = 1000;

    // Guarda las marcas de tiempo (timestamps) de los envíos recientes
    private final Deque<Long> timestamps = new LinkedList<>();

    public FrequencyValidator(MessageSender wrapped) {
        super(wrapped);
    }

    @Override
    public void sendMessage(String message) {
        long now = System.currentTimeMillis();

        // Elimina del registro los timestamps que ya salieron de la ventana de 1 segundo
        while (!timestamps.isEmpty() && (now - timestamps.peekFirst()) >= WINDOW_MILLIS) {
            timestamps.pollFirst();
        }

        // Si ya hay 3 mensajes dentro de la ventana, este (el 4º) se bloquea
        if (timestamps.size() >= MAX_MESSAGES) {
            System.out.println("Mensaje bloqueado por exceso de frecuencia de envío");
            return;
        }

        // Registrar este envío y dejarlo pasar
        timestamps.addLast(now);
        wrapped.sendMessage(message);
    }
}