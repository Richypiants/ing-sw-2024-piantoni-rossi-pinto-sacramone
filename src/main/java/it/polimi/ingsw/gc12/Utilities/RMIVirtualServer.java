package it.polimi.ingsw.gc12.Utilities;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface RMIVirtualServer extends Remote {

    Map<String, RMIVirtualMethod> getMap() throws RemoteException;
}
