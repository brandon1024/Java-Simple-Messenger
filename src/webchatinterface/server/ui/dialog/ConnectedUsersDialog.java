package webchatinterface.server.ui.dialog;

import webchatinterface.server.communication.WebChatServer;
import webchatinterface.server.communication.WebChatServerInstance;
import webchatinterface.server.network.Channel;
import webchatinterface.server.network.ChannelManager;
import webchatinterface.server.util.ResourceLoader;
import webchatinterface.util.ClientUser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The ConnectedUsersWindow class is designed to display a list of users connected that
  *auto-refreshes to display the most current information. The dialog runs on a seperate thread, and 
  *polls {@code Channel.getGlobalMembers()} periodically for client connection data. The thread runs
  *until the user closes the dialog or the server closes.
  *<p>
  *The list contains the client username, availablility, channel, user ID, instance ID and IP address.
 */

public class ConnectedUsersDialog extends JFrame implements Runnable, WindowListener
{
	private static Object AVAILABLE_ICON;
	private static Object BUSY_ICON;
	private static Object AWAY_ICON;
	private static Object APPEAR_OFFLINE_ICON;
	private static Object OFFLINE_ICON;
	private WebChatServer server;
	private ChannelManager channelManager;
	private Container masterPane;
	private boolean isRunning;
	
	static
	{
		try
		{
			AVAILABLE_ICON = new ImageIcon(ImageIO.read(ConnectedUsersDialog.class.getResource("/webchatinterface/server/resources/AVAILABLE.png")));
			BUSY_ICON = new ImageIcon(ImageIO.read(ConnectedUsersDialog.class.getResource("/webchatinterface/server/resources/BUSY.png")));
			AWAY_ICON = new ImageIcon(ImageIO.read(ConnectedUsersDialog.class.getResource("/webchatinterface/server/resources/AWAY.png")));
			APPEAR_OFFLINE_ICON = new ImageIcon(ImageIO.read(ConnectedUsersDialog.class.getResource("/webchatinterface/server/resources/APPEAROFFLINE.png")));
			OFFLINE_ICON = new ImageIcon(ImageIO.read(ConnectedUsersDialog.class.getResource("/webchatinterface/server/resources/OFFLINE.png")));
		}
		catch(IOException | IllegalArgumentException e)
		{
			AVAILABLE_ICON = "";
			BUSY_ICON = "";
			AWAY_ICON = "";
			APPEAR_OFFLINE_ICON = "";
			OFFLINE_ICON = "";
		}
	}
	
	public ConnectedUsersDialog(WebChatServer server)
	{
		super("Connected Clients");
		super.setSize(600,150);
		super.setVisible(true);
		super.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		super.addWindowListener(this);
		super.setIconImage(ResourceLoader.getInstance().getFrameIcon());
		
		this.server = server;
		this.channelManager = ChannelManager.getInstance();
		this.masterPane = super.getContentPane();
		this.isRunning = false;
	}

	public void start()
	{
		if(this.isRunning())
			return;
		
		this.isRunning = true;
		(new Thread(this)).start();
	}
	
	public void run()
	{
		WebChatServerInstance[] data;
		WebChatServerInstance[] previousData = null;
		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.addColumn("");
		tableModel.addColumn("Status");
		tableModel.addColumn("Instance ID");
		tableModel.addColumn("Username");
		tableModel.addColumn("IP Address");
		tableModel.addColumn("Channel");
		tableModel.addColumn("User ID");
		
		JTable clientConnections = new JTable(tableModel)
		{
            private static final long serialVersionUID = -986803268686380681L;
            
            //  Returning the Class of each column will allow different
            //  Renderer to be used based on Class
            @Override
			public Class<?> getColumnClass(int column)
            {
            	switch(column)
            	{
                	case 0: return ImageIcon.class;
                	case 1:
                	case 2:
                	case 3: 
                	case 4:
                	case 5:
                	case 6: return String.class;
                	default: return Object.class;
            	}
            }
        };
        clientConnections.setPreferredScrollableViewportSize(new Dimension(500,150));
        
        JScrollPane scrollPane = new JScrollPane(clientConnections);
		
		this.masterPane.add(scrollPane);
		super.validate();
		
		while(this.isRunning && this.server.isRunning())
		{
			//remove all data from table
			if(tableModel.getRowCount() > 0)
			{
				for(int i = tableModel.getRowCount() - 1; i > -1; i--)
					tableModel.removeRow(i);
			}

			Channel[] channels = this.channelManager.getGlobalChannels();
			for(Channel channel : channels)
			{
				for(WebChatServerInstance client : channel.getChannelMembers())
				{
					Object[] row = new Object[7];

					//Switch Client Availability
					switch(client.getAvailability())
					{
						case ClientUser.AVAILABLE:
							row[0] = AVAILABLE_ICON;
							row[1] = "AVAILABLE";
							break;
						case ClientUser.BUSY:
							row[0] = BUSY_ICON;
							row[1] = "BUSY";
							break;
						case ClientUser.AWAY:
							row[0] = AWAY_ICON;
							row[1] = "AWAY";
							break;
						case ClientUser.APPEAR_OFFLINE:
							row[0] = APPEAR_OFFLINE_ICON;
							row[1] = "APPEAR OFFLINE";
							break;
						case ClientUser.OFFLINE:
							row[0] = OFFLINE_ICON;
							row[1] = "OFFLINE";
							break;
					}

					//Assign Client Information to Object[][]
					row[2] = Integer.toString(client.getID());
					row[3] = client.getUsername();
					row[4] = client.getIP();
					row[5] = client.getChannel().toString();
					row[6] = client.getUserID();

					tableModel.addRow(row);
				}
			}
			
			clientConnections.setColumnSelectionAllowed(false);
		    clientConnections.setRowSelectionAllowed(true);
		    
		    clientConnections.getColumnModel().getColumn(0).setPreferredWidth(16);
		    clientConnections.getColumnModel().getColumn(0).setMinWidth(16);
		    clientConnections.getColumnModel().getColumn(0).setMaxWidth(16);
		    clientConnections.getColumnModel().getColumn(2).setPreferredWidth(100);
		    clientConnections.getColumnModel().getColumn(2).setMinWidth(100);
		    clientConnections.getColumnModel().getColumn(2).setMaxWidth(100);
		   
		    tableModel.fireTableDataChanged();
			
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e){}
		}
	}
	
	private boolean isRunning()
	{
		return this.isRunning;
	}

	public void windowClosed(WindowEvent event)
	{
		this.isRunning = false;
		super.setVisible(false);
		super.dispose();
	}

	public void windowClosing(WindowEvent event)
	{
		this.isRunning = false;
		super.setVisible(false);
		super.dispose();
	}

	public void windowActivated(WindowEvent event){}
	public void windowDeactivated(WindowEvent event){}
	public void windowDeiconified(WindowEvent event){}
	public void windowIconified(WindowEvent event){}
	public void windowOpened(WindowEvent event){}
}
