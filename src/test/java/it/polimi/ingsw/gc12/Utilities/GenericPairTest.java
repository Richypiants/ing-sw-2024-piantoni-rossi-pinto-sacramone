package it.polimi.ingsw.gc12.Utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenericPairTest {

    @Test
    void testEquals() {
        GenericPair<Integer, Integer> uno = new GenericPair<>(0, 0);

        GenericPair<Integer, Integer> due = new GenericPair<>(0, 0);

        assertEquals(uno, due);
    }
}