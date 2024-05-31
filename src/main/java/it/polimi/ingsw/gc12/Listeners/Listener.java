package it.polimi.ingsw.gc12.Listeners;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Network.VirtualClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Listener {

    private final static ExecutorService LISTENERS_EXECUTORS_POOL = Executors.newCachedThreadPool(); //TODO: decidere come farla e se è necessaria;

    private final VirtualClient CLIENT;

    public Listener(VirtualClient client) {
        this.CLIENT = client;
    }

    public VirtualClient getVirtualClient() {
        return CLIENT;
    }

    public void notified(ClientCommand command) {
        ServerController.requestToClient(CLIENT, command);
    }

}
