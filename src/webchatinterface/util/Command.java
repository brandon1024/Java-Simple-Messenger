package webchatinterface.util;

import java.io.Serializable;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
 *<p>
 *Serializable Command Message object that is transmitted by TCP between client 
 *applications through a dedicated server interface.
 *<p>
 *A Command Message object contains an integer representing the command code, a 
 *String object representing an associated message, a timestamp, a sender, and
 *an identification number.
 *<p>
 *Commands are used by the client and server applications to describe specific actions
 *and handle special cases. For instance, commands are used for orderly connection
 *release, version requests, server messages, defining client usernames, and retrieving
 *list of users connected to the server.
 */

public class Command extends TransportEntity implements Serializable
{
	/**Serial Version UID is used as a version control for the class that implements 
	 *the serializable interface.*/
	private static final long serialVersionUID = 2490491320962374714L;

	/**Command code used by the client to request a connection from the server. A connection may
	  *only be granted once the client is verified and authenticated.*/
	public static final int CONNECTION_REQUEST = 1;
	
	/**Command code used by the server in response to a {@code CONNECTION_REQUEST} command,
	  *signaling that the client was verified and communication to the server was granted.*/
	public static final int CONNECTION_AUTHORIZED = 2;
	
	/**Command code used by the server in response to a {@code CONNECTION_REQUEST} command,
	  *signaling that the client was unable to be verified and was denied communication to the
	  *server.*/
	public static final int CONNECTION_DENIED = 3;
	
	/**Command code for orderly connectionr release. Once this command is received, 
	  *client or server application transmits a {@code SUSPEND_ACKNOWLEDGE} before 
	  *closing the data streams.*/
	public static final int CONNECTION_SUSPENDED = 4;
	
	/**Command code for awknowledging a {@code SUSPEND_CONNECTION} or {@code SUSPEND_KICKED} 
	  *command. Client or server application transmits a {@code SUSPEND_ACKNOWLEDGE} before 
	  *closing the data streams.*/
	public static final int CONNECTION_SUSPENDED_AWKNOWLEDGE = 5;
	
	/**Command code for retrieving the most recent client version from the server. Server 
	  *will respond with {@code CLIENT_VERSION} command, wherein the message body is a 
	  *String containing the most recent client version.*/
	public static final int CLIENT_VERSION_REQUEST = 6;
	
	/**Command code used by the server when responding to a {@code CLIENT_VERSION_REQUEST} command. 
	  *The message body of this command will contain the most recent client version.*/
	public static final int CLIENT_VERSION = 7;
	
	/**Command code used by the server to notify all clients to update the list of connected
	  *clients. The Object {@code MESSAGE} included with the Command is a two dimentional
	  *object array with all connected clients and their information.*/
	public static final int CONNECTED_USERS = 8;
	
	/**Command code used by the client to notify other clients that a message is being typed*/
	public static final int MESSAGE_TYPED = 9;
	
	/**Command code used by the client to define user availability to available*/
	public static final int CLIENT_AVAILABILITY_AVAILABLE = 10;
	
	/**Command code used by the client to define user availability to busy*/
	public static final int CLIENT_AVAILABILITY_BUSY = 11;
	
	/**Command code used by the client to define user availability to away*/
	public static final int CLIENT_AVAILABILITY_AWAY = 12;
	
	/**Command code used by the client to define user availability to appear offline*/
	public static final int CLIENT_AVAILABILITY_APPEAR_OFFLINE = 13;
	
	/**Command code used by the client to request a private chatroom with another client*/
	public static final int PRIVATE_CHATROOM_REQUEST = 14;
	
	/**Command code used by the client to deny a request to a private chatroom with another 
	  *client*/
	public static final int PRIVATE_CHATROOM_DENIED = 15;
	
	/**Command code used by the client to authorize a request to a private chatroom with 
	  *another client*/
	public static final int PRIVATE_CHATROOM_AUTHORIZED = 16;
	
	/**Command code used by the client to signal for the private chatroom to close*/
	public static final int PRIVATE_CHATROOM_EXIT = 17;
	
	/**Command code to signal a ping. Ping commands are simply forwarded back to the sender*/
	public static final int PING = 18;
	
	/**Command code to signal to server and all clients that a file transfer has begun*/
	public static final int FILE_TRANSFER = 19;
	
	/***/
	public static final int REASON_SERVER_FULL = 20;
	
	/***/
	public static final int REASON_KICKED = 21;
	
	/***/
	public static final int REASON_INCONSISTENT_USER_ID = 22;
	
	/***/
	public static final int REASON_BLACKLISTED = 23;
	
	/***/
	public static final int REASON_SERVER_CLOSED = 24;
	
	/***/
	public static final int REASON_ROOM_CLOSED = 25;
	
	/***/
	public static final int REASON_GENERIC = 26;
	
	/***/
	public static final int REASON_INCORRECT_CREDENTIALS = 27;
	
	/***/
	public static final int REASON_USERNAME_EMAIL_ALREADY_EXISTS = 28;
	
	/***/
	public static final int REASON_INCOMPATIBLE_CLIENT = 29;
	
	
	/**Unique local command identification number*/
	private final int COMMAND;
	
	/***/
	private final int REASON;
	
	/**A string containing a message that describes the command code.*/
	private final Object MESSAGE;
	
	/**Builds a new Command object with a given command and sender name. 
	  *Assigns a unique local message identification number to {@code ID} 
	  *and assigns the timestamp to {@code TIMESTAMP}.
	  *
	  *@param command the command represented by this Command object 
	  *@param sender the name of the client who created the command
	  *@param senderID the unique 256-bit alphanumeric key associated with each client*/
	public Command(int command, String sender, String senderID)
	{
		super(sender, senderID);
		this.COMMAND = command;
		this.REASON = Command.REASON_GENERIC;
		this.MESSAGE = null;
	}
	
	/***/
	public Command(int command, int reason, String sender, String senderID)
	{
		super(sender, senderID);
		this.COMMAND = command;
		this.REASON = reason;
		this.MESSAGE = null;
	}
	
	/**Builds a new Command object with a given command, descriptive message and 
	  *sender name. Assigns a unique local message identification number to {@code ID}
	  *and assigns the timestamp to {@code TIMESTAMP}.
	  *
	  *@param command the command represented by this Command object
	  *@param message a descriptive message, or a specific literal, to accompany the command
	  *@param sender the name of the client who created the command
	  *@param senderID the unique 256-bit alphanumeric key associated with each client*/
	public Command(int command, Object message, String sender, String senderID)
	{
		super(sender, senderID);
		this.COMMAND = command;
		this.REASON = Command.REASON_GENERIC;
		this.MESSAGE = message;
	}
	
	/**Builds and returns a new string containing the full command, including
	  *the command identification number, timestamp, sender, and command.
	  *<p>
	  *Format:
	  *<ul>
	  *<li>{@code [ID] YYYYMMDD_HHMMSS Sender > Command: [command] Reason: [reason]}
	  </ul>
	  *@return the full command, with command identification number, timestamp,
	  *sender and command*/
	public String getFullCommand()
	{
		return super.toString() + "Command: " + this.COMMAND + " Reason: " + this.REASON;
	}
	
	/**Accessor method for the {@code COMMAND} field of this Command object
	  *@return the command that this Command object represents*/
	public int getCommand()
	{
		return this.COMMAND;
	}
	
	/***/
	public int getReason()
	{
		return this.REASON;
	}
	
	/**Builds and returns a new string containing the full message, including
	  *the command identification number, timestamp, sender, and message.
	  *<p>
	  *Format:
	  *<ul>
	  *<li>{@code [ID] YYYY-MM-DDThh:mm:ss+00:00 Sender > MESSAGE.toString()...}
	  </ul>
	  *@return the full message, with message identification number, timestamp,
	  *sender and message*/
	public String getFullMessage()
	{
		return super.toString() + this.MESSAGE;
	}
	
	/**Accessor method for the {@code MESSAGE} field of this Command object
	  *@return the message packaged within the Command object*/
	public Object getMessage()
	{
		return this.MESSAGE;
	}
}