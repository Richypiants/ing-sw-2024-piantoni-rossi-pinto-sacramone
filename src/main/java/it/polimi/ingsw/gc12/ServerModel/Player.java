package it.polimi.ingsw.gc12.ServerModel;

/**
 * A model for a player outside of games (that is, in the lobby)
 */
public class Player {

    /**
     * This player's nickname
     */
    private String nickname;

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
}

// getNickname() (Getter) -> No test
// setNickname() (Setter senza condizioni particolari) -> No test
