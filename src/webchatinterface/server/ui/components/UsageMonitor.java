package webchatinterface.server.ui.components;

import webchatinterface.AbstractIRC;
import webchatinterface.server.AbstractServer;
import webchatinterface.server.communication.WebChatServer;
import webchatinterface.server.network.ChatRoom;

import javax.swing.*;
import java.awt.*;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The UsageMonitor class extends the Java JPanel. It is responsible for
  *acquiring and displaying useful data with regards to the server application
  *memory allocations, processes, and server status.
  *<p>
  *The implementing class can simply add the UsageMonitor object to the frame.
  */

public class UsageMonitor extends JPanel implements Runnable
{
	private Runtime runtime;
	private WebChatServer server;
	private JLabel usedMem;
	private JLabel freeMem;
	private JLabel totalMem;
	private JLabel maxMem;
	private JLabel messagesSent;
	private JLabel port;
	private JLabel maxConnections;
	private JLabel version;
	private JLabel clientVersion;
	private JLabel status;
	private JLabel filesTransferred;
	private JLabel upTime;
	private JProgressBar memUsage;
	private JProgressBar serverUsage;
	private int serverUpTime;
	
	private UsageMonitor()
	{
		//Build Container Object
		super();
		super.setBorder(BorderFactory.createTitledBorder("Resource Monitor"));
		super.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		this.runtime = Runtime.getRuntime();
		this.serverUpTime = 0;
		
		//Build Inner Container Objects
		JPanel memory = new JPanel();
		memory.setBorder(BorderFactory.createTitledBorder("Memory"));
		JPanel serverInfo = new JPanel();
		serverInfo.setBorder(BorderFactory.createTitledBorder("Server Usage"));
		JPanel usageInfo = new JPanel();
		usageInfo.setLayout(new BoxLayout(usageInfo, BoxLayout.LINE_AXIS));
		JPanel upTimeInfo = new JPanel();
		upTimeInfo.setLayout(new BoxLayout(upTimeInfo, BoxLayout.LINE_AXIS));
		JPanel visualInfo = new JPanel();
		visualInfo.setLayout(new BoxLayout(visualInfo, BoxLayout.LINE_AXIS));
		
		JPanel col1 = new JPanel();
		col1.setLayout(new GridLayout(2,1, 5, 0));
		JPanel col2 = new JPanel();
		col2.setLayout(new GridLayout(2,1, 5, 0));
		JPanel col3 = new JPanel();
		col3.setLayout(new GridLayout(2,1, 5, 0));
		JPanel col4 = new JPanel();
		col4.setLayout(new GridLayout(2,1, 5, 0));
		JPanel col5 = new JPanel();
		col5.setLayout(new GridLayout(2,1, 5, 0));
		JPanel col6 = new JPanel();
		col6.setLayout(new GridLayout(2,1, 5, 0));
		
		//Build JLabels
		JLabel availableProcessors = new JLabel("Available Processors: " + this.availableProcessors());
		this.usedMem = new JLabel("Used Memory: 0");
		this.freeMem = new JLabel("Free Memory: 0");
		this.totalMem = new JLabel("Total Memory: 0");
		this.maxMem = new JLabel("Max Memory: 0");
		
		this.messagesSent = new JLabel("Messages Sent: 0");
		this.filesTransferred = new JLabel("Files Transferred: 0");
		this.port = new JLabel("Server Port: 0");
		this.maxConnections = new JLabel("Max Connections: 0");
		this.version = new JLabel("Server Version: 0.0.0");
		this.clientVersion = new JLabel("Client Version: 0.0.0");
		this.status = new JLabel("Server Status: Suspended");
		this.upTime = new JLabel("Server Up Time: 0d 0h 0m 0s");
		
		this.memUsage = new JProgressBar(0, 100);
		this.serverUsage = new JProgressBar(0, 100);
		this.memUsage.setStringPainted(true);
		this.serverUsage.setStringPainted(true);
		this.memUsage.setForeground(new Color(0,204,0));
		this.serverUsage.setForeground(new Color(0,204,0));
		this.memUsage.setValue(0);
		this.serverUsage.setValue(0);
		
		col1.add(this.usedMem);
		col1.add(this.freeMem);
		
		col2.add(this.totalMem);
		col2.add(this.maxMem);
		
		col3.add(this.messagesSent);
		col3.add(this.filesTransferred);
		
		col4.add(this.port);
		col4.add(this.maxConnections);
		
		col5.add(this.version);
		col5.add(this.clientVersion);
		
		col6.add(this.status);
		col6.add(availableProcessors);
		
		visualInfo.add(new JLabel("Memory Usage: "));
		visualInfo.add(memUsage);
		visualInfo.add(Box.createHorizontalStrut(15));
		visualInfo.add(new JLabel("Server Connection Usage: "));
		visualInfo.add(serverUsage);
		
		upTimeInfo.add(upTime);
		upTimeInfo.add(Box.createHorizontalGlue());
		
		//Add Containers to main Container
		memory.add(col1);
		memory.add(Box.createRigidArea(new Dimension(10,0)));
		memory.add(col2);
		serverInfo.add(col3);
		serverInfo.add(Box.createRigidArea(new Dimension(10,0)));
		serverInfo.add(col4);
		serverInfo.add(Box.createRigidArea(new Dimension(10,0)));
		serverInfo.add(col5);
		serverInfo.add(Box.createRigidArea(new Dimension(10,0)));
		serverInfo.add(col6);
		
		usageInfo.add(memory);
		usageInfo.add(serverInfo);
		
		super.add(usageInfo);
		super.add(upTimeInfo);
		super.add(visualInfo);
	}
	
	public void runServer(WebChatServer server)
	{
		this.server = server;
	}
	
	public void suspendServer()
	{
		this.server = null;
		this.serverUpTime = 0;
	}
	
	public void run()
	{
		while(true)
		{
			this.usedMem.setText("Used Memory: " + this.usedMemory() + " MB");
			this.freeMem.setText("Free Memory: " + this.freeMemory() + " MB");
			this.totalMem.setText("Total Memory: " + this.totalMemory() + " MB");
			this.maxMem.setText("Max Memory: " + this.maxMemory() + " MB");
			
			this.messagesSent.setText("Messages Sent: " + this.objectsCommunicated());
			this.filesTransferred.setText("Files Transferred: " + this.filesCommunicated());
			this.port.setText("Server Port: " + this.serverPort());
			this.maxConnections.setText("Max Connections: " + this.serverMaxConnections());
			this.version.setText("Server Version: " + this.serverVersion());
			this.clientVersion.setText("Client Version: " + this.clientVersion());
			this.status.setText("Server Status: " + this.serverStatus());
			
			this.memUsage.setValue((int)(this.usedMemory() * 100 / this.totalMemory()));
			this.serverUsage.setValue(this.server != null ? (this.serverConnectedUsers() * 100 / this.serverMaxConnections()) : 0);
			this.serverUsage.setString(this.server != null ? this.serverConnectedUsers() + "/" + this.serverMaxConnections() : "Suspended");
			
			this.upTime.setText("Server Up Time: " + serverUpTime());
			
			if(this.memUsage.getValue() >= 75)
				this.memUsage.setForeground(Color.RED);
			else
				this.memUsage.setForeground(new Color(0,204,0));
			
			if(this.serverUsage.getValue() >= 75)
				this.serverUsage.setForeground(Color.RED);
			else
				this.serverUsage.setForeground(new Color(0,204,0));
			
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
	
	private long usedMemory()
	{
		return (runtime.totalMemory() - runtime.freeMemory())/1024/1024;
	}
	
	private long freeMemory()
	{
		return runtime.freeMemory()/1024/1024;
	}
	
	private long totalMemory()
	{
		return runtime.totalMemory()/1024/1024;
	}
	
	private long maxMemory()
	{
		return runtime.maxMemory()/1024/1024;
	}
	
	private int availableProcessors()
	{
		return runtime.availableProcessors();
	}
	
	private long objectsCommunicated()
	{
		if(this.server != null)
			return this.server.getObjectsSent();
		else
			return 0;
	}
	
	private long filesCommunicated()
	{
		if(this.server != null)
			return this.server.getFilesTransferred();
		else
			return 0;
	}
	
	private int serverPort()
	{
		if(this.server != null)
			return AbstractServer.serverPortNumber;
		else
			return 0;
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
	
	private int serverMaxConnections()
	{
		if(this.server != null)
			return AbstractServer.maxConnectedUsers;
		else
			return 0;
	}
	
	private int serverConnectedUsers()
	{
		if(this.server != null)
			return ChatRoom.getGlobalMembersSize();
		else
			return 0;
	}
	
	private String serverStatus()
	{
		if(this.server != null)
			return "Running";
		else
			return "Suspended";
	}

	private String serverVersion()
	{
		return AbstractIRC.SERVER_VERSION;
	}

	private String clientVersion()
	{
		return AbstractIRC.CLIENT_VERSION;
	}

	public static UsageMonitor getInstance()
	{
		return InstanceHolder.INSTANCE;
	}

	private static class InstanceHolder
	{
		private static final UsageMonitor INSTANCE = new UsageMonitor();
	}
}