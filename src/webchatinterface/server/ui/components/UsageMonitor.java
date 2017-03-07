package webchatinterface.server.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import webchatinterface.AbstractIRC;
import webchatinterface.server.AbstractServer;
import webchatinterface.server.WebChatServer;
import webchatinterface.server.util.ChatRoom;

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
	/**Serial Version UID is used as a version control for the class that implements
	 *the serializable interface.*/
	private static final long serialVersionUID = 4773452436998229115L;

	/**The UsageMonitor instance. Accessed using UsageMonitor.getInstance().*/
	private static UsageMonitor instance = new UsageMonitor();
	
	/**The instance of Runtime that allows the application to interface
	  *with the JRE.*/
	private Runtime runtime;
	
	/**The underlying server, which the UsageMonitor is monitoring.*/
	private WebChatServer server;
	
	/**The JLabel for displaying the memory used by the JVM.*/
	private JLabel usedMem;
	
	/**The JLabel for displaying the free memory available to the JVM.*/
	private JLabel freeMem;
	
	/**The JLabel for displaying the total memory available to the JVM.*/
	private JLabel totalMem;
	
	/**The JLabel for displaying the maximum memory available to the JVM.*/
	private JLabel maxMem;
	
	/**The JLabel for displaying the number of messages communicated by the server.*/
	private JLabel messagesSent;
	
	/**The JLabel for displaying the server port number.*/
	private JLabel port;
	
	/**The JLabel for displaying the maximum number of client connections to the server.*/
	private JLabel maxConnections;
	
	/**The JLabel for displaying the server version.*/
	private JLabel version;
	
	/**The JLabel for displaying the most recent client version.*/
	private JLabel clientVersion;
	
	/**The JLabel for displaying the status of the server.*/
	private JLabel status;
	
	/**The JLabel for displaying the number of files transfered between clients.*/
	private JLabel filesTransferred;
	
	/**The JLabel for displaying the total server uptime.*/
	private JLabel upTime;
	
	/**The JProgressBar for displaying the server memory usage, i.e. the used memory
	  *versus the total memory.*/
	private JProgressBar memUsage;
	
	/**The JProgressBar for displaying the server usage, i.e. the number of connected
	  *clients versus the maximum number of concurrent connections.*/
	private JProgressBar serverUsage;
	
	/**The server up time in seconds.*/
	private int serverUpTime;
	
	
	/**Constructs a {@code UsageMonitor} object. Builds a container with
	  *labels and graphical components for displaying useful information regarding
	  *the status of the server.*/
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
	
	/**Establish a reference to the server. Allows the {@code UsageMonitor} to
	  *gather information from the server.
	  *@param server The underlying server to monitor*/
	public void runServer(WebChatServer server)
	{
		this.server = server;
	}
	
	/**Remove the reference to the server once the server is suspended. The
	  *{@code UsageMonitor} will display default information.*/
	public void suspendServer()
	{
		this.server = null;
		this.serverUpTime = 0;
	}
	
	/**Run a threaded instance of the {@code UsageMonitor}. Periodically
	  *polls the server for information regarding memory usage, CPU usage,
	  *runtime and status. Updates the components in the JPanel every 1000ms.
	  *<p>
	  *The {@code run()} method will run indefinitely, until the main server
	  *thread terminates.*/
	@Override
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
	
	/**Accessor method for the memory used by the JVM, in megabytes (MB).
	  *@return the number of megabytes used by the JVM.*/
	private long usedMemory()
	{
		return (runtime.totalMemory() - runtime.freeMemory())/1024/1024;
	}
	
	/**Accessor method for the free memory available to the JVM, in megabytes (MB).
	  *@return the number of megabytes available to the JVM.*/
	private long freeMemory()
	{
		return runtime.freeMemory()/1024/1024;
	}
	
	/**Accessor method for the total memory available to the JVM, in megabytes (MB).
	  *@return the total number of megabytes available to the JVM*/
	private long totalMemory()
	{
		return runtime.totalMemory()/1024/1024;
	}
	
	/**Accessor method for the maximum memory available to the JVM, in megabytes (MB).
	  *@return the maximum number of megabytes available to the JVM.*/
	private long maxMemory()
	{
		return runtime.maxMemory()/1024/1024;
	}
	
	/**Accessor method for the number of available processors to be utilized by the JVM.
	  *@return the number processors available to the JVM*/
	private int availableProcessors()
	{
		return runtime.availableProcessors();
	}
	
	/**Accessor method for the number of objects broadcasted by the server. Initially 0.
	  *@return the number of objects communicated by the server*/
	private long objectsCommunicated()
	{
		if(this.server != null)
			return this.server.getObjectsSent();
		else
			return 0;
	}
	
	/**Accessor method for the number of objects broadcasted by the server. Initially 0.
	  *@return the number of objects communicated by the server*/
	private long filesCommunicated()
	{
		if(this.server != null)
			return this.server.getFilesTransferred();
		else
			return 0;
	}
	
	/**Accessor method for the server port number. Displays 0 when the server is suspended.
	  *@return the server port number*/
	private int serverPort()
	{
		if(this.server != null)
			return AbstractServer.serverPortNumber;
		else
			return 0;
	}
	
	/**Accessor method for the server up time. Reported _d _h _m _s, returns 0 if server is suspended.
	  *@return a string representing the current server uptime*/
	private String serverUpTime()
	{
		int time = this.serverUpTime;
		
		int days = time / 86400;
		int hours = (time % 86400) / 3600;
		int minutes = ((time % 86400) % 3600) / 60;
		int seconds = ((time % 86400) % 3600) % 60;
		
		return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
	}
	
	/**Accessor method for the maximum number of concurrent connections to the server. Returns
	  *1 if the server is suspended.
	  *@return the maximum number of concurrent server connections*/
	private int serverMaxConnections()
	{
		if(this.server != null)
			return AbstractServer.maxConnectedUsers;
		else
			return 0;
	}
	
	/**Accessor method for the number of clients connected to the server. Returns 0 if the
	  *server is suspended.
	  *@return the number of clients connected to the server.*/
	private int serverConnectedUsers()
	{
		if(this.server != null)
			return ChatRoom.getGlobalMembersSize();
		else
			return 0;
	}
	
	/**Accessor method for the server version. Returns 0.0.0 if the server is suspended.
	  *@return the server version*/
	private String serverVersion()
	{
		return AbstractIRC.SERVER_VERSION;
	}
	
	/**Accessor method for the most recent client version. Returns 0.0.0 if the server is suspended.
	  *@return the most recent client version*/
	private String clientVersion()
	{
		return AbstractIRC.CLIENT_VERSION;
	}
	
	/**Accessor method for the status of the server. Returns true of the server is running, and
	  *returns false if the server is suspended.
	  *@return true if server is running, false if server is suspended.*/
	private String serverStatus()
	{
		if(this.server != null)
			return "Running";
		else
			return "Suspended";
	}
	
	/**Accessor method for an instance of {@code UsageMonitor}.
	  *@return an instance of {@code UsageMonitor}*/
	public static UsageMonitor getInstance()
	{
		return UsageMonitor.instance;
	}
}