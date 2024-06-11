package it.polimi.ingsw.gc12.Network.Server;

import it.polimi.ingsw.gc12.Controller.ServerController.ConnectionController;
import it.polimi.ingsw.gc12.Network.RMIMainServer;
import it.polimi.ingsw.gc12.Network.RMIVirtualClient;
import it.polimi.ingsw.gc12.Network.RMIVirtualServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The {@code Server} class represents a server in a client-server communication setup.
 * It implements {@link Runnable} and {@link RMIMainServer}.
 */
public class Server implements Runnable, RMIMainServer {

    /**
     * The singleton instance of the server.
     */
    private final static Server SINGLETON_SERVER = new Server();

    /**
     * The executor service for handling new client connections.
     */
    public final ExecutorService connectionExecutorsPool;

    /**
     * The executor service for handling all the received commands from the clients.
     */
    public final ExecutorService commandExecutorsPool;

    /**
     * The IP address of the server.
     */
    private String serverIPAddress = "localhost";

    /**
     * Constructs a new Server instance, initializing the executors pools and asking for the server IP address.
     */
    private Server() {
        connectionExecutorsPool = new ThreadPoolExecutor(100, 120, Integer.MAX_VALUE, TimeUnit.MINUTES, new SynchronousQueue<>());
        commandExecutorsPool = new ThreadPoolExecutor(100, 120, Integer.MAX_VALUE, TimeUnit.MINUTES, new SynchronousQueue<>());
        System.out.println("Inserisci l'indirizzo IP del server (leave empty for 'localhost'): ");
        serverIPAddress = System.console().readLine();
    }

    /**
     * Starts the server, initializing RMI and socket servers.
     */
    public void run() {
        //RMI server setup
        System.setProperty("java.rmi.server.hostname", serverIPAddress);
        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(5001);
            registry.rebind("codex_naturalis_rmi", this);
            UnicastRemoteObject.exportObject(this, 5001);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        System.out.println("[RMI]: Server listening on {" + registry + "}");

        //Socket server setup
        try (
                ServerSocket serverSocket = new ServerSocket();
        ) {
            serverSocket.bind(new InetSocketAddress(serverIPAddress, 5000));
            System.out.println("[SOCKET]: Server listening on {" + serverSocket.getInetAddress() + "}");

            while (true) {
                Socket client = serverSocket.accept();

                System.out.println("[SOCKET]: New connection accepted from {" + client.getRemoteSocketAddress() + "}");
                connectionExecutorsPool.submit(
                        () -> {
                            SocketClientHandler clientHandler;
                            try {
                                clientHandler = new SocketClientHandler(client, ConnectionController.getInstance());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            while(true) {
                                try {
                                    clientHandler.read();
                                } catch (IOException e) {
                                    clientHandler.close();
                                    e.printStackTrace();
                                    break;
                                }
                            }
                        }
                );

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the singleton instance of the server.
     *
     * @return The singleton instance of the server.
     */
    public static Server getInstance() {
        return SINGLETON_SERVER;
    }

    /**
     * Accepts a connection from an RMI virtual client and returns a stub for future communications between the client-server.
     *
     * @param client The RMI virtual client requesting the connection.
     * @return A stub for the RMI server.
     */
    @Override
    public RMIVirtualServer accept(RMIVirtualClient client) {
        return new RMIServerStub(client, ConnectionController.getInstance());
    }
}
