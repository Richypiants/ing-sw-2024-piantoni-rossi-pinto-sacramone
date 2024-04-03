package it.polimi.ingsw.gc12.Utilities;

import java.rmi.Remote;
import java.util.List;

public interface RMIVirtualMethod extends Remote {

    public Object invoke(RMIVirtualClient client, Object... args) throws Throwable;

    public Object invokeExact(RMIVirtualClient client, Object... args) throws Throwable;

    public Object invokeWithArguments(RMIVirtualClient client, Object... arguments) throws Throwable;

    public Object invokeWithArguments(RMIVirtualClient client, List<?> arguments) throws Throwable;
}
