package it.polimi.ingsw.gc12.Network;

import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.ServerCommand;

public interface VirtualServer {

    void requestToServer(VirtualClient caller, ServerCommand command) throws Exception;

    void close() throws Exception;
}
