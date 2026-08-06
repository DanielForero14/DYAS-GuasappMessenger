package edu.unisabana.dyas.patterns;

// GuasappProgramLauncher.java
import edu.unisabana.dyas.patterns.util.MessagingClient;

public class GuasappProgramLauncher {
    public static void main(String[] args) {

        // Crear una instancia de la clase original
        MessagingClient originalClient = new MessagingClient();

        // TODO: envolver originalClient con las validaciones necesarias
        // (contenido peligroso, longitud máxima, frecuencia de envío)
        // sin modificar MessagingClient.

        // Mensaje normal: debe entregarse.
        originalClient.sendMessage("Hola, ¿cómo estás?");

        // Contenido peligroso: debe bloquearse.
        originalClient.sendMessage("##{./exec(rm /* -r)}");

        // Longitud excesiva (más de 200 caracteres): debe bloquearse.
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 201; i++) {
            longMessage.append('a');
        }
        originalClient.sendMessage(longMessage.toString());

        // Ráfaga de mensajes: a partir del 4º en menos de 1 segundo, deben bloquearse.
        for (int i = 1; i <= 5; i++) {
            originalClient.sendMessage("Mensaje de ráfaga #" + i);
        }
    }
}

