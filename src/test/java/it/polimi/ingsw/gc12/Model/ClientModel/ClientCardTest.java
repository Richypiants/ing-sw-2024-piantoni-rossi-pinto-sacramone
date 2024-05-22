package it.polimi.ingsw.gc12.Model.ClientModel;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ClientCardTest {

    @Test
    void ClientCardTestBuild() {
        ClientCard card = new ClientCard(1, new HashMap<>(), new HashMap<>());
        assertInstanceOf(ClientCard.class, card);
    }
}