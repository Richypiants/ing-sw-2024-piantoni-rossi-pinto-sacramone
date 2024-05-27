package it.polimi.ingsw.gc12.Network;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;

public interface VirtualClient {

    void requestToClient(ClientCommand command) throws Exception;
}
