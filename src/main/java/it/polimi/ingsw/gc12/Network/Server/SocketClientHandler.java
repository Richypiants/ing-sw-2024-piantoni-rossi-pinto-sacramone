package it.polimi.ingsw.gc12.Network.Server;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Commands.Command;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.ControllerInterface;
import it.polimi.ingsw.gc12.Controller.ServerControllerInterface;
import it.polimi.ingsw.gc12.Listeners.Listener;
import it.polimi.ingsw.gc12.Network.NetworkSession;
import it.polimi.ingsw.gc12.Network.SocketHandler;
import it.polimi.ingsw.gc12.Network.VirtualClient;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.RejectedExecutionException;

public class SocketClientHandler extends SocketHandler implements VirtualClient {

    public SocketClientHandler(Socket socket, ControllerInterface controller) throws IOException {
        super(socket, controller);
    }

    @Override
    public void requestToClient(ClientCommand command) throws IOException {
        sendRequest(command);
    }

    @Override
    protected void executeReceivedCommand(Command receivedCommand) {
        System.out.println("[SOCKET][CLIENT]: Request from " + this);
        try {
            Server.getInstance().commandExecutorsPool.submit(
                    () -> ((ServerCommand) receivedCommand).execute(
                            this, (ServerControllerInterface) getController()
                    )
            );
        } catch (RejectedExecutionException e) {
            //TODO: implement answer
        }
    }

    @Override
    public void printError(Exception e) {
        e.printStackTrace();
    }

    @Override
    protected Listener createListener(NetworkSession session) {
        return new Listener(this, this);
    }
}
