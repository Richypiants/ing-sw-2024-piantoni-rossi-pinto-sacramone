package it.polimi.ingsw.gc12.Network.Server;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Commands.Command;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerController;
import it.polimi.ingsw.gc12.Network.SocketHandler;
import it.polimi.ingsw.gc12.Network.VirtualClient;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.RejectedExecutionException;

public class SocketClientHandler extends SocketHandler implements VirtualClient {

    public SocketClientHandler(Socket socket) throws IOException {
        super(socket);
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
                            this, ServerController.getAssociatedController(ServerController.players.get(this))
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
}
