package it.polimi.ingsw.gc12.Client.ClientView;

import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.List;

public abstract class View { //FIXME: turn into interface maybe?

    public abstract void printError(Throwable error);

    public abstract void titleScreen();

    public abstract void connectToServerScreen();

    public abstract void lobbyScreen();

    public abstract void gameScreen();

    public abstract void connectedConfirmation();

    public abstract void updateNickname();

    public abstract void showChat();

    public abstract void showInitialCardsChoice();

    public abstract void showObjectiveCardsChoice();

    public abstract void showCommonPlacedCards();

    public abstract void showLeaderboard(List<Triplet<String, Integer, Integer>> POINTS_STATS, boolean gameEndedDueToDisconnections);

    public abstract void showField(ClientPlayer player);

    public abstract void moveField(GenericPair<Integer, Integer> centerOffset);

    public abstract void showHand();
}
