package it.polimi.ingsw.gc12.Client.ClientView.ViewStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.BroadcastMessageCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.DirectMessageCommand;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;

public class GameScreenState extends ViewState {

    public GameScreenState() {
    }

    @Override
    public void executeState() {
        ClientController.getInstance().view.gameScreen();
    }

    @Override
    public void broadcastMessage(String message) {
        //TODO: message timestamp? add it to the message or send it as parameter? (to avoid local timezone conversions...)
        ClientController.getInstance().requestToServer(new BroadcastMessageCommand(message));
    }

    @Override
    public void directMessage(String receiverNickname, String message) {
        //TODO: message timestamp? add it to the message or send it as parameter? (to avoid local timezone conversions...)
        ClientController.getInstance().requestToServer(new DirectMessageCommand(receiverNickname, message));
    }

    @Override
    public void addChatMessage(String message) {
        ((ClientGame) ClientController.getInstance().currentLobbyOrGame).addMessageToChatLog(message);
        ClientController.getInstance().view.updateChat();
    }

    //TODO: when receiving victory:
    //ClientController.getInstance().viewState = new LeaderboardScreenState();
    //ClientController.getInstance().viewState.executeState();
}
