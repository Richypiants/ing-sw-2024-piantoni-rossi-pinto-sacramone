package it.polimi.ingsw.gc12.View.Client.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Commands.ServerCommands.DrawFromDeckCommand;
import it.polimi.ingsw.gc12.Commands.ServerCommands.DrawFromVisibleCardsCommand;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.List;

public class PlayerTurnDrawState extends GameScreenState {

    public PlayerTurnDrawState() {
        TUICommands = CLIENT_CONTROLLER.isThisClientTurn() ?
                List.of(
                        "'[drawFromDeck | dfd] <deck>' [resource | gold]",
                        "'[drawFromVisibleCards | dfvc] <deck> <position>' [resource | gold] [1 | 2]",
                        "'[showField | sf] <playerID>' to show the player's field",
                        "'[moveField | mf] <x> <y>' moves the field by x cards left and y cards down",
                        "'[broadcastMessage | bm] <message>' to send a message to all players (200 chars max.)",
                        "'[directMessage | dm] <recipient> <message>' to send a private message (200 chars max.)") :
                List.of(
                        "'[showField | sf] <playerID>' to show the player's field",
                        "'[moveField | mf] <x> <y>' moves the field by x cards left and y cards down",
                        "'[broadcastMessage | bm] <message>' to send a message to all players (200 chars max.)",
                        "'[directMessage | dm] <recipient> <message>' to send a private message (200 chars max.)");
    }

    @Override
    public void executeState() {
        selectedView.gameScreen();
    }

    public void restoreScreenState(){
        selectedView.gameScreen();
    }

    @Override
    public void drawFromDeck(String deck) {
        if (invalidDeck(deck)) {
            selectedView.printError(new IllegalArgumentException("The provided deck doesn't exist!"));
        }

        CLIENT.requestToServer(new DrawFromDeckCommand(deck));
    }

    @Override
    public void drawFromVisibleCards(String deck, int position) {
        if (invalidDeck(deck)) {
            selectedView.printError(new IllegalArgumentException("The provided visible card area doesn't exist!"));
            return;
        }
        if (position != 1 && position != 2) {
            selectedView.printError(new IllegalArgumentException("The provided position doesn't exist!"));
            return;
        }

        CLIENT.requestToServer(new DrawFromVisibleCardsCommand(deck, position - 1));
    }

    public void showField(int playerID) {
        ClientGame game = CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame();
        if (playerID < 0 || playerID > game.getPlayersNumber()) {
            selectedView.printError(new IllegalArgumentException("The provided ID doesn't match to a player's ID in the game!"));
            return;
        }

        selectedView.showField(game.getPlayers().get(playerID - 1));
    }

    public void moveField(GenericPair<Integer, Integer> centerOffset) {
        selectedView.moveField(centerOffset);
    }

    private boolean invalidDeck(String deck) {
        return !(deck.equalsIgnoreCase("resource") || deck.equalsIgnoreCase("gold"));
    }

    @Override
    public void transition() {
        currentState = new PlayerTurnPlayState();
    }

    @Override
    public String toString() {
        return "draw phase";
    }
}
