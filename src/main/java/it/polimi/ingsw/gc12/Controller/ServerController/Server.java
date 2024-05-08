package it.polimi.ingsw.gc12.Controller.ServerController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server implements Runnable {

    private final static Server SINGLETON_SERVER = new Server();
    public final ExecutorService connectionExecutorsPool;
    public final ExecutorService commandExecutorsPool;

    private Server() {
        //TODO: check the correct types of queues
        connectionExecutorsPool = new ThreadPoolExecutor(100, 120, Integer.MAX_VALUE, TimeUnit.MINUTES, new SynchronousQueue<>());
        commandExecutorsPool = new ThreadPoolExecutor(100, 120, Integer.MAX_VALUE, TimeUnit.MINUTES, new SynchronousQueue<>());
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
                ServerSocket serverSocket = new ServerSocket();
                //ExecutorService executorsPool = Executors.newCachedThreadPool(/*thread factory here to change default
                //keepAlive timeout and set maximum number of Threads*/)
                //FIXME: how about the ip?
        ) {
            serverSocket.bind(new InetSocketAddress("localhost"/*"codexnaturalis.polimi.it"*/, 5000));
            System.out.println("[SOCKET]: Server listening on {" + serverSocket.getInetAddress() + "}");

            //TODO: choose behaviour (direct handoff vs unbounded vs bounded): currently chosen bounded
            //BlockingQueue<> clientsAwaitingConnection = new ArrayBlockingQueue<>(/*capacity here*/); // this is a bounded queue

            while (true) {
                Socket client = serverSocket.accept();

                System.out.println("[SOCKET]: New connection accepted from {" + client.getRemoteSocketAddress() + "}");
                connectionExecutorsPool.submit(
                        () -> {
                            SocketClientHandler clientHandler;
                            try {
                                clientHandler = new SocketClientHandler(client);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            while(true) {
                                clientHandler.completed();
                            }
                        }
                );

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Server getInstance() {
        return SINGLETON_SERVER;
    }
}
