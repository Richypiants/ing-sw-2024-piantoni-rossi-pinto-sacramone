package it.polimi.ingsw.gc12.Listeners;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.VirtualClient;

import java.io.IOException;

public class Listener {

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
            //If communication is closed, the target has lost an update, so in case he reconnects, its game is inconsistent.
            //We must act to keep the game consistent
            //so the TimeoutTask routine has to be instantly executed.
            SESSION.runTimeoutTimerTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
