package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.BroadcastMessageCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.DirectMessageCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.LeaveGameCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.PlaceCardCommand;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

public abstract class GameScreenState extends ViewState {

    @Override
    public void executeState() {
        ClientController.getInstance().view.gameScreen();
    }

    protected void sendCardToPlace(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {
        ClientCard card = null;
        try {
            card = ClientController.getInstance().viewModel.getGame().getCardsInHand().get(inHandPosition);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("nessuna carta presente alla posizione specificata della mano");
        }

        try {
            ClientController.getInstance().requestToServer(new PlaceCardCommand(coordinates, card.ID, playedSide));
        } catch (Exception e) {
            ClientController.getInstance().view.printError(e);
        }
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

    //FIXME: estrarre tra le costanti anche la dimensione della chat?
    @Override
    public void addChatMessage(String message) {
        if (message.length() < 90)
            ClientController.getInstance().viewModel.getGame().addMessageToChatLog(message);
        else {
            ClientController.getInstance().viewModel.getGame().addMessageToChatLog(message.substring(0, 90));
            ClientController.getInstance().viewModel.getGame().addMessageToChatLog(message.substring(90));
        }
        ClientController.getInstance().view.updateChat();
    }

    @Override
    public void quit() {
        ClientController.getInstance().requestToServer(new LeaveGameCommand());
        super.quit();
    }

    public abstract void transition();

    //TODO: when receiving victory:
    //ClientController.getInstance().viewState = new LeaderboardScreenState();
    //ClientController.getInstance().viewState.executeState();
}
