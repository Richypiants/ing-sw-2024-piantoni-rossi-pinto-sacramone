package it.polimi.ingsw.gc12.Controller;

import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Utilities.Enums.Resource;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.*;

/**
 * The {@code ClientControllerInterface} extends {@link ControllerInterface} to provide
 * methods specific to client-side operations in response to an action required by the Server.
 * <p>
 * Methods defined in this interface are:
 * <ul>
 *     <li>{@link #throwException(Exception)} - Handles and shows exceptions thrown by the server.</li>
 *     <li>{@link #setNickname(String)} - Sets the client's nickname.</li>
 *     <li>{@link #restoreGame(ClientGame, String, Map)} - Restores the game state,
 *     receiving all the info to fully synchronize and represent the game.</li>
 *     <li>{@link #setLobbies(Map)} - Sets all the available lobbies.</li>
 *     <li>{@link #updateLobby(Lobby)} - Updates a lobby after an event has happened.</li>
 *     <li>{@link #startGame(ClientGame)} - Starts a new game.</li>
 *     <li>{@link #confirmObjectiveChoice(int)} - Confirms the chosen objective card.</li>
 *     <li>{@link #placeCard(String, GenericPair, int, Side, EnumMap, List, int)} - Places a card on the game board.</li>
 *     <li>{@link #receiveObjectiveChoice(List)} - Receives the list of objective cards to choose from.</li>
 *     <li>{@link #receiveCard(int)} - Receives an update related to some new received cards, such as a new card obtained in hand.</li>
 *     <li>{@link #replaceCard(List)} - Replaces cards on the game board, such as visible resources or top of the deck cards.</li>
 *     <li>{@link #toggleActive(String)} - Toggles the active state of a player.</li>
 *     <li>{@link #transition(int, int)} - Handles the transitions.</li>
 *     <li>{@link #pauseGame()} - Pauses the game.</li>
 *     <li>{@link #endGame(List, boolean)} - Ends the game and provides the final scores.</li>
 *     <li>{@link #addChatMessage(String, String, boolean)} - Adds a chat message to the chat log.</li>
 * </ul>
 * </p>
 */
public interface ClientControllerInterface extends ControllerInterface {

    /**
     * Handles exceptions that occur during operations
     * requested by the client by throwing the Exception returned by the Server
     *
     * @param e The exception to be handled.
     */
    void throwException(Exception e);

    /**
     * Sets the client's nickname given confirmation by the Server
     *
     * @param nickname The nickname to be set.
     */
    void setNickname(String nickname);

    /**
     * Restores the game state for the client, setting all the parameters to be displayed related to the GameModel and View.
     *
     * @param gameDTO The game data transfer object containing all the relevant game's common information.
     * @param currentState The current state of the game, such as the initial phase, the placing phase or drawing phase.
     * @param PLAYERS_FIELD A map representing the players' fields, containing all the IDs of the placed cards up to this moment.
     */
    void restoreGame(ClientGame gameDTO, String currentState, Map<String, LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> PLAYERS_FIELD);

    /**
     * Sets all the available lobbies, replacing the old info currently stored on the model.
     *
     * @param lobbies A map of available lobbies.
     */
    void setLobbies(Map<UUID, Lobby> lobbies);

    /**
     * Updates the specified lobby after an event has happened, such as joining, leaving or cancelling it.
     *
     * @param lobby The lobby to be updated.
     */
    void updateLobby(Lobby lobby);

    /**
     * Starts a new game for the client.
     *
     * @param gameDTO The game data transfer object containing the game's initial information.
     */
    void startGame(ClientGame gameDTO);

    /**
     * Confirms the chosen objective card requested by the client.
     *
     * @param cardID The ID of the chosen objective card.
     */
    void confirmObjectiveChoice(int cardID);

    /**
     * Places a card on the game board.
     *
     * @param nickname The nickname of the player placing the card.
     * @param coordinates The coordinates where the card is to be placed.
     * @param cardID The ID of the card being placed.
     * @param playedSide The side of the card being played.
     * @param ownedResources The resources owned by the player after playing this card.
     * @param openCorners The list of open corners on the game board.
     * @param points The points eventually scored by placing the card.
     */
    void placeCard(String nickname, GenericPair<Integer, Integer> coordinates, int cardID, Side playedSide,
                   EnumMap<Resource, Integer> ownedResources, List<GenericPair<Integer, Integer>> openCorners,
                   int points);

    /**
     * Receives the list of (two) objective cards to choose from.
     *
     * @param cardIDs A list of objective card IDs.
     */
    void receiveObjectiveChoice(List<Integer> cardIDs);

    /**
     * Receives an update related to a new received card, such as a new card obtained in hand.
     *
     * @param cardID The ID of the received card.
     */
    void receiveCard(int cardID);

    /**
     * Replaces cards on the game board, such as visible resources or top of the deck cards.
     *
     * @param cardPlacements A list of triplets representing card placements.
     */
    void replaceCard(List<Triplet<Integer, String, Integer>> cardPlacements);

    /**
     * Toggles the active status of this player.
     * If the player is currently active, they will become inactive, and vice versa.
     *
     * @param nickname The nickname of the player whose active state is to be toggled.
     */
    void toggleActive(String nickname);

    /**
     * Handles the transitions, creating and assigning a new GameState.
     *
     * @param round The current round of the game.
     * @param currentPlayerIndex The index of the current player.
     */
    void transition(int round, int currentPlayerIndex);

    /**
     * Pauses the game, until the game ends or someone reconnects.
     */
    void pauseGame();

    /**
     * Ends the game and provides the final scores.
     *
     * @param pointsStats A list of triplets containing the player nickname, points, and other stats.
     * @param gameEndedDueToDisconnections Whether the game ended due to disconnections.
     */
    void endGame(List<Triplet<String, Integer, Integer>> pointsStats, boolean gameEndedDueToDisconnections);

    /**
     * Adds a chat message to the chat log.
     *
     * @param senderNickname The nickname of the message sender.
     * @param chatMessage The content of the chat message.
     * @param isPrivate Whether the message is private.
     */
    void addChatMessage(String senderNickname, String chatMessage, boolean isPrivate);
}
