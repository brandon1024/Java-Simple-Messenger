package webchatinterface.server.communication;

import webchatinterface.server.AbstractServer;
import webchatinterface.server.network.Channel;
import webchatinterface.server.network.ChannelManager;
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
	
	private BroadcastHelper()
	{
		this.consoleMng = ConsoleManager.getInstance();
	}

	public synchronized void broadcastMessage(TransportEntity message, Channel channel)
	{
		//for each instance in ConnectionArray
		WebChatServerInstance[] clients = channel.getChannelMembers();
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
		Channel[] channels = ChannelManager.getInstance().getGlobalChannels();
		for(Channel channel : channels)
		{
			for(WebChatServerInstance client : channel.getChannelMembers())
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

	public static BroadcastHelper getInstance()
	{
		return InstanceHolder.INSTANCE;
	}

	private static class InstanceHolder
	{
		private static final BroadcastHelper INSTANCE = new BroadcastHelper();
	}
}
