package it.polimi.ingsw.gc12.ServerController;

public class Main {

    public static void main(String[] args) {
        //Caricare le carte

        //fare un nuovo thread per il server e poi interruptarlo
        Server server = Server.getInstance();
        server.run();
    }
}
