package it.polimi.ingsw.gc12.View.Client.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.List;

public class PlayerTurnPlayState extends GameScreenState {

    public PlayerTurnPlayState() {
        TUICommands =
                CLIENT_CONTROLLER.isThisClientTurn() ?
                List.of(
                        "'[placeCard | pc] <x> <y> [1 | 2 | 3] [front | back]' to place a card ",
                        "    on the field (<x>: x-coordinate, <y>: y-coordinate>)",
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
        //TODO: Which part of the TUI should be printed? player hand if in turn, common placed cards,
        // miniaturized fields updated..., at the moment I'm refreshing everything
        selectedView.gameScreen();
    }

    public void restoreScreenState(){
        selectedView.gameScreen();
    }

    @Override
    public void placeCard(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {
        sendCardToPlace(coordinates, inHandPosition - 1, playedSide);
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

    @Override
    public void transition() {
        currentState = new PlayerTurnDrawState();
    }

    @Override
    public String toString() {
        return "play phase";
    }
}
