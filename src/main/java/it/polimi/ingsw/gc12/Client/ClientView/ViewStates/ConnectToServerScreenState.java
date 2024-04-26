package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.KeepAliveCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.CreatePlayerCommand;

import static java.lang.Thread.sleep;

public class ConnectToServerScreenState extends ViewState {

    public ConnectToServerScreenState() {
    }

    @Override
    public void executeState() {
        String nickname = ClientController.getInstance().view.connectToServerScreen();
        //TODO: print "would you like to retry?"
        do {
            ClientController.getInstance().requestToServer(new CreatePlayerCommand(nickname));
            try {
                sleep(10000);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } while (ClientController.getInstance().serverConnection == null /*|| yes*/);
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
        Thread keepAlive = new Thread(() -> {
            while (true) {
                try {
                    sleep(60000);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                //TODO: update Timer on VirtualServer Timer (add attributes or methods for management)
                ClientController.getInstance().requestToServer(new KeepAliveCommand());
            }
        }); //keepAlive() thread
        //FIXME: dovrebbe essere daemon e terminare assieme al resto del programma, ma non può perchè altrimenti
        // dopo la scelta del nickname il client non prosegue non avendo nulla da fare...
        keepAlive.setDaemon(false);
        keepAlive.start();

        ClientController.getInstance().viewState = new LobbyScreenState();
        ClientController.getInstance().viewState.executeState();
    }
}
