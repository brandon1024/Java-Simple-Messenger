package webchatinterface.server.communication;

import util.KeyGenerator;
import webchatinterface.AbstractIRC;
import webchatinterface.server.AbstractServer;
import webchatinterface.server.ui.components.ConsoleManager;
import webchatinterface.server.account.AccountManager;
import webchatinterface.server.network.ChatRoom;
import webchatinterface.util.*;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

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
	private static int ID = 0;
	
	private WebChatServer server;
	private ConsoleManager consoleMng;
	private BroadcastHelper broadcastHlp;
	private Socket socket;
	private ObjectInputStream messageIn;
	private ObjectOutputStream messageOut;
	private ClientUser client;
	private ChatRoom room;
	private final int INSTANCE_ID;
	private volatile boolean verified = false;
	
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
	
	private void listen()
	{
		//Verify Connection
		this.consoleMng.printConsole("Authenticating Client", false);
		this.establishConnection();
		
		if(this.verified)
		{
			this.consoleMng.printConsole("Client Authenticated: Connection Authorized", false);
			this.consoleMng.printConsole(this.paramString(), false);
			this.broadcastHlp.broadcastMessage(new Message(this.client.getUsername() + " connected", "SERVER", "0"), this.room);
			this.broadcastHlp.broadcastMessage(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.room);
		}
		else
			this.consoleMng.printConsole("Client Authentication Failed: Connection Denied", false);
		
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
						this.broadcastHlp.broadcastMessage((TransferBuffer)message, this.room);
					else
						this.disconnect(Command.REASON_INCONSISTENT_USER_ID);
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
						this.disconnect(Command.REASON_INCONSISTENT_USER_ID);
				}
				else if(message != null && message instanceof Command)
				{
					//validate
					this.validateMessage((Command)message);
					
					if(this.verified)
					{
						switch(((Command)message).getCommand())
						{
							case Command.CONNECTION_SUSPENDED:
							case Command.CONNECTION_SUSPENDED_ACKNOWLEDGE:
								this.consoleMng.printConsole("User Disconnected: " + this.socket.getInetAddress().getHostAddress() + "; Closing Connection", false);
								this.verified = false;
								break;
							case Command.CLIENT_VERSION_REQUEST:
								this.send(new Command(Command.CLIENT_VERSION, AbstractIRC.CLIENT_VERSION, "SERVER", "0"));
								break;
							case Command.MESSAGE_TYPED:
								this.broadcastHlp.broadcastMessage((Command)message, this.room);
								break;
							case Command.CLIENT_AVAILABILITY_AVAILABLE:
								this.client.setAvailability(ClientUser.AVAILABLE);
								this.consoleMng.printConsole(this.client.getUsername() + " Set New Availability : AVAILABLE", false);
								this.broadcastHlp.broadcastMessage(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.room);
								break;
							case Command.CLIENT_AVAILABILITY_BUSY:
								this.client.setAvailability(ClientUser.BUSY);
								this.consoleMng.printConsole(this.client.getUsername() + " Set New Availability : BUSY", false);
								this.broadcastHlp.broadcastMessage(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.room);
								break;
							case Command.CLIENT_AVAILABILITY_AWAY:
								this.client.setAvailability(ClientUser.AWAY);
								this.consoleMng.printConsole(this.client.getUsername() + " Set New Availability : AWAY", false);
								this.broadcastHlp.broadcastMessage(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.room);
								break;
							case Command.CLIENT_AVAILABILITY_APPEAR_OFFLINE:
								this.client.setAvailability(ClientUser.APPEAR_OFFLINE);
								this.consoleMng.printConsole(this.client.getUsername() + " Set New Availability : APPEAR_OFFLINE", false);
								this.broadcastHlp.broadcastMessage(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.room);
								break;
							case Command.PRIVATE_CHATROOM_REQUEST:
							case Command.PRIVATE_CHATROOM_DENIED:
								Object[] recipient = (Object[])(((Command)message).getMessage());
								
								for(WebChatServerInstance client : ChatRoom.getGlobalMembers())
								{
									if(client.getUserID().equals(recipient[1]))
										client.send((Command)message);
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
									this.broadcastHlp.broadcastMessage((Command)message, this.room);
									this.room.closeRoom();
									
									WebChatServerInstance[] roomMembers = this.room.getConnectedClients();
									
									for(WebChatServerInstance member : roomMembers)
										member.setRoom(ChatRoom.publicRoom);
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
								this.broadcastHlp.broadcastMessage((Command)message);
								this.server.addFilesTransferred();
								break;
						}
					}
					else
						this.disconnect(Command.REASON_INCONSISTENT_USER_ID);
				}
				
				if(!this.verified)
					break;
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
		this.broadcastHlp.broadcastMessage(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.room);
	}
	
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
							
							String username = "Guest" + KeyGenerator.generateKey16(KeyGenerator.NUMERIC);
							String userID = KeyGenerator.generateKey64(KeyGenerator.ALPHANUMERIC_MIXED_CASE);
							
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
							String userID = KeyGenerator.generateKey64(KeyGenerator.ALPHANUMERIC_MIXED_CASE);
							
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
	
	private void validateMessage(TransportEntity message)
	{
		if(!message.getSenderID().equals(this.client.getUserID()))
		{
			this.consoleMng.printConsole("Client " + this.INSTANCE_ID  + " connection aborted: Inconsistent User ID", true);
			this.verified = false;
		}
	}
	
	public synchronized void send(TransportEntity message) throws IOException
	{
		this.messageOut.writeObject(message);
		this.messageOut.flush();
	}

	private void setRoom(ChatRoom room)
	{
		this.room.removeMember(this);
		this.room = room;
		this.room.addMember(this);
	}
	
	public ChatRoom getRoom()
	{
		return this.room;
	}
	
	public String getIP()
	{
		return this.socket.getInetAddress().getHostAddress();
	}
	
	public int getID()
	{
		return this.INSTANCE_ID;
	}
	
	public String getUsername()
	{
		return this.client.getUsername();
	}
	
	public String getUserID()
	{
		return this.client.getUserID();
	}
	
	public int getAvailability()
	{
		return this.client.getAvailability();
	}
	
	private String paramString()
	{
		return "\nInstance: " + this.INSTANCE_ID +
				"\nAddress: " + this.socket.getInetAddress().getHostAddress() +
				"\nLocal Port: " + this.socket.getLocalPort() +
				"\nRemote Port: " + this.socket.getPort() + 
				"\nUsername: " + this.client.getUsername() +
				"\nUser ID: " + this.client.getUserID() +
				"\nVerified: " + this.verified;
	}
	
	public String toString()
	{
		return "[" + this.client.getUsername() + "] " + this.socket.toString();
	}
}