package it.polimi.ingsw.gc12.ServerModel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest {

    @Test
    void setNickname() {
        Player p1 = new Player("Giovanni");
        p1.setNickname("Giuan'");
        assertEquals("Giuan'", p1.getNickname());
    }
}