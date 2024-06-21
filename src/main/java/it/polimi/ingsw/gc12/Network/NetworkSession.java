package it.polimi.ingsw.gc12.Network;

import it.polimi.ingsw.gc12.Controller.ControllerInterface;
import it.polimi.ingsw.gc12.Listeners.NetworkListener;
import it.polimi.ingsw.gc12.Model.Player;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents a network session established between a client and the server.
 * <p>
 * This abstract class provides a framework for managing network sessions.
 * It includes functionality for associating a session with a controller, setting and retrieving a network listener for the session,
 * scheduling and anticipate running the task to execute when a connection timeout is detected.
 * </p>
 * <p>
 * Implementations of this class should provide concrete implementations for creating listeners specific to the object they would like to listen on.
 * </p>
 */
public abstract class NetworkSession implements Serializable {

    /**
     * The timeout duration when several keepAlive commands are not received by the server for each session in milliseconds.
     */
    protected static final long SESSION_TIMEOUT = 15000;

    /**
     * The controller interface associated with the network session.
     */
    private transient ControllerInterface controller;
    /**
     * The network listener associated with the network session.
     */
    protected transient NetworkListener listener;
    /**
     * The player associated with the network session.
     */
    private transient Player player;

    /**
     * The timer task for session timeout.
     */
    private transient TimerTask timeoutTask;

    /**
     * Constructs a new network session with the specified controller.
     *
     * @param controller The controller interface associated with the session.
     */
    public NetworkSession(ControllerInterface controller) {
        this.controller = controller;
        this.player = null;
        this.listener = createListener(this);
    }

    /**
     * Gets the controller interface associated with the network session.
     *
     * @return The controller interface associated with the session.
     */
    public ControllerInterface getController() {
        return controller;
    }

    /**
     * Sets the controller interface associated with the network session.
     *
     * @param controller The controller interface to set.
     */
    public void setController(ControllerInterface controller) {
        this.controller = controller;
    }

    /**
     * Gets the player associated with the network session.
     *
     * @return The player associated with the session.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player associated with the network session.
     *
     * @param player The player to set.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Gets the network listener associated with the network session.
     *
     * @return The network listener associated with the session.
     */
    public NetworkListener getListener() {
        return listener;
    }

    /**
     * Retrieves the timeout task associated with the network session.
     *
     * @return The timeout task associated with the session.
     */
    public TimerTask getTimeoutTask() {
        return timeoutTask;
    }

    /**
     * Schedules the execution of the timeout task for this session after {@code SESSION_TIMEOUT} milliseconds.
     * Since the operation is managed by a timer, it is possible to stop its execution prematurely
     * by cancelling the timer task associated with the session.
     *
     * @param timeoutTask The timeout task to schedule.
     */
    public void scheduleTimeoutTimerTask(TimerTask timeoutTask) {
        this.timeoutTask = timeoutTask;
        Timer timer = new Timer(true);
        timer.schedule(timeoutTask, SESSION_TIMEOUT);
    }

    /**
     * Runs the timeout timer task for the session.
     * <p>
     * This method is invoked when the session exceeds the defined timeout duration.
     * It executes the timeout task associated with the session.
     * </p>
     * <p>
     * Additionally, it can also be called before the timer associated with this session expires.
     * For example, this method might be called prematurely when a disconnection is detected in advance by the attempt of sending updates to a disconnected VirtualClient,
     * ensuring that necessary actions are taken even before the timer expires.
     * </p>
     */
    public void runTimeoutTimerTask() {
        timeoutTask.run();
    }

    /**
     * Creates a network listener for the network session.
     * <p>
     * Subclasses should implement this method to create a network listener specific to the objects and events they would like to listen on.
     * This method is intended to be overridden by subclasses to provide custom network listener implementations tailored to their needs.
     * </p>
     *
     * @param session The network session for which to create the listener.
     * @return The listener associated with the session.
     */
    protected abstract NetworkListener createListener(NetworkSession session);

    /**
     * Closes the communication channel of this NetworkSession.
     * <p>
     * Implementations of this method should encapsulate the details of how the communication
     * channel is closed, such as releasing resources and terminating connections.
     */
    public abstract void close();
}
