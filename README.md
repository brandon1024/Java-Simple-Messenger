# Java Simple Messenger
## An Important Note
This project was built for a university project. The project asked that I implement one architectural pattern and twelve (TWELVE!!) design patterns. The code is not pretty, but if you're learning how Java networking or multithreading works, or you need examples of design pattern implementations, you are welcome to have a peek.

This project is inspired by a project I had built in first year of university called Java-IRC. You can browse the old code by checking out the v1 branch. Trust me, though, you probably don't want to do that.

## Overview
For the final project for SWE4403, I implemented a messaging application. It allows two or more clients to communicate with each other over a network, all through a simple command line interface.

The project does not use any third party libraries (other than Lombok), implemented only with vanilla Java SE 10 features.

The client and server applications are both located within the project, and are executed independently. Details on how to run the project can be found below.

Some of the code here is really gross; this is expected, given that we were tasked with implementing so many design patterns. With that said, I tried to keep the code as neat and clean as I could.

### Here are some facts about the server:
The server is multithreaded. There is a main thread that acts as a listener for new new connections. When a new connection is received, a connection handler worker thread is spawned. This nature allows the server to handle more than one connection at a time.

Connections are taken from a ConnectionPool. If the connection pool is empty, no more connections can be accepted. This pool size can be increased if need be.

I used Slf4j and Logback for server logging, and used it pretty extensively to show what the server is doing and how it is handing the requests.

### Here are some facts about the client:
The client is also multithreaded. There are two threads, the main thread that handles interaction with the user, and a connection listener thread that handles communication with the server.

## Protocol
The protocol is relatively simple. Messages are communicated in the form of serialized objects. There are two types: `Authentication` and `Message`.

The `Authentication` message is used to establish a connection. When the client establishes a connection to the server, an `Authentication` message is created and sent to the server. It has information such as the username and session key.

When the server receives the `Authentication` object, it generates a new session key for the user (hash of the client session key and a server secret key), and sends this new `Authentication` object back to the client. The client must use the new session key from the server, or the session will be aborted. This is a simple form of message signing, and allows the server to verify the authenticity of the messages received.

Messages are sent using `Message` objects. These messages are sent to the server, and once validated for authenticity, is broadcasted to all users in the ConnectionPool.

Messages received by the client is placed into a message queue. Messages are only shown when the client uses the `read` command.

## How to Use
This project uses the Gradle build tool to build and run the applications.

To run the server:
```
./gradlew :server:run
```

Once the server is running, in a new terminal session, run the client:
```
./gradlew :client:run -q --console=plain
```

Once the server is started up, it will be available on port 5100. To connect to the server, simply run this command from the client:
```
login <your username> localhost 5100
```

Sending a message:
```
send Hi there this is a message!
```

Reading incoming messages:
```
read
```

Exit:
```
exit
```

## Project Description
### Architecture Implemented
As mentioned earlier, this project makes use of a client-server architecture. Both the client and server are standalone Java command-line applications.

### Design Patterns Implemented
#### Client Project
- Facade Pattern
    - `ca.brandonrichardson.messenger.client.core.MessengerClient` is a facade to the client. It simplifies the process of starting the application. It has a single public method `start()` which initializes the client and handles user input.
- Decorator Pattern
    - The client uses the decorator pattern to handle creating a user interface. The different decorators are used depending on whether the client is authenticated or unauthenticated.
    - `ca.brandonrichardson.messenger.client.ui.Interface` is the base decorator. It handles commands `exit` and `help`.
    - `ca.brandonrichardson.messenger.client.ui.LoginInterface` is the decorator for unauthenticated users. It handles a specific set of commands.
    - `ca.brandonrichardson.messenger.client.ui.AuthenticatedInterface` is the decorator for authenticated users. It also handles a specific set of commands.
    - `ca.brandonrichardson.messenger.client.ui.SimpleInterface` is the default decorator, and provides no additional functionality.
- Singleton Pattern
    - The `ca.brandonrichardson.messenger.client.core.connection.ConnectionListener` is a singleton. It is lazy-loaded using a private inner class.
    - The instance can be retrieved using the static method `getInstance()`.
- Observer Pattern
    - The client uses the Observer pattern to get notified when the ConnectionListener thread experiences some kind of error.
    - The `ca.brandonrichardson.messenger.client.core.MessengerClient` first registers an `ca.brandonrichardson.messenger.client.core.connection.Observer` with the `ca.brandonrichardson.messenger.client.core.connection.ConnectionListener`. If the ConnectionListener experiences an error, it will notify the MessengerClient through the Observable, and instruct the main thread to terminate.
- Iterator Pattern
    - Implemented a custom class that represents a list of possible commands for a given interface. This class is iterable, allowing it to be used within a for-each loop.
    - `ca.brandonrichardson.messenger.client.ui.parseopt.UsageDescriptionList` is the iterable, and `ca.brandonrichardson.messenger.client.ui.parseopt.UsageDescriptionIterator` is the iterator.

#### Common Project
- Builder Pattern
    - The builder pattern is employed to build Message objects. The `ca.brandonrichardson.messenger.common.dto.builder.TransportEntityBuilder` class is used to construct either a `ca.brandonrichardson.messenger.common.dto.builder.AuthenticationEntityBuilder` or a `ca.brandonrichardson.messenger.common.dto.builder.MessageEntityBuilder`.
- Strategy Pattern
    - The `ca.brandonrichardson.messenger.client.svc.keygen.KeyGenerator` uses the strategy pattern to allow the caller to chose which strategy they wish to use to generate the key.
    - There are two classes of generators implemented: RandomGeneratorStrategy which simply generates random strings, and HashGeneratorStrategy which generates a cryptographic hash of a string or array of bytes.
    - In `ca.brandonrichardson.messenger.client.core.MessengerClient`, you can see an example of its usage. In this case, the caller first generates a key of length 16, then passes that into the sha1 hash generator.
    - All strategies inherit `ca.brandonrichardson.messenger.client.svc.keygen.strategy.GeneratorStrategy`, so any strategy can be referenced as a `GeneratorStrategy`.
- Adapter Pattern
    - `ca.brandonrichardson.messenger.common.keygen.strategy.HashGeneratorStrategy` is an adapter for the Java MessageDigest API.
    - It is used to convert a hashed byte array from a byte[] supplier or a string back to a String. The MessageDigest API returns a byte array, hence why this adapter is useful.
    - This class is used by the server to generate a SHA256 hash from a client username and server secret key, and formats it in the appropriate format (default format is byte[], need hex String).

#### Server Project
- Prototype Pattern
    - `ca.brandonrichardson.messenger.server.session.SessionPrototype` is a prototype. It is used by `ca.brandonrichardson.messenger.server.core.MessengerServer` to create a new instance of an empty session using the method `clone()`.
- Object Pool Pattern
    - `ca.brandonrichardson.messenger.server.core.ConnectionPool` is an object pool. It is used by the `ca.brandonrichardson.messenger.server.core.MessengerServer` to retrieve connections. Using this pattern allowed the server to reject incoming connections when the connection pool is empty.
    - The pool is also used by the `ca.brandonrichardson.messenger.server.svc.ConnectionHandler` to broadcast messages to all connected clients.
    - The pool can be resized using the method `setMaxPoolSize`.
- Chain of Responsibility Pattern
    - The chain of responsibility pattern is used to process requests as a chain. There are only two filters in the chain: `ca.brandonrichardson.messenger.server.svc.request.AuthenticationFilter` and `ca.brandonrichardson.messenger.server.svc.request.RequestHandlerFilter`.
    - The `ca.brandonrichardson.messenger.server.svc.request.AuthenticationFilter` is used to authenticate the client. It essentially performs any handshaking between the client and server to establish the session.
    - The `ca.brandonrichardson.messenger.server.svc.request.RequestHandlerFilter` handles receiving messages from the client once the session is valid.
    - New filters can be easily added to the chain by extending `ca.brandonrichardson.messenger.server.svc.request.RequestChainFilter` and implementing the `process()` method. New filters must be added to `ca.brandonrichardson.messenger.server.svc.ConnectionHandler`.
- Pipes and Filters Pattern
    - When I was implementing the Chain of Responsibility pattern for the ConnectionHandler, I was trying to imitate what was implemented by the Spring framework. In doing so, I also implemented a Pipe and Filter pattern.
    - In each filter (`ca.brandonrichardson.messenger.server.svc.request.AuthenticationFilter` and `ca.brandonrichardson.messenger.server.svc.request.RequestHandlerFilter`), the input to one filter is output into the next filter in the chain. So, the filter is able to modify the input in the chain and pass it to the next one.