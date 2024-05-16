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

    private SocketClient() throws IOException {
        //FIXME: how about the ip?
        this.connectionExecutorsPool = Executors.newSingleThreadExecutor();

        Socket socket = new Socket(ClientController.getInstance().serverIPAddress, 5000);
        serverHandler = new SocketServerHandler(socket);

        connectionExecutorsPool.submit(
                () -> {
                    while (true) {
                        try {
                            serverHandler.read();
                        } catch (IOException e) {
                            close();
                            e.printStackTrace();
                            break;
                        }
                    }
                }
        );
        //FIXME: reference escaping?
        ClientController.getInstance().serverConnection = this;
    }

    public static SocketClient getInstance() { //TODO: sincronizzazione (serve?) ed eventualmente lazy
        if (SINGLETON_SOCKET_CLIENT == null) {
            try {
                SINGLETON_SOCKET_CLIENT = new SocketClient();
            } catch (IOException e) {
                ClientController.getInstance().errorLogger.log(e);
            }
        }
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
