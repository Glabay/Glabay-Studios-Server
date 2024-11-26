package dev.glabay.util;


import dev.glabay.Discord;

/**
 * @author Glabay | Glabay-Studios
 * @project Boneyard Nexus
 * @social Discord: Glabay
 * @since 2024-11-23
 */
public class ShutdownHook extends Thread {

    public void run() {
        System.out.println("Successfully called ShutdownHook");
        try {
            System.out.println("Disconnecting from Discord...");
            Discord.shutdown();
        } catch (Exception e) {
            System.err.println("something went wrong...");
            System.err.printf("Error on Shutdown.%n%s", e.getMessage());
        }
        System.out.println("Good-Bye");
    }
}
