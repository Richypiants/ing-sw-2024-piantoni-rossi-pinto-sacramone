package it.polimi.ingsw.gc12.Network;

import it.polimi.ingsw.gc12.Controller.ControllerInterface;
import it.polimi.ingsw.gc12.Listeners.Listener;

import java.io.Serializable;

public abstract class NetworkSession implements Serializable {

    //FIXME: this probably should not go here...
    protected transient Listener listener;
    private transient ControllerInterface controller;
    //private TimerTask timeoutTask;

    public NetworkSession(ControllerInterface controller) {
        this.controller = controller;
        this.listener = createListener();
    }

    public ControllerInterface getController() {
        return controller;
    }

    public void setController(ControllerInterface controller) {
        this.controller = controller;
    }

    public Listener getListener() {
        return listener;
    }

    /*public TimerTask getTimeoutTask() {
        return timeoutTask;
    }*/

    protected abstract Listener createListener();
}
