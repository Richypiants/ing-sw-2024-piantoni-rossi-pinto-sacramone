package it.polimi.ingsw.gc12.Controller;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;

public abstract class SocketHandler<A> implements CompletionHandler<Integer, A> {

    private final AsynchronousSocketChannel channel;
    private final ByteBuffer inputBuffer;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;
    private final ByteArrayOutputStream byteOutputStream;

    public SocketHandler(AsynchronousSocketChannel channel, ByteBuffer buffer) throws IOException {
        this.channel = channel;
        this.inputBuffer = buffer;
        //TODO: handle exceptions (in methods below too)
        this.objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));

        this.byteOutputStream = new ByteArrayOutputStream();
        objectOutputStream = new ObjectOutputStream(byteOutputStream);
        //quando voglio scrivere:

    }

    //TODO: Handle Exceptions
    public Object readObject() throws IOException, ClassNotFoundException {
        return objectInputStream.readObject();
    }

    //TODO: Handle Exceptions
    public ByteBuffer writeObject(Object obj) throws IOException {
        objectOutputStream.writeObject(obj);
        return ByteBuffer.wrap(byteOutputStream.toByteArray());
    }

    protected abstract void invokeFromController(ArrayList<Object> receivedCommand);

    @Override
    public void completed(Integer result, A attachment) {
        //TODO: clean input (or nickname only)???
        ArrayList<Object> receivedCommand = null;
        try {
            //FIXME: add instanceof casting
            receivedCommand = (ArrayList<Object>) readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        receivedCommand.add(1, this);
        //FIXME: e se non lo trova? exception... ma per createPlayer?
        // exceptions: noSuchMethod, InvalidParametersForMethod, NoPlayerFound(sendCreatePlayer),...

        channel.read(inputBuffer, attachment, this);
        invokeFromController(receivedCommand);
    }

    @Override
    public void failed(Throwable exc, A attachment) {

    }

    protected void sendRequest(ArrayList<Object> objects) {
        try {
            channel.write(writeObject(objects));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
