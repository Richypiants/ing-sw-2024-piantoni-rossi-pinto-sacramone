package it.polimi.ingsw.gc12.Model.ClientModel;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ClientCardTest {

    @Test
    void ClientCardTestBuild() {
        ClientCard card = new ClientCard(1, new HashMap<>(), new HashMap<>());
        assertInstanceOf(ClientCard.class, card);
    }

    @Test
    void equalsTest(){
        ClientCard card1 = new ClientCard(1, new HashMap<>(), new HashMap<>());
        ClientCard cardWithSameID = new ClientCard(1, new HashMap<>(), new HashMap<>());
        ClientCard card2 = new ClientCard(2, new HashMap<>(), new HashMap<>());
        Object anotherObject = new Object();

        assertTrue(card1.equals(card1));
        assertFalse(card1.equals(card2));
        assertTrue(card1.equals(cardWithSameID));
        assertFalse(card1.equals(anotherObject));
        assertFalse(card1.equals(null));
    }
}