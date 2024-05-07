package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.VirtualServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketClient implements VirtualServer {

    private static SocketClient SINGLETON_SOCKET_CLIENT = null;
    private static SocketServerHandler<?> serverHandler = null;
    public final ExecutorService commandExecutorsPool;

    private SocketClient() {
        //FIXME: how about the ip?
        this.commandExecutorsPool = Executors.newSingleThreadExecutor();

        try {
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open().bind(null);
            //TODO: when do we close this? in keepAlive not received?
            channel.connect(new InetSocketAddress(ClientController.getInstance().serverIPAddress, 5000)).get();

            //TODO: change size of array and write first message
            ByteBuffer byteBufferIn = ByteBuffer.wrap(new byte[65536]);
            serverHandler = new SocketServerHandler<>(channel, byteBufferIn);
            channel.read(byteBufferIn, null, serverHandler);
        } catch (IOException e) {
            ClientController.getInstance().errorLogger.log(e);
        } catch (ExecutionException e) {
            ClientController.getInstance().errorLogger.log(e);
        } catch (InterruptedException e) {
            ClientController.getInstance().errorLogger.log(e);
        }

        //FIXME: ...reference escaping?
        ClientController.getInstance().serverConnection = this;
    }

    public static SocketClient getInstance() { //TODO: sincronizzazione (serve?) ed eventualmente lazy
        if (SINGLETON_SOCKET_CLIENT == null)
            SINGLETON_SOCKET_CLIENT = new SocketClient();
        return SINGLETON_SOCKET_CLIENT;
    }

    public void close() {
        SINGLETON_SOCKET_CLIENT = null;
        serverHandler.close();
    }

    @Override
    public void requestToServer(VirtualClient caller, ServerCommand command) {
        serverHandler.requestToServer(caller, command);
    }
}
