package ca.brandonrichardson.messenger.server;

import ca.brandonrichardson.messenger.server.core.MessengerServer;

public class MessengerServerApplication {

    public static void main(String[] args) {
        MessengerServer server = new MessengerServer();
        server.start();
    }
}
