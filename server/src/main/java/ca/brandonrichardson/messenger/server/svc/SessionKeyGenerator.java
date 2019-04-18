package ca.brandonrichardson.messenger.server.svc;

import ca.brandonrichardson.messenger.common.keygen.KeyGenerator;
import ca.brandonrichardson.messenger.common.keygen.strategy.HashGeneratorStrategy;

import java.security.NoSuchAlgorithmException;

public class SessionKeyGenerator {

    public static String generateSessionKey(final String username, final String salt) throws NoSuchAlgorithmException {
        return KeyGenerator.generateKey(username + salt, HashGeneratorStrategy.SHA1HashStrategy());
    }

    public static boolean isValidSessionKey(final String username, final String salt, final String sessionKey) throws NoSuchAlgorithmException {
        String expected = SessionKeyGenerator.generateSessionKey(username, salt);
        return expected.equals(sessionKey);
    }
}
