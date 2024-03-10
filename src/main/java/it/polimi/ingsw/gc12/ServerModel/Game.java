
import java.util.ArrayList;
package it.polimi.ingsw.gc12.ServerModel;

public class Game {
    private int currentPlayer;
    private int currentTurn;
    private CardDeck resourceCardsDeck;
    private CardDeck goldCardsDeck;
    private ResourceCard[] placedResourceCards = new ResourceCard[2];
    private GoldCard[] placedGoldCards = new GoldCard[2];
    private ObjectiveCard[] commonObjectives = new ObjectiveCard[2];

    public Game() {
        // Initialization logic goes here
    }

    public void nextPlayer() {
        // Implementation depends on game logic
    }

    public Player getCurrentPlayer() {
        // Implementation depends on game logic
        return null; // Placeholder
    }

    public void increaseTurn() {
        currentTurn++;
    }

    public int getTurnNumber() {
        return currentTurn;
    }

    public CardDeck getResourceDeck() {
        return resourceCardsDeck;
    }

    public CardDeck getGoldDeck() {
        return goldCardsDeck;
    }

    public ResourceCard[] getPlacedResources() {
        return placedResourceCards;
    }

    public GoldCard[] getPlacedGold() {
        return placedGoldCards;
    }

    public ObjectiveCard[] getCommonObjectives() {
        return commonObjectives;
    }

    // Note: The drawFromDeck and drawFromVisibleCards methods' implementations depend on further details not provided in the UML diagram
    public PlayableCard drawFromDeck(CardDeck deck) {
        // Implementation depends on deck logic
        return null; // Placeholder
    }

    public PlayableCard drawFromVisibleCards(PlayableCard[] cards) {
        // Implementation depends on game logic
        return null; // Placeholder
    }
}
