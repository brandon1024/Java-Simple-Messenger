package ca.brandonrichardson.messenger.client;

import ca.brandonrichardson.messenger.client.core.MessengerClient;

public class MessengerApplication {

    public static void main(String[] args) {
        MessengerClient client = new MessengerClient();
        client.start();
    }
}
