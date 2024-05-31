package it.polimi.ingsw.gc12.Network;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIMainServer extends Remote {

    RMIVirtualServer accept(RMIVirtualClient client) throws RemoteException;
}
