package it.polimi.ingsw.gc12.ServerModel;

import java.util.ArrayList;

public class Game{
    //FIXME: should we make it a set?
    private final ArrayList<InGamePlayer> LIST_OF_GAME_PLAYERS = new ArrayList<InGamePlayer>();
    private int currentPlayer;
    private int currentTurn;
    private final CardDeck RESOURCE_CARDS_DECK;
    private final CardDeck GOLD_CARDS_DECK;
    private final ResourceCard[] PLACED_RESOURCE_CARDS = new ResourceCard[2];
    private final GoldCard[] PLACED_GOLD_CARDS = new GoldCard[2];
    private final ObjectiveCard[] COMMON_OBJECTIVES = new ObjectiveCard[2];

    //FIXME: assuming that the GameLobby we are evolving from is full and there are maxPlayers ready to go
    public Game(GameLobby lobby) {

        ArrayList<Player> copyOfLobby = lobby.getListOfPlayers();

        for(int i = 0; i < lobby.getPlayersNumber(); i++){
            LIST_OF_GAME_PLAYERS.add( new InGamePlayer( copyOfLobby.remove( (int) (Math.random() * copyOfLobby.size()) )));
        }

        currentPlayer = 0;
        currentTurn = 0;

        RESOURCE_CARDS_DECK = new CardDeck( JSONParser.fromJSONtoCardDeckConstructor() );
        GOLD_CARDS_DECK = new CardDeck(JSONParser.fromJSONtoCardDeckConstructor() );

        PLACED_RESOURCE_CARDS[0]= (ResourceCard) RESOURCE_CARDS_DECK.draw();
        PLACED_RESOURCE_CARDS[1]= (ResourceCard) RESOURCE_CARDS_DECK.draw();
        PLACED_GOLD_CARDS[0]= (GoldCard) GOLD_CARDS_DECK.draw();
        PLACED_GOLD_CARDS[1]= (GoldCard) GOLD_CARDS_DECK.draw();

        CardDeck objectiveCardsDeck = new CardDeck( JSONParser.fromJSONtoCardDeckConstructor() );
        COMMON_OBJECTIVES[0]= (ObjectiveCard) objectiveCardsDeck.draw();
        COMMON_OBJECTIVES[1]= (ObjectiveCard) objectiveCardsDeck.draw();
    }

    public void nextPlayer() {
        if(currentPlayer == 3)
            this.increaseTurn();
        currentPlayer = (currentPlayer+1) % LIST_OF_GAME_PLAYERS.size();
    }

    public Player getCurrentPlayer() {
        return LIST_OF_GAME_PLAYERS.get(currentPlayer);
    }

    public void increaseTurn() {
        currentTurn++;
    }

    public int getTurnNumber() {
        return currentTurn;
    }

    public ResourceCard[] getPlacedResources() {
        return PLACED_RESOURCE_CARDS;
    }

    public GoldCard[] getPlacedGold() {
        return PLACED_GOLD_CARDS;
    }

    public ObjectiveCard[] getCommonObjectives() {
        return COMMON_OBJECTIVES;
    }

    public PlayableCard drawFrom(CardDeck deck) {
        return (PlayableCard) deck.draw();
    }

    //Given a pattern matching string {gold, resource} and a valid position {0, 1}, returns the selected card and replaces it on the board
    //FIXME: The card isn't copied, but passed directly, but it shouldn't be a problem
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
// drawFrom() -> No test
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
