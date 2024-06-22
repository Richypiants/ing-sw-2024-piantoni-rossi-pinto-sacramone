package it.polimi.ingsw.gc12.View.Client.ViewStates;

import it.polimi.ingsw.gc12.Commands.KeepAliveCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.CreatePlayerCommand;

import static java.lang.Thread.sleep;

/**
 * Represents the connection setup state of the client-side view.
 * Extends {@link ViewState}.
 */
public class ConnectionSetupState extends ViewState {

    /**
     * Constructs a new ConnectionSetupState.
     */
    public ConnectionSetupState() {
    }

    /**
     * Executes the behavior of the connection setup state by displaying the connection setup screen on the selected view.
     */
    @Override
    public void executeState() {
        selectedView.connectionSetupScreen();
    }

    /**
     * Connects to the server using the specified server IP address, communication technology,
     * and nickname. Handles retry logic if connection fails or nickname is already in use.
     *
     * @param serverIPAddress        The IP address of the server.
     * @param communicationTechnology The communication technology (e.g., "Socket", "RMI").
     * @param nickname               The nickname chosen by the client.
     */
    @Override
    public void connect(String serverIPAddress, String communicationTechnology, String nickname) {
        synchronized (ViewState.class) {
            do {
                CLIENT.setupCommunication(serverIPAddress, communicationTechnology);
                try {
                    // Wait 25 seconds before asking whether to retry connecting to the server.
                    // Notified by SocketClient or RMIClientSkeleton when they successfully establish a connection.
                    ViewState.class.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e); // Should never happen
                }

                if (CLIENT.serverConnection == null) {
                    if (!selectedView.retryConnectionPrompt(true)) {
                        selectedView.quittingScreen();
                        System.exit(0);
                    }
                }
            } while (CLIENT.serverConnection == null);
        }

        CLIENT.requestToServer(new CreatePlayerCommand(nickname));

        // Use a wait timeout to handle both a network error and the notification of a nickname already in use.
        // This wait(...) gets notified by the updateNickname function below.
        synchronized (ViewState.class) {
            try {
                ViewState.class.wait(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e); // Should never happen
            }
        }

        if (CLIENT_CONTROLLER.VIEWMODEL.getOwnNickname().isEmpty()) {
            if (selectedView.retryConnectionPrompt(false)) {
                CLIENT_CONTROLLER.VIEWMODEL.clearModel();
                currentState = new TitleScreenState();
                currentState.executeState();
            } else {
                selectedView.quittingScreen();
                System.exit(0);
            }
        }
    }

    /**
     * Updates the nickname confirmation and starts a keep-alive mechanism to maintain the connection.
     */
    @Override
    public void updateNickname() {
        selectedView.connectedConfirmation();
        synchronized (ViewState.class) {
            ViewState.class.notifyAll();
        }

        CLIENT.keepAlive = new Thread(() -> {
            CLIENT.disconnected = true;
            while (true) {
                //FIXME: forse invertire la synchronized con la riga sopra?
                CLIENT.requestToServer(new KeepAliveCommand());
                synchronized (CLIENT.DISCONNECTED_LOCK) {
                    try {
                        CLIENT.DISCONNECTED_LOCK.wait(5000);
                        if (CLIENT.disconnected) {
                            selectedView.disconnectedScreen();
                            (new Thread(this::tryReconnection)).start();
                            break;
                        } else
                            CLIENT.disconnected = true;
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    // When disconnecting, we interrupt keepAlive thread as we no longer have to send pings to server.
                    break;
                }
            }
        }); // keepAlive() thread
        CLIENT.keepAlive.setDaemon(true);
        CLIENT.keepAlive.start();
    }

    /**
     * Attempts to reconnect to the server with the previously used nickname after a disconnection.
     */
    private void tryReconnection() {
        String ownNickname = CLIENT_CONTROLLER.VIEWMODEL.getOwnNickname();
        CLIENT_CONTROLLER.VIEWMODEL.clearModel();
        ViewState.setCurrentState(new ConnectionSetupState());
        ViewState.getCurrentState().connect(CLIENT.serverIPAddress, CLIENT.communicationTechnology, ownNickname);
    }

    /**
     * Returns a string representation of the connection setup state.
     *
     * @return The string "connection setup".
     */
    @Override
    public String toString() {
        return "connection setup";
    }
}
