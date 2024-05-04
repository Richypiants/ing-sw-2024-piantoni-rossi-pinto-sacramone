package it.polimi.ingsw.gc12.Model.Cards;

import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
A card deck from which to draw cards during games
 */
public class CardDeck<T extends Card>{

    /**
    The group of cards which form the deck
     */
    private final Stack<T> DECK;

    /**
    Constructs a deck of cards by copying the passed cards, shuffling and then pushing them into the card stack
     */
    public CardDeck(List<T> deck) {
        List<T> copy = new ArrayList<>(deck);

        Collections.shuffle(copy);
        this.DECK = new Stack<>();
        while (!copy.isEmpty()) {
            this.push(copy.removeFirst());
        }
    }

    /**
    Pushes a card into the stack
     */
    private void push(T toInsert){
        this.DECK.push(toInsert);
    }

    /**
    Pops the first card of the stack and returns it to the caller
     */
    public T draw() throws EmptyDeckException {
        if (DECK.isEmpty())
            throw new EmptyDeckException();
        return this.DECK.pop();
    }

    public T peek() {
        if(DECK.isEmpty())
            return null;
        return this.DECK.peek();
    }

    /**
    Returns true if the deck has no cards, false otherwise
     */
    public boolean isEmpty() {
        return this.DECK.isEmpty();
    }
}

// push -> Si test
//         - Edge cases
//           toInsert undefined
//
// draw -> Si test
//         DECK.isEmpty() = TRUE
//
// empty -> No test
