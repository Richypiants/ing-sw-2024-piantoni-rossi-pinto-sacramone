package it.polimi.ingsw.gc12.Model.Cards;

import it.polimi.ingsw.gc12.Utilities.Exceptions.EmptyDeckException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Represents a deck of cards used in the game, allowing cards to be drawn,
 * shuffled, and managed in a stack, with visibility of the top one.
 *
 * @param <T> The type of card in the deck, which must extend the {@link Card} class.
 */
public class CardDeck<T extends Card>{

    /**
     * The stack of cards that forms the deck.
     */
    private final Stack<T> DECK;

    /**
     * Constructs a deck of cards by copying the provided list of cards,
     * shuffling them, and then pushing them into the stack.
     *
     * @param deck The list of cards to initialize the deck with.
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
     * Pushes a card onto the stack.
     *
     * @param toInsert The card to be added to the deck.
     */
    private void push(T toInsert){
        this.DECK.push(toInsert);
    }

    /**
     * Draws the top card from the deck and returns it.
     *
     * @return The top card from the deck.
     * @throws EmptyDeckException If the deck is empty.
     */
    public T draw() throws EmptyDeckException {
        if (DECK.isEmpty())
            throw new EmptyDeckException();
        return this.DECK.pop();
    }

    /**
     * Shows the top card of the deck without removing it.
     *
     * @return The top card of the deck, or null if the deck is empty.
     */
    public T peek() {
        if(DECK.isEmpty())
            return null;
        return this.DECK.peek();
    }

    /**
     * Checks if the deck is empty.
     *
     * @return true if the deck has no cards, false otherwise.
     */
    public boolean isEmpty() {
        return this.DECK.isEmpty();
    }
}
