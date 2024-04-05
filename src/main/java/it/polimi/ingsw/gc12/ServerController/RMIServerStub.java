package it.polimi.ingsw.gc12.ServerController;

import it.polimi.ingsw.gc12.Utilities.RMIVirtualMethod;
import it.polimi.ingsw.gc12.Utilities.RMIVirtualServer;

import java.lang.invoke.MethodHandle;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RMIServerStub implements RMIVirtualServer {

    private static final RMIServerStub SINGLETON_RMI_SERVER = new RMIServerStub(Controller.commandHandles);
    private final Map<String, RMIVirtualMethod> commandHandles;

    protected RMIServerStub(Map<String, MethodHandle> commandHandles) {
        HashMap<String, RMIVirtualMethod> tmp = new HashMap<>();
        commandHandles.forEach((key, value) -> tmp.put(key, new RMIMethodHandle(value)));
        this.commandHandles = Collections.unmodifiableMap(tmp);
    }

    //FIXME: gestire qui createPlayer?

    public static RMIServerStub getInstance() {
        return SINGLETON_RMI_SERVER;
    }

    @Override
    public Map<String, RMIVirtualMethod> getMap() {
        return commandHandles;
    }

}
