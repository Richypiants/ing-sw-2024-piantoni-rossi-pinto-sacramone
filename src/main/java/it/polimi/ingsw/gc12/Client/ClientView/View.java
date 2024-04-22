package it.polimi.ingsw.gc12.Client.ClientView;

public abstract class View { //FIXME: turn into interface maybe?

    public abstract void titleScreen();

    public abstract void chooseNicknameScreen();

    public abstract void connectToServerScreen();

    public abstract void lobbyScreen();

    public abstract void gameScreen();
}
