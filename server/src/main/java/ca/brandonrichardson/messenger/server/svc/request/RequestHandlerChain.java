package ca.brandonrichardson.messenger.server.svc.request;

import ca.brandonrichardson.messenger.common.dto.Message;
import ca.brandonrichardson.messenger.server.core.Connection;
import ca.brandonrichardson.messenger.server.core.ConnectionPool;
import ca.brandonrichardson.messenger.server.infra.RuntimeConstants;
import ca.brandonrichardson.messenger.server.svc.SessionKeyGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class RequestHandlerChain extends RequestChainFilter {

    @Override
    public void process(final Connection connection, final RequestChain next) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        ObjectInputStream messageIn = connection.getInputStream();

        while (true) {
            Object objIn = messageIn.readObject();

            if (objIn instanceof Message) {
                log.info("Message received from {}", connection.getSession().getUsername());

                Message message = (Message) objIn;
                if (!SessionKeyGenerator.isValidSessionKey(connection.getSession().getUsername(), RuntimeConstants.secret, message.getSessionKey())) {
                    log.info("Invalid session key for user {}; dropping connection.", connection.getSession().getUsername());
                    break;
                }

                message.setSessionKey(null);
                log.info("Broadcasting message from {}.", connection.getSession().getUsername());

                ConnectionPool.getInstance().getActiveConnections().parallelStream().forEach((c) -> {
                    try {
                        c.getOutputStream().writeObject(message);
                        log.trace("Message broadcasted to {}.", c.getSession().getUsername());
                    } catch (IOException e) {
                        log.warn("Unable to broadcast message to {}.", c.getSession().getUsername());
                    }
                });
            } else {
                log.warn("Unexpected message received from {}.", connection.getSession().getUsername());
            }
        }
    }
}
