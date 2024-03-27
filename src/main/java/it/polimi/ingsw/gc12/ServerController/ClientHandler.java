package it.polimi.ingsw.gc12.ServerController;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ClientHandler<V, A> implements CompletionHandler<V, A> {

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

    //TODO: questo è solo per leggere client e rispondere... manca l'handler per update! (se ci sarà...)
    @Override
    public void completed(V result, A attachment) {
        /*ArrayList<Object> receivedCommand = (ArrayList<Object>) readObject();

        Map<String, Command> commandsMap.get(receivedCommand.getFirst())
                .execute(receivedCommand.subList(1, receivedCommand.size()));

        Player player = function(clientSocket)...

        command.execute(Player player, ArrayList<Object> objects);

        //nel command se nickname ricevuto valido: put(this) in Map
        */
    }

    @Override
    public void failed(Throwable exc, A attachment) {

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
}
