package it.polimi.ingsw.gc12.View.Client.ViewStates;

import it.polimi.ingsw.gc12.Commands.KeepAliveCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.CreatePlayerCommand;
import it.polimi.ingsw.gc12.Controller.Client.ClientController;
import it.polimi.ingsw.gc12.Network.Client.RMIClientSkeleton;
import it.polimi.ingsw.gc12.Network.Client.SocketClient;

import static java.lang.Thread.sleep;

public class ConnectionSetupState extends ViewState {

    public ConnectionSetupState() {
    }

    @Override
    public void executeState() {
        selectedView.connectionSetupScreen();
    }

    @Override
    public void connect(String serverIPAddress, String communicationTechnology, String nickname) {
        synchronized (this) {
            do {
                CLIENT.setupCommunication(serverIPAddress, communicationTechnology);
                try {
                    //Wait 5 seconds before asking whether to retry connecting to the server.
                    //Notified by SocketClient or RMIClientSkeleton when they successfully establish a connection
                    this.wait(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e); //Should never happen
                }
                if (CLIENT.serverConnection == null)
                    if (!selectedView.retryConnectionPrompt(true)) {
                        selectedView.quittingScreen();
                        System.exit(0);
                    }
            } while (CLIENT.serverConnection == null);
        }

        CLIENT.requestToServer(new CreatePlayerCommand(nickname));

        //We use the wait timeout to handle both a network error and the notification of a nickname already in use.
        //This wait(...) gets notified by the updateNickname function below.
        synchronized (this) {
            try {
                this.wait(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e); //Should never happen
            }
        }

        if (CLIENT_CONTROLLER.VIEWMODEL.getOwnNickname().isEmpty()) {
            if (selectedView.retryConnectionPrompt(false)) {
                currentState = new TitleScreenState();
                currentState.executeState();
            } else {
                selectedView.quittingScreen();
                System.exit(0);
            }
        }
    }

    @Override
    public void updateNickname() {
        selectedView.connectedConfirmation();
        synchronized (this) {
            this.notifyAll();
        }

        CLIENT.keepAlive = new Thread(() -> {
            while (true) {
                CLIENT.requestToServer(new KeepAliveCommand());
                synchronized (CLIENT.DISCONNECTED_LOCK) {
                    try {
                        CLIENT.DISCONNECTED_LOCK.wait(15000);
                        if (CLIENT.disconnected) {
                            selectedView.disconnectedScreen();
                            tryReconnection();
                        } else
                            CLIENT.disconnected = true;
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    //When disconnecting we interrupt keepAlive thread as we no longer have to send pings to server
                    break;
                }
            }
        }); //keepAlive() thread
        CLIENT.keepAlive.setDaemon(true);
        CLIENT.keepAlive.start();
    }

    private void tryReconnection() {
        String ownNickname = CLIENT_CONTROLLER.VIEWMODEL.getOwnNickname();
        while (true) {
            CLIENT_CONTROLLER.VIEWMODEL.clearModel();
            try {
                if (CLIENT.session != null) {
                    ((RMIClientSkeleton) CLIENT.session).close();
                    CLIENT.session = RMIClientSkeleton.getInstance();
                } else if (CLIENT.serverConnection != null) {
                    CLIENT.serverConnection.close();
                    CLIENT.serverConnection = SocketClient.getInstance();
                }
            } catch (Exception e) {
                ClientController.getInstance().ERROR_LOGGER.log(e);
            }
            CLIENT.requestToServer(new CreatePlayerCommand(ownNickname));
            try {
                CLIENT.DISCONNECTED_LOCK.wait(15000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (!CLIENT.disconnected) {
                CLIENT.disconnected = true;
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "connection setup";
    }
}
