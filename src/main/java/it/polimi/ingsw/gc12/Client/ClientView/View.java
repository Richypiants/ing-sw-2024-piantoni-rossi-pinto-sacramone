package it.polimi.ingsw.gc12.Client.ClientView;

import java.util.UUID;

public abstract class View { //FIXME: turn into interface maybe?

    public abstract void titleScreen();

    public abstract void connectToServerScreen();

    public abstract void lobbyScreen();

    public abstract void gameScreen();

    public abstract void connectedConfirmation();

    public abstract void updateNickname();
}
