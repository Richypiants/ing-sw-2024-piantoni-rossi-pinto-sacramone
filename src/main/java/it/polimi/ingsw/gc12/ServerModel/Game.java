package it.polimi.ingsw.gc12.ServerModel;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gc12.ServerModel.Cards.*;
import it.polimi.ingsw.gc12.ServerModel.GameStates.GameState;
import it.polimi.ingsw.gc12.ServerModel.GameStates.SetupState;
import it.polimi.ingsw.gc12.Utilities.JSONParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
A structure for games after they have started
 */
//FIXME: should inherit from GameLobby to manage the start of games more easily in the Controller...
public class Game{

    /**
    The list of player participating in this game
     */
    private final ArrayList<InGamePlayer> LIST_OF_GAME_PLAYERS;
    /**
    The deck of Resource cards of this game
     */
    private final CardDeck<ResourceCard> RESOURCE_CARDS_DECK;
    /**
    The deck of Gold cards of this game
     */
    private final CardDeck<GoldCard> GOLD_CARDS_DECK;
    /**
    The two Resource cards placed on the table
     */
    private final ResourceCard[] PLACED_RESOURCE_CARDS;
    /**
    The two Gold cards placed on the table
     */
    private final GoldCard[] PLACED_GOLD_CARDS;
    /**
    The two common Objective cards placed on the table
     */
    private final ObjectiveCard[] COMMON_OBJECTIVES;
    /**
     */
    private GameState currentState;
    /**
    The current turn's number (starting from 1 in the first turn)
     */
    private int currentRound;

    /**
    Constructs a new game instance from the lobby passed as parameter
     */
    public Game(GameLobby lobby) {

        //TODO: make this unmodifiable
        this.LIST_OF_GAME_PLAYERS = lobby.getListOfPlayers()
                .stream()
                .map(InGamePlayer::new)
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(this.LIST_OF_GAME_PLAYERS);

        this.currentRound = 0;
        this.currentState = new SetupState(this);

        this.RESOURCE_CARDS_DECK = new CardDeck<>(JSONParser.deckFromJSONConstructor("resource_cards.json", new TypeToken<>() {
        }));
        this.GOLD_CARDS_DECK = new CardDeck<>(JSONParser.deckFromJSONConstructor("gold_cards.json", new TypeToken<>() {
        }));

        this.PLACED_RESOURCE_CARDS = new ResourceCard[2];
        PLACED_RESOURCE_CARDS[0]= (ResourceCard) RESOURCE_CARDS_DECK.draw();
        PLACED_RESOURCE_CARDS[1]= (ResourceCard) RESOURCE_CARDS_DECK.draw();

        this.PLACED_GOLD_CARDS = new GoldCard[2];
        PLACED_GOLD_CARDS[0]= (GoldCard) GOLD_CARDS_DECK.draw();
        PLACED_GOLD_CARDS[1]= (GoldCard) GOLD_CARDS_DECK.draw();

        this.COMMON_OBJECTIVES = new ObjectiveCard[2];
    }

    /**
    Returns the player who is currently playing
     */
    public ArrayList<InGamePlayer> getPlayers() {
        return new ArrayList<>(LIST_OF_GAME_PLAYERS);
    }

    /**
    Increases the turn number
     */
    public void increaseTurn() {
        currentRound++;
    }

    /**
    Changes the currentState of this game to newState
     */
    public void setState(GameState newState) {
        currentState = newState;
    }

    /**
    Returns the current game state (of type GameState)
     */
    public GameState getCurrentState() {
        return currentState;
    }

    /**
    Returns the player that is currently playing
     */
    public Player getCurrentPlayer() {
        return currentState.getCurrentPlayer();
    }

    /**
    Returns the turn number
     */
    public int getTurnNumber() {
        return currentRound;
    }

    //FIXME: are these below unsafe returns? (Reference escaping?)

    /**
    Returns the Resource cards placed on the table
     */
    public CardDeck<ResourceCard> getResourceCardsDeck() {
        return RESOURCE_CARDS_DECK;
    }

    public CardDeck<GoldCard> getGoldCardsDeck() {
        return GOLD_CARDS_DECK;
    }

    public ResourceCard[] getPlacedResources() {
        return PLACED_RESOURCE_CARDS;
    }

    /**
    Returns the Gold cards placed on the table
     */
    public GoldCard[] getPlacedGold() {
        return PLACED_GOLD_CARDS;
    }

    /**
    Returns the Objective cards placed on the table
     */
    public ObjectiveCard[] getCommonObjectives() {
        return COMMON_OBJECTIVES;
    }

    public void setCommonObjectives(ObjectiveCard[] objectives) {
        if (objectives.length == 2) {
            COMMON_OBJECTIVES[0] = objectives[0];
            COMMON_OBJECTIVES[1] = objectives[1];
        }
    }

    /**
    Draws from the deck passed as parameter and returns the drawn card
     */
    public PlayableCard drawFrom(CardDeck<?> deck) {
        return (PlayableCard) deck.draw();
    }

    /**
    Given a pattern matching string {gold, resource} and a valid position {0, 1}, returns the selected card and
    replaces it on the board
     */

    //FIXME: change in UML
    public PlayableCard drawFrom(Card[] deck, int position) {
        PlayableCard returnedCard = null;

        if (Arrays.equals(deck, PLACED_GOLD_CARDS)) {
            returnedCard = PLACED_GOLD_CARDS[position];
            PLACED_GOLD_CARDS[position] = GOLD_CARDS_DECK.draw();
        } else if (Arrays.equals(deck, PLACED_RESOURCE_CARDS)) {
            returnedCard = PLACED_RESOURCE_CARDS[position];
            PLACED_RESOURCE_CARDS[position] = RESOURCE_CARDS_DECK.draw();
        } else {
            //TODO: UnmatchedStringException
        }
        //TODO: DrawnCardIsNullException
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
