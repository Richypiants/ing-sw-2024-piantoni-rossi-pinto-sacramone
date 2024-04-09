package it.polimi.ingsw.gc12.ServerController;

import it.polimi.ingsw.gc12.Utilities.RMIVirtualClient;
import it.polimi.ingsw.gc12.Utilities.RMIVirtualMethod;

import java.lang.invoke.MethodHandle;
import java.util.List;

public class RMIMethodHandle implements RMIVirtualMethod {
    private final MethodHandle method;

    protected RMIMethodHandle(MethodHandle method) {
        this.method = method;
    }

    //TODO: remove these that we never use?
    @Override
    public Object invoke(RMIVirtualClient client, Object... args) throws Throwable {
        return method.invoke(client, args);
    }

    @Override
    public Object invokeExact(RMIVirtualClient client, Object... args) throws Throwable {
        return method.invokeExact(client, args);
    }

    @Override
    public Object invokeWithArguments(RMIVirtualClient client, Object... arguments) throws Throwable {
        return method.invokeWithArguments(client, arguments);
    }

    @Override
    public Object invokeWithArguments(RMIVirtualClient client, List<?> arguments) throws Throwable {
        return method.invokeWithArguments(client, arguments);
    }
}
