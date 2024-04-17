package it.polimi.ingsw.gc12.Utilities;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands.ClientCommand;

public interface VirtualClient {

    void requestToClient(ClientCommand command) throws Exception;
}
