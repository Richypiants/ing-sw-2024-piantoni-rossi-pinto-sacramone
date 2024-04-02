package it.polimi.ingsw.gc12.Client.ClientModel;

import it.polimi.ingsw.gc12.ServerModel.GameStates.GameState;

import java.util.List;

public class ClientLobby {

    /**
     * The list of players who have already joined this lobby
     */
    protected final List<String> LIST_OF_PLAYERS_NICKNAMES;
    /**
     * The maximum number of players which can join this lobby
     */
    private int maxPlayers;
    /**
     *
     */
    private GameState currentState; //TODO ---> serve solo se controller controlla che le azioni siano valide prima
}
