package webchatinterface.server.util;

import webchatinterface.AbstractIRC;
import webchatinterface.helpers.DataHelper;
import webchatinterface.server.AbstractServer;
import webchatinterface.server.communication.WebChatServer;
import webchatinterface.server.network.ChannelManager;
import webchatinterface.server.ui.components.UsageMonitor;

import java.awt.*;

public class UsageMonitorManager implements Runnable
{
	private Runtime runtime;
	private WebChatServer server;
	private ChannelManager channelManager;
	private UsageMonitor usageMonitorPanel;
	private int serverUpTime;

	public UsageMonitorManager()
	{
		this.runtime = Runtime.getRuntime();
		this.server = null;
		this.channelManager = ChannelManager.getInstance();
		this.usageMonitorPanel = UsageMonitor.getInstance();
		this.serverUpTime = 0;
	}

	public void start(WebChatServer server)
	{
		this.server = server;
		(new Thread(this)).start();
	}

	public void stop()
	{
		this.server = null;
		this.serverUpTime = 0;
	}

	public void run()
	{
		while(true)
		{
			this.usageMonitorPanel.changeUsedMemory(DataHelper.formatBytes(this.getUsedMemory(), 2));
			this.usageMonitorPanel.changeFreeMemory(DataHelper.formatBytes(this.runtime.freeMemory(), 2));
			this.usageMonitorPanel.changeTotalMemory(DataHelper.formatBytes(this.runtime.totalMemory(), 2));
			this.usageMonitorPanel.changeMaxMemory(DataHelper.formatBytes(this.runtime.maxMemory(), 2));
			this.usageMonitorPanel.changeMessagesSent(this.server != null ? this.server.getObjectsSent() : 0L);
			this.usageMonitorPanel.changeFilesTransferred(this.server != null ? this.server.getFilesTransferred() : 0L);
			this.usageMonitorPanel.changePortNumber(this.server != null ? AbstractServer.serverPortNumber : 0);
			this.usageMonitorPanel.changeMaxConnections(this.server != null ? AbstractServer.maxConnectedUsers : 0);
			this.usageMonitorPanel.changeServerVersion(AbstractIRC.SERVER_VERSION);
			this.usageMonitorPanel.changeClientVersion(AbstractIRC.CLIENT_VERSION);
			this.usageMonitorPanel.changeStatus(this.server != null ? "Running" : "Suspended");
			this.usageMonitorPanel.changeMemoryUsageValue(this.runtime.totalMemory() != 0 ? (int)(this.getUsedMemory() * 100 / this.runtime.totalMemory()) : 0);
			this.usageMonitorPanel.changeMemoryUsageText(DataHelper.formatBytes(this.getUsedMemory(), 2) + "/" + DataHelper.formatBytes(this.runtime.totalMemory(), 2));
			this.usageMonitorPanel.changeServerUsageValue(this.server != null ? (AbstractServer.maxConnectedUsers != 0 ? this.channelManager.getGlobalChannelSize() * 100 / AbstractServer.maxConnectedUsers : 0) : 0);
			this.usageMonitorPanel.changeServerUsageText(this.server != null ? this.channelManager.getGlobalChannelSize() + "/" + AbstractServer.maxConnectedUsers : "Suspended");
			this.usageMonitorPanel.changeServerUpTimeText(this.serverUpTime());
			this.usageMonitorPanel.changeAvailableProcessors(this.runtime.availableProcessors());

			if(this.runtime.totalMemory() != 0 && (int)(this.getUsedMemory() * 100 / this.runtime.totalMemory()) >= 75)
				this.usageMonitorPanel.changeMemoryUsageForeground(Color.RED);
			else
				this.usageMonitorPanel.changeMemoryUsageForeground(new Color(0,204,0));

			if(AbstractServer.maxConnectedUsers != 0 && this.channelManager.getGlobalChannelSize() * 100 / AbstractServer.maxConnectedUsers >= 75)
				this.usageMonitorPanel.changeServerUsageForeground(Color.RED);
			else
				this.usageMonitorPanel.changeServerUsageForeground(new Color(0,204,0));

			try
			{
				Thread.sleep(1000);

				if(this.server != null)
					this.serverUpTime++;
			}
			catch(InterruptedException e)
			{
				AbstractServer.logException(e);
			}
		}
	}

	private String serverUpTime()
	{
		int time = this.serverUpTime;

		int days = time / 86400;
		int hours = (time % 86400) / 3600;
		int minutes = ((time % 86400) % 3600) / 60;
		int seconds = ((time % 86400) % 3600) % 60;

		return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
	}

	private long getUsedMemory()
	{
		return this.runtime.totalMemory() - this.runtime.freeMemory();
	}

	public static UsageMonitorManager getInstance()
	{
		return UsageMonitorManager.InstanceHolder.INSTANCE;
	}

	private static class InstanceHolder
	{
		private static final UsageMonitorManager INSTANCE = new UsageMonitorManager();
	}
}
