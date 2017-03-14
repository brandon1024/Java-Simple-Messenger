package webchatinterface.client.communication;

import webchatinterface.AbstractIRC;
import webchatinterface.client.AbstractClient;
import webchatinterface.client.authentication.Authenticator;
import webchatinterface.client.communication.filetransfer.FileTransferManager;
import webchatinterface.client.session.Session;
import webchatinterface.client.ui.WebChatClientGUI;
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
	private FileTransferManager transferManager;
	private Socket socket;
	private ObjectInputStream messageIn;
	private ObjectOutputStream messageOut;
	private Session session;
	private ClientUser client;
	private Object[][] connectedUsers;
	private volatile boolean RUN;

	public WebChatClient(WebChatClientGUI parent, Session session) throws IOException
	{
		this.graphicalUserInterface = parent;
		this.transferManager = new FileTransferManager(this.graphicalUserInterface);
		this.session = session;
		this.socket = new Socket(this.session.hostAddress, this.session.portNumber);
		this.messageOut = new ObjectOutputStream(this.socket.getOutputStream());
		this.messageIn = new ObjectInputStream(this.socket.getInputStream());
		this.client = AbstractClient.getClientUser();
		this.RUN = false;
	}
	
	public void start()
	{
		if(this.RUN)
			return;

		this.RUN = true;
		(new Thread(this)).start();
	}
	
	public void run()
	{
		this.requestConnection();
		this.listen();
		this.disconnect();
	}

	private void requestConnection()
	{
		//Send Connection Request Command and Await Connection Authorization
		try
		{
			if(this.session.guest)
			{
				Object[] data = {true, AbstractIRC.CLIENT_VERSION};
				this.send(new Command(Command.CONNECTION_REQUEST, data, "CLIENT", "0"));
			}
			else
			{
				Object[] data = {false, this.session.newAccount, AbstractIRC.CLIENT_VERSION, this.session.emailAddress, this.session.username, this.session.password};
				this.send(new Command(Command.CONNECTION_REQUEST, data, "CLIENT", "0"));
				Authenticator.removeSensitiveInformation(this.session.password);
			}
		}
		catch (IOException e)
		{
			AbstractClient.logException(e);
			this.graphicalUserInterface.disconnect("Unable to send CONNECTION_REQUEST command");
			this.RUN = false;
			return;
		}

		//Await Connection Authorization
		boolean unverified = true;
		while(unverified && this.RUN)
		{
			Object objectIn;
			try
			{
				if((objectIn = this.messageIn.readObject()) == null)
					continue;

				if(objectIn instanceof Command)
				{
					//Get Command ID
					int commandID = ((Command)objectIn).getCommand();

					//If Server Authorized Connection
					if(commandID == Command.CONNECTION_AUTHORIZED)
					{
						Object[] data = (Object[])((Command)objectIn).getMessage();
						this.client.signIn((String)data[0], (String)data[1]);
						this.graphicalUserInterface.connectionAuthorized();
						unverified = false;
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
				this.graphicalUserInterface.disconnect("Connection Failed; invalid I/O streams or incompatible server protocol");
			}
			catch(ConnectionDeniedException e)
			{
				AbstractClient.logException(e);
				this.graphicalUserInterface.disconnect(e.getMessage());
			}
		}
	}
	
	private void listen()
	{
		while(this.RUN)
		{
			Object message;
			try
			{
				if((message = this.messageIn.readObject()) == null)
					continue;

				if(message instanceof Message)
					this.processMessage((Message)message);
				else if(message instanceof Command)
					this.processCommand((Command)message);
				else if(message instanceof TransferBuffer)
					this.transferManager.processTransferBuffer((TransferBuffer)message);
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
				this.graphicalUserInterface.disconnect("Connection Reset: Error Occurred");
				this.RUN = false;
			}
		}
	}
	
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
	
	private void processMessage(Message message)
	{
		this.graphicalUserInterface.displayMessage(message);
	}
	
	private void processCommand(Command com) throws CannotEstablishConnectionException
	{
		switch(com.getCommand())
		{
			//Periodic Connected Users List Update from Server	
			case Command.CONNECTED_USERS:
				this.connectedUsers = (Object[][])com.getMessage();
				break;
			//If Connection Closes, Determine Reason
			case Command.CONNECTION_SUSPENDED:
				if(com.getReason() == Command.REASON_BLACKLISTED)
					this.RUN = false;
				else if(com.getReason() == Command.REASON_INCONSISTENT_USER_ID)
					this.RUN = false;
				else if(com.getReason() == Command.REASON_KICKED)
					this.RUN = false;
				else if(com.getReason() == Command.REASON_CHANNEL_CLOSED)
					this.RUN = false;
				else if(com.getReason() == Command.REASON_SERVER_FULL)
					this.RUN = false;
				else
				{
					try
					{
						this.send(new Command(Command.CONNECTION_SUSPENDED_ACKNOWLEDGE, this.client.getUsername(), this.client.getUserID()));
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
				this.transferManager.executeInboundTransfer(com);
				break;
			//Process Interface Commands
			default:
				this.graphicalUserInterface.processCommand(com);
		}
	}
	
	public synchronized void send(Message message) throws IOException
	{
		//Send Message Object to Server
		this.messageOut.writeObject(message);
		this.messageOut.flush();
	}
	
	public synchronized void send(TransferBuffer message) throws IOException
	{
		//Send Message Object to Server
		this.messageOut.writeObject(message);
		this.messageOut.flush();
	}
	
	public synchronized void send(Command com) throws IOException
	{
		//Send Message Object to Server
		this.messageOut.writeObject(com);
		this.messageOut.flush();
	}
	
	public void send(File file)
	{
		this.transferManager.executeOutboundTransfer(this, file);
	}
	
	public Object[][] getConnectedUsers()
	{
		return this.connectedUsers;
	}
	
	public boolean isRunning()
	{
		return this.RUN;
	}
}