package it.polimi.ingsw.gc12.Controller.ServerController;

public class Main {

    public static void main(String[] args) {
        //Caricamento di tutti i comandi validi

        //fare un nuovo thread per poter poi interruptare il server: fare setDaemon(true) a tutto?
        Server server = Server.getInstance();
        server.start();
    }
}
