package it.polimi.ingsw.gc12.ServerModel;

import java.util.ArrayList;
import java.util.Stack;
import java.lang.Math;

// This class implements a standard card deck to draw from during a game
public class CardDeck {
    private Stack<Card> deck; // Data structure chosen to delegate the card deck functionalities

    // Constructor for a CardDeck: it receives a list of cards from the parsed JSON, then it randomly
    // extracts them and pushes them into the stack to form the deck
    protected CardDeck(ArrayList<Card> deck) {
        int index = 0;
        ArrayList<Card> copy = new ArrayList<Card>(deck);

        //FIXME: make cards or their attributes final?

        this.deck = new Stack<Card>();
        while (copy.isEmpty()) {
            index = (int) (Math.random() * copy.size());
            if(index == copy.size()) continue;
            this.push(copy.remove(index));
        }
    }

    private void push(Card toInsert){
        this.deck.push(toInsert);
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
