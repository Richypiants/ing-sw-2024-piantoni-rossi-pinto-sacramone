package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.Commands.KeepAliveCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.CreatePlayerCommand;

import static java.lang.Thread.sleep;

public class ConnectToServerScreenState extends ViewState {

    public ConnectToServerScreenState() {
    }

    @Override
    public void executeState() {
        ClientController.getInstance().view.connectToServerScreen();
    }

    @Override
    public void connect(String serverIPAddress, String communicationTechnology, String nickname) {
        //TODO: print "would you like to retry?"
        ClientController.getInstance().serverIPAddress = serverIPAddress;
        do {
            try {
                ClientController.getInstance().setCommunicationTechnology(communicationTechnology);
            } catch(Exception e1) {
                //TODO: problema se l'host è online ma la porta è chiusa, perchè lancia una exception
                // "Connessione rifiutata dall'host remoto" tipo
                ClientController.getInstance().errorLogger.log(e1);

                try {
                    sleep(10000);
                } catch (Exception e2) {
                    ClientController.getInstance().errorLogger.log(e2);
                }
            }
        } while (ClientController.getInstance().serverConnection == null /*|| nicknameNotAccepted || yes*/);

        //TODO: se ricevo nickname già in uso, ripetere la richiesta
        try {
            ClientController.getInstance().requestToServer(new CreatePlayerCommand(nickname));
        } catch(Exception e) {
            ClientController.getInstance().errorLogger.log(e);
        }

        //TODO: this state isn't notified when the server replies with an error related to the inability of creating a player.
        synchronized (ClientController.getInstance().LOCK) {
            try {
                ClientController.getInstance().LOCK.wait(10000);
            } catch (InterruptedException e) {
                ClientController.getInstance().errorLogger.log(e);
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
        ClientController.getInstance().view.connectedConfirmation();

        //KeepAlive timer FIXME: needed? there is a KEEPALIVE option on AsynchronousSocketChannel
        ClientController.getInstance().keepAlive = new Thread(() -> {
            while (true) {
                try {
                    sleep(30000);
                } catch (Exception e) {
                    ClientController.getInstance().errorLogger.log(e);
                    break;
                }
                //TODO: update Timer on VirtualServer Timer (add attributes or methods for management)
                ClientController.getInstance().requestToServer(new KeepAliveCommand());
            }
        }); //keepAlive() thread
        ClientController.getInstance().keepAlive.setDaemon(true);
        ClientController.getInstance().keepAlive.start();

        //ClientController.getInstance().viewState = new LobbyScreenState();
        //ClientController.getInstance().viewState.executeState();
    }
}
