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
    public void connect(String nickname) {
        //TODO: print "would you like to retry?"
        do {
            //TODO: problema se l'host è online ma la porta è chiusa, perchè lancia una exception
            // "Connessione rifiutata dall'host remoto" tipo
            ClientController.getInstance().requestToServer(new CreatePlayerCommand(nickname));
            try {
                sleep(10000);
            } catch (Exception e) {
                ClientController.getInstance().errorLogger.log(e);
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
