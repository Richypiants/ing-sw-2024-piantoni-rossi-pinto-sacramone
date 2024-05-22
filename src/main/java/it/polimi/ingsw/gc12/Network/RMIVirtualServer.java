package it.polimi.ingsw.gc12.Network;

import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.ServerCommand;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIVirtualServer extends Remote, VirtualServer {

    @Override
    void requestToServer(VirtualClient caller, ServerCommand command) throws RemoteException;

    //FIXME: useless for RMI, but it has to be here...
    @Override
    void close() throws RemoteException;
}
