package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Controller.Commands.ClientCommands.ClientCommand;
import it.polimi.ingsw.gc12.Controller.Commands.Command;
import it.polimi.ingsw.gc12.Controller.Commands.ServerCommands.ServerCommand;
import it.polimi.ingsw.gc12.Controller.SocketHandler;
import it.polimi.ingsw.gc12.Utilities.VirtualClient;
import it.polimi.ingsw.gc12.Utilities.VirtualServer;

import java.io.IOException;
import java.net.Socket;

public class SocketServerHandler extends SocketHandler implements VirtualServer {

    public SocketServerHandler(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public void requestToServer(VirtualClient caller, ServerCommand command) {
        try {
            sendRequest(command);
        } catch (IOException e) {
            printError(e);
        }
    }

    @Override
    protected void executeReceivedCommand(Command receivedCommand) {
        ClientController.getInstance().commandExecutorsPool.submit(
                () -> ((ClientCommand) receivedCommand).execute(ClientController.getInstance())
        );
    }

    @Override
    public void printError(Exception e){
        ClientController.getInstance().errorLogger.log(e);
    }
}
