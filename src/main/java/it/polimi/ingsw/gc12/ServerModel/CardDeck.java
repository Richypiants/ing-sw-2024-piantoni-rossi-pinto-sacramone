package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;
import java.util.Stack;

// This class implements a standard card deck to draw from during a game
public class CardDeck {
    private Stack<Card> deck; // Data structure chosen to delegate the card deck functionalities

    // Constructor for a CardDeck: it receives a list of cards from the parsed JSON, then it randomly
    // extracts them and pushes them into the stack to form the deck
    protected CardDeck(ArrayList<Card> deck) {
        int index = 0;
        this.deck = new Stack<>();

        while (deck.isEmpty()) {
            //TODO: index to be set randomly here

            //FIXME: make cards or their attributes final?
            //FIXME: should we wrap the Stack.deck() call by adding a private method in this class?
            //FIXME: this ArrayList should be passed as a copy so that we can remove elements...s
            this.deck.push(deck.remove(index));
        }
    }

    // Pop the first card of the stack and return it to the caller
    protected Card draw() {
        if (!deck.isEmpty()) {
            return deck.pop();
        }

        //TODO: add EmptyDeckException?
        return null; // Placeholder for empty deck scenario
    }

    // Empty-stack checker to delegate control
    public boolean empty() {
        return deck.isEmpty();
    }
}
