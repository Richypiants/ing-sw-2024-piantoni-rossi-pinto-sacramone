package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Command;
import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.SocketHandler;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.RejectedExecutionException;

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
        System.out.println("[SOCKET][CLIENT]: Request from " + this);
        try {
            Server.getInstance().commandExecutorsPool.submit(
                    () -> ((ServerCommand) receivedCommand).execute(this, ServerController.getInstance())
            );
        } catch (RejectedExecutionException e) {
            //TODO: implement answer
        }
    }

    @Override
    public void printError(Exception e){
        e.printStackTrace();
    }
}
