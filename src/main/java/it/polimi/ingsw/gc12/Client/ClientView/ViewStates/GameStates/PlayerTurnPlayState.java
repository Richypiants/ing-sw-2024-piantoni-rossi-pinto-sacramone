package it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.List;

public class PlayerTurnPlayState extends GameScreenState {

    public PlayerTurnPlayState() {

        //TODO: add showField <playerID>
        TUICommands =
                ClientController.getInstance().isThisClientTurn() ?
                List.of(
                    "'placeCard <x> <y> <inHandPosition> <side>' (x,y): coordinate di piazzamento,",
                    "    inHandPosition: [1]...[n], side: [front][back]",
                    "'broadcastMessage <message>' per inviare un messaggio in gioco (max 200 chars)",
                    "'directMessage <recipient> <message> per inviare un messaggio privato @recipient in gioco (max 200 chars)") :
                List.of(
                    "'broadcastMessage <message>' per inviare un messaggio in gioco (max 200 chars)",
                    "'directMessage <recipient> <message> per inviare un messaggio privato @recipient in gioco (max 200 chars)");
    }

    @Override
    public void executeState() {
        super.executeState();
        //TODO: Which part of the TUI should be printed? player hand if in turn, common placed cards,
        // miniaturized fields updated..., at the moment I'm refreshing everything

        ClientController.getInstance().view.gameScreen();
    }

    @Override
    public void placeCard(GenericPair<Integer, Integer> coordinates, int inHandPosition, Side playedSide) {
        sendCardToPlace(coordinates, inHandPosition - 1, playedSide);
    }

    public void showField(int playerID) {
        ClientGame game = ClientController.getInstance().viewModel.getGame();
        if(playerID < 0 || playerID > game.getMaxPlayers())
            throw new IllegalArgumentException("The provided ID doesn't match to a player's ID in the game.");

        ClientController.getInstance().view.showField(game.getPlayers().get(playerID-1));
    }

    @Override
    public void transition() {
        ClientController.getInstance().viewState = new PlayerTurnDrawState();
    }
}
