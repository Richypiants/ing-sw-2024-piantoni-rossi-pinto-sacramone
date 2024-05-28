package it.polimi.ingsw.gc12.Model;

import it.polimi.ingsw.gc12.Utilities.Color;

import java.io.Serializable;

/**
 * Represents a player outside of games, typically in a lobby.
 * <p>
 * This class stores basic information about a player such as their nickname and color.
 * It implements {@link Serializable} to allow player instances to be serialized.
 * </p>
 */
public class Player implements Serializable {

    /**
     * This player's nickname
     */
    private String nickname;

    /**
     * This player's color.
     * <p>
     * The default color is set to {@link Color#NO_COLOR}. Color selection logic should be implemented
     * to assign a specific color to a player.
     * </p>
     */
    private Color color = Color.NO_COLOR; //TODO: implement color selection logic

    /**
     * Constructs a Player with the specified nickname.
     *
     * @param nickname The nickname of the player.
     */
    public Player(String nickname) {
        this.nickname = nickname;
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


    //TODO: Add Color in toString when implemented
    /**
     * Returns a string representation of this player
     * The string representation is the player's nickname.
     * </p>
     *
     * @return The nickname of the player as a string.
     */
    @Override
    public String toString() {
        return this.nickname;
    }
}

