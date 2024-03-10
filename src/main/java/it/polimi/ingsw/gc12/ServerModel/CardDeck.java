package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;
import java.util.Stack;

public class CardDeck {
    private Stack<Card> deck;

    protected CardDeck(ArrayList<Card> cards) {
        deck = new Stack<>();
        deck.addAll(cards);
    }

    protected Card draw() {
        if (!deck.isEmpty()) {
            return deck.pop();
        }
        return null; // Placeholder for empty deck scenario
    }

    protected void push(Card card) {
        deck.push(card);
    }

    protected boolean empty() {
        return deck.isEmpty();
    }
}
