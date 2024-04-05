package it.polimi.ingsw.gc12.Client.ClientController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

public class Client {

    public Client() {
        //TODO
    }

    public void run() {
        //Client server setup
        try (
                AsynchronousSocketChannel channel = AsynchronousSocketChannel.open().bind(null);
        ) {
            //FIXME: how about the ip?

            channel.connect(new InetSocketAddress("codexnaturalis.polimi.it", 5000)).get();

            try {
                //TODO: change size of array and write first message
                ByteBuffer byteBufferIn = ByteBuffer.wrap(new byte[0]);
                SocketServerHandler<AsynchronousSocketChannel, ByteBuffer> serverHandler =
                        new SocketServerHandler<>(channel, byteBufferIn);
                try { //FIXME: remove try/catch construct
                    ArrayList<Object> parameters = new ArrayList<>();
                    parameters.add("createPlayer");
                    //TODO: formattare il messaggio
                    channel.write(serverHandler.writeObject(parameters));
                    channel.read(byteBufferIn, channel, new SocketServerHandler<>(channel, byteBufferIn));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (RejectedExecutionException e) {
                //dire al client che il server è sovraccarico
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
