package ca.brandonrichardson.messenger.client.core.connection;

public interface Observable {

    void registerObserver(Observer observer);

    void notifyObservers(boolean error);
}
