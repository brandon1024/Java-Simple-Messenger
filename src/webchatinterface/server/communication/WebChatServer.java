package webchatinterface.server.communication;

import webchatinterface.server.AbstractServer;
import webchatinterface.server.account.BlacklistManager;
import webchatinterface.server.network.Channel;
import webchatinterface.server.network.ChannelManager;
import webchatinterface.server.ui.WebChatServerGUI;
import webchatinterface.server.ui.components.ConsoleManager;
import webchatinterface.util.Command;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

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
	private ChannelManager channelManager;
	private BroadcastScheduler broadcastScheduler;
	private ServerSocket serverSocket;
	private long objectsSent;
	private long filesTransferred;
	private volatile boolean RUN = true;

	public WebChatServer()
	{
		this.consoleMng = ConsoleManager.getInstance();
		this.channelManager = ChannelManager.getInstance();
		this.broadcastScheduler = BroadcastScheduler.getInstance();
		this.objectsSent = 0;
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
		
		//Start BroadcastScheduler Thread
		this.broadcastScheduler.start();
		
		//Listen for Incoming Connections
		listen();
	}
	
	public void suspend()
	{
		//gracefully stop listening to ServerSocket
		this.broadcastScheduler.stop();
		this.RUN = false;
		
		//disconnect all connected users
		for(Channel channel : this.channelManager.getGlobalChannels())
		{
			for(WebChatServerInstance client : channel.getChannelMembers())
				client.disconnect(Command.REASON_SERVER_CLOSED);
		}

		//close ServerSocket
		try
		{
			if(this.serverSocket != null)
				this.serverSocket.close();
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
				this.serverSocket = new ServerSocket(AbstractServer.serverPortNumber);
			else
				this.serverSocket = new ServerSocket(AbstractServer.serverPortNumber, 50, InetAddress.getByName(AbstractServer.serverBindIPAddress));
			
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
					socket = this.serverSocket.accept();
					
					if(this.channelManager.getGlobalChannelSize() >= AbstractServer.maxConnectedUsers)
					{
						WebChatServerInstance chatCom = new WebChatServerInstance(this, socket);
						chatCom.start();
						this.disconnectUser(chatCom, Command.REASON_SERVER_FULL);
						this.consoleMng.printConsole("User Prevented from Connecting; server full (" + AbstractServer.maxConnectedUsers + " connections)", true);
						loopCTRL = true;
					}
					else if(BlacklistManager.isBlacklisted(socket.getLocalAddress().getHostName()))
					{
						WebChatServerInstance chatCom = new WebChatServerInstance(this, socket);
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
				WebChatServerInstance chatCom = new WebChatServerInstance(this, socket);
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
	
	public void showBroadcastMessageDialog(WebChatServerGUI parent)
	{
		this.broadcastScheduler.showEditScheduledMessagesDialog(parent);
	}
	
	public Object[][] getConnectedUsers()
	{
		ArrayList<WebChatServerInstance> clients = new ArrayList<WebChatServerInstance>();
		for(Channel channel : this.channelManager.getGlobalChannels())
		{
			for(WebChatServerInstance client : channel.getChannelMembers())
			{
				clients.add(client);
			}
		}

		Object[][] list = new Object[clients.size()][5];
		for(int index = 0; index < clients.size(); index++)
		{
			list[index][0] = clients.get(index).getUsername();
			list[index][1] = clients.get(index).getUserID();
			list[index][2] = clients.get(index).getIP();
			list[index][3] = clients.get(index).getAvailability();
			list[index][4] = clients.get(index).getChannel().toString();
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
		this.filesTransferred++;
	}
	
	public long getFilesTransferred()
	{
		return this.filesTransferred;
	}
	
	public boolean isRunning()
	{
		return this.RUN;
	}
}