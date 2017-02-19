package webchatinterface.server;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import webchatinterface.AbstractIRC;
import webchatinterface.server.ui.ConsoleManager;
import webchatinterface.server.util.BroadcastHelper;
import webchatinterface.server.util.ChatRoom;
import webchatinterface.util.Command;
import webchatinterface.util.Message;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
  *<p>
  *The {@code WebChatServer} class accepts new client connections and creates threaded connection 
  *instances to handle the receipt and broadcast of objects to other users.
  *<p>
  *The {@code WebChatServer} class is also responsible for managing blacklisted users, kicking 
  *users, broadcasting message objects and managing resources. 
  */

public class WebChatServer implements Runnable
{
	/**The console manager for the graphical user interface. The console manager 
	  *is used to append messages and status to the console.
	  *@see ConsoleManager*/
	private ConsoleManager consoleMng;
	
	/**A reference to the BroadcastHelper, responsible for broadcasting messages to all clients connected
	  *to the server, and responsible for scheduled server messages*/
	private BroadcastHelper broadcastHlp;
	
	/**The server socket, which responds to and establishes a connection with 
	  *a remote client. The server socket listens to the network, and is the 
	  *heart of the server application.
	  *@see ServerSocket*/
	private ServerSocket servSocket;
	
	/**The total number of messages/objects transmitted throughout the life
	  *of this server.*/
	private long objectsSent;
	
	/**The total number of files transfered throughout the life
	  *of this server.*/
	private long filesTransfered;
	
	/**Variable used to close the {@code WebChatServer} thread*/
	private volatile boolean RUN = true;
	
	/**Builds a {@code WebChatServer} instance. Establishes framework for
	  *server communication over TCP with a dedicated client application.
	  *<p>
	  *The server is established on a given bind address and port number.
	  *The implementing class must run the {@code WebChatServer} thread
	  *before clients can connect.
	  *@param consoleMng The server console manager. Used to append messages
	  *and status to the graphical user interface console.
	  *@param logger A reference to the logger responsible for logging thrown exceptions to 
	  *a log file in the application directory
	  *@param broadcastHlp The BroadcastHelper instance used to help faciliate message and
	  *command broadcasting
	  *@param address The server bind address. If null, default address is used.
	  *@param port The server port number. If port number is out of range, default 
	  *port number 5100 is used.
	  *@param maxConnections the maximum number of concurrent client connections
	  *to the server.
	  */
	public WebChatServer(ConsoleManager consoleMng)
	{
		this.consoleMng = consoleMng;
		this.objectsSent = 0;
		
		this.broadcastHlp = new BroadcastHelper(this.consoleMng);
	}
	
	public void start()
	{
		(new Thread(this)).start();
	}
	
	/**Initializes and runs the server thread on given port and bind address.
	  *Starts the BroadCastHelper thread. Invokes the listen method. When the 
	  *{@code listen()} method finishes,
      *the server thread closes.*/
	@Override
	public void run()
	{
		this.consoleMng.printConsole("Initializing Server", false);
		this.consoleMng.printConsole("Using Server IP " + AbstractServer.serverBindIPAddress, false);
		
		//Use Port Number from Command Line Argument or Use Default Port 5100
		if(AbstractServer.serverPortNumber <= 0 || AbstractServer.serverPortNumber >= 65535)
		{
			this.consoleMng.printConsole("Invalid Server Port " + AbstractServer.serverPortNumber, true);
			AbstractServer.serverPortNumber = 5100;
			this.consoleMng.printConsole("Using Default Port " + AbstractServer.serverPortNumber, false);
		}
		else
		{
			this.consoleMng.printConsole("Using Port " + AbstractServer.serverPortNumber, false);
		}
		
		//Start BroadcastHelper Thread
		this.broadcastHlp.start();
		
		//Listen for Incoming Connections
		listen();
	}
	
	/**Suspends the server. Sets {@code RUN} to false, which closes
	  *{@code listen()} and stops the server thread. The {@code suspend()}
	  *method also closes each client connection.
	  *<p>
	  *Once the server is suspended, a new {@code WebChatServer} isntance
	  *must be created and run on a new thread.*/
	public void suspend()
	{
		//gracefully stop listening to ServerSocket
		this.broadcastHlp.stop();
		this.RUN = false;
		
		//disconnect all connected users
		for(WebChatServerInstance member : ChatRoom.getGlobalMembers())
		{
			member.disconnect(Command.REASON_SERVER_CLOSED);
		}
		
		//close ServerSocket
		try
		{
			if(this.servSocket != null)
			{
				this.servSocket.close();
			}
		}
		catch (IOException e)
		{
			AbstractServer.logException(e);
		}
		
		this.consoleMng.printConsole("Server Suspended", false);
	}
	
	/**Opens a server socket on the current state parameters defined
	  *by the {@code WebChatserver} instance variables. Subsequently,
	  *the socket accepts incoming client connections.
	  *<p>If {@code address} field is null, the server socket is not
	  *bound to any IP address. The server will stop if an exception is
	  *thrown by the server socket, and a description of the error will
	  *be logged by the {@code ConsoleManager}.
	  *@see #address
	  *@see #port*/
	public void listen()
	{
		try
		{
			//Create Server Socket on Specified Port
			this.consoleMng.printConsole("Opening Socket on Port " + AbstractServer.serverPortNumber, false);
			
			if(AbstractServer.serverBindIPAddress.equals("default"))
			{
				this.servSocket = new ServerSocket(AbstractServer.serverPortNumber);
			}
			else
			{
				this.servSocket = new ServerSocket(AbstractServer.serverPortNumber, 50, InetAddress.getByName(AbstractServer.serverBindIPAddress));
			}
			
			this.consoleMng.printConsole("Sucessfully Opened Socket on Port " + AbstractServer.serverPortNumber, false);
			this.consoleMng.printConsole("Awaiting Client Connection...", false);
			
			//While Server is Running
			while(this.RUN)
			{
				boolean loopCTRL = false;
				Socket socket;
				
				//handle blacklisted users and full server
				do
				{
					//Accept Incoming Connections to Socket
					socket = this.servSocket.accept();
					
					if(ChatRoom.getGlobalMembersSize() >= AbstractServer.maxConnectedUsers)
					{
						WebChatServerInstance chatCom = new WebChatServerInstance(this, this.broadcastHlp, this.consoleMng, socket);
						chatCom.start();
						this.disconnectUser(chatCom, Command.REASON_SERVER_FULL);
						this.consoleMng.printConsole("User Prevented from Connecting; server full (" + AbstractServer.maxConnectedUsers + " connections)", true);
						loopCTRL = true;
					}
					else if(isBlackListed(socket.getLocalAddress().getHostName()))
					{
						WebChatServerInstance chatCom = new WebChatServerInstance(this, this.broadcastHlp, this.consoleMng, socket);
						chatCom.start();
						this.disconnectUser(chatCom, Command.REASON_BLACKLISTED);
						this.consoleMng.printConsole("User Prevented from Connecting; blacklisted user", true);
						socket.close();
						loopCTRL = true;
					}
					else
					{
						loopCTRL = false;
					}
				}
				while(loopCTRL);
				
				this.consoleMng.printConsole("Connection Request From: " + socket.getInetAddress().getHostAddress(), false);
				
				//Start Server Instance Thread
				WebChatServerInstance chatCom = new WebChatServerInstance(this, this.broadcastHlp, this.consoleMng, socket);
				chatCom.start();
			}
		}
		catch(SocketException e)
		{
			//If SocketException is thrown, it is likely caused by suspend() method.
			//This behavior is expected. This allows the listen() method to exit the
			//while() loop
			AbstractServer.logException(e);
		}
		catch(UnknownHostException e)
		{
			this.consoleMng.printConsole("Critical Error Occured; The server bind IP address could not be resolved.", true);
			AbstractServer.logException(e);
		}
		catch(IOException e)
		{
			this.consoleMng.printConsole("Critical Error Occured; I/O error occured when opening server socket", true);
			AbstractServer.logException(e);
		}
		catch(Exception e)
		{
			this.consoleMng.printConsole("Critical Error Occured; Restart Server", true);
			AbstractServer.logException(e);
		}
	}
	
	/**Disconnects or kicks a client connection from the server. The
	  *{@code disconnectUser()} method sends a {@code SUSPEND_KICKED} command
	  *to the client, which initiates orderly connection release.
	  *<p>
	  *Unlike {@code blackListUser()}, the client is able to reconnect
	  *immediately after being disconnected.
	  *@param clientServerConnection The client connection instance to be disconnected
	  *@return Returns true if the client connection was found and the client was successfully
	  *disconnected. Returns false if the client connection was not found and could not
	  *be disconnected.
	  *@see Command#CONNECTION_KICKED*/
	public void disconnectUser(WebChatServerInstance clientServerConnection, int reason)
	{
		clientServerConnection.disconnect(reason);
	}
	
	/**Disconnects or kicks a client connection from the server, and blacklists the
	  *client IP address from future connection requests. Subsequently, the
	  *client will be prevented from connecting to the server, unless the
	  *blacklist configuration file, found in the temporary directory, is changed or removed.
	  *@param clientServerConnection The client connection instance to be disconnected
	  *and permanantly prevented from future connections to the server.
	  *@see Command#CONNECTION_KICKED*/
	public void blackListUser(WebChatServerInstance clientServerConnection)
	{
		try
		{
			FileOutputStream blackListFileOut = new FileOutputStream(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "BLACKLIST.dat", true);
			ObjectOutputStream blackListOut = new ObjectOutputStream(blackListFileOut);
			
			try
			{
				//output blacklist details to file
				blackListOut.writeObject(clientServerConnection.getIP());
			}
			catch (IOException e)
			{
				this.consoleMng.printConsole("Unable to Blacklist User; unable to write to BLACKLIST.dat", true);
				AbstractServer.logException(e);
			}
			
			blackListOut.close();
		}
		catch (FileNotFoundException e)
		{
			this.consoleMng.printConsole("Unable to Blacklist User; unable to access BLACKLIST.dat", true);
			AbstractServer.logException(e);
		}
		catch(IOException e)
		{
			this.consoleMng.printConsole("Unable to Blacklist User; unable to access BLACKLIST.dat", true);
			AbstractServer.logException(e);
		}
	}
	
	/**Determines whether a given IP address is blacklisted. It tests this by comparing
	  *the string paramter to the blacklist configuration file found in the temporary directory.
	  *@param IP The IP address to check against the blacklist configuration file.
	  *@return Returns true if the given IP address is blacklisted, false otherwise.*/
	public boolean isBlackListed(String IP)
	{
		//read from blacklist file to determine if user is blacklisted
		try
		{
			FileInputStream blackListFileIn = new FileInputStream(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "BLACKLIST.dat");
			ObjectInputStream blackListIn = new ObjectInputStream(blackListFileIn);
			
			while(true)
			{
				String IPIn;
				
				try
				{
					IPIn = (String) blackListIn.readObject();
				}
				catch (EOFException e)
				{
					AbstractServer.logException(e);
					blackListIn.close();
					return false;
				}
				
				if(IPIn.equals(IP))
				{
					blackListIn.close();
					return true;
				}
			}
		}
		catch(EOFException e)
		{
			AbstractServer.logException(e);
		}
		catch(Exception e)
		{
			AbstractServer.logException(e);
		}
		
		return false;
	}
	
	/**Removes all records of blacklisted IP addresses by appending an empty String to the blacklist 
	  *configuration file from the server temporary directory.*/
	public void clearBlackListRecord()
	{
		try
		{
			FileOutputStream blackListFileOut = new FileOutputStream(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "BLACKLIST.dat", true);
			ObjectOutputStream blackListOut = new ObjectOutputStream(blackListFileOut);
			blackListOut.writeObject(new String(""));
			blackListOut.close();
			
			this.consoleMng.printConsole("Successfully Relieved Blacklisted Users", false);
		}
		catch(FileNotFoundException e)
		{
			this.consoleMng.printConsole("No Blacklist File Found", true);
			AbstractServer.logException(e);
		}
		catch(IOException e)
		{
			this.consoleMng.printConsole("Unable to Clear Blacklist Record", false);
			AbstractServer.logException(e);
		}
	}
	
	/**Broadcasts a {@code Message} object to all connected clients.
	  *@param message The message to broadcast to all connected clients.*/
	public void broadcast(Message message)
	{
		this.broadcastHlp.broadcastMessage(message);
	}
	
	/**Broadcasts a {@code Command} object to all connected clients.
	  *@param com The command to broadcast to all connected clients.*/
	public void broadcast(Command com)
	{
		this.broadcastHlp.broadcastCommand(com);
	}
	
	/**Display the Message broadcast dialog. Allows the user to broadcast a message, or specifify
	  *an automated server message with specific broadcast frequency.
	  *@see webchatinterface.server.util.BroadcastHelper#showBroadcastMessageDialog()*/
	public void showBroadcastMessageDialog()
	{
		this.broadcastHlp.showBroadcastMessageDialog();
	}
	
	/**Construct and return a 2d object array containing information regarding all the clients
	  *connected to the server.
	  *<p>
	  *[Username][User ID][User IP][Availability][Room]
	  *...
	  *@return a two dimentional array with information regarding each client connected to the
	  *server.*/
	public Object[][] getConnectedUsers()
	{
		int size = ChatRoom.getGlobalMembersSize();
		
		WebChatServerInstance[] connectedClients = ChatRoom.getGlobalMembers();
		
		Object[][] list = new Object[size][5];
		
		size = 0;
		for(WebChatServerInstance client : connectedClients)
		{
			list[size][0] = client.getUsername();
			list[size][1] = client.getUserID();
			list[size][2] = client.getIP();
			list[size][3] = client.getAvailability();
			list[size][4] = client.getRoom().toString();
			size++;
		}
		
		return list;
	}
	
	/**Increments the counter for the number of objects communicated
	  *through the server to clients. This includes all {@code Message}, {@code Command}, and
	  *{@code MultimediaMessage} objects.
	  *@see #objectsSent*/
 	public void addObjectSent()
	{
		this.objectsSent++;
	}
	
	/**Accessor method for the number of objects communicated through the server to
	  *clients. This includes all {@code Message} and {@code Command} objects.
	  *@return the total number of objects broadcasted by the server
	  *@see #objectsSent*/
	public long getObjectsSent()
	{
		return this.objectsSent;
	}
	
	/**Increments the counter for the number of files transfered
	  *through the server to clients.*/
	public void addFilesTransfered()
	{
		this.filesTransfered++;
	}
	
	/**Accessor method for the number of files transfered through the server to
	  *clients.
	  *@return the total number of files transfered by the server*/
	public long getFilesTransfered()
	{
		return this.filesTransfered;
	}
	
	/**Accessor method for the state of the server. If the server is running,
	  *{@code isRunning()} will return true. Otherwise, the method will return false.
	  *@return true if server is running, false if server is suspended*/
	public boolean isRunning()
	{
		return this.RUN;
	}
}