package it.polimi.ingsw.gc12.Main.Server;

import it.polimi.ingsw.gc12.Network.Server.Server;

/**
 * Entry point for the server application. Starts the server thread and provides a command-line interface
 * to manage server shutdown.
 */
public class ServerMain {

    /**
     * Main method tu run the server application.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Initialize server instance
        Server serverThread = Server.getInstance();

        // Set server thread as daemon and start it
        serverThread.setDaemon(true);
        serverThread.start();

        // Command-line interface to manage server shutdown
        String interrupt;
        do {
            // Wait for user input to shut down server
            interrupt = System.console().readLine();
        } while (!interrupt.trim().equalsIgnoreCase("close"));

        // Shutdown server command executors pool
        Server.getInstance().commandExecutorsPool.shutdownNow();

        // Interrupt server thread and exit application
        serverThread.interrupt();
        System.exit(0);
    }
}
