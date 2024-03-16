package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;

//TODO: complete from UML and add comments for documentation

public class GameLobby {
    private int maxPlayers;
    private ArrayList<Player> setOfPlayers;

    public GameLobby(int maxPlayers, Player creatorPlayer) {
        this.maxPlayers = maxPlayers;
        this.setOfPlayers = new ArrayList<Player>();
        addPlayer(creatorPlayer);
    }

    public void addPlayer(Player player) {
        if(setOfPlayers.size() < maxPlayers) {
            setOfPlayers.add(player);
        }
    }

    public void removePlayer(Player player) {
        setOfPlayers.remove(player);
    }

    public ArrayList<Player> getSetOfPlayers() {
        return new ArrayList<Player>(setOfPlayers);
    }

    public int getPlayersNumber() {
        return setOfPlayers.size();
    }

    public void setMaxPlayers(int numOfMaxPlayers) {
        this.maxPlayers = numOfMaxPlayers;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }
}
