package it.polimi.ingsw.gc12.ServerModel;

import java.util.ArrayList;

/*
A template for a game lobby where players wait for new games to start
 */
public class GameLobby {

    /*
    The list of players who have already joined this lobby
     */
    private final ArrayList<Player> LIST_OF_PLAYERS;
    /*
    The maximum number of players which can join this lobby
     */
    private int maxPlayers;

    /*
    Constructs a game lobby of at most maxPlayers players and which contains the player who has created it
     */
    public GameLobby(int maxPlayers, Player creatorPlayer) {
        this.maxPlayers = maxPlayers;
        this.LIST_OF_PLAYERS = new ArrayList<>();
        addPlayer(creatorPlayer);
    }

    /*
    Constructs a lobby from another lobby passed as parameter
     */
    public GameLobby(GameLobby copyFrom) {
        this.maxPlayers = copyFrom.getMaxPlayers();
        this.LIST_OF_PLAYERS = new ArrayList<>(copyFrom.getListOfPlayers());
    }

    /*
    If this lobby is not full, adds player to it
     */
    public void addPlayer(Player player) {
        if(LIST_OF_PLAYERS.size() < maxPlayers) {
            LIST_OF_PLAYERS.add(player);
        }
    }

    /*
    Removes player from this lobby
     */
    public void removePlayer(Player player) {
        LIST_OF_PLAYERS.remove(player);
    }

    /*
    Returns a copy of the list of players in the lobby
     */
    public ArrayList<Player> getListOfPlayers() {
        return new ArrayList<>(LIST_OF_PLAYERS);
    }

    /*
    Returns the number of players currently in the lobby
     */
    public int getPlayersNumber() {
        return LIST_OF_PLAYERS.size();
    }

    /*
    Returns the maximum number of players for this lobby
     */
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    /*
    Sets the maximum number of players in the lobby to a new value
     */
    public void setMaxPlayers(int numOfMaxPlayers) {
        if (numOfMaxPlayers <= 4) {
            this.maxPlayers = numOfMaxPlayers;
        }
    }
}

// addPlayer() -> Si test
//                - Statement coverage (l'if non dovrebbe creare problemi dato che è un check banale)
//
//                - Casi limite
//                  Player undefined
//
// removePlayer() -> Si test
//                 - Casi limite
//                   Player undefined
//
// getListOfPlayer() (Getter) -> No test
// getPlayersNumber() (Getter) -> No test
// setMaxPlayer() (Setter senza condizioni particolari) -> No test
// getMaxPlayers() (Getter) -> No test