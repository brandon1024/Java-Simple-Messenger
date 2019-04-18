package ca.brandonrichardson.messenger.server.svc.request;

import ca.brandonrichardson.messenger.common.dto.Authentication;
import ca.brandonrichardson.messenger.server.core.Connection;
import ca.brandonrichardson.messenger.server.infra.RuntimeConstants;
import ca.brandonrichardson.messenger.server.svc.SessionKeyGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class AuthenticationFilterChain extends RequestChainFilter {

    @Override
    public void process(final Connection connection, final RequestChain next) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        ObjectOutputStream messageOut = connection.getOutputStream();
        ObjectInputStream messageIn = connection.getInputStream();

        log.info("Processing authentication for {}.", connection.getSocket().getInetAddress().getHostAddress());

        int count = 0;
        while (true) {
            Object objIn = messageIn.readObject();
            if (!(objIn instanceof Authentication)) {
                if(count++ == 10) {
                    log.warn("Authentication aborted for {}; exceeded number of attempts.", connection.getSocket().getInetAddress().getHostAddress());
                    return;
                }

                continue;
            }

            Authentication auth = (Authentication) objIn;
            String sessionKey = SessionKeyGenerator.generateSessionKey(auth.getUsername(), RuntimeConstants.secret);

            connection.getSession().setUsername(auth.getUsername());
            connection.getSession().setSessionKey(sessionKey);
            auth.setSessionKey(connection.getSession().getSessionKey());

            log.info("Successfully authenticated user {} {}", auth.getUsername(), String.format("%8.8s...", auth.getSessionKey()));

            messageOut.writeObject(auth);
            messageOut.flush();
            break;
        }

        next.process(connection);
    }
}
