package it.polimi.ingsw.gc12.Client.ClientModel;

import it.polimi.ingsw.gc12.Model.GameLobby;

import java.util.List;

public class ClientModel {

    /**
     * This player's nickname
     */
    private String ownNickname;
    private List<GameLobby> lobbies;
    private GameLobby currentLobby;

}
