package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.BroadcastMessageCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.DirectMessageCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.LeaveGameCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.PlaceCardCommand;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

public abstract class GameScreenState extends ViewState {

    @Override
    public abstract void executeState();

    public abstract void restoreScreenState();

    protected void sendCardToPlace(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {
        ClientCard card;
        try {
            card = CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().getCardsInHand().get(inHandPosition);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("nessuna carta presente alla posizione specificata della mano");
        }

        try {
            CLIENT.requestToServer(new PlaceCardCommand(coordinates, card.ID, playedSide));
        } catch (Exception e) {
            selectedView.printError(e);
        }
    }

    @Override
    public void broadcastMessage(String message) {
        //TODO: message timestamp? add it to the message or send it as parameter? (to avoid local timezone conversions...)
        CLIENT.requestToServer(new BroadcastMessageCommand(message));
    }

    @Override
    public void directMessage(String receiverNickname, String message) {
        //TODO: message timestamp? add it to the message or send it as parameter? (to avoid local timezone conversions...)
        CLIENT.requestToServer(new DirectMessageCommand(receiverNickname, message));
    }

    //FIXME: estrarre tra le costanti anche la dimensione della chat?
    @Override
    public void showReceivedChatMessage(String message) {
        if (message.length() < 90)
            CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().addMessageToChatLog(message);
        else {
            CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().addMessageToChatLog(message.substring(0, 90));
            CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().addMessageToChatLog(message.substring(90));
        }
        selectedView.showChat();
    }

    @Override
    public void quit() {
        CLIENT.requestToServer(new LeaveGameCommand());
        synchronized (CLIENT) {
            try {
                selectedView.quittingScreen();
                CLIENT.wait();
            } catch (InterruptedException e) {
                CLIENT_CONTROLLER.ERROR_LOGGER.log(e);
            }
        }
        super.quit();
    }

    public abstract void transition();

    //TODO: when receiving victory:
    //ClientController.getInstance().viewState = new LeaderboardScreenState();
    //ClientController.getInstance().viewState.executeState();
}
