package it.polimi.ingsw.gc12.Controller.ServerController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.*;

public class Server implements Runnable {

    private final static Server SINGLETON_SERVER = new Server();
    public final ExecutorService commandExecutorsPool;

    private Server() {
        commandExecutorsPool = new ThreadPoolExecutor(100, 120, Integer.MAX_VALUE, TimeUnit.MINUTES,
                //TODO: check that it's the correct type of queue
                new SynchronousQueue<>()
        );
    }

    public void run() {
        //RMI server setup
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(5001);
            registry.rebind("codex_naturalis_rmi", RMIServerStub.getInstance());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        System.out.println("[RMI]: Server listening on {" + registry + "}");

        //Socket server setup
        try (
                AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open()
                        .bind(new InetSocketAddress("localhost"/*"codexnaturalis.polimi.it"*/, 5000));
                //ExecutorService executorsPool = Executors.newCachedThreadPool(/*thread factory here to change default
                //keepAlive timeout and set maximum number of Threads*/)
                //FIXME: how about the ip?
        ) {
            System.out.println("[SOCKET]: Server listening on {" + serverSocket.getLocalAddress() + "}");

            //TODO: choose behaviour (direct handoff vs unbounded vs bounded): currently chosen bounded
            //BlockingQueue<> clientsAwaitingConnection = new ArrayBlockingQueue<>(/*capacity here*/); // this is a bounded queue

            while (true) {
                AsynchronousSocketChannel channel = serverSocket.accept().get();

                System.out.println("[SOCKET]: New connection accepted from {" + channel.getRemoteAddress() + "}");

                try {
                    //add connection to queue
                    //executorsPool.submit(
                    //        () -> {
                    //TODO: change size of array
                    ByteBuffer byteBufferIn = ByteBuffer.wrap(new byte[65536]);
                    try { //FIXME: remove try/catch construct
                        channel.read(byteBufferIn, null,
                                new SocketClientHandler<>(channel, byteBufferIn)
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //        }
                    //);
                } catch (RejectedExecutionException e) {
                    //dire al client che il server è sovraccarico
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            // Main interrupts this thread when closing down server
            // serverSocket channel is autoclosed because of the try-with-resources statement
            throw new RuntimeException(e);
        }
    }

    public static Server getInstance() {
        return SINGLETON_SERVER;
    }
}
