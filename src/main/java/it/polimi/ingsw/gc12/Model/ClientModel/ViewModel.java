package it.polimi.ingsw.gc12.Model.ClientModel;

import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ViewModel {

    private String ownNickname;
    private Map<UUID, GameLobby> lobbies;
    private GenericPair<UUID, GameLobby> currentLobbyOrGame = null;

    public ViewModel() {
        ownNickname = "";
        lobbies = new HashMap<>();
        currentLobbyOrGame = new GenericPair<>(null, null);
    }

    public String getOwnNickname() {
        return ownNickname;
    }

    public void setOwnNickname(String ownNickname) {
        this.ownNickname = ownNickname;
    }

    public Map<UUID, GameLobby> getLobbies() {
        //FIXME: reference escaping?
        return lobbies;
    }

    public void setLobbies(Map<UUID, GameLobby> lobbies) {
        this.lobbies = lobbies;
    }

    public void putLobby(UUID lobbyUUID, GameLobby lobby) {
        lobbies.put(lobbyUUID, lobby);
    }

    public void removeLobby(UUID lobbyUUID) {
        lobbies.remove(lobbyUUID);
    }

    public UUID getCurrentLobbyUUID() {
        return currentLobbyOrGame.getX();
    }

    public GameLobby getCurrentLobby() {
        return currentLobbyOrGame.getY();
    }

    public boolean inLobbyOrGame() {
        return currentLobbyOrGame.getY() != null;
    }

    //FIXME: passare solo lo UUID?
    public void joinLobbyOrGame(UUID lobbyUUID, GameLobby lobby) {
        currentLobbyOrGame = new GenericPair<>(lobbyUUID, lobby);
    }

    public void leaveLobbyOrGame() {
        currentLobbyOrGame = new GenericPair<>(null, null);
    }

    public ClientGame getGame() {
        return (ClientGame) currentLobbyOrGame.getY();
    }

}
