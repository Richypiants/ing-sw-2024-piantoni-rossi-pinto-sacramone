package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;

//TODO: complete from UML and add comments for documentation

public class GameLobby {
    private int maxPlayers;
    private ArrayList<Player> setOfPlayers;

    public GameLobby(int numOfMaxPlayers, Player creatorPlayer) {
        this.maxPlayers = numOfMaxPlayers;
        this.setOfPlayers = new ArrayList<>();
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
        return new ArrayList<>(setOfPlayers);
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

    // Note: The startGame method implementation depends on further details not provided in the UML diagram
    public void startGame() {
        // Implementation depends on game logic
    }
}
