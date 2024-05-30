package it.polimi.ingsw.gc12.Model.ClientModel;

import it.polimi.ingsw.gc12.Model.GameLobby;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a client-side game that extends the {@link GameLobby}.
 * This class handles the state of the game from the client's perspective.
 */
public class ClientGame extends GameLobby implements Serializable {

    /**
     * The player associated to this client
     */
    private final ClientPlayer MYSELF;
    /**
     * The cards in this player's hand
     */
    private final ArrayList<ClientCard> OWN_HAND;
    //TODO: valutare private final Map<String, ClientCard[]> PLACED_CARDS;
    /**
     * The two resource cards visible to all the players placed on the table.
     */
    private final ClientCard[] PLACED_RESOURCE_CARDS;
    /**
     * The two gold cards visible to all the players placed on the table.
     */
    private final ClientCard[] PLACED_GOLD_CARDS;
    /**
     * The two objective cards visible to all the players placed on the table.
     */
    private final ClientCard[] COMMON_OBJECTIVES;
    /**
     * The card on top of the resource deck
     */
    private ClientCard topDeckResourceCard;
    /**
     * The card on top of the gold deck
     */
    private ClientCard topDeckGoldCard;
    /**
     * The secret Objective Card chosen by this player
     */
    private ClientCard ownObjective;
    /**
     * The current turn's number
     */
    private int currentRound;
    /**
     * The index of the player currently playing.
     * A value of -1 means that it is a setup phase
     * and each player has to perform an action
     */
    private int currentPlayerIndex;
    private final List<String> chatLog;

    /**
     * Constructs a new ClientGame with the specified parameters.
     *
     * @param maxPlayers the maximum number of players in the game
     * @param players the list of players in the game
     * @param myself the player object representing the current client
     * @param ownHand the cards in the player's hand
     * @param placedResourceCards the resource cards placed on the table
     * @param placedGoldCards the gold cards placed on the table
     * @param commonObjectives the common objective cards placed on the table
     * @param topDeckResourceCard the top resource card of the deck
     * @param topDeckGoldCard the top gold card of the deck
     * @param ownObjective the secret objective card chosen by the player
     * @param currentRound the current round of the game
     * @param currentPlayerIndex the index of the player currently playing
     */
    public ClientGame(
            int maxPlayers,
            List<ClientPlayer> players,
            ClientPlayer myself,
            ArrayList<ClientCard> ownHand,
            ClientCard[] placedResourceCards,
            ClientCard[] placedGoldCards,
            ClientCard[] commonObjectives,
            ClientCard topDeckResourceCard,
            ClientCard topDeckGoldCard,
            ClientCard ownObjective,
            int currentRound,
            int currentPlayerIndex
            ){
        super(maxPlayers, players);
        this.MYSELF = myself;
        this.OWN_HAND = ownHand;
        this.PLACED_RESOURCE_CARDS = placedResourceCards;
        this.PLACED_GOLD_CARDS = placedGoldCards;
        this.COMMON_OBJECTIVES = commonObjectives;
        this.topDeckResourceCard = topDeckResourceCard;
        this.topDeckGoldCard = topDeckGoldCard;
        this.ownObjective = ownObjective;
        this.currentRound = currentRound;
        this.currentPlayerIndex = currentPlayerIndex;
        this.chatLog = new ArrayList<>();
    }

    /**
     * Returns the list of players in the game.
     *
     * @return the list of {@code ClientPlayer} objects representing the players in the game
     */
    @Override
    public ArrayList<ClientPlayer> getPlayers() {
        return super.getPlayers()
                .stream()
                .map((player) -> (ClientPlayer) player)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the player associated to this client.
     *
     * @return the {@code ClientPlayer} object representing this client player
     */
    public ClientPlayer getThisPlayer(){
        return this.MYSELF;
    }

    /**
     * Returns the cards in the player's hand.
     *
     * @return the list of {@code ClientCard} objects representing the cards in hand
     */
    public ArrayList<ClientCard> getCardsInHand(){
        return OWN_HAND;
    }

    /**
     * Adds a card to the player's hand.
     *
     * @param card the {@code ClientCard} to be added to the hand
     */
    public void addCardToHand(ClientCard card){
        OWN_HAND.add(card);
    }

    /**
     * Removes a card from the player's hand.
     *
     * @param card the {@code ClientCard} to be removed from the hand
     */
    public void removeCardFromHand(ClientCard card){
        OWN_HAND.remove(card);
    }

    /**
     * Returns the resource cards placed on the table.
     *
     * @return an array of {@code ClientCard} objects representing the placed resource cards
     */
    public ClientCard[] getPlacedResources() {
        return PLACED_RESOURCE_CARDS;
    }

    /**
     * Replaces one of the resource cards placed on the table.
     *
     * @param card the {@code ClientCard} to be placed
     * @param position the position at which the card is to be placed
     */
    public void setPlacedResources(ClientCard card, int position) {
        PLACED_RESOURCE_CARDS[position] = card;
    }

    /**
     * Returns the gold cards placed on the table.
     *
     * @return an array of {@code ClientCard} objects representing the placed gold cards
     */
    public ClientCard[] getPlacedGolds() {
        return PLACED_GOLD_CARDS;
    }

    /**
     * Replaces one of the gold cards placed on the table.
     *
     * @param card the {@code ClientCard} to be placed
     * @param position the position at which the card has to be placed
     */
    public void setPlacedGold(ClientCard card, int position) {
        PLACED_GOLD_CARDS[position] = card;
    }

    /**
     * Returns the common objective cards placed on the table.
     *
     * @return an array of {@code ClientCard} objects representing the common objectives
     */
    public ClientCard[] getCommonObjectives() {
        return COMMON_OBJECTIVES;
    }

    /**
     * Sets a common objective card at the specified position.
     *
     * @param objective the {@code ClientCard} representing the objective
     * @param position the position at which the objective card has to be placed [0, 1]
     */
    public void setCommonObjectives(ClientCard objective, int position) {
        COMMON_OBJECTIVES[position] = objective;
    }

    /**
     * Returns the top resource card on the deck.
     *
     * @return the {@code ClientCard} representing the card on top of the resource deck
     */
    public ClientCard getTopDeckResourceCard() {
        return topDeckResourceCard;
    }

    /**
     * Sets the top resource card on the deck.
     *
     * @param card the {@code ClientCard} to be set as the card on top of the resource deck
     */
    public void setTopDeckResourceCard(ClientCard card) {
        topDeckResourceCard = card;
    }

    /**
     * Returns the top gold card on the deck.
     *
     * @return the {@code ClientCard} representing the card on top of the gold deck
     */
    public ClientCard getTopDeckGoldCard() {
        return topDeckGoldCard;
    }

    /**
     * Sets the top gold card on the deck.
     *
     * @param card the {@code ClientCard} to be set as the card on top of the gold deck
     */
    public void setTopDeckGoldCard(ClientCard card) {
        topDeckGoldCard = card;
    }

    /**
     * Returns the secret objective card chosen by the player.
     *
     * @return the {@code ClientCard} representing the secret objective
     */
    public ClientCard getOwnObjective(){
        return ownObjective;
    }

    /**
     * Sets the secret objective card chosen by the player.
     *
     * @param card the {@code ClientCard} to be set as the secret objective
     */
    public void setOwnObjective(ClientCard card){
        ownObjective = card;
    }

    /**
     * Returns the current round of the game.
     *
     * @return the current round number
     */
    public int getCurrentRound(){
        return currentRound;
    }

    /**
     * Sets the current round of the game.
     *
     * @param currentRound the round number to be set
     */
    public void setCurrentRound(int currentRound){ this.currentRound = currentRound; }

    /**
     * Returns the index of the current player.
     *
     * @return the index of the current player
     */
    public int getCurrentPlayerIndex() {return currentPlayerIndex; }

    /**
     * Sets the index representing the current player.
     *
     * @param currentPlayerIndex the index of the player to be set as current
     */
    public void setCurrentPlayerIndex(int currentPlayerIndex) { this. currentPlayerIndex = currentPlayerIndex; }

    /**
     * Returns the chat log of the game.
     *
     * @return an unmodifiable list of strings representing the chat log
     */
    public List<String> getChatLog() {
        return Collections.unmodifiableList(chatLog);
    }

    /**
     * Adds a message to the chat log.
     *
     * @param message the message to be added to the chat log
     */
    public void addMessageToChatLog(String message) {
        chatLog.add(message);
    }
}
