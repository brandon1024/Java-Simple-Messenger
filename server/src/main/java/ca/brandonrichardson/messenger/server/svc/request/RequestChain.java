package ca.brandonrichardson.messenger.server.svc.request;

import ca.brandonrichardson.messenger.server.core.Connection;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface RequestChain {

    default void process(final Connection connection) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {}
}
