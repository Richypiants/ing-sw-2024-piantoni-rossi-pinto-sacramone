package it.polimi.ingsw.gc12.Controller.ServerController;

public class Main {

    public static void main(String[] args) {
        Thread serverThread = new Thread(Server.getInstance());
        serverThread.setDaemon(true);
        serverThread.start(); //FIXME: rendere il server un vero e proprio thread o tenere Runnable?

        String interrupt;
        do {
            interrupt = System.console().readLine();
        } while (!interrupt.trim().equalsIgnoreCase("close"));
        Server.getInstance().commandExecutorsPool.shutdownNow();
        serverThread.interrupt();
        //TODO: main doesn't completely stop, other shutdown to perform?
    }
}
