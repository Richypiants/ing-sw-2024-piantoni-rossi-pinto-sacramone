package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Controller.SocketHandler;
import it.polimi.ingsw.gc12.Utilities.VirtualServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;

public class SocketServerHandler<A> extends SocketHandler<A> implements VirtualServer {

    public SocketServerHandler(AsynchronousSocketChannel channel, ByteBuffer buffer) throws IOException {
        super(channel, buffer);
    }

    @Override
    public void requestToClient(ArrayList<Object> objects) {
        sendRequest(objects);
    }

    @Override
    protected void invokeFromController(ArrayList<Object> receivedCommand) {
        try {
            ClientController.commandHandles.get((String) receivedCommand.removeFirst())
                    .invokeWithArguments(receivedCommand);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
