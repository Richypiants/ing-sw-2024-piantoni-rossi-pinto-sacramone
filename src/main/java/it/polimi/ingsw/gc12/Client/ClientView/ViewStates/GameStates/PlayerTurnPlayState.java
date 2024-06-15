package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.List;

public class PlayerTurnPlayState extends GameScreenState {

    public PlayerTurnPlayState() {
        TUICommands =
                CLIENT_CONTROLLER.isThisClientTurn() ?
                List.of(
                    "'placeCard <x> <y> <inHandPosition> <side>' coordinates: x y",
                    "    inHandPosition: [1]...[n], side: [front][back]",
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
        if (playerID < 0 || playerID > game.getPlayersNumber())
            throw new IllegalArgumentException("The provided ID doesn't match to a player's ID in the game.");

        selectedView.showField(game.getPlayers().get(playerID - 1));
    }

    public void moveField(GenericPair<Integer, Integer> centerOffset) {
        selectedView.moveField(centerOffset);
    }

    @Override
    public void transition() {
        currentState = new PlayerTurnDrawState();
    }
}
