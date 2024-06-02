package it.polimi.ingsw.gc12.Network;

import it.polimi.ingsw.gc12.Controller.ControllerInterface;
import it.polimi.ingsw.gc12.Listeners.Listener;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public abstract class NetworkSession implements Serializable {

    protected static final long SESSION_TIMEOUT = 30000;

    //FIXME: this probably should not go here...
    protected transient Listener listener;
    private transient ControllerInterface controller;
    private TimerTask timeoutTask;

    public NetworkSession(ControllerInterface controller) {
        this.controller = controller;
        this.listener = createListener(this);
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

    public TimerTask getTimeoutTask() {
        return timeoutTask;
    }

    public void renewTimeoutTimerTask(TimerTask timeoutTask) {
        this.timeoutTask = timeoutTask;
        Timer timer = new Timer(true);
        timer.schedule(timeoutTask, SESSION_TIMEOUT);
    }

    public void runTimeoutTimerTask() {
        System.out.println("[SERVER] " + listener.getVirtualClient() + " didn't send any keepAlive in " + SESSION_TIMEOUT / 1000
                + " seconds or the game has sent an update and its state is inconsistent, disconnecting....");
        timeoutTask.run();
    }

    protected abstract Listener createListener(NetworkSession session);
}
