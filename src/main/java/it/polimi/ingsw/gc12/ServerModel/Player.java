package it.polimi.ingsw.gc12.ServerModel;

public class Player {
    private String nickname;
    
    public Player(String nickname) {
        this.nickname = nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getNickname() {
        return this.nickname;
    }
}
