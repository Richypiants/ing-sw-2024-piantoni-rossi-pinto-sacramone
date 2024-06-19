package it.polimi.ingsw.gc12.View.Client.ViewStates;

import it.polimi.ingsw.gc12.Commands.ServerCommands.CreatePlayerCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.KeepAliveCommand;
import it.polimi.ingsw.gc12.Controller.Client.ClientController;

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
                    this.wait(5000);
                } catch (InterruptedException e) {
                    CLIENT_CONTROLLER.ERROR_LOGGER.log(e);
                }
                if (CLIENT.serverConnection == null)
                    if (!selectedView.retryConnectionPrompt(true)) {
                        selectedView.quittingScreen();
                        quit();
                        return;
                    }
            } while (CLIENT.serverConnection == null);
        }

        CLIENT.requestToServer(new CreatePlayerCommand(nickname));

        //We use the wait timeout to handle both a network error and the notification of a nickname already in use.
        synchronized (this) {
            try {
                this.wait(5000);
            } catch (InterruptedException e) {
                ClientController.getInstance().ERROR_LOGGER.log(e);
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
                try {
                    sleep(5000);
                } catch (Exception e) {
                    ClientController.getInstance().ERROR_LOGGER.log(e);
                    break;
                }
            }
        }); //keepAlive() thread
        CLIENT.keepAlive.setDaemon(true);
        CLIENT.keepAlive.start();
    }
}
