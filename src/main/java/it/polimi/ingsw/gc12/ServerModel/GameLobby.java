package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;

//TODO: complete from UML and add comments for documentation

public class GameLobby {
    private int maxPlayers;
    private final ArrayList<Player> LIST_OF_PLAYERS;

    public GameLobby(int maxPlayers, Player creatorPlayer) {
        this.maxPlayers = maxPlayers;
        this.LIST_OF_PLAYERS = new ArrayList<Player>();
        addPlayer(creatorPlayer);
    }

    public GameLobby(GameLobby copyFrom) {
        this.maxPlayers = copyFrom.getMaxPlayers();
        this.LIST_OF_PLAYERS = new ArrayList<Player>(copyFrom.getListOfPlayers());
    }

    public void addPlayer(Player player) {
        if(LIST_OF_PLAYERS.size() < maxPlayers) {
            LIST_OF_PLAYERS.add(player);
        }
    }

    public void removePlayer(Player player) {
        LIST_OF_PLAYERS.remove(player);
    }

    public ArrayList<Player> getListOfPlayers() {
        return new ArrayList<Player>(LIST_OF_PLAYERS);
    }

    public int getPlayersNumber() {
        return LIST_OF_PLAYERS.size();
    }

    public void setMaxPlayers(int numOfMaxPlayers) {
        this.maxPlayers = numOfMaxPlayers;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }
}

// addPlayer() -> No test (l'if non dovrebbe creare problemi dato che è un check banale)
// removePlayer() -> No test
// getListOfPlayer() (Getter) -> No test
// getPlayersNumber() (Getter) -> No test
// setMaxPlayer() (Setter senza condizioni particolari) -> No test
// getMaxPlayers() (Getter) -> No test