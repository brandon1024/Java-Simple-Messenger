package ca.brandonrichardson.messenger.server.svc.request;

import ca.brandonrichardson.messenger.server.core.Connection;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class SimpleRequestChain implements RequestChain {

    private RequestChainFilter[] filters;

    private SimpleRequestChain(final RequestChainFilter[] filters) {
        this.filters = filters;
    }

    @Override
    public void process(final Connection connection) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        for(int i = 0; i < filters.length - 1; i++) {
            filters[i].setNext(filters[i+1]);
        }

        if(filters.length > 0) {
            filters[0].process(connection);
        }
    }

    public static SimpleRequestChain of(final RequestChainFilter... filters) {
        return new SimpleRequestChain(filters);
    }
}
