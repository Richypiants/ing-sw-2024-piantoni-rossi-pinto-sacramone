package it.polimi.ingsw.gc12.Client.ClientModel;

import it.polimi.ingsw.gc12.ServerModel.GameStates.GameState;

import java.util.ArrayList;
import java.util.List;

public class ClientLobby {

    /**
     * The list of players who have already joined this lobby
     */
    private List<String> OTHER_PLAYERS;
    /**
     * The maximum number of players which can join this lobby
     */
    private int maxPlayers;

    private int currentPlayers;
    /**
     *
     */
    private GameState currentState; //TODO ---> serve solo se controller controlla che le azioni siano valide
    // se non serve aggiungere currentPlayer in ClientGame

    public ClientLobby(List<String> otherPlayers, int maxPlayers, int currentPlayers){
        this.OTHER_PLAYERS = new ArrayList<>(otherPlayers);
        this.maxPlayers = maxPlayers;
        this.currentPlayers = currentPlayers;
    }

    public List<String> getPlayers() {
        return OTHER_PLAYERS;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }
}
