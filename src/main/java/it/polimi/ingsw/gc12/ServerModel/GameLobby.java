package it.polimi.ingsw.gc12.ServerModel;

import it.polimi.ingsw.gc12.ServerModel.GameStates.GameState;
import it.polimi.ingsw.gc12.ServerModel.GameStates.NotStartedState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//FIXME: fix UML
/**
A template for a game lobby where players wait for new games to start
 */
public class GameLobby {

    /**
    The list of players who have already joined this lobby
     */
    protected final List<Player> LIST_OF_PLAYERS;
    /**
    The maximum number of players which can join this lobby
     */
    private int maxPlayers;
    /**
     *
     */
    private GameState currentState;

    /**
    Constructs a game lobby of at most maxPlayers players and which contains the player who has created it
     */
    public GameLobby(int maxPlayers, Player creatorPlayer) {
        this.maxPlayers = maxPlayers;
        this.LIST_OF_PLAYERS = new ArrayList<>();
        addPlayer(creatorPlayer);
        this.currentState = new NotStartedState();
    }

    /**
    Constructs a lobby from another lobby passed as parameter
     */
    protected GameLobby(int maxPlayers, List<? extends Player> players) {
        this.maxPlayers = maxPlayers;
        Collections.shuffle(players);
        this.LIST_OF_PLAYERS = Collections.unmodifiableList(players);
    }

    /**
    If this lobby is not full, adds player to it
     */
    public void addPlayer(Player player) {
        if(LIST_OF_PLAYERS.size() < maxPlayers) {
            LIST_OF_PLAYERS.add(player);
        }
    }

    /**
    Removes player from this lobby
     */
    public void removePlayer(Player player) {
        LIST_OF_PLAYERS.remove(player);
    }

    /**
    Returns a copy of the list of players in the lobby
     */
    public ArrayList<Player> getListOfPlayers() {
        return new ArrayList<>(LIST_OF_PLAYERS);
    }

    /**
    Returns the number of players currently in the lobby
     */
    public int getPlayersNumber() {
        return LIST_OF_PLAYERS.size();
    }

    /**
    Returns the maximum number of players for this lobby
     */
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    /**
    Sets the maximum number of players in the lobby to a new value
     */
    public void setMaxPlayers(int numOfMaxPlayers) {
        if (numOfMaxPlayers <= 4) {
            this.maxPlayers = numOfMaxPlayers;
        }
    }

    /**
     * Changes the currentState of this game to newState
     */
    public void setState(GameState newState) {
        currentState = newState;
    }

    /**
     * Returns the current game state (of type GameState)
     */
    public GameState getCurrentState() {
        return currentState;
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