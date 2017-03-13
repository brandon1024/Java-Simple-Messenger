package webchatinterface.server.ui.components;

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

public class UsageMonitor extends JPanel
{
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
	private JLabel availableProcessors;
	private JProgressBar memUsage;
	private JProgressBar serverUsage;
	
	private UsageMonitor()
	{
		//Build Container Object
		super();
		super.setBorder(BorderFactory.createTitledBorder("Resource Monitor"));
		super.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
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
		this.availableProcessors = new JLabel("Available Processors: 0");
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
		col6.add(this.availableProcessors);
		
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

	public void changeUsedMemory(String usedMemory)
	{
		this.usedMem.setText("Used Memory: " + usedMemory);
	}

	public void changeFreeMemory(String freeMemory)
	{
		this.freeMem.setText("Free Memory: " + freeMemory);
	}

	public void changeTotalMemory(String totalMemory)
	{
		this.totalMem.setText("Total Memory: " + totalMemory);
	}

	public void changeMaxMemory(String maxMemory)
	{
		this.maxMem.setText("Max Memory: " + maxMemory);
	}

	public void changeMessagesSent(long messagesSent)
	{
		this.messagesSent.setText("Messages Sent: " + messagesSent);
	}

	public void changeFilesTransferred(long filesTransferred)
	{
		this.filesTransferred.setText("Files Transferred: " + filesTransferred);
	}

	public void changePortNumber(int portNumber)
	{
		this.port.setText("Server Port: " + portNumber);
	}

	public void changeMaxConnections(int maxConnections)
	{
		this.maxConnections.setText("Max Connections: " + maxConnections);
	}

	public void changeServerVersion(String serverVersion)
	{
		this.version.setText("Server Version: " + serverVersion);
	}

	public void changeClientVersion(String clientVersion)
	{
		this.clientVersion.setText("Client Version: " + clientVersion);
	}

	public void changeStatus(String status)
	{
		this.status.setText("Server Status: " + status);
	}

	public void changeMemoryUsageValue(int value)
	{
		this.memUsage.setValue(value);
	}

	public void changeMemoryUsageText(String text)
	{
		this.memUsage.setString(text);
	}

	public void changeMemoryUsageForeground(Color color)
	{
		this.memUsage.setForeground(color);
	}

	public void changeServerUsageValue(int value)
	{
		this.serverUsage.setValue(value);
	}

	public void changeServerUsageText(String text)
	{
		this.serverUsage.setString(text);
	}

	public void changeServerUsageForeground(Color color)
	{
		this.serverUsage.setForeground(color);
	}

	public void changeServerUpTimeText(String text)
	{
		this.upTime.setText(text);
	}

	public void changeAvailableProcessors(int availableProcessors)
	{
		this.availableProcessors.setText("Available Processors: " + availableProcessors);
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