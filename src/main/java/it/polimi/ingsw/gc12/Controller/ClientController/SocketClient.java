package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.VirtualServer;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketClient implements VirtualServer {

    private static SocketClient SINGLETON_SOCKET_CLIENT = null;
    private static SocketServerHandler serverHandler = null;
    public final ExecutorService connectionExecutorsPool;
    public final ExecutorService commandExecutorsPool;

    private SocketClient() {
        //FIXME: how about the ip?
        this.connectionExecutorsPool = Executors.newSingleThreadExecutor();
        this.commandExecutorsPool = Executors.newSingleThreadExecutor();

        try{
            Socket socket = new Socket(ClientController.getInstance().serverIPAddress, 5000);
            serverHandler = new SocketServerHandler(socket);

            connectionExecutorsPool.submit(
                    () -> {
                        while(true) {
                            serverHandler.completed();
                        }
                    }
            );
        } catch (IOException e) {
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
