package it.polimi.ingsw.gc12.Model;

import it.polimi.ingsw.gc12.Model.Server.InGamePlayer;
import it.polimi.ingsw.gc12.Utilities.Enums.Color;

import java.io.Serializable;

/**
 * Represents a player outside of games, typically in a lobby.
 *
 * This class stores basic information about a player such as their nickname and color.
 * It implements {@link Serializable} to allow player instances to be serialized.
 */
public class Player implements Serializable {

    /**
     * This player's nickname.
     */
    private String nickname;

    /**
     * This player's color.
     * The default color is set to be {@link Color#NO_COLOR}.
     */
    private Color color = Color.NO_COLOR;

    /**
     * The active status of this player, indicating whether the player is currently connected to the game and playing.
     */
    private boolean active;

    /**
     * Constructs a Player with the specified nickname.
     *
     * @param nickname The nickname of the player.
     */
    public Player(String nickname) {
        this.nickname = nickname;
        active = true;
    }

    /**
     * Constructs a Player by copying the attributes from another Player.
     * This constructor is primarily used by the {@link InGamePlayer} class to create a new Player
     * instance based on an existing one.
     *
     * @param copyFrom The Player to copy attributes from.
     */
    public Player(Player copyFrom){
        this.nickname = copyFrom.getNickname();
        this.color = copyFrom.getColor();
        this.active = copyFrom.isActive();
    }

    /**
     * Returns this player's nickname.
     *
     * @return The nickname of the player.
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Sets this player's nickname.
     *
     * @param nickname The new nickname of the player.
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Returns this player's color.
     *
     * @return The color of the player.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Sets this player's color.
     *
     * @param color The new color of the player.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Returns the activity status of this player.
     *
     * @return {@code true} if the player is active, {@code false} otherwise.
     */
    public synchronized boolean isActive() {
        return active;
    }

    /**
     * Toggles the active status of this player.
     * If the player is currently active, they will become inactive, and vice versa.
     */
    public synchronized void toggleActive() {
        active = !active;
    }

    /**
     * Returns a string representation of this player
     * The string representation is the player's nickname, along with the eventually assigned color.
     * </p>
     *
     * @return The nickname of the player as a string.
     */
    @Override
    public String toString() {
        return this.nickname + ", " + color;
    }
}

