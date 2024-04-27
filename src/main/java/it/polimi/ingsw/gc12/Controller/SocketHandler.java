package it.polimi.ingsw.gc12.Controller;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public abstract class SocketHandler<A> implements CompletionHandler<Integer, A> {

    private final AsynchronousSocketChannel channel;
    private final ByteBuffer inputBuffer;
    private ObjectInputStream objectInputStream = null;
    private final ObjectOutputStream objectOutputStream;
    private final ByteArrayOutputStream byteOutputStream;

    public SocketHandler(AsynchronousSocketChannel channel, ByteBuffer buffer) throws IOException {
        this.channel = channel;
        this.inputBuffer = buffer;
        //TODO: handle exceptions (in methods below too)
        this.byteOutputStream = new ByteArrayOutputStream();
        objectOutputStream = new ObjectOutputStream(byteOutputStream);
        //quando voglio scrivere:
    }

    //TODO: Handle Exceptions
    public Object readObject() throws IOException, ClassNotFoundException {
        if(objectInputStream == null){
            this.objectInputStream = new ObjectInputStream(new ByteArrayInputStream(inputBuffer.array()));
        }
        return objectInputStream.readObject();
    }

    //TODO: Handle Exceptions
    public synchronized ByteBuffer writeObject(Object obj) throws IOException {
        objectOutputStream.reset();
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        return ByteBuffer.wrap(byteOutputStream.toByteArray());
    }

    protected abstract void executeReceivedCommand(Command receivedCommand);

    @Override
    public void completed(Integer result, A attachment) {
        Command receivedCommand = null;
        try {
            //FIXME: add instanceof casting
            receivedCommand = (Command) readObject();
            //System.out.println("[SOCKET-HANDLER]: Received command " + receivedCommand.getClass() + " from {" + channel.getRemoteAddress() + "}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        //FIXME: e se non lo trova? exception... ma per createPlayer?
        // exceptions: noSuchMethod, InvalidParametersForMethod, NoPlayerFound(sendCreatePlayer),...

        inputBuffer.clear();
        channel.read(inputBuffer, attachment, this);
        executeReceivedCommand(receivedCommand);
    }

    @Override
    public void failed(Throwable exc, A attachment) {
        exc.printStackTrace();
    }

    protected synchronized void sendRequest(Command command) {
        try {
            channel.write(writeObject(command), null, new CompletionHandler<>() {
                //TODO: non serve... cancellare?
                @Override
                public void completed(Integer result, Object attachment) {
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    exc.printStackTrace();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
