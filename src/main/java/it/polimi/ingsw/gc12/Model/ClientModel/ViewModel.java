package it.polimi.ingsw.gc12.Model.ClientModel;

import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Model.Room;
import it.polimi.ingsw.gc12.Utilities.JSONParsers.ClientParsers.ClientJSONParser;

import java.util.*;

/**
 * Represents the client's view model, managing the player's nickname related to the client,
 * the list of game lobbies, and the current lobby or game if the player is in it.
 */
public class ViewModel {

    /**
     * The map of cards used to graphically represent them on the clients.
     * Each client card is mapped to its unique ID for easy access.
     */
    public static final Map<Integer, ClientCard> CARDS_LIST = loadCards();

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
     * The room (lobby or client game) this client is currently in.
     */
    private Room currentRoom;

    /**
     * Constructs a new ViewModel with an empty nickname and no lobbies.
     */
    public ViewModel() {
        ownNickname = "";
        lobbies = new HashMap<>();
        currentRoom = null;
    }

    /**
     * Loads the client cards from JSON files.
     *
     * @return A map of client card IDs to client cards.
     */
    private static Map<Integer, ClientCard> loadCards() {
        Map<Integer, ClientCard> tmp = new HashMap<>();
        Objects.requireNonNull(ClientJSONParser.generateClientCardsFromJSON("/jsonFiles/ClientJsonFiles/client_cards.json"))
                .forEach((card) -> tmp.put(card.ID, card));
        tmp.put(-1, new ClientCard(-1, null, null));
        return Collections.unmodifiableMap(tmp);
    }

    /**
     * Clears this model instance, so that operations that need a fresh reboot can discard all the previous data.
     */
    public void clearModel() {
        ownNickname = "";
        lobbies = new HashMap<>();
        currentRoom = null;
    }

    /**
     * Gets the player's own nickname.
     *
     * @return the player's own nickname
     */
    public synchronized String getOwnNickname() {
        return ownNickname;
    }

    /**
     * Sets the player's own nickname.
     *
     * @param ownNickname the new nickname
     */
    public synchronized void setOwnNickname(String ownNickname) {
        this.ownNickname = ownNickname;
    }

    /**
     * Gets the map containing all the open lobbies.
     *
     * @return the map containing the lobbies
     */
    public synchronized Map<UUID, Lobby> getLobbies() {
        return lobbies;
    }

    /**
     * Sets the map of lobbies
     *
     * @param lobbies the new map of lobbies
     */
    public synchronized void setLobbies(Map<UUID, Lobby> lobbies) {
        this.lobbies = lobbies;
    }

    /**
     * Adds a lobby to the list of lobbies.
     *
     * @param lobbyUUID the unique identifier of the lobby
     * @param lobby the lobby to add
     */
    public synchronized void putLobby(UUID lobbyUUID, Lobby lobby) {
        lobbies.put(lobbyUUID, lobby);
    }

    /**
     * Removes a lobby from the list of lobbies.
     *
     * @param lobbyUUID the unique identifier of the lobby to remove
     */
    public synchronized void removeLobby(UUID lobbyUUID) {
        lobbies.remove(lobbyUUID);
    }

    /**
     * Gets the UUID of the current lobby.
     *
     * @return the UUID of the current lobby
     */
    public synchronized UUID getCurrentRoomUUID() {
        return currentRoom == null ? null : currentRoom.getRoomUUID();
    }

    /**
     * Checks if the player is currently in a room (lobby or client game).
     *
     * @return true if the player is in a room, false otherwise
     */
    public synchronized boolean inRoom() {
        return currentRoom != null;
    }

    /**
     * Joins a room (lobby or client game).
     *
     * @param room the room to join
     */
    public synchronized void joinRoom(Room room) {
        currentRoom = room;
    }

    /**
     * Leaves the current room (lobby or client game).
     */
    public synchronized void leaveRoom() {
        currentRoom = null;
    }

    /**
     * Gets the current lobby the player is in.
     * This method does not perform any check whether the player is actually in a lobby and not in a game.
     * It only forces a cast into a GameLobby.
     *
     * @return the current lobby in which the player is in
     */
    public synchronized Lobby getCurrentLobby() {
        return (Lobby) currentRoom;
    }

    /**
     * Gets the current game the player is in.
     * This method does not perform any check whether the player is actually in a game and not in a lobby.
     * It only forces a cast into a ClientGame.
     *
     * @return the current game in which the player is in
     */
    public synchronized ClientGame getCurrentGame() {
        return (ClientGame) currentRoom;
    }
}
