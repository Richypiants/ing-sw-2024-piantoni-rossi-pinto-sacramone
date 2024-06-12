package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Network.Server.Server;

public class Main {

    public static void main(String[] args) {
        Server serverThread = Server.getInstance();
        serverThread.setDaemon(true);
        serverThread.start();

        String interrupt;
        do {
            interrupt = System.console().readLine();
        } while (!interrupt.trim().equalsIgnoreCase("close"));
        Server.getInstance().commandExecutorsPool.shutdownNow();
        serverThread.interrupt();
        //TODO: main doesn't completely stop, other shutdown to perform?
    }
}
