package it.polimi.ingsw.gc12.Client.ClientView;

public abstract class View { //FIXME: turn into interface maybe?

    public abstract void printError(Throwable error);

    public abstract void titleScreen();

    public abstract String connectToServerScreen();

    public abstract void lobbyScreen();

    public abstract void gameScreen();

    public abstract void connectedConfirmation();

    public abstract void updateNickname();

    public abstract void updateChat();
}
