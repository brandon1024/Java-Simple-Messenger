package ca.brandonrichardson.messenger.common.keygen.strategy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@FunctionalInterface
public interface HashGeneratorStrategy extends GeneratorStrategy {

    String generate(final byte[] input) throws NoSuchAlgorithmException;

    static HashGeneratorStrategy SHA1HashStrategy() {
        return input -> {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(input);

            final StringBuilder builder = new StringBuilder();
            for(byte b : encodedHash) {
                builder.append(String.format("%02x", b));
            }

            return builder.toString();
        };
    }
}
