package webchatinterface.server.communication;

import webchatinterface.server.AbstractServer;
import webchatinterface.server.network.ChatRoom;
import webchatinterface.server.ui.components.ConsoleManager;
import webchatinterface.util.TransportEntity;

import java.io.IOException;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
  *<p>
  *The {@code BroadcastHelper} class is designed to simply broadcasting Message
  *and Command objects to clients connected to the server. It also facilitates
  *scheduled server message broadcasting.
  */

public class BroadcastHelper
{
	private ConsoleManager consoleMng;
	
	public BroadcastHelper(ConsoleManager consoleMng)
	{
		this.consoleMng = consoleMng;
	}

	public synchronized void broadcastMessage(TransportEntity message, ChatRoom room)
	{
		//for each instance in ConnectionArray
		WebChatServerInstance[] clients = room.getConnectedClients();

		for(WebChatServerInstance client : clients)
		{
			try
			{
				client.send(message);
			}
			catch (IOException e)
			{
				this.consoleMng.printConsole("Unable to Broadcast Message to: " + client.toString(), true);
				AbstractServer.logException(e);
			}
		}
	}
	
	public synchronized void broadcastMessage(TransportEntity message)
	{
		//for each instance in ConnectionArray
		WebChatServerInstance[] clients = ChatRoom.getGlobalMembers();
			
		for(WebChatServerInstance client : clients)
		{
			try
			{
				client.send(message);
			}
			catch (IOException e)
			{
				this.consoleMng.printConsole("Unable to Broadcast Message to: " + client.toString(), true);
				AbstractServer.logException(e);
			}
		}
	}
}
