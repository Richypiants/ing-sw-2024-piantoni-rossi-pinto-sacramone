package it.polimi.ingsw.gc12.Model;

import it.polimi.ingsw.gc12.Utilities.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest {

    @Test
    void setNickname() {
        Player p1 = new Player("Sacri");
        p1.setNickname("Piants");
        assertEquals("Piants", p1.getNickname());
    }

    @Test
    void setColorTest() {
        Player p1 = new Player("Sacri");
        p1.setColor(Color.RED);
        assertEquals(Color.RED, p1.getColor());
    }
}