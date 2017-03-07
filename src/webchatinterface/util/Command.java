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

	public static final int CONNECTION_REQUEST = 1;
	public static final int CONNECTION_AUTHORIZED = 2;
	public static final int CONNECTION_DENIED = 3;
	public static final int CONNECTION_SUSPENDED = 4;
	public static final int CONNECTION_SUSPENDED_ACKNOWLEDGE = 5;
	public static final int CLIENT_VERSION_REQUEST = 6;
	public static final int CLIENT_VERSION = 7;
	public static final int CONNECTED_USERS = 8;
	public static final int MESSAGE_TYPED = 9;
	public static final int CLIENT_AVAILABILITY_AVAILABLE = 10;
	public static final int CLIENT_AVAILABILITY_BUSY = 11;
	public static final int CLIENT_AVAILABILITY_AWAY = 12;
	public static final int CLIENT_AVAILABILITY_APPEAR_OFFLINE = 13;
	public static final int PRIVATE_CHATROOM_REQUEST = 14;
	public static final int PRIVATE_CHATROOM_DENIED = 15;
	public static final int PRIVATE_CHATROOM_AUTHORIZED = 16;
	public static final int PRIVATE_CHATROOM_EXIT = 17;
	public static final int PING = 18;
	public static final int FILE_TRANSFER = 19;
	public static final int REASON_SERVER_FULL = 20;
	public static final int REASON_KICKED = 21;
	public static final int REASON_INCONSISTENT_USER_ID = 22;
	public static final int REASON_BLACKLISTED = 23;
	public static final int REASON_SERVER_CLOSED = 24;
	public static final int REASON_ROOM_CLOSED = 25;
	public static final int REASON_GENERIC = 26;
	public static final int REASON_INCORRECT_CREDENTIALS = 27;
	public static final int REASON_USERNAME_EMAIL_ALREADY_EXISTS = 28;
	public static final int REASON_INCOMPATIBLE_CLIENT = 29;
	
	private final int COMMAND;
	private final int REASON;
	private final Object MESSAGE;
	
	public Command(int command, String sender, String senderID)
	{
		super(sender, senderID);
		this.COMMAND = command;
		this.REASON = Command.REASON_GENERIC;
		this.MESSAGE = null;
	}
	
	public Command(int command, int reason, String sender, String senderID)
	{
		super(sender, senderID);
		this.COMMAND = command;
		this.REASON = reason;
		this.MESSAGE = null;
	}
	
	public Command(int command, Object message, String sender, String senderID)
	{
		super(sender, senderID);
		this.COMMAND = command;
		this.REASON = Command.REASON_GENERIC;
		this.MESSAGE = message;
	}
	
	public String getFullCommand()
	{
		return super.toString() + "Command: " + this.COMMAND + " Reason: " + this.REASON;
	}
	
	public int getCommand()
	{
		return this.COMMAND;
	}
	
	public int getReason()
	{
		return this.REASON;
	}
	
	public String getFullMessage()
	{
		return super.toString() + this.MESSAGE;
	}
	
	public Object getMessage()
	{
		return this.MESSAGE;
	}
}