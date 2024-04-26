package it.polimi.ingsw.gc12.Utilities;

import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.ServerCommand;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIVirtualServer extends Remote, VirtualServer {

    @Override
    void requestToServer(VirtualClient caller, ServerCommand command) throws RemoteException;
}
