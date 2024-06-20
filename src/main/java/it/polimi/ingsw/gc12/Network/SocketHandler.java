package it.polimi.ingsw.gc12.Network;

import it.polimi.ingsw.gc12.Commands.Command;
import it.polimi.ingsw.gc12.Controller.ControllerInterface;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@code SocketHandler} class is an abstract representation of a network session
 * utilizing socket-based communication in a client-server architecture.
 * <p>
 * This class extends {@link NetworkSession} and provides functionality to handle communication
 * with clients or servers over TCP sockets.
 * Implementations of this class are responsible for linking the executions of the received commands
 * and handling the management of logging errors.
 * </p>
 * <p>
 * Subclasses must implement the {@link #executeReceivedCommand(Command)} method to define
 * custom behavior for processing received commands.
 * The implementation of the {@link #printError(Exception)} method is subject to the desired behavior on client or server.
 * </p>
 *
 * @see NetworkSession for the base class providing additional functionality
 * @see Command for the structure of commands exchanged between client and server
 */
public abstract class SocketHandler extends NetworkSession {

    /**
     * The socket used for communication with the other actor.
     */
    private final Socket socket;

    /**
     * The input stream for receiving objects.
     */
    private final ObjectInputStream objectInputStream;

    /**
     * The output stream for sending objects.
     */
    private final ObjectOutputStream objectOutputStream;

    /**
     * The queue for outgoing commands to be written.
     */
    private final LinkedBlockingQueue<Command> writeQueue;

    /**
     * Constructs a new {@code SocketHandler} with the specified socket and controller.
     *
     * @param socket     The socket associated with the connection.
     * @param controller The controller interface for handling communication events.
     * @throws IOException if an I/O error occurs while creating the input and output streams.
     */
    public SocketHandler(Socket socket, ControllerInterface controller) throws IOException {
        super(controller);
        this.socket = socket;
        this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        this.objectInputStream = new ObjectInputStream(socket.getInputStream());

        this.writeQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Writes an object to the output stream.
     *
     * @param obj The object to be sent over the network.
     * @throws IOException if an I/O error occurs while writing the object.
     */
    public void writeObject(Object obj) throws IOException {
        //FIXME: rimuovere
        try {
            objectOutputStream.reset();
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
        } catch (NotSerializableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a command from the input stream and delegate its execution to the {@link #executeReceivedCommand(Command)} method.
     *
     * @throws IOException if an I/O error occurs while reading the command.
     */
    public void read() throws IOException {
        Command receivedCommand = null;
        try {
            receivedCommand = (Command) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            printError(e);
        }

        synchronized (this) {
            executeReceivedCommand(receivedCommand);
        }
    }

    /**
     * Sends a command to the client or server.
     * <p>
     * This method adds the command to the write queue, writes it to the output stream,
     * and manages synchronization with other write operations.
     * If the queue contains more than one command that has to be written,
     * the requester will wait and then be notified when the previous write action has been fulfilled.
     * </p>
     *
     * @param command The command to be sent.
     * @throws IOException if an I/O error occurs while writing the command.
     */
    protected void sendRequest(Command command) throws IOException {
        try {
            writeQueue.put(command);
            if (writeQueue.size() > 1)
                synchronized (command) {
                try {
                    command.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e); //Should never happen
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e); //Should never happen
        }

        writeObject(command);

        try {
            writeQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e); //Should never happen
        }
        if (!writeQueue.isEmpty()) {
            Command notifiedCommand = writeQueue.peek();
            synchronized (notifiedCommand) {
                notifiedCommand.notify();
            }
        }
    }

    /**
     * Executes a received command.
     * <p>
     * Subclasses must implement this method to define custom behavior for processing
     * commands received from the client or server.
     * </p>
     *
     * @param receivedCommand The command received from the client or server.
     */
    protected abstract void executeReceivedCommand(Command receivedCommand);

    /**
     * Handles the printing of error messages.
     * Subclasses must implement this method to define how error messages are handled or logged.
     *
     * @param e The exception representing the error.
     */
    public abstract void printError(Exception e);

    /**
     * Closes the socket and eventually releases any associated resources.
     */
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            printError(e);
        }
    }

}
