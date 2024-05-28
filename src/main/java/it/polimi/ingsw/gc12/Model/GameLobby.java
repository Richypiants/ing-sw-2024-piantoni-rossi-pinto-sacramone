package it.polimi.ingsw.gc12.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A game lobby where players wait for new games to start.
 * This class manages the list of players who have joined the lobby and keeps track of the maximum number of players allowed.
 */
public class GameLobby implements Serializable {

    /**
     * The list of players who have already joined this lobby.
     */
    private final List<Player> LIST_OF_PLAYERS;

    /**
     * The maximum number of players which can join this lobby.
     */
    private int maxPlayers;

    /**
     * Constructs a game lobby with a specified maximum number of players and adds the player who created it.
     *
     * @param creatorPlayer The player who created the lobby.
     * @param maxPlayers The maximum number of players allowed in this lobby.
     */
    public GameLobby(Player creatorPlayer, int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.LIST_OF_PLAYERS = new ArrayList<>();
        addPlayer(creatorPlayer);
    }

    /**
     * Constructs a game lobby from a list of players and a specified maximum number of players.
     *
     * @param maxPlayers The maximum number of players allowed in this lobby.
     * @param players The list of players to be copied to this lobby.
     */
    protected GameLobby(int maxPlayers, List<? extends Player> players) {
        this.maxPlayers = maxPlayers;
        this.LIST_OF_PLAYERS = new ArrayList<>(players);
    }

    /**
     * Adds a player to the lobby if the lobby is not already full, otherwise it silently terminates.
     *
     * @param player The player to be added to the lobby.
     */
    public void addPlayer(Player player) {
        if(LIST_OF_PLAYERS.size() < maxPlayers) {
            LIST_OF_PLAYERS.add(player);
        }
    }

    /**
     * Removes a player from the lobby.
     *
     * @param player The player to be removed from the lobby.
     */
    public void removePlayer(Player player) {
        LIST_OF_PLAYERS.remove(player);
    }

    /**
     * Returns the list of players currently in the lobby.
     *
     * @return A copy of the list of players in the lobby.
     */
    public ArrayList<? extends Player> getPlayers() {
        return new ArrayList<>(LIST_OF_PLAYERS);
    }

    /**
     * Returns the number of players currently in the lobby.
     *
     * @return The number of players in the lobby.
     */
    public int getPlayersNumber() {
        return LIST_OF_PLAYERS.size();
    }

    /**
     * Returns the maximum number of players allowed in this lobby.
     *
     * @return The maximum number of players for this lobby.
     */
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    /**
     * Sets a new maximum number of players for this lobby. The maximum value cannot exceed 4.
     *
     * @param numOfMaxPlayers The new maximum number of players.
     */
    public void setMaxPlayers(int numOfMaxPlayers) {
        if (numOfMaxPlayers <= 4) {
            this.maxPlayers = numOfMaxPlayers;
        }
    }

    /**
     * Shuffles the players contained in the list.
     */
    public void shufflePlayers() {
        Collections.shuffle(LIST_OF_PLAYERS);
    }

    /**
     * Returns a string representation of the lobby, including the maximum number of players and the list of players.
     *
     * @return A string representation of the lobby.
     */
    @Override
    public String toString() {
        return "GameLobby{" + "maxPlayers=" + maxPlayers + " [" + LIST_OF_PLAYERS + "]}";
    }
}
