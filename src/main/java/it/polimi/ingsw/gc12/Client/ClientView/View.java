package it.polimi.ingsw.gc12.Client.ClientView;

public abstract class View { //FIXME: turn into interface maybe?

    public abstract void printError(Throwable error);

    public abstract void titleScreen();

    public abstract void connectToServerScreen();

    public abstract void lobbyScreen();

    public abstract void gameScreen();

    public abstract void connectedConfirmation();

    public abstract void updateNickname();

    public abstract void updateChat();

    public abstract void showInitialCardsChoice();

    public abstract void showObjectiveCardsChoice();

    public abstract void showCommonPlacedCards();

    public abstract void showField();

    public abstract void showHand();
}
