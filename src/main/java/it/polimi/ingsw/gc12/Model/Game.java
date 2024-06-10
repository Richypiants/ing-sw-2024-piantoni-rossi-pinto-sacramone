package it.polimi.ingsw.gc12.Model;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.PlaceCardCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ReplaceCardCommand;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ToggleActiveCommand;
import it.polimi.ingsw.gc12.Listeners.Listenable;
import it.polimi.ingsw.gc12.Listeners.Listener;
import it.polimi.ingsw.gc12.Model.Cards.*;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Utilities.Exceptions.*;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a game session after it has started, keeping track of the players in the associated game lobby.
 * This class manages the decks of cards, the players' information related to the game, such as their hands, fields, and secret objectives,
 * and the current state of the game.
 */
public class Game extends Room implements Listenable {

    private final List<Listener> GAME_LISTENERS;
    /**
     * The deck of Resource cards of this game.
     */
    private final CardDeck<ResourceCard> RESOURCE_CARDS_DECK;
    /**
     * The deck of Gold cards of this game.
     */
    private final CardDeck<GoldCard> GOLD_CARDS_DECK;
    /**
     * The deck of Objective cards of this game.
     */
    private final CardDeck<ObjectiveCard> OBJECTIVE_CARDS_DECK;
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
     The index which points to the player which is playing right now (-1 when the game is in the setup phase)
     */
    private int currentPlayer;
    /**
     * The number of remaining turns before the game ends (-1 when the game is not in in final phase)
     */
    private int finalPhaseCounter;

    /**
     * Constructs a new Game instance from the specified lobby.
     * Initializes decks, shuffles players, and sets up the game state.
     *
     * @param lobby The Lobby instance from which this game is created.
     */
    public Game(Lobby lobby) {
        super(lobby.getRoomUUID(), lobby.getPlayers()
                .stream()
                .map(InGamePlayer::new)
                .collect(Collectors.toCollection(ArrayList::new)));

        this.GAME_LISTENERS = new ArrayList<>();

        Collections.shuffle(LIST_OF_PLAYERS);

        this.currentRound = 0;

        this.RESOURCE_CARDS_DECK = new CardDeck<>(ServerModel.cardsList.values().stream()
                .filter((card -> card instanceof ResourceCard))
                .map((card) -> (ResourceCard) card)
                .toList());
        this.GOLD_CARDS_DECK = new CardDeck<>(ServerModel.cardsList.values().stream()
                .filter((card -> card instanceof GoldCard))
                .map((card) -> (GoldCard) card)
                .toList());
        this.OBJECTIVE_CARDS_DECK = new CardDeck<>(ServerModel.cardsList.values().stream()
                .filter((card -> card instanceof ObjectiveCard))
                .map((card) -> (ObjectiveCard) card)
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

        this.currentPlayer = -1;
        this.finalPhaseCounter = -1;
    }

    /**
     * Converts the current game state back to a lobby state.
     * This method is useful for scenarios where the players in-game need to be converted in player instances,
     * such as after a game ends and players return to a lobby.
     *
     * @return A new Lobby instance reflecting only the current active players.
     */
    public Lobby toLobby(){
        List<Player> playersList = new ArrayList<>();

        for(var inGamePlayer : getPlayers()){
            if(inGamePlayer.isActive()) {
                Player targetPlayer = inGamePlayer.toPlayer();
                playersList.add(targetPlayer);
            }
        }

        Lobby returnLobby = new Lobby(getRoomUUID(), playersList.removeFirst(), playersList.size() + 1);
        for (var player : playersList) {
            try {
                returnLobby.addPlayer(player);
            } catch (FullLobbyException ignored) {
            }
        }

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
    public void increaseRound() {
        currentRound++;
    }

    /**
     * Retrieves the index of the player who is currently taking their turn.
     *
     * @return The int value representing the current player index in the players' list.
     */
    public int getCurrentPlayerIndex() {
        return currentPlayer;
    }

    /**
     * Retrieves the player who is currently taking their turn.
     *
     * @return The InGamePlayer instance representing the current player.
     */
    public InGamePlayer getCurrentPlayer() {
        if (currentPlayer != -1)
            return getPlayers().get(currentPlayer);
        //FIXME: Maybe not a null but something else?
        return null;
    }

    //FIXME: check javadoc copypasted from gamestates when moving this field in here

    /**
     * Increases the current player counter, making it point to the next player, and also increasing the currentRound
     * value after everyone has played in the current round
     */
    public void nextPlayer() {
        if (currentPlayer == getPlayers().size() - 1)
            increaseRound();

        do {
            currentPlayer = (currentPlayer + 1) % getPlayers().size();
            if (finalPhaseCounter != -1)
                finalPhaseCounter--;
            if (finalPhaseCounter == 0)
                //There's no need to find another active player, since the game is ended.
                break;
        } while (!getPlayers().get(currentPlayer).isActive());
    }

    /**
     * Gets the current round number.
     *
     * @return The current round number as an integer.
     */
    public int getRoundNumber() {
        return currentRound;
    }

    /**
     * Gets the number of turns left until the end of the game.
     *
     * @return The number of turns left until the end of the game as an integer.
     */
    public int getFinalPhaseCounter() {
        return finalPhaseCounter;
    }

    /**
     * Initializes the counter of turns left until the end of the game.
     */
    public void initializeFinalPhaseCounter() {
        finalPhaseCounter = 2 * getPlayers().size() - currentPlayer;
    }

    /**
     * Decreases the counter of turns left until the end of the game by 1.
     */
    public void decreaseFinalPhaseCounter() {
        finalPhaseCounter--;
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
     * Retrieves the deck of ObjectiveCards placed on the table.
     *
     * @return The CardDeck of ObjectiveCard instances.
     */
    public CardDeck<ObjectiveCard> getObjectiveCardsDeck() {
        return OBJECTIVE_CARDS_DECK;
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
     * Draws and sets the common objective cards for the game.
     */
    public void generateCommonObjectives() {
        try {
            COMMON_OBJECTIVES[0] = OBJECTIVE_CARDS_DECK.draw();
            COMMON_OBJECTIVES[1] = OBJECTIVE_CARDS_DECK.draw();
        } catch (EmptyDeckException ignored) {}

        notifyListeners(new ReplaceCardCommand(List.of(
                new Triplet<>(COMMON_OBJECTIVES[0].ID, "objective_visible", 0),
                new Triplet<>(COMMON_OBJECTIVES[1].ID, "objective_visible", 1)
        )));
    }

    /**
     * Sets the common objective cards for the game avoiding the randomness given by the shuffled deck.
     *
     * @param objectiveCards the desired objectiveCards to be set
     */
    public void setCommonObjectives(ObjectiveCard[] objectiveCards){
        COMMON_OBJECTIVES[0] = objectiveCards[0];
        COMMON_OBJECTIVES[1] = objectiveCards[1];
    }

    public Map<InGamePlayer, ArrayList<ObjectiveCard>> generateSecretObjectivesSelection() {
        Map<InGamePlayer, ArrayList<ObjectiveCard>> objectivesSelection = new HashMap<>();

        for (InGamePlayer target : getPlayers()) {
            ArrayList<ObjectiveCard> personalObjectiveCardsSelection = new ArrayList<>();
            try {
                personalObjectiveCardsSelection.add(OBJECTIVE_CARDS_DECK.draw());
                personalObjectiveCardsSelection.add(OBJECTIVE_CARDS_DECK.draw());
            } catch (EmptyDeckException ignored) {} //FIXME: non viene mai lanciata, System.exit(-1)
            objectivesSelection.put(target, personalObjectiveCardsSelection);
            target.setObjectivesSelection(personalObjectiveCardsSelection);
        }

        return objectivesSelection;
    }

    public void placeCard(InGamePlayer target, GenericPair<Integer, Integer> coordinates, PlayableCard card, Side playedSide)
            throws InvalidCardPositionException, NotEnoughResourcesException, CardNotInHandException {
        target.placeCard(coordinates, card, playedSide);
        notifyListeners(new PlaceCardCommand(target.getNickname(), coordinates, card.ID, playedSide,
                target.getOwnedResources(), target.getOpenCorners(), target.getPoints()));
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
    public PlayableCard drawFrom(PlayableCard[] deck, int position) throws EmptyDeckException {
        PlayableCard returnedCard;
        PlayableCard replacingCard = null;
        String deckType = "";

        returnedCard = deck[position];
        try {
            if (Arrays.equals(deck, PLACED_GOLD_CARDS)) {
                deckType = "gold";
                replacingCard = drawFrom(getGoldCardsDeck());
            } else if (Arrays.equals(deck, PLACED_RESOURCE_CARDS)) {
                deckType = "resource";
                replacingCard = drawFrom(getResourceCardsDeck());
            }
        } catch (EmptyDeckException ignored) {} //Simply we do not care, as we are already assigning null value at declaration of replacingCard
        deck[position] = replacingCard;

        if (returnedCard == null)
            throw new EmptyDeckException();

        notifyListeners(new ReplaceCardCommand(List.of(new Triplet<>(replacingCard == null ? -1 : replacingCard.ID, deckType + "_visible", position))));

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
        T topDeckCard = deck.peek();
        notifyListeners(new ReplaceCardCommand(List.of(new Triplet<>(topDeckCard == null ? -1 : topDeckCard.ID, deck.getDeckType() + "_deck", -1))));

        return topDeckCard;
    }

    public void toggleActive(InGamePlayer target) {
        target.toggleActive();
        notifyListeners(new ToggleActiveCommand(target.getNickname()));
    }


    @Override
    public void addListener(Listener listener) {
        synchronized (GAME_LISTENERS) {
            GAME_LISTENERS.add(listener);
        }
    }

    @Override
    public void removeListener(Listener listener) {
        synchronized (GAME_LISTENERS) {
            GAME_LISTENERS.remove(listener);
        }
    }

    @Override
    public void notifyListeners(ClientCommand command) {
        synchronized (GAME_LISTENERS) {
            for (var listener : GAME_LISTENERS)
                listener.notified(command);
        }
    }

    /**
     * Creates an instance of Client, which contains only the essential information
     * needed by a client to correctly create and manage its local instance of the game.
     * The generated Data Transfer Object (DTO) will include only the relevant personal
     * information of the specified player.
     *
     * @param receiver The player for whom the DTO is created. The relevant information in the DTO will pertain to this player.
     * @return A Client instance containing the necessary information for the specified player.
     */
    public ClientGame generateDTO(InGamePlayer receiver) {
        Map<Integer, ClientCard> clientCards = ServerModel.clientCardsList;
        List<Player> players = this.getPlayers().stream()
                .map((player) -> new ClientPlayer(player, player.getOpenCorners(), player.getOwnedResources(), player.getPoints()))
                .collect(Collectors.toCollection(ArrayList::new));

        return new ClientGame(
                getRoomUUID(),
                players,
                (ClientPlayer) players.stream().filter((player) -> player.getNickname().equals(receiver.getNickname())).findAny().orElseThrow(),
                receiver.getCardsInHand().stream()
                        .map((card) -> clientCards.get(card.ID))
                        .collect(Collectors.toCollection(ArrayList<ClientCard>::new)),
                Arrays.stream(getPlacedResources())
                        .map((card) -> clientCards.get(card.ID))
                        .toArray(ClientCard[]::new),
                Arrays.stream(getPlacedGolds())
                        .map((card) -> clientCards.get(card.ID))
                        .toArray(ClientCard[]::new),
                Arrays.stream(getCommonObjectives())
                        .map((card) -> card == null ? null : clientCards.get(card.ID))
                        .toArray(ClientCard[]::new),
                clientCards.get(peekFrom(getResourceCardsDeck()) == null ? -1 : peekFrom(getResourceCardsDeck()).ID),
                clientCards.get(peekFrom(getGoldCardsDeck()) == null ? -1 : peekFrom(getGoldCardsDeck()).ID),
                receiver.getSecretObjective() == null ? null : clientCards.get(receiver.getSecretObjective().ID),
                getRoundNumber(),
                getPlayers().indexOf(getCurrentPlayer())
        );
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