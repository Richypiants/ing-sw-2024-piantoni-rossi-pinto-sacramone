package it.polimi.ingsw.gc12.ServerController;

public class Main {

    public static void main(String[] args) {
        //TODO: Caricare le carte

        //Caricamento di tutti i comandi validi

        //fare un nuovo thread per poter poi interruptare il server
        Server server = Server.getInstance();
        server.run();
    }
}
