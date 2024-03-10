
import java.util.ArrayList;

public class GameLobby {
    private int maxPlayers;
    private ArrayList<Player> setOfPlayers;

    public GameLobby(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.setOfPlayers = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        if(setOfPlayers.size() < maxPlayers) {
            setOfPlayers.add(player);
        }
    }

    public void removePlayer(Player player) {
        setOfPlayers.remove(player);
    }

    public ArrayList<Player> getSetOfPlayers() {
        return new ArrayList<>(setOfPlayers);
    }

    public int getPlayersNumber() {
        return setOfPlayers.size();
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    // Note: The startGame method implementation depends on further details not provided in the UML diagram
    public void startGame() {
        // Implementation depends on game logic
    }
}
