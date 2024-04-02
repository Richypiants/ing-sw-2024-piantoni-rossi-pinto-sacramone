package it.polimi.ingsw.gc12.ServerController;

import it.polimi.ingsw.gc12.ServerModel.Player;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler<V, A> implements CompletionHandler<V, A> {

    private final static Map<AsynchronousSocketChannel, Player> socketsToPlayers = new HashMap<>();
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;
    private final ByteArrayOutputStream byteOutputStream;
    AsynchronousSocketChannel clientSocket;

    public ClientHandler(AsynchronousSocketChannel clientSocket, ByteBuffer buffer) throws IOException {
        //si scrive così?
        this.clientSocket = clientSocket;
        //TODO: handle exceptions (in methods below too)
        this.objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));

        this.byteOutputStream = new ByteArrayOutputStream();
        objectOutputStream = new ObjectOutputStream(byteOutputStream);
        //quando voglio scrivere:

    }

    public static void main(String[] args) throws Throwable {
        //TODO: trasformare in test che stampa tutti i nomi dei metodi nella mappa per verificare che si possano
        // chiamare tutti e soli i metodi validi

        System.out.println(Controller.commandHandles.keySet());
        //Controller.commandHandles.get("createHandles").invoke();

        /*final int[] pos = {0};

        System.out.println(currentState.getClass().getMethod(
                                "placeInitialCard",
                                parameters.stream()
                                        .map(Object::getClass)
                                        .collect(() -> new Class<?>[parameters.size()],
                                                (c1, c2) -> {
                                                    c1[pos[0]] = c2;
                                                    pos[0]++;
                                                },
                                                (c1, c2) -> System.out.println()
                                        )
                        )
                        .invoke(currentState, parameters.toArray())
        );*/
    }

    //TODO: Handle Exceptions
    private Object readObject() throws IOException, ClassNotFoundException {
        return objectInputStream.readObject();
    }

    //TODO: Handle Exceptions
    private ByteBuffer writeObject(Object obj) throws IOException {
        objectOutputStream.writeObject(obj);
        return ByteBuffer.wrap(byteOutputStream.toByteArray());
    }

    //TODO: questo è solo per leggere client e rispondere... manca l'handler per update! (se ci sarà...)
    @Override
    public void completed(V result, A attachment) {
        //TODO: clean input (or nickname only)???
        ArrayList<Object> receivedCommand = null;
        try {
            receivedCommand = (ArrayList<Object>) readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        receivedCommand.add(1, Controller.players.get(clientSocket));
        //FIXME: e se non lo trova? exception... ma per createPlayer?

        //TODO: riferimento al Game?
        try {
            Controller.commandHandles.get((String) receivedCommand.removeFirst())
                    .invokeWithArguments(receivedCommand);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        //nel command se nickname ricevuto valido: put(this) in Map
    }

    @Override
    public void failed(Throwable exc, A attachment) {

    }
}
