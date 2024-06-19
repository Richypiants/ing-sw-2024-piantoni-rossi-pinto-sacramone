package it.polimi.ingsw.gc12.View.Client;

import it.polimi.ingsw.gc12.Controller.Client.ClientController;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Model.ClientModel.ViewModel;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.ArrayList;
import java.util.List;

public abstract class View {

    protected final static ClientController CLIENT_CONTROLLER = ClientController.getInstance();
    protected final static ViewModel VIEWMODEL = CLIENT_CONTROLLER.VIEWMODEL;

    public abstract void printError(Throwable error);

    public abstract void titleScreen();

    public abstract void connectionSetupScreen();

    public abstract boolean retryConnectionPrompt(boolean causedByNetworkError);

    public abstract void connectedConfirmation();

    public abstract void quittingScreen();

    public abstract void lobbiesScreen();

    public abstract void gameScreen();

    public abstract void awaitingScreen();

    public abstract void showNickname();

    public abstract void updateChat();

    public abstract void showInitialCardsChoice();

    public abstract void showObjectiveCardsChoice(ArrayList<ClientCard> objectivesSelection);

    public abstract void showCommonPlacedCards();

    public abstract void leaderboardScreen(List<Triplet<String, Integer, Integer>> POINTS_STATS, boolean gameEndedDueToDisconnections);

    public abstract void showField(ClientPlayer player);

    public abstract void moveField(GenericPair<Integer, Integer> centerOffset);

    public abstract void showHand();
}
