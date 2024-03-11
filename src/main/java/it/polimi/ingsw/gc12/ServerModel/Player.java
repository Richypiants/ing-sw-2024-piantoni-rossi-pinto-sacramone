package it.polimi.ingsw.gc12.ServerModel;

// This class defines a player entity outside games (that is, in the lobby)
public class Player {
    private String nickname; // Nickname set from the user

    // Constructor for a standard player
    public Player(String nickname) {
        this.nickname = nickname;
    }

    // Getter method for nickname
    public String getNickname() {
        return this.nickname;
    }

    // Setter method for nickname
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
