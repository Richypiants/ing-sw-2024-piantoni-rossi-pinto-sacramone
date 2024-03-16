package it.polimi.ingsw.gc12.ServerModel;

import java.util.ArrayList;
import java.util.Stack;
import java.lang.Math;

// This class implements a standard card deck to draw from during a game
public class CardDeck {
    private final Stack<Card> DECK; // Data structure chosen to delegate the card deck functionalities

    // Constructor for a CardDeck: it receives a list of cards from the parsed JSON, then it randomly
    // extracts them and pushes them into the stack to form the deck
    protected CardDeck(ArrayList<Card> deck) {
        int index = 0;
        ArrayList<Card> copy = new ArrayList<Card>(deck);

        //FIXME: make cards or their attributes final?

        this.DECK = new Stack<Card>();
        while (copy.isEmpty()) {
            index = (int) (Math.random() * copy.size());
            this.push(copy.remove(index));
        }
    }

    private void push(Card toInsert){
        this.DECK.push(toInsert);
    }

    // Pop the first card of the stack and return it to the caller
    protected Card draw() {
        if (!DECK.isEmpty()) {
            return DECK.pop();
        }

        //TODO: add EmptyDeckException?
        return null; // Placeholder for empty deck scenario
    }

    // Empty-stack checker to delegate control
    public boolean empty() {
        return DECK.isEmpty();
    }
}
