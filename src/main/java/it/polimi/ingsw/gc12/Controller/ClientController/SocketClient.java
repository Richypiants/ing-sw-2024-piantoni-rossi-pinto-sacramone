package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Utilities.VirtualServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SocketClient implements VirtualServer {

    private static SocketClient SINGLETON_SOCKET_CLIENT = null;
    private static SocketServerHandler<?> serverHandler = null;

    private SocketClient() {
        //FIXME: how about the ip?
        try (AsynchronousSocketChannel channel = AsynchronousSocketChannel.open().bind(null)) {
            channel.connect(new InetSocketAddress("codexnaturalis.polimi.it", 5000)).get();

            //TODO: change size of array and write first message
            ByteBuffer byteBufferIn = ByteBuffer.wrap(new byte[0]);
            serverHandler = new SocketServerHandler<>(channel, byteBufferIn);
            ArrayList<Object> parameters = new ArrayList<>();
            parameters.add("createPlayer");

            //TODO: formattare il messaggio
            channel.write(serverHandler.writeObject(parameters));
            channel.read(byteBufferIn, null, serverHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static SocketClient getInstance() { //TODO: sincronizzazione (serve?) ed eventualmente lazy
        if (SINGLETON_SOCKET_CLIENT == null)
            SINGLETON_SOCKET_CLIENT = new SocketClient();
        return SINGLETON_SOCKET_CLIENT;
    }

    @Override
    public void requestToClient(ArrayList<Object> arguments) {
        serverHandler.requestToClient(arguments);
    }
}
