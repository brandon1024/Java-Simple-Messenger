package ca.brandonrichardson.messenger.client.core.connection;

import ca.brandonrichardson.messenger.client.session.ServerDetails;
import ca.brandonrichardson.messenger.client.session.Session;
import ca.brandonrichardson.messenger.common.dto.Authentication;
import ca.brandonrichardson.messenger.common.dto.TransportEntity;
import ca.brandonrichardson.messenger.common.dto.builder.TransportEntityBuilder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ConnectionListener implements Runnable, Observable {

    private List<Observer> observers;

    private Queue<TransportEntity> messageInputQueue;

    private Session session;

    private ServerDetails serverDetails;

    private ObjectOutputStream outputStream;

    private volatile boolean running, hasFailed;

    private ConnectionListener() {
        this.observers = new ArrayList<>();
        this.messageInputQueue = new LinkedList<>();
        this.serverDetails = null;
        this.session = null;
        this.running = false;
        this.hasFailed = false;
    }

    public void start(final Session session, final String ipAddress, final int portNumber) {
        if(!this.running) {
            this.session = session;
            this.serverDetails = new ServerDetails(ipAddress, portNumber);
            this.hasFailed = false;

            (new Thread(this)).start();
        }
    }

    public void suspend() {
        this.running = false;
    }

    @Override
    public void run() {
        this.running = true;

        try(Socket socket = new Socket(this.serverDetails.getServerAddress(), this.serverDetails.getServerPortNumber());
            ObjectOutputStream messageOut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream messageIn = new ObjectInputStream(socket.getInputStream())) {

            this.outputStream = messageOut;

            Authentication auth = TransportEntityBuilder.auth()
                    .setSessionKey(session.getSessionKey())
                    .setUsername(session.getUsername())
                    .build();

            messageOut.writeObject(auth);
            messageOut.flush();

            boolean unverified = true;
            while(this.running && unverified) {
                Object objectIn;
                if((objectIn = messageIn.readObject()) == null) {
                    continue;
                }

                if(objectIn instanceof Authentication) {
                    Authentication signedAuth = (Authentication)objectIn;
                    this.session.setUsername(signedAuth.getUsername());
                    this.session.setSessionKey(signedAuth.getSessionKey());

                    unverified = false;
                }
            }

            this.notifyObservers(true);

            while(this.running) {
                Object message;
                if((message = messageIn.readObject()) != null) {
                    this.messageInputQueue.add((TransportEntity) message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            this.hasFailed = true;

            this.notifyObservers(false);
        } finally {
            this.messageInputQueue = new LinkedList<>();
            this.serverDetails = null;
            this.session = null;
            this.running = false;
        }
    }

    public synchronized void send(final TransportEntity entity) throws IOException {
        if(this.hasFailed) {
            throw new RuntimeException("Thread has failed unexpectedly.");
        }

        entity.setSessionKey(this.session.getSessionKey());
        outputStream.writeObject(entity);
        outputStream.flush();
    }

    public synchronized List<TransportEntity> getMessages() {
        if(this.hasFailed) {
            throw new RuntimeException("Thread has failed unexpectedly.");
        }

        List<TransportEntity> newMessages = List.copyOf(messageInputQueue);
        messageInputQueue.clear();

        return newMessages;
    }

    @Override
    public void registerObserver(final Observer observer) {
        this.observers.add(observer);
    }

    @Override
    public void notifyObservers(final boolean error) {
        for(Observer observer : this.observers) {
            observer.update(error);
        }
    }

    public static ConnectionListener getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final ConnectionListener INSTANCE = new ConnectionListener();
    }
}
