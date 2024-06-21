package it.polimi.ingsw.gc12.View.Client.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Commands.ServerCommands.BroadcastMessageCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.DirectMessageCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.LeaveGameCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.PlaceCardCommand;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;

public abstract class GameScreenState extends ViewState {

    @Override
    public abstract void executeState();

    public abstract void restoreScreenState();

    protected void sendCardToPlace(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {
        ClientCard card;
        try {
            card = CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().getCardsInHand().get(inHandPosition);
        } catch (IndexOutOfBoundsException e) {
            selectedView.printError(new IllegalArgumentException("There's no card in the specified hand position!"));
            return;
        }

        CLIENT.requestToServer(new PlaceCardCommand(coordinates, card.ID, playedSide));
    }

    @Override
    public void broadcastMessage(String message) {
        CLIENT.requestToServer(new BroadcastMessageCommand(message));
    }

    @Override
    public void directMessage(String receiverNickname, String message) {
        CLIENT.requestToServer(new DirectMessageCommand(receiverNickname, message));
    }

    //TODO: could show message timestamp (only when on receiving, so that it corresponds to the local timezone or local machine's clock)
    @Override
    public void showReceivedChatMessage(String message) {
        if (message.length() < 90)
            CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().addMessageToChatLog(message);
        else {
            CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().addMessageToChatLog(message.substring(0, 90));
            CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame().addMessageToChatLog(message.substring(90));
        }
        selectedView.updateChat();
    }

    @Override
    public void quit() {
        new Thread(() -> {
            synchronized (CLIENT) {
                try {
                    CLIENT.requestToServer(new LeaveGameCommand());
                    selectedView.quittingScreen();
                    //Notified by CLIENT.requestToServer() function, which gets executed in another thread
                    CLIENT.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e); //Should never happen
                }
            }
            super.quit();
        });
    }

    public abstract void transition();
}
