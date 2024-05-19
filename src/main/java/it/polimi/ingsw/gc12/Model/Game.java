package it.polimi.ingsw.gc12.Model;

import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Model.Cards.*;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.GameStates.GameState;
import it.polimi.ingsw.gc12.Model.GameStates.SetupState;
import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A structure for games after they have started
 */
//FIXME: should inherit from GameLobby to manage the start of games more easily in the Controller... fix UML
public class Game extends GameLobby {

    /**
     * The deck of Resource cards of this game
     */
    private final CardDeck<ResourceCard> RESOURCE_CARDS_DECK;
    /**
     * The deck of Gold cards of this game
     */
    private final CardDeck<GoldCard> GOLD_CARDS_DECK;
    /**
     * The two Resource cards placed on the table
     */
    private final ResourceCard[] PLACED_RESOURCE_CARDS;
    /**
     * The two Gold cards placed on the table
     */
    private final GoldCard[] PLACED_GOLD_CARDS;
    /**
     * The two common Objective cards placed on the table
     */
    private final ObjectiveCard[] COMMON_OBJECTIVES;
    /**
     * The current turn's number (starting from 1 in the first turn)
     */
    private int currentRound;
    /**
     *
     */
    private GameState currentState;

    /**
     * Constructs a new game instance from the lobby passed as parameter
     */
    public Game(GameLobby lobby) {
        super(lobby.getMaxPlayers(), lobby.getPlayers()
                .stream()
                .map(InGamePlayer::new)
                .collect(Collectors.toList()));

        shufflePlayers();

        this.currentRound = 0;
        setState(new SetupState(this));

        this.RESOURCE_CARDS_DECK = new CardDeck<>(ServerController.getInstance().cardsList.values().stream()
                .filter((card -> card instanceof ResourceCard))
                .map((card) -> (ResourceCard) card)
                .toList());
        this.GOLD_CARDS_DECK = new CardDeck<>(ServerController.getInstance().cardsList.values().stream()
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
        } catch(EmptyDeckException e) {
            //cannot happen as deck has just been initialized
            e.printStackTrace();
        }

        this.COMMON_OBJECTIVES = new ObjectiveCard[2];

        this.currentState = new SetupState(this);
    }

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
     * Returns the player who is currently playing
     */
    @Override
    public ArrayList<InGamePlayer> getPlayers() {
        return super.getPlayers()
                .stream()
                .map((player) -> (InGamePlayer) player)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<InGamePlayer> getActivePlayers() {
        return super.getPlayers()
                .stream()
                .map((player) -> (InGamePlayer) player)
                .filter(InGamePlayer::isActive)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Increases the turn number
     */
    public void increaseTurn() {
        currentRound++;
    }

    /**
     * Returns the player that is currently playing
     */
    public InGamePlayer getCurrentPlayer() {
        return getCurrentState().getCurrentPlayer();
    }

    /**
     * Returns the turn number
     */
    public int getTurnNumber() {
        return currentRound;
    }

    //FIXME: are these below unsafe returns? (Reference escaping?)

    /**
     * Returns the deck of ResourceCards placed on the table
     */
    public CardDeck<ResourceCard> getResourceCardsDeck() {
        return RESOURCE_CARDS_DECK;
    }

    /**
     * Returns the deck of GoldCards placed on the table
     */
    public CardDeck<GoldCard> getGoldCardsDeck() {
        return GOLD_CARDS_DECK;
    }

    /**
     * Returns the ResourceCards placed on the table
     */
    public ResourceCard[] getPlacedResources() {
        return PLACED_RESOURCE_CARDS;
    }

    /**
     * Returns the GoldCards placed on the table
     */
    public GoldCard[] getPlacedGolds() {
        return PLACED_GOLD_CARDS;
    }

    /**
     * Returns the ObjectiveCards placed on the table
     */
    public ObjectiveCard[] getCommonObjectives() {
        return COMMON_OBJECTIVES;
    }

    public void setCommonObjectives(ObjectiveCard[] objectives) {
        COMMON_OBJECTIVES[0] = objectives[0];
        COMMON_OBJECTIVES[1] = objectives[1];
    }

    /**
     * Draws from the deck passed as parameter and returns the drawn card
     */
    public <T extends Card> T drawFrom(CardDeck<T> deck) throws EmptyDeckException {
        return deck.draw();
    }

    /**
     * Given a pattern matching string {gold, resource} and a valid position {0, 1}, returns the selected card and
     * replaces it on the board
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
        } else {
            //TODO: UnmatchedStringException
        }

        if (returnedCard == null)
            throw new EmptyDeckException();

        return returnedCard;
    }

    public <T extends Card> T peekFrom(CardDeck<T> deck){
        return deck.peek();
    }
    /**
     * Changes the currentState of this game to newState
     */
    //TODO:ADDED Sync keyword
    public void setState(GameState newState) {
        currentState = newState;
    }

    /**
     * Returns the current game state (of type GameState)
     */
    public GameState getCurrentState() {
        return currentState;
    }

    public ClientGame generateDTO(InGamePlayer receiver){
        return new ClientGame(this, receiver);
        //TODO: create the DTO from the current class, we also need DTOs for creating ClientPlayer and ClientCard.
        /*    public ClientGame(int maxPlayers, List<ClientPlayer > players, ArrayList< ClientCard > ownHand,
                ClientCard[] placedResourceCards, ClientCard[] placedGoldCards,
                ClientCard[] commonObjectives, ClientCard ownObjective, int currentRound){*/
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
// getPlacedGolds()  (Getter) -> No test
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
