package ca.brandonrichardson.messenger.server.svc;

import ca.brandonrichardson.messenger.server.core.Connection;
import ca.brandonrichardson.messenger.server.core.ConnectionPool;
import ca.brandonrichardson.messenger.server.svc.request.AuthenticationFilterChain;
import ca.brandonrichardson.messenger.server.svc.request.RequestHandlerChain;
import ca.brandonrichardson.messenger.server.svc.request.SimpleRequestChain;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class ConnectionHandler implements Runnable {

    private Connection connection;

    public ConnectionHandler(final Connection connection) {
        this.connection = connection;
    }

    public void start() {
        (new Thread(this)).start();
    }

    @Override
    public void run() {
        try(Socket socket = this.connection.getSocket();
            ObjectOutputStream messageOut = this.connection.getOutputStream();
            ObjectInputStream messageIn = this.connection.getInputStream()) {
            log.info("Connection handler for {} initialized successfully.", this.connection.getSocket().getInetAddress().getHostAddress());

            //create filter chain
            SimpleRequestChain.of(
                    new AuthenticationFilterChain(),
                    new RequestHandlerChain()
            ).process(connection);

        } catch(EOFException e) {
            log.info("Client {} terminated session.", this.connection.getSession().getUsername());
        } catch (IOException e) {
            log.error("Connection handler thread terminating due to unexpected IOException.", e);
        } catch (ClassNotFoundException e) {
            log.error("Connection handler thread terminating due to unexpected ClassNotFoundException.", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("Connection handler thread terminating due to unexpected NoSuchAlgorithmException.", e);
        } finally {
            this.connection.reset();
            ConnectionPool.getInstance().releaseConnection(this.connection);
        }
    }
}
