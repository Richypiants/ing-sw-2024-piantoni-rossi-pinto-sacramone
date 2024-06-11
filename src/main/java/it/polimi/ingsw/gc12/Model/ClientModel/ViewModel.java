package it.polimi.ingsw.gc12.Model.ClientModel;

import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Room;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents the client's view model, managing the player's nickname related to the client,
 * the list of game lobbies, and the current lobby or game if the player is in it.
 */
public class ViewModel {

    /**
     * The nickname associated to this client.
     */
    private String ownNickname;

    /**
     * The map containing all the lobbies in which this client can join
     * since they aren't full.
     */
    private Map<UUID, Lobby> lobbies;

    /**
     * The pair containing the unique identifier and the lobby or game this client is currently in.
     */
    private Room currentLobbyOrGame;

    /**
     * Constructs a new ViewModel with an empty nickname and no lobbies.
     */
    public ViewModel() {
        ownNickname = "";
        lobbies = new HashMap<>();
        currentLobbyOrGame = null;
    }

    /**
     * Gets the player's own nickname.
     *
     * @return the player's own nickname
     */
    public String getOwnNickname() {
        return ownNickname;
    }

    /**
     * Sets the player's own nickname.
     *
     * @param ownNickname the new nickname
     */
    public void setOwnNickname(String ownNickname) {
        this.ownNickname = ownNickname;
    }

    /**
     * Gets the map containing all the open lobbies.
     *
     * @return the map containing the lobbies
     */
    public Map<UUID, Lobby> getLobbies() {
        return lobbies;
    }

    /**
     * Sets the map of lobbies
     *
     * @param lobbies the new map of lobbies
     */
    public void setLobbies(Map<UUID, Lobby> lobbies) {
        this.lobbies = lobbies;
    }

    /**
     * Adds a lobby to the list of lobbies.
     *
     * @param lobbyUUID the unique identifier of the lobby
     * @param lobby the lobby to add
     */
    public void putLobby(UUID lobbyUUID, Lobby lobby) {
        lobbies.put(lobbyUUID, lobby);
    }

    /**
     * Removes a lobby from the list of lobbies.
     *
     * @param lobbyUUID the unique identifier of the lobby to remove
     */
    public void removeLobby(UUID lobbyUUID) {
        lobbies.remove(lobbyUUID);
    }

    /**
     * Gets the UUID of the current lobby.
     *
     * @return the UUID of the current lobby
     */
    public UUID getCurrentLobbyUUID() {
        return currentLobbyOrGame == null ? null : currentLobbyOrGame.getRoomUUID();
    }

    /**
     * Checks if the player is currently in a lobby or game.
     *
     * @return true if the player is in a lobby or game, false otherwise
     */
    public boolean inLobbyOrGame() {
        return currentLobbyOrGame != null;
    }

    /**
     * Joins a lobby or game.
     *
     * @param room the lobby or game to join
     */
    public void joinLobbyOrGame(Room room) {
        currentLobbyOrGame = room;
    }

    /**
     * Leaves the current lobby or game.
     */
    public void leaveLobbyOrGame() {
        currentLobbyOrGame = null;
    }

    /**
     * Gets the current lobby the player is in.
     * This method does not perform any check whether the player is actually in a lobby and not in a game.
     * It only forces a cast into a GameLobby.
     *
     * @return the current lobby in which the player is in
     */
    public Lobby getCurrentLobby() {
        return (Lobby) currentLobbyOrGame;
    }

    /**
     * Gets the current game the player is in.
     * This method does not perform any check whether the player is actually in a game and not in a lobby.
     * It only forces a cast into a ClientGame.
     *
     * @return the current lobby in which the player is in
     */
    public ClientGame getGame() {
        return (ClientGame) currentLobbyOrGame;
    }
}
