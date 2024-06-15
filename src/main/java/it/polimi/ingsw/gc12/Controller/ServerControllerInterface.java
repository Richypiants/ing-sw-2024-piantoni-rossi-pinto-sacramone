package it.polimi.ingsw.gc12.Controller;

import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Utilities.Enums.Color;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;

import java.util.UUID;

/**
 * The {@code ServerControllerInterface} extends {@link ControllerInterface} to define methods
 * specific to server-side operations in response to requests from clients or triggered by the fulfillment of certain conditions.
 * <p>
 * Methods defined in this interface include:
 * <ul>
 *     <li>{@link #createPlayer(NetworkSession, String)} - Registers a new instance of player,
 *     linking it with their NetworkSession and the requested nickname.</li>
 *     <li>{@link #setNickname(NetworkSession, String)} - Updates the player's nickname.</li>
 *     <li>{@link #keepAlive(NetworkSession)} - Keeps the session alive to prevent disconnection.</li>
 *     <li>{@link #createLobby(NetworkSession, int)} - Creates a new lobby with a specified maximum number of players.</li>
 *     <li>{@link #joinLobby(NetworkSession, UUID)} - Allows a player to join a specified lobby.</li>
 *     <li>{@link #pickColor(NetworkSession, Color)} - Allows a player to pick a color before starting a game.</li>
 *     <li>{@link #leaveLobby(NetworkSession, boolean)} - Handles a player leaving a lobby, distinguishing if it is due to a disconnection or voluntary.</li>
 *     <li>{@link #pickObjective(NetworkSession, int)} - Allows a player to pick one of the objective cards by its ID from the previously given choice.</li>
 *     <li>{@link #placeCard(NetworkSession, GenericPair, int, Side)} - Places a card on the game board at specified coordinates.</li>
 *     <li>{@link #drawFromDeck(NetworkSession, String)} - Allows a player to draw a card from a specified deck.</li>
 *     <li>{@link #drawFromVisibleCards(NetworkSession, String, int)} - Allows a player to draw a card from the visible ones at a specified position.</li>
 *     <li>{@link #leaveGame(NetworkSession)} - Handles a player leaving the game, also managing their disconnection from the server.</li>
 *     <li>{@link #directMessage(NetworkSession, String, String)} - Sends a direct message to another player.</li>
 *     <li>{@link #broadcastMessage(NetworkSession, String)} - Broadcasts a message to all players.</li>
 * </ul>
 * </p>
 */
public interface ServerControllerInterface extends ControllerInterface {

    /**
     * Registers a new instance of player,
     * linking it with their NetworkSession and the requested nickname.
     *
     * @param sender The network session of the player making the request.
     * @param nickname The nickname to be assigned to the new player.
     */
    void createPlayer(NetworkSession sender, String nickname);

    /**
     * Updates the player's nickname.
     *
     * @param sender The network session of the player making the request.
     * @param nickname The new nickname to be set.
     */
    void setNickname(NetworkSession sender, String nickname);

    /**
     * Keeps the session alive to prevent disconnection.
     *
     * @param sender The network session of the player making the request.
     */
    void keepAlive(NetworkSession sender);

    /**
     * Creates a new lobby with the specified maximum number of players.
     *
     * @param sender The network session of the player making the request.
     * @param maxPlayers The maximum number of players allowed in the lobby.
     */
    void createLobby(NetworkSession sender, int maxPlayers);

    /**
     * Allows a player to join a specified lobby.
     *
     * @param sender The network session of the player making the request.
     * @param lobbyUUID The unique identifier of the lobby to join.
     */
    void joinLobby(NetworkSession sender, UUID lobbyUUID);

    /**
     * Allows a player to pick a color before starting a game.
     *
     * @param sender The network session of the player making the request.
     * @param color The color chosen by the player.
     */
    void pickColor(NetworkSession sender, Color color);

    /**
     * Handles a player leaving a lobby, distinguishing if it is due to a disconnection or voluntary.
     *
     * @param sender The network session of the player making the request.
     * @param isInactive Whether the player is to be marked as inactive.
     */
    void leaveLobby(NetworkSession sender, boolean isInactive);

    /**
     * Allows a player to pick one of the objective cards by its ID from the previously given choice.
     *
     * @param sender The network session of the player making the request.
     * @param cardID The ID of the objective card to pick.
     */
    void pickObjective(NetworkSession sender, int cardID);

    /**
     * Places a card on the game board at specified coordinates.
     *
     * @param sender The network session of the player making the request.
     * @param coordinates The coordinates where the card is to be placed.
     * @param cardID The ID of the card being placed.
     * @param playedSide The side of the card being played.
     */
    void placeCard(NetworkSession sender, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide);

    /**
     * Allows a player to draw a card from a specified deck.
     *
     * @param sender The network session of the player making the request.
     * @param deck The name of the deck from which to draw a card.
     */
    void drawFromDeck(NetworkSession sender, String deck);

    /**
     * Allows a player to draw a card from the visible ones at a specified position.
     *
     * @param sender The network session of the player making the request.
     * @param deck The name of the deck from which to draw a card.
     * @param position The position of the card to draw from the visible cards.
     */
    void drawFromVisibleCards(NetworkSession sender, String deck, int position);

    /**
     * Handles a player leaving the game, also managing their disconnection from the server.
     *
     * @param sender The network session of the player making the request.
     */
    void leaveGame(NetworkSession sender);

    /**
     * Sends a direct message to another player.
     *
     * @param sender The network session of the player making the request.
     * @param receiverNickname The nickname of the player who receives the message.
     * @param message The message to be sent.
     */
    void directMessage(NetworkSession sender, String receiverNickname, String message);

    /**
     * Broadcasts a message to all players.
     *
     * @param sender The network session of the player making the request.
     * @param message The message to be broadcast.
     */
    void broadcastMessage(NetworkSession sender, String message);
}
