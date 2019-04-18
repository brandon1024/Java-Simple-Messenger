package ca.brandonrichardson.messenger.server.core;

import ca.brandonrichardson.messenger.server.infra.RuntimeConstants;
import ca.brandonrichardson.messenger.server.session.SessionPrototype;
import ca.brandonrichardson.messenger.server.svc.ConnectionHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class MessengerServer {

    public void start() {
        log.info("Starting server on port {}.", RuntimeConstants.portNumber);

        SessionPrototype emptySession = new SessionPrototype(null, null);
        try(ServerSocket serverSocket = new ServerSocket(RuntimeConstants.portNumber)) {
            log.info("Server started successfully.");

            while(true) {
                Socket socket = serverSocket.accept();
                log.info("Connection received from {}.", socket.getInetAddress().getHostAddress());

                if(ConnectionPool.getInstance().isFull()) {
                    socket.close();
                    log.warn("Connection dropped due to a full connection pool.");
                }

                Connection connection = ConnectionPool.getInstance().acquireConnection();
                connection.setSocket(socket);
                connection.setInputStream(new ObjectInputStream(socket.getInputStream()));
                connection.setOutputStream(new ObjectOutputStream(socket.getOutputStream()));
                connection.setSession(emptySession.clone());

                log.info("Successfully established connection to {}; initializing connection handler thread.", socket.getInetAddress().getHostAddress());

                new ConnectionHandler(connection).start();
            }
        } catch (IOException e) {
            log.error("Unrecoverable IOException throw in MessengerServer.", e);
            System.exit(1);
        }
    }
}
