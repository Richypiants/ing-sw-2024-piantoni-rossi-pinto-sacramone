package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Command;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.SocketHandler;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class SocketClientHandler<A> extends SocketHandler<A> implements VirtualClient {

    public SocketClientHandler(AsynchronousSocketChannel channel, ByteBuffer buffer) throws IOException {
        super(channel, buffer);
    }

    @Override
    public void requestToClient(ClientCommand command) {
        sendRequest(command);
    }

    @Override
    protected void executeReceivedCommand(Command receivedCommand) {
        try {
            System.out.println("[SOCKET][CLIENT]: Request from " + this);
            //TODO: make executors do this?
            ((ServerCommand) receivedCommand).execute(this, ServerController.getInstance());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
