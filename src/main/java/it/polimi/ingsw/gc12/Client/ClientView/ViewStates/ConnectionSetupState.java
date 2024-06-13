package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.CreatePlayerCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.KeepAliveCommand;

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
        //TODO: print "would you like to retry?"
        do {
            try {
                CLIENT_CONTROLLER.setupCommunication(serverIPAddress, communicationTechnology);
            } catch(Exception e1) {
                //TODO: problema se l'host è online ma la porta è chiusa, perchè lancia una exception
                // "Connessione rifiutata dall'host remoto" tipo
                CLIENT_CONTROLLER.ERROR_LOGGER.log(e1);

                try {
                    sleep(10000);
                } catch (Exception e2) {
                    CLIENT_CONTROLLER.ERROR_LOGGER.log(e2);
                }
            }
        } while (CLIENT.serverConnection == null /*|| nicknameNotAccepted || yes*/);

        //TODO: se ricevo nickname già in uso, ripetere la richiesta
        try {
            CLIENT.requestToServer(new CreatePlayerCommand(nickname));
        } catch(Exception e) {
            ClientController.getInstance().ERROR_LOGGER.log(e);
        }

        //TODO: this state isn't notified when the server replies with an error related to the inability of creating a player.
        synchronized (this) {
            try {
                this.wait(10000);
            } catch (InterruptedException e) {
                ClientController.getInstance().ERROR_LOGGER.log(e);
            }
        }

        //TODO: Operations when a player couldn't be created ....

        //potenzialmente readUntil se anche la GUI ce l'avrà

        /*if(yes)){
            ClientController.getInstance().viewState = new TitleScreenState();
            ClientController.getInstance().viewState.executeState();
        }*/
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
