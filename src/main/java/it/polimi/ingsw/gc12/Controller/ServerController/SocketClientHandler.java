package it.polimi.ingsw.gc12.Controller.ServerController;

import it.polimi.ingsw.gc12.Controller.SocketHandler;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;

public class SocketClientHandler<A> extends SocketHandler<A> implements VirtualClient {

    public SocketClientHandler(AsynchronousSocketChannel channel, ByteBuffer buffer) throws IOException {
        super(channel, buffer);
    }

    @Override
    public void requestToServer(ArrayList<Object> objects) {
        sendRequest(objects);
    }

    @Override
    protected void invokeFromController(ArrayList<Object> receivedCommand) {
        try {
            //TODO: make executors do this?
            ServerController.commandHandles.get((String) receivedCommand.removeFirst())
                    .invokeWithArguments(receivedCommand);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
