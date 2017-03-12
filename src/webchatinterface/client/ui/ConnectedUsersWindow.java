package webchatinterface.client.ui;

import webchatinterface.client.AbstractClient;
import webchatinterface.client.communication.WebChatClient;
import webchatinterface.client.util.ResourceLoader;
import webchatinterface.util.ClientUser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
 *<p>
 *The ConnectedUsersWindow class is designed to display a list of users connected to the server that
 *auto-refreshes to display the most current information. The dialog runs on a seperate thread, and 
 *polls {@code client.getConnectedUsers()} periodically for client connection data. The thread runs
 *until the user closes the dialog or the client back-end closes.
 *<p>
 *Clients who are offline or appear offline do not show up in the list. The list contains the client
 *username, availability, and channel.
 */

public class ConnectedUsersWindow extends JFrame implements Runnable, WindowListener
{
	private static ImageIcon AVAILABLE_ICON;
	private static ImageIcon BUSY_ICON;
	private static ImageIcon AWAY_ICON;
	private WebChatClient client;
	private ClientUser clientUser;
	private volatile boolean isRunning;
	
	static
	{
		ResourceLoader rl = ResourceLoader.getInstance();
		AVAILABLE_ICON = ResourceLoader.bufferedImageToImageIcon(rl.getStatusAvailableIcon());
		BUSY_ICON = ResourceLoader.bufferedImageToImageIcon(rl.getStatusBusyIcon());
		AWAY_ICON = ResourceLoader.bufferedImageToImageIcon(rl.getStatusAwayIcon());
	}
	
	public ConnectedUsersWindow(WebChatClient client, ClientUser clientUser)
	{
		super("Connected Users");
		super.setSize(new Dimension(500,150));
		super.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		super.addWindowListener(this);
		super.setIconImage(ResourceLoader.getInstance().getFrameIcon());
		
		this.client = client;
		this.clientUser = clientUser;
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
		super.setVisible(true);
		
		Object[][] data;
		Object[][] previousData = null;
		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.addColumn("");
		tableModel.addColumn("Username");
		tableModel.addColumn("Status");
		tableModel.addColumn("Channel");
		
		JTable clientConnections = new JTable(tableModel)
		{
            @Override
			public Class<?> getColumnClass(int column)
            {
            	switch(column)
            	{
                	case 0: return ImageIcon.class;
                	case 1:
                	case 2:
                	case 3: return String.class;
                	default: return Object.class;
            	}
            }
        };
        clientConnections.setPreferredScrollableViewportSize(new Dimension(500,150));
        
        JScrollPane scrollPane = new JScrollPane(clientConnections);
		super.getContentPane().add(scrollPane);
		super.validate();
		
		while(this.isRunning && this.client.isRunning())
		{
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
				AbstractClient.logException(e);
			}

			data = this.client.getConnectedUsers();
			
			if(data == previousData)
				continue;

			previousData = data;
			
			//remove all data from table
			for(int i = tableModel.getRowCount() - 1; i > -1; i--)
				tableModel.removeRow(i);
			
			for(Object[] dataElement : data)
			{
				Object[] row = new Object[4];
				
				//Switch Client Availability
				switch((Integer)dataElement[3])
				{
					case ClientUser.AVAILABLE:
						row[0] = AVAILABLE_ICON;
						row[2] = "AVAILABLE";
						break;
					case ClientUser.BUSY:
						row[0] = BUSY_ICON;
						row[2] = "BUSY";
						break;
					case ClientUser.AWAY:
						row[0] = AWAY_ICON;
						row[2] = "AWAY";
						break;
					case ClientUser.APPEAR_OFFLINE:
					case ClientUser.OFFLINE:
						continue;
				}
				
				//Assign Client Information to Object[][]
				if(dataElement[1].equals(this.clientUser.getUserID()))
					row[1] = dataElement[0] + "(Me)";
				else
					row[1] = dataElement[0];
				
				row[3] = dataElement[4];
				
				tableModel.addRow(row);
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
