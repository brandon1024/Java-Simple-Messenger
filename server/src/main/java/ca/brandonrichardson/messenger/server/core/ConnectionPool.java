package ca.brandonrichardson.messenger.server.core;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
public class ConnectionPool {

    private Map<Connection, Long> freeConnections, usedConnections;

    private int maxPoolSize;

    private ConnectionPool() {
        this.maxPoolSize = 10;
        log.debug("Initializing connection pool with default size {}.", this.maxPoolSize);

        this.freeConnections = new HashMap<>(this.maxPoolSize);
        this.usedConnections = new HashMap<>(this.maxPoolSize);
    }

    public synchronized Connection acquireConnection() {
        if(this.isFull()) {
            throw new NoSuchElementException("Cannot acquire connection from pool; no available connections.");
        }

        if(this.usedConnections.size() < this.maxPoolSize && this.freeConnections.isEmpty()) {
            log.debug("No available connections in connection pool, but still below max pool size. Instantiating new connection.");
            Connection newConnection = new Connection();
            this.usedConnections.put(newConnection, System.currentTimeMillis());

            return newConnection;
        }

        log.debug("Acquiring connection from connection pool.");
        Map.Entry<Connection, Long> entry = this.freeConnections.entrySet().iterator().next();
        Connection connection = entry.getKey();
        this.freeConnections.remove(connection);
        this.usedConnections.put(connection, System.currentTimeMillis());

        return connection;
    }

    public synchronized void releaseConnection(final Connection connection) {
        if(!this.usedConnections.containsKey(connection)) {
            throw new IllegalArgumentException("Connection does not exist in used connection pool.");
        }

        log.debug("Releasing connection to pool.");
        this.usedConnections.remove(connection);
        this.freeConnections.put(connection, System.currentTimeMillis());
    }

    public void setMaxPoolSize(final int poolSize) {
        if(poolSize < 0) {
            throw new IllegalArgumentException("Cannot set pool size to a negative value.");
        }

        log.debug("Connection pool resized to {}", poolSize);
        this.maxPoolSize = poolSize;
    }

    public synchronized Collection<Connection> getActiveConnections() {
        return this.usedConnections.keySet();
    }

    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

    public boolean isFull() {
        return this.usedConnections.size() >= this.maxPoolSize;
    }

    public static ConnectionPool getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final ConnectionPool INSTANCE = new ConnectionPool();
    }
}
