package webchatinterface.server;

import webchatinterface.server.communication.WebChatServerInstance;
import webchatinterface.server.ui.components.ConsoleManager;
import webchatinterface.server.account.BlacklistManager;
import webchatinterface.server.communication.BroadcastHelper;
import webchatinterface.server.network.ChatRoom;
import webchatinterface.util.Command;

import java.io.IOException;
import java.net.*;

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
	private ConsoleManager consoleMng;
	private BroadcastHelper broadcastHlp;
	private ServerSocket servSocket;
	private long objectsSent;
	private long filesTransfered;
	private volatile boolean RUN = true;

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
			this.consoleMng.printConsole("Using Port " + AbstractServer.serverPortNumber, false);
		
		//Start BroadcastHelper Thread
		this.broadcastHlp.start();
		
		//Listen for Incoming Connections
		listen();
	}
	
	public void suspend()
	{
		//gracefully stop listening to ServerSocket
		this.broadcastHlp.stop();
		this.RUN = false;
		
		//disconnect all connected users
		for(WebChatServerInstance member : ChatRoom.getGlobalMembers())
			member.disconnect(Command.REASON_SERVER_CLOSED);
		
		//close ServerSocket
		try
		{
			if(this.servSocket != null)
				this.servSocket.close();
		}
		catch (IOException e)
		{
			AbstractServer.logException(e);
		}
		
		this.consoleMng.printConsole("Server Suspended", false);
	}
	
	private void listen()
	{
		try
		{
			//Create Server Socket on Specified Port
			this.consoleMng.printConsole("Opening Socket on Port " + AbstractServer.serverPortNumber, false);
			
			if(AbstractServer.serverBindIPAddress.equals("default"))
				this.servSocket = new ServerSocket(AbstractServer.serverPortNumber);
			else
				this.servSocket = new ServerSocket(AbstractServer.serverPortNumber, 50, InetAddress.getByName(AbstractServer.serverBindIPAddress));
			
			this.consoleMng.printConsole("Successfully Opened Socket on Port " + AbstractServer.serverPortNumber, false);
			this.consoleMng.printConsole("Awaiting Client Connection...", false);
			
			//While Server is Running
			while(this.RUN)
			{
				boolean loopCTRL;
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
					else if(BlacklistManager.isBlacklisted(socket.getLocalAddress().getHostName()))
					{
						WebChatServerInstance chatCom = new WebChatServerInstance(this, this.broadcastHlp, this.consoleMng, socket);
						chatCom.start();
						this.disconnectUser(chatCom, Command.REASON_BLACKLISTED);
						this.consoleMng.printConsole("User Prevented from Connecting; blacklisted user", true);
						socket.close();
						loopCTRL = true;
					}
					else
						loopCTRL = false;
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
			this.consoleMng.printConsole("Critical Error Occurred; The server bind IP address could not be resolved.", true);
			AbstractServer.logException(e);
		}
		catch(IOException e)
		{
			this.consoleMng.printConsole("Critical Error Occurred; I/O error Occurred when opening server socket", true);
			AbstractServer.logException(e);
		}
		catch(Exception e)
		{
			this.consoleMng.printConsole("Critical Error Occurred; Restart Server", true);
			AbstractServer.logException(e);
		}
	}
	
	public void disconnectUser(WebChatServerInstance clientServerConnection, int reason)
	{
		clientServerConnection.disconnect(reason);
	}

	public void blacklistUser(WebChatServerInstance clientServerConnection)
	{
		BlacklistManager.blacklistIPAddress(clientServerConnection.getIP());
	}
	
	public void showBroadcastMessageDialog()
	{
		this.broadcastHlp.showBroadcastMessageDialog();
	}
	
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
	
	public void addObjectSent()
	{
		this.objectsSent++;
	}
	
	public long getObjectsSent()
	{
		return this.objectsSent;
	}
	
	public void addFilesTransferred()
	{
		this.filesTransfered++;
	}
	
	public long getFilesTransferred()
	{
		return this.filesTransfered;
	}
	
	public boolean isRunning()
	{
		return this.RUN;
	}
}