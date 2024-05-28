package it.polimi.ingsw.gc12.Model;

import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.GameState;
import it.polimi.ingsw.gc12.Controller.ServerController.GameStates.SetupState;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Cards.*;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a game session after it has started, keeping track of the players in the associated game lobby.
 * This class manages the decks of cards, the players' information related to the game, such as their hands, fields, and secret objectives,
 * and the current state of the game.
 */
public class Game extends GameLobby {

    /**
     * The deck of Resource cards of this game.
     */
    private final CardDeck<ResourceCard> RESOURCE_CARDS_DECK;
    /**
     * The deck of Gold cards of this game.
     */
    private final CardDeck<GoldCard> GOLD_CARDS_DECK;
    /**
     * The two resource cards visible to all the players placed on the table.
     */
    private final ResourceCard[] PLACED_RESOURCE_CARDS;
    /**
     * The two gold cards visible to all the players placed on the table.
     */
    private final GoldCard[] PLACED_GOLD_CARDS;
    /**
     * The two objective cards visible to all the players placed on the table.
     */
    private final ObjectiveCard[] COMMON_OBJECTIVES;
    /**
     * The current turn's number, which is handled by the GameState.
     */
    private int currentRound;
    /**
     * The current state of the game.
     */
    private GameState currentState;

    /**
     * Constructs a new Game instance from the specified lobby.
     * Initializes decks, shuffles players, and sets up the game state.
     *
     * @param lobby The GameLobby instance from which this game is created.
     */
    public Game(GameLobby lobby) {
        super(lobby.getMaxPlayers(), lobby.getPlayers()
                .stream()
                .map(InGamePlayer::new)
                .collect(Collectors.toList()));

        shufflePlayers();

        this.currentRound = 0;

        this.RESOURCE_CARDS_DECK = new CardDeck<>(ServerController.cardsList.values().stream()
                .filter((card -> card instanceof ResourceCard))
                .map((card) -> (ResourceCard) card)
                .toList());
        this.GOLD_CARDS_DECK = new CardDeck<>(ServerController.cardsList.values().stream()
                .filter((card -> card instanceof GoldCard))
                .map((card) -> (GoldCard) card)
                .toList());

        this.PLACED_RESOURCE_CARDS = new ResourceCard[2];
        this.PLACED_GOLD_CARDS = new GoldCard[2];

        try {
            PLACED_RESOURCE_CARDS[0] = RESOURCE_CARDS_DECK.draw();
            PLACED_RESOURCE_CARDS[1] = RESOURCE_CARDS_DECK.draw();

            PLACED_GOLD_CARDS[0] = GOLD_CARDS_DECK.draw();
            PLACED_GOLD_CARDS[1] = GOLD_CARDS_DECK.draw();
        } catch(EmptyDeckException ignored) {
            //cannot happen as deck has just been initialized
        }

        this.COMMON_OBJECTIVES = new ObjectiveCard[2];

        this.currentState = new SetupState(this);
    }

    /**
     * Converts the current game state back to a lobby state.
     * This method is useful for scenarios where the players in-game need to be converted in player instances,
     * such as after a game ends and players return to a lobby.
     *
     * @return A new GameLobby instance reflecting only the current active players.
     */
    public GameLobby toLobby(){
        List<Player> playersList = new ArrayList<>();

        for(var inGamePlayer : getPlayers()){
            if(inGamePlayer.isActive()) {
                Player targetPlayer = inGamePlayer.toPlayer();
                playersList.add(targetPlayer);
            }
        }

        GameLobby returnLobby = new GameLobby(playersList.removeFirst(), getMaxPlayers());
        for(var player : playersList)
            returnLobby.addPlayer(player);

        return returnLobby;
    }

    /**
     * Returns all players in the game, both active and inactive.
     *
     * @return An ArrayList of InGamePlayer instances representing all players in the game.
     */
    @Override
    public ArrayList<InGamePlayer> getPlayers() {
        return super.getPlayers()
                .stream()
                .map((player) -> (InGamePlayer) player)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns only the active players in the game.
     *
     * @return An ArrayList of InGamePlayer instances representing the active players in the game.
     */
    public ArrayList<InGamePlayer> getActivePlayers() {
        return super.getPlayers()
                .stream()
                .map((player) -> (InGamePlayer) player)
                .filter(InGamePlayer::isActive)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Increments the current round number, indicating the progression of the game.
     */
    public void increaseTurn() {
        currentRound++;
    }

    /**
     * Retrieves the player who is currently taking their turn.
     *
     * @return The InGamePlayer instance representing the current player.
     */
    public InGamePlayer getCurrentPlayer() {
        return getCurrentState().getCurrentPlayer();
    }

    /**
     * Gets the current round number.
     *
     * @return The current round number as an integer.
     */
    public int getTurnNumber() {
        return currentRound;
    }

    /**
     * Retrieves the deck of ResourceCards placed on the table.
     *
     * @return The CardDeck of ResourceCards instances.
     */
    public CardDeck<ResourceCard> getResourceCardsDeck() {
        return RESOURCE_CARDS_DECK;
    }

    /**
     * Retrieves the deck of GoldCards placed on the table.
     *
     * @return The CardDeck of GoldCard instances.
     */
    public CardDeck<GoldCard> getGoldCardsDeck() {
        return GOLD_CARDS_DECK;
    }

    /**
     * Retrieves the array of ResourceCards currently placed on the table.
     *
     * @return An array of ResourceCard instances.
     */
    public ResourceCard[] getPlacedResources() {
        return PLACED_RESOURCE_CARDS;
    }

    /**
     * Retrieves the array of GoldCards currently placed on the table.
     *
     * @return An array of GoldCard instances.
     */
    public GoldCard[] getPlacedGolds() {
        return PLACED_GOLD_CARDS;
    }

    /**
     * Retrieves the array of ObjectiveCards currently placed on the table.
     *
     * @return An array of ObjectiveCard instances.
     */
    public ObjectiveCard[] getCommonObjectives() {
        return COMMON_OBJECTIVES;
    }

    /**
     * Sets the common objective cards for the game.
     *
     * @param objectives An array of two ObjectiveCard instances.
     */
    public void setCommonObjectives(ObjectiveCard[] objectives) {
        COMMON_OBJECTIVES[0] = objectives[0];
        COMMON_OBJECTIVES[1] = objectives[1];
    }

    /**
     * Draws a card from the specified deck.
     *
     * @param deck The deck to draw from.
     * @param <T>  The type of cards in the deck.
     * @return The drawn card of type T.
     * @throws EmptyDeckException if the deck is empty.
     */
    public <T extends Card> T drawFrom(CardDeck<T> deck) throws EmptyDeckException {
        return deck.draw();
    }

    /**
     * Draws a card from the specified deck at a given position and replaces it with a new card from the deck.
     *
     * @param deck     The array of cards from which to draw.
     * @param position The position of the card to draw.
     * @return The drawn card.
     * @throws EmptyDeckException if the deck is empty.
     */
    public PlayableCard drawFrom(Card[] deck, int position) throws EmptyDeckException {
        PlayableCard returnedCard = null;

        if (Arrays.equals(deck, PLACED_GOLD_CARDS)) {
            returnedCard = PLACED_GOLD_CARDS[position];
            try {
                PLACED_GOLD_CARDS[position] = drawFrom(getGoldCardsDeck());
            } catch (EmptyDeckException e) {
                PLACED_GOLD_CARDS[position] = null;
            }
        } else if (Arrays.equals(deck, PLACED_RESOURCE_CARDS)) {
            returnedCard = PLACED_RESOURCE_CARDS[position];
            try {
                PLACED_RESOURCE_CARDS[position] = drawFrom(getResourceCardsDeck());
            } catch (EmptyDeckException e) {
                PLACED_RESOURCE_CARDS[position] = null;
            }
        }

        if (returnedCard == null)
            throw new EmptyDeckException();

        return returnedCard;
    }

    /**
     * Peeks at the top card of the specified deck without removing it.
     *
     * @param deck The deck to peek from.
     * @param <T>  The type of cards in the deck.
     * @return The top card of type T.
     */

    public <T extends Card> T peekFrom(CardDeck<T> deck){
        return deck.peek();
    }
    /**
     * Changes the currentState of this game to newState
     *
     * @param newState A new GameState
     */
    public void setState(GameState newState) {
        currentState = newState;
    }
    /**
     * Retrieves the current state associated to this game.
     *
     * @return The current state.
     */
    public GameState getCurrentState() {
        return currentState;
    }

    /**
     * Creates an instance of ClientGame, which contains only the essential information
     * needed by a client to correctly create and manage its local instance of the game.
     * The generated Data Transfer Object (DTO) will include only the relevant personal
     * information of the specified player.
     *
     * @param receiver The player for whom the DTO is created. The relevant information in the DTO will pertain to this player.
     * @return A ClientGame instance containing the necessary information for the specified player.
     */

    public ClientGame generateDTO(InGamePlayer receiver){
        return new ClientGame(this, receiver);
    }

    /**
     * Generates a temporary map of fields to players. This map contains only the ID associated to the placed cards for each player,
     * where each player's field is represented as a LinkedHashMap with specific key-value pairs indicating card placements.
     *
     * @return A map where each key is a player's nickname and each value is a LinkedHashMap representing that player's field.
     */
    public Map<String,LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> generateTemporaryFieldsToPlayers() {
        Map<String,LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>>> playersField = new HashMap<>();

        for(var player : getPlayers()) {
            LinkedHashMap<GenericPair<Integer, Integer>, GenericPair<Integer, Side>> playerField = new LinkedHashMap<>();
            for (var thisPlayerFieldEntry : player.getPlacedCards().sequencedEntrySet())
                playerField.put(thisPlayerFieldEntry.getKey(), new GenericPair<>(thisPlayerFieldEntry.getValue().getX().ID, thisPlayerFieldEntry.getValue().getY()));
            playersField.put(player.getNickname(), playerField);
        }

        return playersField;
    }
}