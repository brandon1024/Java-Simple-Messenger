package ca.brandonrichardson.messenger.server.core;

import ca.brandonrichardson.messenger.server.session.SessionPrototype;
import lombok.Getter;
import lombok.Setter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Getter
@Setter
public class Connection {

    private Socket socket;

    private ObjectInputStream inputStream;

    private ObjectOutputStream outputStream;

    private SessionPrototype session;

    public void reset() {
        this.socket = null;
        this.inputStream = null;
        this.outputStream = null;
        this.session = null;
    }
 }
