package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.VirtualServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

public class SocketClient implements VirtualServer {

    private static SocketClient SINGLETON_SOCKET_CLIENT = null;
    private static SocketServerHandler<?> serverHandler = null;

    private SocketClient() {
        //FIXME: how about the ip?
        try {
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open().bind(null);
            //TODO: when do we close this? in keepAlive not received?
            channel.connect(new InetSocketAddress("localhost", 5000)).get();

            //TODO: change size of array and write first message
            ByteBuffer byteBufferIn = ByteBuffer.wrap(new byte[65536]);
            serverHandler = new SocketServerHandler<>(channel, byteBufferIn);
            channel.read(byteBufferIn, null, serverHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //FIXME: ...reference escaping?
        ClientController.getInstance().serverConnection = this;
    }

    public static SocketClient getInstance() { //TODO: sincronizzazione (serve?) ed eventualmente lazy
        if (SINGLETON_SOCKET_CLIENT == null)
            SINGLETON_SOCKET_CLIENT = new SocketClient();
        return SINGLETON_SOCKET_CLIENT;
    }

    @Override
    public void requestToServer(VirtualClient caller, ServerCommand command) {
        serverHandler.requestToServer(caller, command);
    }
}
