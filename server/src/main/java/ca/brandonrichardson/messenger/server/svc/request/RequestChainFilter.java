package ca.brandonrichardson.messenger.server.svc.request;

import ca.brandonrichardson.messenger.server.core.Connection;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public abstract class RequestChainFilter implements RequestChain {

    private RequestChainFilter next;

    public RequestChainFilter() {
        this.next = null;
    }

    @Override
    public void process(final Connection connection) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        this.process(connection, next);
    }

    public abstract void process(final Connection connection, final RequestChain next) throws NoSuchAlgorithmException, IOException, ClassNotFoundException;

    public void setNext(final RequestChainFilter filter) {
        this.next = filter;
    }
}
