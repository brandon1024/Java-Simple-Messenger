package webchatinterface.server;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import util.CodeGenerator;
import webchatinterface.AbstractIRC;
import webchatinterface.server.ui.ConsoleManager;
import webchatinterface.server.util.AccountManager;
import webchatinterface.server.util.BroadcastHelper;
import webchatinterface.server.util.ChatRoom;
import webchatinterface.util.TransferBuffer;
import webchatinterface.util.ClientUser;
import webchatinterface.util.Command;
import webchatinterface.util.Message;
import webchatinterface.util.TransportEntity;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
  *<p>
  *A threaded instance of the WebChatServerInstance class handles the receipt and broadcast
  *of objects to and from the socket. Each instance of this class represents a connection to
  *a client over the network or internet.
  */

public class WebChatServerInstance implements Runnable
{
	/**Local class variable used by {@code WebChatServerInstance()} constructor to assign unique 
	  *identification number to the client-server connection instance*/
	private static int ID = 0;
	
	/**A reference to the underlying server. Used to access list of connected users.
	  *@see webchatinterface.server.WebChatServer#getConnectedUsers()*/
	private WebChatServer server;
	
	/**A reference to the console manager. Allows for messages and information to be displayed
	  *to the console window.
	  *@see ConsoleManager*/
	private ConsoleManager consoleMng;
	
	/**A reference to the BroadcastHelper, responsible for broadcasting messages to all clients connected
	  *to the server, and responsible for scheduled server messages*/
	private BroadcastHelper broadcastHlp;
	
	/**The client-server connection socket; one endpoint of a communication link running on a
	  *network or over the internet between the server and a client.
	  *@see Socket*/
	private Socket socket;
	
	/**The {@code ObjectOutputStream} used to communicate objects to the client.*/
	private ObjectInputStream messageIn;
	
	/**The {@code ObjectOutputStream} used to receive objects from the client.*/
	private ObjectOutputStream messageOut;
	
	/**The ClientUser object representing a model of the user, the user status, and parameters.
	 *@see webchatinterface.util.ClientUser*/
	private ClientUser client;
	
	/**The chatroom in which the client is a member.*/
	private ChatRoom room;
	
	/**The unique identification number assocaited with an instance of the WebChatServerInstance.
	  *Each instance is given a unique identification number, starting at zero.*/
	private final int INSTANCE_ID;
	
	/**The verification status of the client user. If true, full privileges granted. If false, user
	  *is disconnected from server.*/
	private volatile boolean verified = false;
	
	/**Builds a {@code WebChatServerInstance} object. Establishes a two way communication
	  *connection with a dedicated client over the network or wide web. Once the link is
	  *established by the {@code WebChatServer}, the socket is used to initialize input
	  *and output streams to the client.
	  *<p>
	  *By default, the client is added to the public chatroom, and the availability of the
	  *client is set to AVAILABLE.
	  *@param server The underlying server. Used to interface with the public field
	  *ConnectionArray.
	  *@param broadcastHlp The BroadcastHelper instance used to help faciliate message and
	  *command broadcasting
	  *@param consoleMng The server console manager. Used to append messages and status to
	  *the graphical user interface console.
	  *@param logger A reference to the logger responsible for logging thrown exceptions to 
	  *a log file in the application directory
	  *@param socket The client-server socket.
	  *@throws IOException if an IOException is thrown while establishing Object Input and Output
	  *streams from the client-server socket.
	  */
	public WebChatServerInstance(WebChatServer server, BroadcastHelper broadcastHlp, ConsoleManager consoleMng, Socket socket) throws IOException
	{
		//Assign Fields, Open Streams
		this.server = server;
		this.consoleMng = consoleMng;
		this.broadcastHlp = broadcastHlp;
		this.socket = socket;
		this.messageOut = new ObjectOutputStream(this.socket.getOutputStream());
		this.messageIn = new ObjectInputStream(this.socket.getInputStream());
		this.INSTANCE_ID = ID++;
		
		//Set Room to Public Chatroom
		this.room = ChatRoom.publicRoom;
		this.room.addMember(this);
		
		//Initialize ClientUser Object With Default Availability
		this.client = new ClientUser();
		this.client.setAvailability(ClientUser.AVAILABLE);
	}
	
	public void start()
	{
		(new Thread(this)).start();
	}
	
	/**Runs the {@code WebChatServerInstance} thread. Invokes {@code listen()}, and closes the
	  *streams and socket when {@code listen()} is popped off the call stack.*/
	@Override
	public void run()
	{
		this.listen();
		
		try
		{
			this.messageIn.close();
			this.messageOut.close();
			this.socket.close();
		}
		catch(IOException e)
		{
			AbstractServer.logException(e);
		}
	}
	
	/**The listen() method handles communication with the client. Once the client is verified,
	  *the listen() method listens to the {@code ObjectInputStream}. {@code Message}, 
	  *{@code MultimediaMessage}, and {@code TransferBuffer} objects are broadcasted to all 
	  *clients connected to the server. {@code Command} objects are handled according to their 
	  *function. See {@code Command} constant fields.
	  *<p>
	  *The {@code WebChatServerInstance} will continue to listen to the streams indefinitely, until an IOException
	  *is thrown by the streams, the user ID becomes inconsistent, or a {@code SUSPEND} command is received.*/
	private void listen()
	{
		//Verify Connection
		this.consoleMng.printConsole("Authenticating Client", false);
		this.establishConnection();
		
		if(this.verified)
		{
			this.consoleMng.printConsole("Client Authenticated: Connection Authorized", false);
			this.consoleMng.printConsole(this.paramString(), false);
			this.broadcastHlp.broadcastMessage(new Message(this.client.getUsername() + " Connected", "SERVER", "0"), this.room);
			this.broadcastHlp.broadcastCommand(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.room);
		}
		else
		{
			this.consoleMng.printConsole("Client Authentication Failed: Connection Denied", false);
		}
		
		//If not verified, exit.
		while(this.verified)
		{
			try
			{
				//If Message Received
				Object message = this.messageIn.readObject();
				
				if(message != null && message instanceof TransferBuffer)
				{
					//validate
					this.validateMessage((TransferBuffer)message);
					
					if(this.verified)
					{
						this.broadcastHlp.broadcastMessage((TransferBuffer)message, this.room);
					}
					else
					{
						this.disconnect(Command.REASON_INCONSISTENT_USER_ID);
					}	
				}
				else if(message != null && message instanceof Message)
				{
					//validate
					this.validateMessage((Message)message);
					this.server.addObjectSent();
					
					if(this.verified)
					{
						//Log and Print to Console
						consoleMng.printConsole("Message Broadcasted From: " + this.client.getUsername() + " : " + this.socket.getInetAddress().getHostAddress(), false);
						this.broadcastHlp.broadcastMessage((Message)message, this.room);
					}
					else
					{
						this.disconnect(Command.REASON_INCONSISTENT_USER_ID);
					}
				}
				else if(message != null && message instanceof Command)
				{
					//validate
					this.validateCommand((Command)message);
					
					if(this.verified)
					{
						switch(((Command)message).getCommand())
						{
							case Command.CONNECTION_SUSPENDED:
							case Command.CONNECTION_SUSPENDED_AWKKNOWLEDGE:
								this.consoleMng.printConsole("User Disconnected: " + this.socket.getInetAddress().getHostAddress() + "; Closing Connection", false);
								this.verified = false;
								break;
							case Command.CLIENT_VERSION_REQUEST:
								this.send(new Command(Command.CLIENT_VERSION, AbstractIRC.CLIENT_VERSION, "SERVER", "0"));
								break;
							case Command.MESSAGE_TYPED:
								this.broadcastHlp.broadcastCommand((Command)message, this.room);
								break;
							case Command.CLIENT_AVAILABILITY_AVAILABLE:
								this.client.setAvailability(ClientUser.AVAILABLE);
								this.consoleMng.printConsole(this.client.getUsername() + " Set New Availability : AVAILABLE", false);
								this.broadcastHlp.broadcastCommand(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.room);
								break;
							case Command.CLIENT_AVAILABILITY_BUSY:
								this.client.setAvailability(ClientUser.BUSY);
								this.consoleMng.printConsole(this.client.getUsername() + " Set New Availability : BUSY", false);
								this.broadcastHlp.broadcastCommand(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.room);
								break;
							case Command.CLIENT_AVAILABILITY_AWAY:
								this.client.setAvailability(ClientUser.AWAY);
								this.consoleMng.printConsole(this.client.getUsername() + " Set New Availability : AWAY", false);
								this.broadcastHlp.broadcastCommand(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.room);
								break;
							case Command.CLIENT_AVAILABILITY_APPEAR_OFFLINE:
								this.client.setAvailability(ClientUser.APPEAR_OFFLINE);
								this.consoleMng.printConsole(this.client.getUsername() + " Set New Availability : APPEAR_OFFLINE", false);
								this.broadcastHlp.broadcastCommand(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.room);
								break;
							case Command.PRIVATE_CHATROOM_REQUEST:
							case Command.PRIVATE_CHATROOM_DENIED:
								Object[] recipient = (Object[])(((Command)message).getMessage());
								
								for(WebChatServerInstance client : ChatRoom.getGlobalMembers())
								{
									if(client.getUserID().equals(recipient[1]))
									{
										client.send((Command)message);
									}
								}
								break;
							case Command.PRIVATE_CHATROOM_AUTHORIZED:
								recipient = (Object[])(((Command)message).getMessage());
								
								for(WebChatServerInstance client : ChatRoom.getGlobalMembers())
								{
									if(client.getUserID().equals(recipient[1]))
									{
										client.send((Command)message);
										
										ChatRoom newPrivateRoom = new ChatRoom();
										
										this.setRoom(newPrivateRoom);
										client.setRoom(newPrivateRoom);
									}
								}
								break;
							case Command.PRIVATE_CHATROOM_EXIT:
								if(!this.room.equals(ChatRoom.publicRoom))
								{
									this.broadcastHlp.broadcastCommand((Command)message, this.room);
									this.room.closeRoom();
									
									WebChatServerInstance[] roomMembers = this.room.getConnectedClients();
									
									for(WebChatServerInstance member : roomMembers)
									{
										member.setRoom(ChatRoom.publicRoom);
									}
								}
								break;
							case Command.FILE_TRANSFER:
								Object[] manifestInfo = (Object[]) ((Command)message).getMessage();
								long fileSize = (long) manifestInfo[1];
								long bufferSize = (long) manifestInfo[2];
								String fileTransferID = (String)manifestInfo[3];
								String filename = (String)manifestInfo[4];
								this.consoleMng.printConsole(this.client.getUsername()
										+ " Initialized a File Transfer:"
										+ "\nFilename: " + filename
										+ "\nFile Size: " + fileSize + "bytes"
										+ "\nBuffer Size: " + bufferSize + "bytes"
										+ "\nTransfer ID: " + fileTransferID, false);
								this.broadcastHlp.broadcastCommand((Command)message);
								this.server.addFilesTransfered();
								break;
						}
					}
					else
					{
						this.disconnect(Command.REASON_INCONSISTENT_USER_ID);
					}
				}
				
				if(!this.verified)
				{
					break;
				}
			}
			catch(EOFException e)
			{
				this.consoleMng.printConsole("FATAL ERROR: Thread " + this.INSTANCE_ID + ": Stream Closed Unexpectedly; Connection Aborted", true);
				AbstractServer.logException(e);
				break;
			}
			catch(StreamCorruptedException e)
			{
				this.consoleMng.printConsole("FATAL ERROR: Thread " + this.INSTANCE_ID + ": Stream Closed Unexpectedly; Connection Aborted", true);
				AbstractServer.logException(e);
				break;
			}
			catch(IOException e)
			{
				this.consoleMng.printConsole("FATAL ERROR: Thread " + this.INSTANCE_ID + ": Stream Closed Unexpectedly; Connection Aborted", true);
				AbstractServer.logException(e);
				break;
			}
			catch(SecurityException e)
			{
				this.consoleMng.printConsole("FATAL ERROR: Thread " + this.INSTANCE_ID + ": Untrusted Subclass Illegally Overrode Security-Sensitive Methods", true);
				AbstractServer.logException(e);
				break;
			}
			catch(NullPointerException e)
			{
				this.consoleMng.printConsole("FATAL ERROR: Thread " + this.INSTANCE_ID + ": Stream Closed Unexpectedly; Connection Aborted", true);
				AbstractServer.logException(e);
				break;
			}
			catch (ClassNotFoundException e)
			{
				this.consoleMng.printConsole("FATAL ERROR: Thread " + this.INSTANCE_ID + ": Incompatible Client; Connection Aborted", true);
				AbstractServer.logException(e);
				break;
			}
		}
		
		//remove connection from connection array
		this.room.removeMember(this);
		this.consoleMng.printConsole("Successfully Removed User From List of Connected Users", false);
			
		//broadcast message to all users
		this.broadcastHlp.broadcastMessage(new Message(this.client.getUsername() + " disconnected", "SERVER", "0"), this.room);
		this.broadcastHlp.broadcastCommand(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.room);
	}
	
	/**Initiates orderly connection release. {@code disconnect()} sends a {@code SUSPEND_CONNECTION} command
	  *to the client. Once an ackowledgement command is received, the instance will gracefully terminate.*/
	public void disconnect(int reason)
	{
		try
		{
			//send SUSPEND command to user, and wait for ACK command
			this.send(new Command(Command.CONNECTION_SUSPENDED, reason, "SERVER", "0"));
		}
		catch (IOException e)
		{
			this.consoleMng.printConsole("Exception Thrown While Attempting to Send CONNECTION_SUSPENDED Command", true);
			AbstractServer.logException(e);
		}
		
		switch(reason)
		{
			case Command.REASON_KICKED:
			case Command.REASON_BLACKLISTED:
			case Command.REASON_INCONSISTENT_USER_ID:
			case Command.REASON_SERVER_FULL:
			case Command.REASON_SERVER_CLOSED:
				this.verified = false;
		}
	}
	
	/**Executes connection verification procedures. A connection is allowed if the a proper
	  *CONNECTION_REQUEST Command is received and the username and userID fields meet
	  *criteria*/
	private void establishConnection()
	{
		//Verify User
		while(!this.verified)
		{
			Object objIn;
			try
			{
				objIn = this.messageIn.readObject();
				
				//Filter Received Objects for CONNECTION_REQUEST Command
				if(objIn != null && objIn instanceof Command)
				{
					int com = ((Command)objIn).getCommand();
					
					if(com == Command.CONNECTION_REQUEST)
					{
						Object[] data = (Object[])((Command)objIn).getMessage();
						boolean isGuest = (boolean)data[0];
						
						if(isGuest)
						{
							String clientVersion = (String)data[1];
							
							if(!clientVersion.equals(AbstractIRC.CLIENT_VERSION))
							{
								this.send(new Command(Command.CONNECTION_DENIED, Command.REASON_INCOMPATIBLE_CLIENT, "SERVER", "0"));
								this.verified = false;
								break;
							}
							
							String username = "Guest" + CodeGenerator.generate64bitKey(CodeGenerator.NUMERIC);
							String userID = CodeGenerator.generate256bitKey(CodeGenerator.ALPHANUMERIC_MIXED_CASE);
							
							this.client.setUsername(username);
							this.client.setUserID(userID);
							
							Object[] responseData = {username, userID};
							this.send(new Command(Command.CONNECTION_AUTHORIZED, responseData, "SERVER", "0"));
							this.verified = true;
						}
						else
						{
							boolean newAccount = (boolean)data[1];
							String clientVersion = (String)data[2];
							String emailAddress = (String)data[3];
							String username = (String)data[4];
							byte[] password = (byte[])data[5];
							String userID = CodeGenerator.generate256bitKey(CodeGenerator.ALPHANUMERIC_MIXED_CASE);
							
							if(!clientVersion.equals(AbstractIRC.CLIENT_VERSION))
							{
								this.send(new Command(Command.CONNECTION_DENIED, Command.REASON_INCOMPATIBLE_CLIENT, "SERVER", "0"));
								this.verified = false;
								break;
							}
							
							if(newAccount)
							{
								boolean successful;
								try
								{
									successful = AccountManager.createNewAccount(emailAddress.getBytes(), username.getBytes(), password);
								}
								catch(FileNotFoundException e)
								{
									this.consoleMng.printConsole("AccountManager account database file not found. Unable to verify user credentials.", true);
									AbstractServer.logException(e);
									successful = false;
								}
								catch(IOException | ClassNotFoundException e)
								{
									this.consoleMng.printConsole("AccountManager unable to read account database file. Unable to verify user credentials.", true);
									AbstractServer.logException(e);
									successful = false;
								}
								catch (NoSuchAlgorithmException e)
								{
									this.consoleMng.printConsole("AccountManager hashing algorithm is not supported. Unable to verify user credentials.", true);
									AbstractServer.logException(e);
									successful = false;
								}
								
								if(successful)
								{
									this.client.setUsername(username);
									this.client.setUserID(userID);
									
									Object[] responseData = {username, userID};
									this.send(new Command(Command.CONNECTION_AUTHORIZED, responseData, "SERVER", "0"));
									this.verified = true;
								}
								else
								{
									this.send(new Command(Command.CONNECTION_DENIED, Command.REASON_USERNAME_EMAIL_ALREADY_EXISTS, "SERVER", "0"));
									this.verified = false;
									break;
								}
							}
							else
							{
								boolean successful;
								try
								{
									successful = AccountManager.verifyCredentials(username.getBytes(), password);
								}
								catch(FileNotFoundException e)
								{
									this.consoleMng.printConsole("AccountManager account database file not found. Unable to verify user credentials.", true);
									AbstractServer.logException(e);
									successful = false;
								}
								catch(IOException | ClassNotFoundException e)
								{
									this.consoleMng.printConsole("AccountManager unable to read account database file. Unable to verify user credentials.", true);
									AbstractServer.logException(e);
									successful = false;
								}
								catch (NoSuchAlgorithmException e)
								{
									this.consoleMng.printConsole("AccountManager hashing algorithm is not supported. Unable to verify user credentials.", true);
									AbstractServer.logException(e);
									successful = false;
								}
								
								if(successful)
								{
									this.client.setUsername(username);
									this.client.setUserID(userID);
									
									Object[] responseData = {username, userID};
									this.send(new Command(Command.CONNECTION_AUTHORIZED, responseData, "SERVER", "0"));
									this.verified = true;
								}
								else
								{
									this.send(new Command(Command.CONNECTION_DENIED, Command.REASON_INCORRECT_CREDENTIALS, "SERVER", "0"));
									this.verified = false;
									break;
								}
							}
						}
					}
					else
					{
						this.verified = false;
						break;
					}
				}
			}
			catch (ClassNotFoundException e)
			{
				this.consoleMng.printConsole("Unrecognized Object Received: " + e.getMessage(), true);
				AbstractServer.logException(e);
				this.verified = false;
				break;
			}
			catch(IOException e)
			{
				this.consoleMng.printConsole("Exception Thrown While Attempting to Send Message: " + e.getMessage(), true);
				AbstractServer.logException(e);
				this.verified = false;
				break;
			}
		}
	}
	
	/**Validates a message received by the client. If the client UserID signature on the message
	  *obejct does not match the UserID on record, the connection is flagged as unverified and closes.
	  *@param message the message to validate.*/
	private void validateMessage(TransportEntity message)
	{
		if(!message.getSenderID().equals(this.client.getUserID()))
		{
			this.consoleMng.printConsole("Client " + this.INSTANCE_ID  + " connection aborted: Inconsistent User ID", true);
			this.verified = false;
		}
	}
	
	/**Validates a command received by the client. If the client UserID signature on the command
	  *obejct does not match the UserID on record, the connection is flagged as unverified and closes.
	  *@param command the command to validate.*/
	private void validateCommand(Command command)
	{
		if(!command.getSenderID().equals(this.client.getUserID()))
		{
			this.consoleMng.printConsole("Client " + this.INSTANCE_ID  + " connection aborted: Inconsistent User ID", true);
			this.verified = false;
		}
	}
	
	/**Writes a {@code Message} object to the {@code ObjectOutputStream}.
	  *@see WebChatServer#objectsSent
	  *@param message The {@code Message} object to be broadcasted to all clients.
	  *@throws IOException if an IOException is thrown while attempting to write to the object output stream.*/
	public synchronized void send(Message message) throws IOException
	{
		this.messageOut.writeObject(message);
		this.messageOut.flush();
	}
	
	/**Writes a {@code TransferBuffer} object to the {@code ObjectOutputStream}.
	  *@see WebChatServer#objectsSent
	  *@param message The {@code TransferBuffer} object to be broadcasted to all clients.
	  *@throws IOException if an IOException is thrown while attempting to write to the object output stream.*/
	public synchronized void send(TransferBuffer message) throws IOException
	{
		this.messageOut.writeObject(message);
		this.messageOut.flush();
	}
	
	/**Writes a {@code Command} object to the {@code ObjectOutputStream}.
	  *@see WebChatServer#objectsSent
	  *@param command The {@code Command} object to be broadcasted to all clients.
	  *@throws IOException if an IOException is thrown while attempting to write to the object output stream.*/
	public synchronized void send(Command command) throws IOException
	{
		this.messageOut.writeObject(command);
		this.messageOut.flush();
	}
	
	/**@deprecated The {@code send(Object obj)} method is not used with 
	  *the dedicated {@code WebChatServer} server application. Any object 
	  *that is not an instance of {@code Message}, {@code MultimediaMessage}, 
      *or {@code Command} objects received by the dedicated {@code WebChatServer} 
	  *will simply be ignored and discarded. However, this method may be useful 
	  *if one wishes to develop a unique server application.
	  *<p>
	  *Writes a Object to the {@code ObjectOutputStream}. Updates the server counter for the
	  *number of obejcts sent.
	  *@see WebChatServer#objectsSent
	  *@param obj The object to be broadcasted to all clients.
	  *@throws IOException if an IOException is thrown while attempting to write to the object output stream.*/
	@Deprecated
	public synchronized void send(Object obj) throws IOException
	{
		this.messageOut.writeObject(obj);
		this.messageOut.flush();
	}

	/**Sets the ChatRoom in which the client is connected to a new room. Removes this instance
	  *from the old chatroom, mutates the room field of this instance, and adds this instance
	  *to the new room.
	  *@param room The new chatroom to connect to.*/
	public void setRoom(ChatRoom room)
	{
		this.room.removeMember(this);
		this.room = room;
		this.room.addMember(this);
	}
	
	/**Accessor method for the room field of this object.
	  *@return the chatroom in which this instance is connected*/
	public ChatRoom getRoom()
	{
		return this.room;
	}
	
	/**Accessor method for the IP address associated with the client connected to the server through this
	  *instance of {@code WebChatServerInstance}.
	  *@return the client IP address.*/
	public String getIP()
	{
		return this.socket.getInetAddress().getHostAddress();
	}
	
	/**Accessor method for the unique identification number associated with this instance of {@code WebChatServerInstance}.
	  *@return the unique identification number associated with this client-server connection isntance.*/
	public int getID()
	{
		return this.INSTANCE_ID;
	}
	
	/**Accessor method for the username associated with the client connected to the server through this
	  *instance of {@code WebChatServerInstance}.
	  *@return the username specified by the client*/
	public String getUsername()
	{
		return this.client.getUsername();
	}
	
	/**Accessor method for the unique identification key associated with the client connected to the
	  *server through this instance of {@code WebChatServerInstance}.
	  *@return the unique identification key for the client*/
	public String getUserID()
	{
		return this.client.getUserID();
	}
	
	/**Accessor method for the availability of the client connected to the server through this instance 
	  *of {@code WebChatServerInstance}.
	  *@return the availability of the client, as described by the static fields in [@code ClientUser}.
	  *@see webchatinterface.util.ClientUser*/
	public int getAvailability()
	{
		return this.client.getAvailability();
	}
	
	/**Used to access specific information regarding this instance of {@code WebChatServerInstance}.
	  *<p>
	  *Format:
	  *Instance: INSTANCE_ID
	  *Address: HOSTADDRESS
	  *Local Port: LOCAL SERVER PORT
	  *Remote Port: REMOTE SERVER PORT
	  *Username: USERNAME
	  *Verified: TRUE/FALSE
	  *@return a textual representation of this client-server connection instance.*/
	public String paramString()
	{
		return "\nInstance: " + this.INSTANCE_ID +
				"\nAddress: " + this.socket.getInetAddress().getHostAddress() +
				"\nLocal Port: " + this.socket.getLocalPort() +
				"\nRemote Port: " + this.socket.getPort() + 
				"\nUsername: " + this.client.getUsername() +
				"\nUser ID: " + this.client.getUserID() +
				"\nVerified: " + this.verified;
	}
	
	/**Overridden {@code toString()} method, used to access basic information regarding the socket
	  *and client username.
	  *<p>
	  *Format: [username] "socket.toString()"
	  *@return a textual representation of this client-server connection instance.*/
	@Override
	public String toString()
	{
		return "[" + this.client.getUsername() + "] " + this.socket.toString();
	}
}