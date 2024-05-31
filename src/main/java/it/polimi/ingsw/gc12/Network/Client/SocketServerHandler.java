package it.polimi.ingsw.gc12.Network.Client;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Controller.ClientControllerInterface;
import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Commands.Command;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.ControllerInterface;
import it.polimi.ingsw.gc12.Listeners.Listener;
import it.polimi.ingsw.gc12.Network.SocketHandler;
import it.polimi.ingsw.gc12.Network.VirtualServer;

import java.io.IOException;
import java.net.Socket;

public class SocketServerHandler extends SocketHandler implements VirtualServer {

    public SocketServerHandler(Socket socket, ControllerInterface controller) throws IOException {
        super(socket, controller);
    }

    @Override
    public void requestToServer(ServerCommand command) {
        try {
            sendRequest(command);
        } catch (IOException e) {
            printError(e);
        }
    }

    @Override
    protected void executeReceivedCommand(Command receivedCommand) {
        ClientController.getInstance().commandExecutorsPool.submit(
                () -> ((ClientCommand) receivedCommand).execute((ClientControllerInterface) getController())
        );
    }

    @Override
    public void printError(Exception e){
        ClientController.getInstance().errorLogger.log(e);
    }

    @Override
    protected Listener createListener() {
        return null;
    }
}
