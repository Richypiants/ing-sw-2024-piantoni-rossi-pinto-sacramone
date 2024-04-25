package it.polimi.ingsw.gc12.Utilities;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands.ClientCommand;

import java.rmi.Remote;

public interface RMIVirtualClient extends Remote, VirtualClient {

    @Override
    void requestToClient(ClientCommand command) throws Exception;
}
