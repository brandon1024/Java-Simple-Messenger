package webchatinterface.server.util;

import webchatinterface.AbstractIRC;
import webchatinterface.helpers.DataHelper;
import webchatinterface.helpers.TimeHelper;
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
	private volatile boolean RUN;

	public UsageMonitorManager()
	{
		this.runtime = Runtime.getRuntime();
		this.server = null;
		this.channelManager = ChannelManager.getInstance();
		this.usageMonitorPanel = UsageMonitor.getInstance();
		this.serverUpTime = 0;
		this.RUN = false;
	}

	public void start(WebChatServer server)
	{
		this.server = server;
		if(this.RUN)
			return;

		(new Thread(this)).start();
		this.RUN = true;
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
			this.usageMonitorPanel.changeUsedMemory(this.getUsedMemoryAsString());
			this.usageMonitorPanel.changeFreeMemory(this.getFreeMemoryAsString());
			this.usageMonitorPanel.changeTotalMemory(this.getTotalMemoryAsString());
			this.usageMonitorPanel.changeMaxMemory(this.getMaxMemoryAsString());
			this.usageMonitorPanel.changeMessagesSent(this.getMessagesSent());
			this.usageMonitorPanel.changeFilesTransferred(this.getFilesTransferred());
			this.usageMonitorPanel.changePortNumber(this.getServerPortNumber());
			this.usageMonitorPanel.changeMaxConnections(this.getMaxConnections());
			this.usageMonitorPanel.changeServerVersion(AbstractIRC.SERVER_VERSION);
			this.usageMonitorPanel.changeClientVersion(AbstractIRC.CLIENT_VERSION);
			this.usageMonitorPanel.changeStatus(this.getServerStatus());
			this.usageMonitorPanel.changeMemoryUsageValue(this.getMemoryUsageValue());
			this.usageMonitorPanel.changeMemoryUsageText(this.getMemoryUsageAsString());
			this.usageMonitorPanel.changeServerUsageValue(this.getServerUsageValue());
			this.usageMonitorPanel.changeServerUsageText(this.getServerUsageAsString());
			this.usageMonitorPanel.changeServerUpTimeText(this.getServerUpTimeAsString());
			this.usageMonitorPanel.changeAvailableProcessors(this.runtime.availableProcessors());
			this.usageMonitorPanel.changeMemoryUsageForeground(this.getMemoryUsageColor());
			this.usageMonitorPanel.changeServerUsageForeground(this.getServerUsageColor());

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

	private long getUsedMemory()
	{
		return this.runtime.totalMemory() - this.runtime.freeMemory();
	}

	private String getUsedMemoryAsString()
	{
		return DataHelper.formatBytes(this.getUsedMemory(), 2);
	}

	private String getFreeMemoryAsString()
	{
		return DataHelper.formatBytes(this.runtime.freeMemory(), 2);
	}

	private String getTotalMemoryAsString()
	{
		return DataHelper.formatBytes(this.runtime.totalMemory(), 2);
	}

	private String getMaxMemoryAsString()
	{
		return DataHelper.formatBytes(this.runtime.maxMemory(), 2);
	}

	private long getMessagesSent()
	{
		return this.server != null ? this.server.getObjectsSent() : 0L;
	}

	private long getFilesTransferred()
	{
		return this.server != null ? this.server.getFilesTransferred() : 0L;
	}

	private int getServerPortNumber()
	{
		return this.server != null ? AbstractServer.serverPortNumber : 0;
	}

	private int getMaxConnections()
	{
		return this.server != null ? AbstractServer.maxConnectedUsers : 0;
	}

	private String getServerStatus()
	{
		return this.server != null ? "Running" : "Suspended";
	}

	private int getMemoryUsageValue()
	{
		if(this.runtime.totalMemory() == 0)
			return 0;

		return (int)(this.getUsedMemory() * 100 / this.runtime.totalMemory());
	}

	private String getMemoryUsageAsString()
	{
		String usedMemory = DataHelper.formatBytes(this.getUsedMemory(), 2);
		String totalMemory = DataHelper.formatBytes(this.runtime.totalMemory(), 2);
		return usedMemory + "/" + totalMemory;
	}

	private int getServerUsageValue()
	{
		if(this.server == null)
			return 0;
		if(AbstractServer.maxConnectedUsers == 0)
			return 0;

		return this.channelManager.getGlobalChannelSize() * 100 / AbstractServer.maxConnectedUsers;
	}

	private String getServerUsageAsString()
	{
		if(this.server == null)
			return "Suspended";

		return this.channelManager.getGlobalChannelSize() + "/" + AbstractServer.maxConnectedUsers;
	}

	private String getServerUpTimeAsString()
	{
		return TimeHelper.formatIntegerTime(this.serverUpTime);
	}

	private Color getMemoryUsageColor()
	{
		if(this.getMemoryUsageValue() >= 75)
			return Color.RED;

		return new Color(0,204,0);
	}

	private Color getServerUsageColor()
	{
		if(this.getServerUsageValue() >= 75)
			return Color.RED;

		return new Color(0,204,0);
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
