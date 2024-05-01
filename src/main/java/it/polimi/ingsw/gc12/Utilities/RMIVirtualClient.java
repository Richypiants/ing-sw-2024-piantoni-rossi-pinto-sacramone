package it.polimi.ingsw.gc12.Utilities;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIVirtualClient extends Remote, VirtualClient {

    @Override
    void requestToClient(ClientCommand command) throws RemoteException;
}
