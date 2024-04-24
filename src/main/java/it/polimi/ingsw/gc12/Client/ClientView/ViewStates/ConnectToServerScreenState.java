package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.KeepAliveCommand;

import static java.lang.Thread.sleep;

public class ConnectToServerScreenState extends ViewState {

    public ConnectToServerScreenState() {
        ClientController.getInstance().view.connectToServerScreen();
        //FIXME: what if connection has failed?
        Thread keepAlive = new Thread(() -> {
            while (true) {
                try {
                    sleep(60000);
                    ClientController.getInstance().serverConnection.requestToServer(
                            ClientController.getInstance().thisClient, new KeepAliveCommand());
                    //TODO: update Timer on VirtualServer Timer (add attributes or methods for management)
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }); //keepAlive() thread
        //FIXME: dovrebbe essere daemon e terminare assieme al resto del programma, ma non può perchè altrimenti
        // dopo la scelta del nickname il client non prosegue non avendo nulla da fare...
        //keepAlive.setDaemon(true);
        keepAlive.start();
    }

    @Override
    public void updateNickname() {
        ClientController.getInstance().view.connectedConfirmation();
        transition();
    }

    @Override
    public void transition() {
        ClientController.getInstance().viewState = new LobbyScreenState();
    }
}
