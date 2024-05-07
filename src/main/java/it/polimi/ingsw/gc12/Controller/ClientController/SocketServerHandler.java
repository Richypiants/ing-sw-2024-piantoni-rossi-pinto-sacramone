package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Commands.Command;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.SocketHandler;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.VirtualServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class SocketServerHandler<A> extends SocketHandler<A> implements VirtualServer {

    public SocketServerHandler(AsynchronousSocketChannel channel, ByteBuffer buffer) throws IOException {
        super(channel, buffer);
    }

    @Override
    public void requestToServer(VirtualClient caller, ServerCommand command) {
        sendRequest(command);
    }

    @Override
    protected void executeReceivedCommand(Command receivedCommand) {
        SocketClient.getInstance().commandExecutorsPool.submit(
                () -> ((ClientCommand) receivedCommand).execute(ClientController.getInstance())
        );
    }

    @Override
    public void printError(Exception e){
        ClientController.getInstance().errorLogger.log(e);
    }
}
