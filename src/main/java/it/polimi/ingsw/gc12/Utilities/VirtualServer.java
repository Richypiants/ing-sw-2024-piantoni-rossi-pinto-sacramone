package it.polimi.ingsw.gc12.Utilities;

import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.ServerCommand;

public interface VirtualServer {

    void requestToServer(VirtualClient caller, ServerCommand command) throws Exception;
}
