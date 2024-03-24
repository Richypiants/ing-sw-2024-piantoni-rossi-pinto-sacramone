package it.polimi.ingsw.gc12.ServerModel.Cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

/*
A card deck from which to draw cards during games
 */
public class CardDeck {

    /*
    The group of cards which form the deck
     */
    private final Stack<Card> DECK;

    /*
    Constructs a deck of cards by copying the passed cards, shuffling and then pushing them into the card stack
     */
    protected CardDeck(ArrayList<Card> deck) {
        ArrayList<Card> copy = new ArrayList<>(deck);

        Collections.shuffle(copy);
        this.DECK = new Stack<>();
        while (!copy.isEmpty()) {
            this.push(copy.removeFirst());
        }
    }

    /*
    Pushes a card into the stack
     */
    private void push(Card toInsert){
        this.DECK.push(toInsert);
    }

    /*
    Pops the first card of the stack and returns it to the caller
     */
    protected Card draw() {
        if (!this.DECK.isEmpty()) {
            return this.DECK.pop();
        }

        //TODO: add EmptyDeckException?
        return null; // Placeholder for empty deck scenario
    }

    /*
    Returns true if the deck has no cards, false otherwise
     */
    public boolean isEmpty() {
        return this.DECK.isEmpty();
    }
}

// push -> Si test
//         - Casi limite
//           toInsert undefined
//
// draw -> Si test
//         DECK.isEmpty() = TRUE
//
// empty -> No test
