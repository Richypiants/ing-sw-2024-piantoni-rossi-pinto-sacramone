package it.polimi.ingsw.gc12.Model;

import it.polimi.ingsw.gc12.Utilities.Color;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnavailableColorException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A game lobby where players wait for new games to start.
 * This class manages the list of players who have joined the lobby and keeps track of the maximum number of players allowed.
 */
public class Lobby extends Room implements Serializable {

    /**
     * The list of colors which have not yet been chosen by players in this lobby.
     */
    private final List<Color> AVAILABLE_COLORS;

    /**
     * The maximum number of players which can join this lobby.
     */
    private int maxPlayers;

    /**
     * Constructs a game lobby with a specified maximum number of players, initializes the list of available colors
     * and adds the player who created it.
     *
     * @param creatorPlayer The player who created the lobby.
     * @param maxPlayers The maximum number of players allowed in this lobby.
     */
    public Lobby(Player creatorPlayer, int maxPlayers) {
        super(new ArrayList<>());
        this.AVAILABLE_COLORS = Arrays.stream(Color.values())
                .filter((color) -> !(color.equals(Color.NO_COLOR) || color.equals(Color.BLACK)))
                .collect(Collectors.toCollection(ArrayList::new));
        this.maxPlayers = maxPlayers;
        addPlayer(creatorPlayer);
    }

    /**
     * Constructs a game lobby from a list of players and a specified maximum number of players.
     *
     * @param maxPlayers The maximum number of players allowed in this lobby.
     * @param players The list of players to be copied to this lobby.
     */
    protected Lobby(int maxPlayers, List<? extends Player> players) {
        super(new ArrayList<>(players));
        this.AVAILABLE_COLORS = List.of();
        this.maxPlayers = maxPlayers;
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
     * Removes a player from the lobby and makes the eventually selected color available again.
     *
     * @param player The player to be removed from the lobby.
     */
    public void removePlayer(Player player) {
        if (!player.getColor().equals(Color.NO_COLOR)) {
            AVAILABLE_COLORS.add(player.getColor());
            player.setColor(Color.NO_COLOR);
        }
        LIST_OF_PLAYERS.remove(player);
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
     * Returns the list of colors which have not yet been selected by players in this lobby.
     *
     * @return The list of colors which have not yet been picked for this lobby.
     */
    public List<Color> getAvailableColors() {
        return new ArrayList<>(AVAILABLE_COLORS);
    }

    /**
     * Assigns the selected color to the specified player, provided that it is available. If the player already has an
     * assigned color, it is made available again.
     *
     * @param player The player which is requesting the color assignment.
     * @param color  The color to be assigned to the player.
     * @throws UnavailableColorException if the selected color is not available, or if it is black (reserved color for
     *                                   the starting player token).
     */
    public void assignColor(Player player, Color color) throws UnavailableColorException {
        if (!AVAILABLE_COLORS.contains(color) || color.equals(Color.BLACK) || color.equals(Color.NO_COLOR))
            throw new UnavailableColorException();

        if (!player.getColor().equals(Color.NO_COLOR))
            AVAILABLE_COLORS.add(player.getColor());
        AVAILABLE_COLORS.remove(color);
        player.setColor(color);
    }

    /**
     * Shuffles the players contained in the list.
     */
    public void shufflePlayers() {
        Collections.shuffle(LIST_OF_PLAYERS);
    }

    /**
     * Returns a string representation of the lobby, including the maximum number of players, the list of players
     * in this lobby and the list of currently available colors.
     *
     * @return A string representation of the lobby.
     */
    @Override
    public String toString() {
        return "Lobby{" + "maxPlayers=" + maxPlayers +
                " players=[" + LIST_OF_PLAYERS + "] availableColors=" + AVAILABLE_COLORS + "}";
    }
}
