package it.polimi.ingsw.gc12.Model;

import it.polimi.ingsw.gc12.Utilities.Color;

import java.io.Serializable;

/**
 * A model for a player outside of games (that is, in the lobby)
 */
public class Player implements Serializable {

    /**
     * This player's nickname
     */
    private String nickname;
    /**
     * This player's color
     */
    private Color color = Color.NO_COLOR; //TODO: implement color selection logic

    /**
     * Constructs a standard player
     */
    public Player(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Constructs a player from another given player (needed to be called by InGamePlayer's constructor
     */
    public Player(Player copyFrom){
        this.nickname = copyFrom.getNickname();
        this.color = copyFrom.getColor();
    }

    /**
     * Returns this player's nickname
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Sets this player's nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Returns this player's color
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Sets this player's nickname
     */
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return this.nickname;
    }
}

// getNickname() (Getter) -> No test
// setNickname() (Setter senza condizioni particolari) -> No test
