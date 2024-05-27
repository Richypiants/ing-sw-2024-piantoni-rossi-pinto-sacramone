package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Network.Client.SocketClient;
import it.polimi.ingsw.gc12.Network.Server.Server;
import org.junit.jupiter.api.BeforeEach;

class ServerTest {
    Server server;
    SocketClient client;

    @BeforeEach
    void serverClientInit() {
        server = Server.getInstance();
        client = SocketClient.getInstance();
    }


}