package it.polimi.ingsw.gc12.Controller;

import it.polimi.ingsw.gc12.Controller.Commands.Command;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class SocketHandler<A> implements CompletionHandler<Integer, A> {

    private final AsynchronousSocketChannel channel;
    private final ByteBuffer inputBuffer;
    private ObjectInputStream objectInputStream = null;
    private final ObjectOutputStream objectOutputStream;
    private final ByteArrayOutputStream byteOutputStream;
    private final LinkedBlockingQueue<ByteBuffer> writeQueue;

    public SocketHandler(AsynchronousSocketChannel channel, ByteBuffer buffer) throws IOException {
        this.channel = channel;
        this.inputBuffer = buffer;
        //TODO: handle exceptions (in methods below too)
        this.byteOutputStream = new ByteArrayOutputStream();
        this.objectOutputStream = new ObjectOutputStream(byteOutputStream);
        this.writeQueue = new LinkedBlockingQueue<>();
    }

    //TODO: Handle Exceptions
    public Object readObject() throws IOException, ClassNotFoundException {
        if(objectInputStream == null){
            this.objectInputStream = new ObjectInputStream(new ByteArrayInputStream(inputBuffer.array()));
        }
        return objectInputStream.readObject();
    }

    //TODO: Handle Exceptions
    public ByteBuffer writeObject(Object obj) throws IOException {
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
            printError(e);
        } catch (ClassNotFoundException e) {
            printError(e);
        }

        //FIXME: e se non lo trova? exception... ma per createPlayer?
        // exceptions: noSuchMethod, InvalidParametersForMethod, NoPlayerFound(sendCreatePlayer),...

        inputBuffer.clear();

        //FIXME: aggiungere coda qui!!!!
        synchronized (this) {
            channel.read(inputBuffer, attachment, this);
            executeReceivedCommand(receivedCommand);
        }
    }

    @Override
    public void failed(Throwable exc, A attachment) {
        exc.printStackTrace();
    }

    protected void sendRequest(Command command) {
        try {
            ByteBuffer outputBuffer = writeObject(command);
            try {
                writeQueue.put(outputBuffer);
                if(writeQueue.size() > 1)
                    synchronized(outputBuffer) {
                        try {
                            outputBuffer.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            channel.write(outputBuffer, null, new CompletionHandler<>() {
                //TODO: non serve... cancellare?
                @Override
                public void completed(Integer result, Object attachment) {
                    try {
                        writeQueue.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if(!writeQueue.isEmpty()) {
                        ByteBuffer notifiedBuffer = writeQueue.peek();
                        synchronized(notifiedBuffer) {
                            notifiedBuffer.notify();
                        }
                    }
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    exc.printStackTrace();
                }
            });
        } catch (IOException e) {
            printError(e);
        }
    }

    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            printError(e);
        }
    }

    public abstract void printError(Exception e);
}
