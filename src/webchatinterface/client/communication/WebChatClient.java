package webchatinterface.client.communication;

import webchatinterface.AbstractIRC;
import webchatinterface.client.AbstractClient;
import webchatinterface.client.ui.WebChatClientGUI;
import webchatinterface.client.util.authentication.AuthenticationException;
import webchatinterface.client.util.authentication.Authenticator;
import webchatinterface.client.util.filetransfer.FileTransferExecutor;
import webchatinterface.util.ClientUser;
import webchatinterface.util.Command;
import webchatinterface.util.Message;
import webchatinterface.util.TransferBuffer;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
  *<p>
  *The {@code WebChatClient} class provides the the interface with which an application can 
  *communicate {@code Message} objects to a dedicated server over a TCP connection. The {@code 
  *WebChatClient} class is threaded, and thus must be run by invoking its {@code run()} method.
  *<p>
  *Once the {@code run()} is invoked, the client will listen to the socket connection until the 
  *{@code disconnect()} method is invoked, or an exception is thrown while listening to the socket.
  */

public class WebChatClient implements Runnable
{
	private WebChatClientGUI graphicalUserInterface;
	private Socket socket;
	private ObjectInputStream messageIn;
	private ObjectOutputStream messageOut;
	private Authenticator auth;
	private ClientUser client;
	private Object[][] connectedUsers;
	private ArrayList<FileTransferExecutor> ongoingTransfers;
	private volatile boolean RUN = false;
	
	/**Builds a {@code WebChatClient} object. Establishes framework for communication over TCP with 
	  *a dedicated server application.
	  *<p>
	  *A direct connection with the server is established with the specified host name address and 
	  *port number. If a connection cannot be established for any reason, an IOException is thrown.
	  *@throws	IOException if the client cannot establish a connection with the server
	  *@param 	parent 		the graphical user interface with which the client can
	  *		display inbound messages. The graphical user interface must implement
	  *		the appropriate {@code appendToChat()} and {@code processCommand()} methods.
	 * @throws AuthenticationException thrown if an error occurred while attempting to instantiate the client
	  */
	public WebChatClient(WebChatClientGUI parent, Authenticator auth) throws IOException, AuthenticationException
	{
		this.graphicalUserInterface = parent;
		this.auth = auth;
		this.socket = new Socket(this.auth.getHostAddress(), this.auth.getPortNumber());
		this.messageOut = new ObjectOutputStream(this.socket.getOutputStream());
		this.messageIn = new ObjectInputStream(this.socket.getInputStream());
		this.client = AbstractClient.getClientUser();
		this.ongoingTransfers = new ArrayList<FileTransferExecutor>();
	}
	
	/**Starts the WebChatClient thread, immediately invoking run().*/
	public void start()
	{
		if(this.isRunning())
			return;

		this.RUN = true;
		(new Thread(this)).start();
	}
	
	/**Runs the client thread, allowing message objects to be sent and received by the server.*/
	@Override
	public void run()
	{
		this.listen();
		this.disconnect();
	}
	
	/**Listens to {@code ObjectInputStream} for inbound Message objects. Forwards received messages 
	  *to the {@code WebChatClientGUI}.*/
	private void listen()
	{
		try
		{
			this.requestConnection();
		}
		catch(ConnectionDeniedException e)
		{
			AbstractClient.logException(e);
			this.graphicalUserInterface.disconnect(e.getMessage());
			this.RUN = false;
		}
		catch(CannotEstablishConnectionException e)
		{
			AbstractClient.logException(e);
			this.graphicalUserInterface.disconnect("Connection Reset: Unable to Communicate with the Server");
			this.RUN = false;
		}
		
		while(this.RUN)
		{
			Object message;
			try
			{
				//Read Stream
				message = this.messageIn.readObject();
				
				//If a transfer buffer is received
				if(message != null && message instanceof TransferBuffer)
					this.processTransferBuffer((TransferBuffer)message);
				//If Message Received, append to chat window
				else if(message != null && message instanceof Message)
					this.processMessage((Message)message);
				
				//If Command Received, Process Command
				if(message != null && message instanceof Command)
					this.processCommand((Command)message);
			}
			catch(SocketException e)
			{
				if(!this.socket.isClosed())
				{
					AbstractClient.logException(e);
					this.graphicalUserInterface.disconnect("Connection Reset: Cant Reach Host");
					this.RUN = false;
				}
			}
			catch(IOException e)
			{
				AbstractClient.logException(e);
				this.graphicalUserInterface.disconnect("Connection Reset: Unable to Communicate with the Server");
				this.RUN = false;
			}
			catch(ClassNotFoundException e)
			{
				AbstractClient.logException(e);
				this.graphicalUserInterface.disconnect("Connection Reset: Incompatible Client");
				this.RUN = false;
			}
			catch(Exception e)
			{
				AbstractClient.logException(e);
				this.graphicalUserInterface.disconnect("Connection Reset: Error Occured");
				this.RUN = false;
			}
		}
	}
	
	/**Disconnects the client from the server. Closes the client thread, the client socket, and 
	  *object streams.*/
	public void disconnect()
	{
		try
		{
			this.RUN = false;
			
			this.messageIn.close();
			this.messageOut.close();
			this.socket.close();
		}
		catch (IOException e)
		{
			AbstractClient.logException(e);
		}
	}
	
	/***/
	private void requestConnection() throws CannotEstablishConnectionException
	{
		//Send Connection Request Command and Await Connection Authorization
		try
		{
			if(this.auth.isGuest())
			{
				Object[] data = {this.auth.isGuest(), AbstractIRC.CLIENT_VERSION};
				this.send(new Command(Command.CONNECTION_REQUEST, data, "CLIENT", "0"));
			}
			else
			{
				Object[] data = {this.auth.isGuest(), auth.isNewMember(), AbstractIRC.CLIENT_VERSION, auth.getEmailAddress(), auth.getUsername(), auth.getPassword()};
				this.send(new Command(Command.CONNECTION_REQUEST, data, "CLIENT", "0"));
				this.auth.removeSensitiveInformation();
			}
			
			this.auth = null;
		}
		catch (IOException e)
		{
			AbstractClient.logException(e);
			throw new CannotEstablishConnectionException("Unable to send CONNECTION_REQUEST command");
		}
		
		//Await Connection Authorization
		boolean clientVerified = false;
		while(!clientVerified)
		{
			try
			{
				//Read Object From Stream
				Object objectIn = this.messageIn.readObject();
			
				if(objectIn != null && objectIn instanceof Command)
				{
					//Get Command ID
					int commandID = ((Command)objectIn).getCommand();
					
					//If Server Authorized Connection
					if(commandID == Command.CONNECTION_AUTHORIZED)
					{
						Object[] data = (Object[])((Command)objectIn).getMessage();
						this.client.signIn((String)data[0], (String)data[1]);
						this.graphicalUserInterface.connectionAuthorized();
						clientVerified = true;
					}
					//If Server Denied Connection
					else if(commandID == Command.CONNECTION_DENIED)
					{
						switch(((Command)objectIn).getReason())
						{
							case Command.REASON_BLACKLISTED:
								throw new ConnectionDeniedException("Connection Denied; Blacklisted Account");
							case Command.REASON_INCOMPATIBLE_CLIENT:
								throw new ConnectionDeniedException("Connection Denied; Incompatible Client Version");
							case Command.REASON_INCORRECT_CREDENTIALS:
								throw new ConnectionDeniedException("Connection Denied; Incorrect Username or Password");
							case Command.REASON_SERVER_FULL:
								throw new ConnectionDeniedException("Connection Denied; Server is Full");
							case Command.REASON_USERNAME_EMAIL_ALREADY_EXISTS:
								throw new ConnectionDeniedException("Connection Denied; Email Address or Username Already Exists");
							case Command.REASON_GENERIC:
								throw new ConnectionDeniedException("Connection Denied; Reason Unknown");
						}
					}
				}
			}
			catch (ClassNotFoundException | IOException e)
			{
				AbstractClient.logException(e);
				throw new CannotEstablishConnectionException("Connection Failed; invalid I/O streams or incompatible server protocol");
			}
		}
	}
	
	/***/
	private void processMessage(Message message)
	{
		this.graphicalUserInterface.displayMessage(message);
	}
	
	/***/
	private void processTransferBuffer(TransferBuffer buffer)
	{
		//Get TransferBuffer ID
		String messageTransferID = buffer.getTransferID();
		Iterator<FileTransferExecutor> iterator = this.ongoingTransfers.iterator();
		
		//Iterate All Ongoing File Transfers
		while(iterator.hasNext())
		{
			FileTransferExecutor next = iterator.next();
			
			//If Transfer is Running, Pass on TransferBuffer
			if(next.isRunning())
			{
				if(next.getTransferID().equals(messageTransferID))
					next.bufferReceived(buffer);
			}
			//Else Remove From List of Ongoing Transfers
			else
				iterator.remove();
		}
	}
	
	/***/
	private void processCommand(Command com) throws CannotEstablishConnectionException
	{
		switch(com.getCommand())
		{
			//Periodic Connected Users List Update from Server	
			case Command.CONNECTED_USERS:
				this.setConnectedUsers((Object[][])com.getMessage());
				break;
			//If Connection Closes, Determine Reason
			case Command.CONNECTION_SUSPENDED:
				if(com.getReason() == Command.REASON_BLACKLISTED)
					this.RUN = false;
				else if(com.getReason() == Command.REASON_INCONSISTENT_USER_ID)
					this.RUN = false;
				else if(com.getReason() == Command.REASON_KICKED)
					this.RUN = false;
				else if(com.getReason() == Command.REASON_ROOM_CLOSED)
					this.RUN = false;
				else if(com.getReason() == Command.REASON_SERVER_FULL)
					this.RUN = false;
				else
				{
					try
					{
						this.send(new Command(Command.CONNECTION_SUSPENDED_AWKNOWLEDGE, this.client.getUsername(), this.client.getUserID()));
					}
					catch(IOException e)
					{
						AbstractClient.logException(e);
						this.RUN = false;
					}
				}
				this.graphicalUserInterface.processCommand(com);
				break;
			//If Client Receives File Transfer Manifest
			case Command.FILE_TRANSFER:
				FileTransferExecutor FileTransferExecutor = new FileTransferExecutor(this.graphicalUserInterface, this);
				FileTransferExecutor.start(com);
				this.ongoingTransfers.add(FileTransferExecutor);
				break;
			//Process Interface Commands
			default:
				this.graphicalUserInterface.processCommand(com);
		}
	}
	
	/**Sends a message object to the server.
	  *@param 	message 	the message to be communicated to the server
	  *@throws 	IOException if an exception occured while writing to the
	  *		object stream*/
	public synchronized void send(Message message) throws IOException
	{
		//Send Message Object to Server
		this.messageOut.writeObject(message);
		this.messageOut.flush();
	}
	
	/**Sends a byte array message object to the server.
	  *@param 	message 	the message to be communicated to the server
	  *@throws 	IOException if an exception occured while writing to the
	  *		object stream*/
	public synchronized void send(TransferBuffer message) throws IOException
	{
		//Send Message Object to Server
		this.messageOut.writeObject(message);
		this.messageOut.flush();
	}
	
	/**Sends a command object to the server.
	  *@param 	com 	the command to be communicated to the server
	  *@throws 	IOException if an exception occured while writing to the
	  *		object stream*/
	public synchronized void send(Command com) throws IOException
	{
		//Send Message Object to Server
		this.messageOut.writeObject(com);
		this.messageOut.flush();
	}
	
	/**Send a file from local storage to the server. Initializes a new FileTransferExecutor
	  *thread.
	  *@param 	file 	the file to be sent to the server
	  *@see 	webchatinterface.client.util.filetransfer.FileTransferExecutor*/
	public synchronized void send(File file)
	{
		(new FileTransferExecutor(this.graphicalUserInterface, this)).start(file);
	}
	
	/**Accessor method for the Object array representing a list of users connected to the
	  *server, along with their information.
	  *<p>
	  *[Username][User ID][User IP][Availability][Room]
	  *@return 		an array containing a list of users connected to the server*/
	public Object[][] getConnectedUsers()
	{
		return this.connectedUsers;
	}
	
	/**Mutator method for the Object array representing a list of users connected to the
	  *server.
	  *@param 	newConnectedUsers 	an array containing a list of users connected to the server*/
	private void setConnectedUsers(Object[][] newConnectedUsers)
	{
		this.connectedUsers = newConnectedUsers;
	}
	
	/**Accessor method for the state of the client thread thread. If the client is running,
	  *{@code isRunning()} will return true. Otherwise, the method will return false.
	  *@return true if client is running, false if client is suspended*/
	public boolean isRunning()
	{
		return this.RUN;
	}
}