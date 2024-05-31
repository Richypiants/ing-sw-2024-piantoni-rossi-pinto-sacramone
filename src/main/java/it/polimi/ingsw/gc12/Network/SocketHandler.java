package it.polimi.ingsw.gc12.Network;

import it.polimi.ingsw.gc12.Controller.Commands.Command;
import it.polimi.ingsw.gc12.Controller.ControllerInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class SocketHandler extends NetworkSession {

    private final Socket socket;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;
    private final LinkedBlockingQueue<Command> writeQueue;

    public SocketHandler(Socket socket, ControllerInterface controller) throws IOException {
        super(controller);
        this.socket = socket;
        this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        this.objectInputStream = new ObjectInputStream(socket.getInputStream());

        this.writeQueue = new LinkedBlockingQueue<>();
    }

    //TODO: Handle Exceptions
    public void writeObject(Object obj) throws IOException {
        objectOutputStream.reset();
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
    }

    protected abstract void executeReceivedCommand(Command receivedCommand);

    public void read() throws IOException {
        Command receivedCommand = null;
        try {
            //FIXME: add instanceof casting
            receivedCommand = (Command) objectInputStream.readObject();
            //System.out.println("[SOCKET-HANDLER]: Received command " + receivedCommand.getClass() + " from {" + channel.getRemoteAddress() + "}");
        } catch (ClassNotFoundException e) {
            printError(e);
        }

        //FIXME: aggiungere coda qui!!!!
        synchronized (this) {
            executeReceivedCommand(receivedCommand);
        }
    }

    protected void sendRequest(Command command) throws IOException {
        try {
            writeQueue.put(command);
            if (writeQueue.size() > 1)
                synchronized (command) {
                try {
                    command.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        writeObject(command);

        try {
            writeQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!writeQueue.isEmpty()) {
            Command notifiedCommand = writeQueue.peek();
            synchronized (notifiedCommand) {
                notifiedCommand.notify();
            }
        }
    }

    public abstract void printError(Exception e);

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            printError(e);
        }
    }
}
