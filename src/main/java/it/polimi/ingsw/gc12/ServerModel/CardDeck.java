package it.polimi.ingsw.gc12.ServerModel;
import java.util.ArrayList;
import java.util.Stack;

public class CardDeck {
    private Stack<Card> deck;

    public CardDeck(ArrayList<Card> cards) {
        deck = new Stack<>();
        deck.addAll(cards);
    }

    public Card draw() {
        if (!deck.isEmpty()) {
            return deck.pop();
        }
        return null; // Placeholder for empty deck scenario
    }

    public void push(Card card) {
        deck.push(card);
    }

    public boolean empty() {
        return deck.isEmpty();
    }
}
