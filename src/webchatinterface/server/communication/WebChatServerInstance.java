package webchatinterface.server.communication;

import util.KeyGenerator;
import webchatinterface.AbstractIRC;
import webchatinterface.server.AbstractServer;
import webchatinterface.server.network.Channel;
import webchatinterface.server.network.ChannelManager;
import webchatinterface.server.ui.components.ConsoleManager;
import webchatinterface.server.account.AccountManager;
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
	private ChannelManager channelManager;
	private BroadcastHelper broadcastHlp;
	private Socket socket;
	private ObjectInputStream messageIn;
	private ObjectOutputStream messageOut;
	private ClientUser client;
	private Channel channel;
	private final int INSTANCE_ID;
	private volatile boolean verified = false;
	
	public WebChatServerInstance(WebChatServer server, Socket socket) throws IOException
	{
		//Assign Fields, Open Streams
		this.server = server;
		this.consoleMng = ConsoleManager.getInstance();
		this.channelManager = ChannelManager.getInstance();
		this.broadcastHlp = BroadcastHelper.getInstance();
		this.socket = socket;
		this.messageOut = new ObjectOutputStream(this.socket.getOutputStream());
		this.messageIn = new ObjectInputStream(this.socket.getInputStream());
		this.INSTANCE_ID = ID++;
		
		//Set Channel to Public
		this.channel = this.channelManager.publicChannel;
		this.channel.addChannelMember(this);
		
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
		this.establishConnection();
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

	private void establishConnection()
	{
		try
		{
			while(!this.verified)
			{
				Object objIn = this.messageIn.readObject();

				if(objIn == null || !(objIn instanceof Command))
					continue;

				Command com = (Command)objIn;

				if(com.getCommand() != Command.CONNECTION_REQUEST)
				{
					this.consoleMng.printConsole("Connection Denied : Invalid Authentication Sequence : " + this.getIP(), true);
					this.verified = false;
					break;
				}

				this.consoleMng.printConsole("Connection Requested : " + this.getIP(), false);

				Object[] data = (Object[])com.getMessage();
				boolean isGuest = (boolean)data[0];

				if(isGuest)
				{
					this.consoleMng.printConsole("Assigning Guest Credentials : " + this.getIP(), false);
					String clientVersion = (String)data[1];

					if(!clientVersion.equals(AbstractIRC.CLIENT_VERSION))
					{
						this.consoleMng.printConsole("Connection Denied : Incompatible Client Version : " + this.getIP(), false);
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
					this.consoleMng.printConsole("Connection Authorized : " + username + " : " + this.getIP(), false);
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
						this.consoleMng.printConsole("Connection Denied : Incompatible Client Version : " + this.getIP(), false);
						this.send(new Command(Command.CONNECTION_DENIED, Command.REASON_INCOMPATIBLE_CLIENT, "SERVER", "0"));
						this.verified = false;
						break;
					}

					if(newAccount)
					{
						boolean successful = false;
						try
						{
							this.consoleMng.printConsole("New Account Request : " + this.getIP(), false);
							successful = AccountManager.createNewAccount(emailAddress.getBytes(), username.getBytes(), password);
						}
						catch(FileNotFoundException e)
						{
							this.consoleMng.printConsole("Unable to Verify User Credentials : AccountManager Account Database File Not Found", true);
							AbstractServer.logException(e);
						}
						catch(IOException | ClassNotFoundException e)
						{
							this.consoleMng.printConsole("Unable to Verify User Credentials : AccountManager Unable to Read Account Database File", true);
							AbstractServer.logException(e);
						}
						catch (NoSuchAlgorithmException e)
						{
							this.consoleMng.printConsole("Unable to Verify User Credentials : Unsupported MessageDigest Algorithm", true);
							AbstractServer.logException(e);
						}

						if(successful)
						{
							this.consoleMng.printConsole("Connection Authorized : " + username + " : " + this.getIP(), false);
							this.client.setUsername(username);
							this.client.setUserID(userID);

							Object[] responseData = {username, userID};
							this.send(new Command(Command.CONNECTION_AUTHORIZED, responseData, "SERVER", "0"));
							this.verified = true;
						}
						else
						{
							this.consoleMng.printConsole("Connection Denied : Account Creation Aborted : Username or Email Address Exists : " + this.getIP(), false);
							this.send(new Command(Command.CONNECTION_DENIED, Command.REASON_USERNAME_EMAIL_ALREADY_EXISTS, "SERVER", "0"));
							this.verified = false;
							break;
						}
					}
					else
					{
						boolean successful = false;

						try
						{
							this.consoleMng.printConsole("Verifying Credentials : " + this.getIP(), false);
							successful = AccountManager.verifyCredentials(username.getBytes(), password);
						}
						catch(FileNotFoundException e)
						{
							this.consoleMng.printConsole("Unable to Verify User Credentials : AccountManager Account Database File Not Found", true);
							AbstractServer.logException(e);
						}
						catch(IOException | ClassNotFoundException e)
						{
							this.consoleMng.printConsole("Unable to Verify User Credentials : AccountManager Unable to Read Account Database File", true);
							AbstractServer.logException(e);
						}
						catch (NoSuchAlgorithmException e)
						{
							this.consoleMng.printConsole("Unable to Verify User Credentials : Unsupported MessageDigest Algorithm", true);
							AbstractServer.logException(e);
						}

						if(successful)
						{
							this.consoleMng.printConsole("Connection Authorized : " + username + " : " + this.getIP(), false);
							this.client.setUsername(username);
							this.client.setUserID(userID);

							Object[] responseData = {username, userID};
							this.send(new Command(Command.CONNECTION_AUTHORIZED, responseData, "SERVER", "0"));
							this.verified = true;
						}
						else
						{
							this.consoleMng.printConsole("Connection Denied : Invalid Credentials : " + this.getIP(), false);
							this.send(new Command(Command.CONNECTION_DENIED, Command.REASON_INCORRECT_CREDENTIALS, "SERVER", "0"));
							this.verified = false;
							break;
						}
					}
				}
			}
		}
		catch (ClassNotFoundException e)
		{
			this.consoleMng.printConsole("Unrecognized Object Received: " + e.getMessage(), true);
			AbstractServer.logException(e);
			this.verified = false;
		}
		catch(IOException e)
		{
			this.consoleMng.printConsole("Exception Thrown While Attempting to Send Message: " + e.getMessage(), true);
			AbstractServer.logException(e);
			this.verified = false;
		}
	}
	
	private void listen()
	{
		if(this.verified)
		{
			this.broadcastHlp.broadcastMessage(new Message(this.client.getUsername() + " connected", "SERVER", "0"), this.channel);
			this.broadcastHlp.broadcastMessage(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.channel);
		}

		try
		{
			while(this.verified)
			{
				//If Message Received
				Object message = this.messageIn.readObject();

				if(message == null)
					continue;

				this.validateMessage((TransportEntity) message);

				if(!this.verified)
				{
					this.disconnect(Command.REASON_INCONSISTENT_USER_ID);
					break;
				}

				if(message instanceof TransferBuffer)
				{
					this.broadcastHlp.broadcastMessage((TransferBuffer) message, this.channel);
				}
				else if(message instanceof Message)
				{
					this.consoleMng.printConsole("Message Broadcasted : " + this.client.getUsername() + " : " + this.getIP(), false);
					this.broadcastHlp.broadcastMessage((Message) message, this.channel);
				}
				else if(message instanceof Command)
				{
					switch(((Command) message).getCommand())
					{
						case Command.CONNECTION_SUSPENDED:
						case Command.CONNECTION_SUSPENDED_ACKNOWLEDGE:
							this.consoleMng.printConsole("Client Disconnected : " + this.client.getUsername() + " : " + this.getIP(), false);
							this.verified = false;
							break;
						case Command.CLIENT_VERSION_REQUEST:
							this.send(new Command(Command.CLIENT_VERSION, AbstractIRC.CLIENT_VERSION, "SERVER", "0"));
							break;
						case Command.MESSAGE_TYPED:
							this.broadcastHlp.broadcastMessage((Command) message, this.channel);
							break;
						case Command.CLIENT_AVAILABILITY_AVAILABLE:
							this.client.setAvailability(ClientUser.AVAILABLE);
							this.consoleMng.printConsole("Availability Changed : AVAILABLE : " + this.client.getUsername() + " : " + this.getIP(), false);
							this.broadcastHlp.broadcastMessage(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.channel);
							break;
						case Command.CLIENT_AVAILABILITY_BUSY:
							this.client.setAvailability(ClientUser.BUSY);
							this.consoleMng.printConsole("Availability Changed : BUSY : " + this.client.getUsername() + " : " + this.getIP(), false);
							this.broadcastHlp.broadcastMessage(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.channel);
							break;
						case Command.CLIENT_AVAILABILITY_AWAY:
							this.client.setAvailability(ClientUser.AWAY);
							this.consoleMng.printConsole("Availability Changed : AWAY : " + this.client.getUsername() + " : " + this.getIP(), false);
							this.broadcastHlp.broadcastMessage(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.channel);
							break;
						case Command.CLIENT_AVAILABILITY_APPEAR_OFFLINE:
							this.client.setAvailability(ClientUser.APPEAR_OFFLINE);
							this.consoleMng.printConsole("Availability Changed : APPEAR OFFLINE : " + this.client.getUsername() + " : " + this.getIP(), false);
							this.broadcastHlp.broadcastMessage(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.channel);
							break;
						case Command.PRIVATE_CHANNEL_REQUEST:
						case Command.PRIVATE_CHANNEL_DENIED:
							Object[] recipient = (Object[]) (((Command) message).getMessage());
							Channel[] channels = this.channelManager.getGlobalChannels();
							for(Channel channel : channels)
							{
								for(WebChatServerInstance client : channel.getChannelMembers())
								{
									if(client.getUserID().equals(recipient[1]))
									{
										client.send((Command) message);
										break;
									}
								}
							}
							break;
						case Command.PRIVATE_CHANNEL_AUTHORIZED:
							recipient = (Object[]) (((Command) message).getMessage());
							channels = this.channelManager.getGlobalChannels();
							for(Channel channel : channels)
							{
								for(WebChatServerInstance client : channel.getChannelMembers())
								{
									if(client.getUserID().equals(recipient[1]))
									{
										client.send((Command) message);
										Channel newPrivateChannel = this.channelManager.newChannel("Private Channel", false, false);
										this.setChannel(newPrivateChannel);
										client.setChannel(newPrivateChannel);
										break;
									}
								}
							}
							break;
						case Command.PRIVATE_CHANNEL_EXIT:
							if(!this.channel.equals(this.channelManager.publicChannel))
							{
								this.broadcastHlp.broadcastMessage((Command) message, this.channel);
								WebChatServerInstance[] channelMembers = this.channel.getChannelMembers();
								for(WebChatServerInstance member : channelMembers)
									member.setChannel(this.channelManager.publicChannel);
							}
							break;
						case Command.FILE_TRANSFER:
							Object[] manifestInfo = (Object[]) ((Command) message).getMessage();
							long fileSize = (long) manifestInfo[1];
							String fileTransferID = (String) manifestInfo[3];
							String filename = (String) manifestInfo[4];
							this.consoleMng.printConsole("File Transfer : " + this.client.getUsername() + " : " + this.getIP(), false);
							this.consoleMng.printConsole("\tFilename: " + filename + " : File Size: " + fileSize + "bytes" + " : Transfer ID: " + fileTransferID, false);
							this.broadcastHlp.broadcastMessage((Command) message);
							this.server.addFilesTransferred();
							break;
					}
				}
			}
		}
		catch(IOException e)
		{
			this.consoleMng.printConsole("FATAL ERROR : Thread " + this.INSTANCE_ID + " : Unexpected IOException : See LOG", true);
			AbstractServer.logException(e);
		}
		catch(SecurityException e)
		{
			this.consoleMng.printConsole("FATAL ERROR : Thread " + this.INSTANCE_ID + " : Unexpected SecurityException : See LOG", true);
			AbstractServer.logException(e);
		}
		catch(NullPointerException e)
		{
			this.consoleMng.printConsole("FATAL ERROR : Thread " + this.INSTANCE_ID + " : NullPointerException : See LOG", true);
			AbstractServer.logException(e);
		}
		catch (ClassNotFoundException e)
		{
			this.consoleMng.printConsole("FATAL ERROR : Thread " + this.INSTANCE_ID + " : Undefined Communication Protocol : See LOG", true);
			AbstractServer.logException(e);
		}
		
		//remove connection from connection array
		this.channel.removeChannelMember(this);
			
		//broadcast message to all users
		this.broadcastHlp.broadcastMessage(new Message(this.client.getUsername() + " disconnected", "SERVER", "0"), this.channel);
		this.broadcastHlp.broadcastMessage(new Command(Command.CONNECTED_USERS, this.server.getConnectedUsers(), "SERVER", "0"), this.channel);
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
			this.consoleMng.printConsole("FATAL ERROR : Thread " + this.INSTANCE_ID + ": Unexpected IOException : See LOG", true);
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
	
	private void validateMessage(TransportEntity message)
	{
		if(!message.getSenderID().equals(this.client.getUserID()))
		{
			this.consoleMng.printConsole("Connection Aborted : Inconsistent User ID : " + this.getIP(), true);
			this.verified = false;
		}
	}
	
	public synchronized void send(TransportEntity message) throws IOException
	{
		this.messageOut.writeObject(message);
		this.messageOut.flush();
	}

	private void setChannel(Channel channel)
	{
		this.channel.removeChannelMember(this);
		this.channel = channel;
		this.channel.addChannelMember(this);
	}
	
	public Channel getChannel()
	{
		return this.channel;
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
				"\nAddress: " + this.getIP() +
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