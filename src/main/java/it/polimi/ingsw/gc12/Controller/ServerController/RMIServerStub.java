package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Utilities.RMIVirtualServer;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

public class RMIServerStub implements RMIVirtualServer {

    private static final RMIServerStub SINGLETON_RMI_SERVER = new RMIServerStub();

    private RMIServerStub() {
    }

    public static RMIServerStub getInstance() {
        return SINGLETON_RMI_SERVER;
    }

    @Override
    public void requestToServer(VirtualClient caller, ServerCommand command) throws Exception {
        command.execute(caller, ServerController.getInstance());
    }

}
