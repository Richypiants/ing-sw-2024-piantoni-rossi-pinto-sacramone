package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.DrawFromDeckCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.DrawFromVisibleCardsCommand;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.List;

public class PlayerTurnDrawState extends GameScreenState {

    public PlayerTurnDrawState() {
        TUICommands = CLIENT_CONTROLLER.isThisClientTurn() ?
                List.of(
                        "'drawFromDeck <deck>' [resource][gold]",
                        "'drawFromVisibleCards <deck> <position>' [resource][gold] [1][2]",
                        "'showField <playerID>' to show the player's field",
                        "'moveField <x> <y>' moves the field by x cards right and y cards up",
                        "'broadcastMessage <message>' to send a message to all players (max 200 chars)",
                        "'directMessage <recipient> <message>' to send a private message @recipient (max 200 chars)") :
                List.of(
                        "'showField <playerID>' to show the player's field",
                        "'moveField <x> <y>' moves the field by x cards right and y cards up",
                        "'broadcastMessage <message>' to send a message to all players (max 200 chars)",
                        "'directMessage <recipient> <message>' to send a private message @recipient (max 200 chars)");
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
        if (invalidDeck(deck)) throw new IllegalArgumentException("The provided deck doesn't exist!");
        try {
            CLIENT.requestToServer(new DrawFromDeckCommand(deck));
        } catch (Exception e) {
            selectedView.printError(e);
        }
    }

    @Override
    public void drawFromVisibleCards(String deck, int position) {
        if (invalidDeck(deck)) throw new IllegalArgumentException("The provided visible card area doesn't exist!");
        if (position != 1 && position != 2) throw new IllegalArgumentException("The provided position doesn't exist!");
        try {
            CLIENT.requestToServer(new DrawFromVisibleCardsCommand(deck, position - 1));
        } catch (Exception e) {
            selectedView.printError(e);
        }
    }

    public void showField(int playerID) {
        ClientGame game = CLIENT_CONTROLLER.VIEWMODEL.getCurrentGame();
        if (playerID < 0 || playerID > game.getPlayersNumber())
            throw new IllegalArgumentException("The provided ID doesn't match to a player's ID in the game.");

        selectedView.showField(game.getPlayers().get(playerID - 1));
    }

    //FIXME: TUI-only function, could be moved inside...
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
}
