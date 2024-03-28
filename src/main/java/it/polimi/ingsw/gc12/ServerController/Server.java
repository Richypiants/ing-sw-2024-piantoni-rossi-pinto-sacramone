package it.polimi.ingsw.gc12.ServerController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class Server {

    private final static Server SINGLETON_SERVER = new Server();

    private Server() {
        //TODO
    }

    public static Server getInstance() {
        return SINGLETON_SERVER;
    }

    public void run() {
        try (
                AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open()
                        .bind(new InetSocketAddress("codexnaturalis.polimi.it", 5000));
                ExecutorService executorsPool = Executors.newCachedThreadPool(/*thread factory here to change default keepAlive timeout
            and set maximum number of Threads*/)
        ) {
            //FIXME: how about the ip?

            //TODO: choose behaviour (direct handoff vs unbounded vs bounded): currently chosen bounded
            //BlockingQueue<> clientsAwaitingConnection = new ArrayBlockingQueue<>(/*capacity here*/); // this is a bounded queue

            //System.out.println(((InetSocketAddress)serverSocket.getLocalAddress()).getHostString());

            /* finchè non leggiamo fine: */
            while (true) {
                AsynchronousSocketChannel client = serverSocket.accept().get();

                try {
                    //add connection to queue
                    executorsPool.submit(
                            () -> {
                                //TODO: change size of array
                                ByteBuffer byteBufferIn = ByteBuffer.wrap(new byte[0]);
                                try { //FIXME: remove try/catch construct
                                    client.read(byteBufferIn, client, new ClientHandler<>(client, byteBufferIn));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
                } catch (RejectedExecutionException e) {
                    //dire al client che il server è sovraccarico
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        /*listener.accept(null, new CompletionHandler<AsynchronousSocketChannel,Void>() {
            public void completed(AsynchronousSocketChannel ch, Void att) {
                // accept the next connection
                listener.accept(null, this);

                // handle this connection
                handle(ch);
            }
            public void failed(Throwable exc, Void att) {
          ...
            }
        });*/

    }
}
