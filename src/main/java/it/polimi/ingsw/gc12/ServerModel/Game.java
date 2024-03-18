package it.polimi.ingsw.gc12.ServerModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/*
A structure for games after they have started
 */
//FIXME: should inherit from GameLobby to manage the start of games more easily in the Controller...
public class Game{

    /*
    The list of player participating in this game
     */
    //FIXME: should we make it a set?
    private final ArrayList<InGamePlayer> LIST_OF_GAME_PLAYERS;
    /*
    The deck of Resource cards of this game
     */
    private final CardDeck RESOURCE_CARDS_DECK;
    /*
    The deck of Gold cards of this game
     */
    private final CardDeck GOLD_CARDS_DECK;
    /*
    The two Resource cards placed on the table
     */
    private final ResourceCard[] PLACED_RESOURCE_CARDS;
    /*
    The two Gold cards placed on the table
     */
    private final GoldCard[] PLACED_GOLD_CARDS;
    /*
    The two common Objective cards placed on the table
     */
    private final ObjectiveCard[] COMMON_OBJECTIVES;
    /*
    The index which points to the player which is playing right now (starting from 0 when the game starts)
     */
    private int currentPlayer;
    /*
    The current turn's number (starting from 1 in the first turn)
     */
    private int currentTurn;

    /*
    Constructs a new game instance from the lobby passed as parameter
     */
    public Game(GameLobby lobby) {

        this.LIST_OF_GAME_PLAYERS = lobby.getListOfPlayers()
                .stream()
                .map((player) -> (InGamePlayer) player)
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(this.LIST_OF_GAME_PLAYERS);

        this.currentPlayer = 0;
        this.currentTurn = 0;

        this.RESOURCE_CARDS_DECK = new CardDeck(JSONParser.fromJSONtoCardDeckConstructor());
        this.GOLD_CARDS_DECK = new CardDeck(JSONParser.fromJSONtoCardDeckConstructor());

        this.PLACED_RESOURCE_CARDS = new ResourceCard[2];
        PLACED_RESOURCE_CARDS[0]= (ResourceCard) RESOURCE_CARDS_DECK.draw();
        PLACED_RESOURCE_CARDS[1]= (ResourceCard) RESOURCE_CARDS_DECK.draw();

        this.PLACED_GOLD_CARDS = new GoldCard[2];
        PLACED_GOLD_CARDS[0]= (GoldCard) GOLD_CARDS_DECK.draw();
        PLACED_GOLD_CARDS[1]= (GoldCard) GOLD_CARDS_DECK.draw();

        CardDeck objectiveCardsDeck = new CardDeck(JSONParser.fromJSONtoCardDeckConstructor());
        this.COMMON_OBJECTIVES = new ObjectiveCard[2];
        COMMON_OBJECTIVES[0]= (ObjectiveCard) objectiveCardsDeck.draw();
        COMMON_OBJECTIVES[1]= (ObjectiveCard) objectiveCardsDeck.draw();
    }

    /*
    Increases the current player counter, making it point to the next player, increasing the turn after everyone
    has played in the current turn
     */
    public void nextPlayer() {
        if (currentPlayer == LIST_OF_GAME_PLAYERS.size()) {
            this.increaseTurn();
        }
        currentPlayer = (currentPlayer + 1) % LIST_OF_GAME_PLAYERS.size();
    }

    /*
    Returns the player who is currently playing
     */
    public Player getCurrentPlayer() {
        return LIST_OF_GAME_PLAYERS.get(currentPlayer);
    }

    /*
    Increases the turn number
     */
    public void increaseTurn() {
        currentTurn++;
    }

    /*
    Returns the turn number
     */
    public int getTurnNumber() {
        return currentTurn;
    }

    //FIXME: are these below unsafe returns? (Reference escaping?)
    /*
    Returns the Resource cards placed on the table
     */
    public ResourceCard[] getPlacedResources() {
        return PLACED_RESOURCE_CARDS;
    }

    /*
    Returns the Gold cards placed on the table
     */
    public GoldCard[] getPlacedGold() {
        return PLACED_GOLD_CARDS;
    }

    /*
    Returns the Objective cards placed on the table
     */
    public ObjectiveCard[] getCommonObjectives() {
        return COMMON_OBJECTIVES;
    }

    /*
    Draws from the deck passed as parameter and returns the drawn card
     */
    public PlayableCard drawFrom(CardDeck deck) {
        return (PlayableCard) deck.draw();
    }

    /*
    Given a pattern matching string {gold, resource} and a valid position {0, 1}, returns the selected card and
    replaces it on the board
     */
    //FIXME: The card isn't copied, but passed directly, but it shouldn't be a problem after it is made final
    public PlayableCard drawFromVisibleCards(String whichType, int position) {
        PlayableCard returnedCard = null;

        if( position != 0 && position != 1) {
            //TODO: InvalidPositionException
        }
        if( whichType.trim().equalsIgnoreCase("gold")){
            returnedCard = PLACED_GOLD_CARDS[position];
            PLACED_GOLD_CARDS[position] = (GoldCard) GOLD_CARDS_DECK.draw();
        } else if( whichType.trim().equalsIgnoreCase("resource")){
            returnedCard = PLACED_RESOURCE_CARDS[position];
            PLACED_RESOURCE_CARDS[position] = (ResourceCard) RESOURCE_CARDS_DECK.draw();
        } else {
            //TODO: UnmatchedStringException
        }
        return returnedCard;
    }
}

// nextPlayer() -> Si test (verificare se funziona bene / i due casi per l'if)
//                 Statement coverage
//                 currentPlayer = 3
//
//                 Edge Coverage (anche se già con lo statement coverage vengono eseguite tutte le righe ...
//                 ... e non credo che this.increaseTurn() crei problemi)
//                 currentPlayer != 3 (2)
//
// getCurrentPlayer() (Getter) -> No test
// increaseTurn() -> No test
// getTurnNumber() (Getter) -> No test
// getPlacedResources() (Getter) -> No test
// getPlacedGold()  (Getter) -> No test
// getCommonObjectives() (Getter) -> No test
// drawFrom() -> Si test
//               - Casi limite
//                 deck undefined
//
// drawFromVisibleCards() -> Si test
//                           Statement coverage
//                           position = 0
//                           position = 2
//                           whichType = gold
//                           whichType = resource
//                           whichType = CavoloCappuccioRosso
//
//                           Edge and Condition coverage (Non necessario)
//                           position = 1
