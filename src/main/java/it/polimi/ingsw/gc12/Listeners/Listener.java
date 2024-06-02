package it.polimi.ingsw.gc12.Listeners;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.VirtualClient;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Listener {

    private final static ExecutorService LISTENERS_EXECUTORS_POOL = Executors.newCachedThreadPool(); //TODO: decidere come farla e se è necessaria;

    //FIXME: not a generic listener if it has to know Session and Client...... maybe make abstract and then subclass in ServerModelUpdateListener?
    private final NetworkSession SESSION;
    private final VirtualClient CLIENT;

    public Listener(NetworkSession session, VirtualClient client) {
        this.SESSION = session;
        this.CLIENT = client;
    }

    public VirtualClient getVirtualClient() {
        return this.CLIENT;
    }

    public void notified(ClientCommand command) {
        try {
            CLIENT.requestToClient(command);
        } catch (IOException e) {
            //If communication is closed, the target has lost an update, so in case he reconnects, its game is inconsistent, we must send the update,
            //so the TimeoutTask routine has to be instantly executed.
            SESSION.runTimeoutTimerTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
