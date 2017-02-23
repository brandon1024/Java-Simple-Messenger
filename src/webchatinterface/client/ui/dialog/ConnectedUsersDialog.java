package webchatinterface.client.ui.dialog;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import webchatinterface.client.AbstractClient;
import webchatinterface.client.communication.WebChatClient;
import webchatinterface.util.ClientUser;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
 *<p>
 *The ConnectedUsersDialog class is designed to display a list of users connected to the server that
 *auto-refreshes to display the most current information. The dialog runs on a seperate thread, and 
 *polls {@code client.getConnectedUsers()} periodically for client connection data. The thread runs
 *until the user closes the dialog or the client back-end closes.
 *<p>
 *Clients who are offline or appear offline do not show up in the list. The list contains the client
 *username, availablility, and chatroom.
 */

public class ConnectedUsersDialog extends JFrame implements Runnable, WindowListener
{
	/**Serial Version UID is used as a version control for the class that implements
	 *the serializable interface.*/
	private static final long serialVersionUID = -5653100898102353268L;

	/**Object representing an icon for users with available status*/
	private static Object AVAILABLE_ICON;
	
	/**Object representing an icon for users with busy status*/
	private static Object BUSY_ICON;
	
	/**Object representing an icon for users with away status*/
	private static Object AWAY_ICON;
	
	/**A reference to the communication thread. Used to poll {@code getConnectedUsers()}*/
	private WebChatClient client;
	
	/**A reference to the object representing the user of the application.*/
	private ClientUser clientUser;
	
	/**Content pane for the dialog frame.*/
	private Container masterPane;
	
	/**Control variable used to exit the thread when the client closes or the user selected to
	  *exit the frame.*/
	private volatile boolean isRunning;
	
	static
	{
		try
		{
			AVAILABLE_ICON = new ImageIcon(ImageIO.read(ConnectedUsersDialog.class.getResource("/webchatinterface/client/resources/AVAILABLE.png")));
			BUSY_ICON = new ImageIcon(ImageIO.read(ConnectedUsersDialog.class.getResource("/webchatinterface/client/resources/BUSY.png")));
			AWAY_ICON = new ImageIcon(ImageIO.read(ConnectedUsersDialog.class.getResource("/webchatinterface/client/resources/AWAY.png")));
		}
		catch(IOException | IllegalArgumentException e)
		{
			AbstractClient.logException(e);
			AVAILABLE_ICON = "";
			BUSY_ICON = "";
			AWAY_ICON = "";
		}
	}
	
	/**Constructor for a new ConnectedUsersDialog. Constructs the frame and sets objects fields.
	  *@param client A reference to the client communication thread. Used to poll {@code getConnectedUsers()}
	  *@param clientUser A reference to the client user object.*/
	public ConnectedUsersDialog(WebChatClient client, ClientUser clientUser)
	{
		super("Connected Users");
		super.setSize(new Dimension(500,150));
		super.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		super.addWindowListener(this);
		
		try
		{
			super.setIconImage(ImageIO.read(ConnectedUsersDialog.class.getResource("/webchatinterface/client/resources/CLIENTICON.png")));
		}
		catch (Exception e)
		{
			AbstractClient.logException(e);
		}
		
		this.client = client;
		this.clientUser = clientUser;
		this.masterPane = super.getContentPane();
		this.isRunning = false;
	}

	/**Starts the ConnectedUsersDialog thread. If the thread is already running,
	  *start() will simply return.*/
	public void start()
	{
		if(this.isRunning())
			return;
		
		this.isRunning = true;
		(new Thread(this)).start();
	}
	
	/**Executed when the thread starts. Periodically polls {@code client.getConnectedUsers()}
	  *and displays the information in a table. The table will automatically refresh every second.
	  *The thread will run until the control variable isRunning is false, or the client thread
	  *terminates.*/
	@Override
	public void run()
	{
		super.setVisible(true);
		
		Object[][] data;
		Object[][] previousData = null;
		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.addColumn("");
		tableModel.addColumn("Username");
		tableModel.addColumn("Status");
		tableModel.addColumn("Chatroom");
		
		JTable clientConnections = new JTable(tableModel)
		{
            private static final long serialVersionUID = -986803268686380681L;
            
            //  Returning the Class of each column will allow different
            //  renderer to be used based on Class
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
		
		this.masterPane.add(scrollPane);
		super.validate();
		
		while(this.isRunning && this.client.isRunning())
		{
			data = this.client.getConnectedUsers();
			
			if(previousData == null)
				previousData = data;
			else if(Arrays.equals(data, previousData))
			{
				try
				{
					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{
					AbstractClient.logException(e);
				}
				continue;
			}
			else
				previousData = data;
			
			//remove all data from table
			if(tableModel.getRowCount() > 0)
			{
			    for(int i = tableModel.getRowCount() - 1; i > -1; i--)
			    	tableModel.removeRow(i);
			}
			
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
			
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
				AbstractClient.logException(e);
			}
		}
	}
	
	/**Accessor method for the state of the ConnectedUsersDialog thread. If the thread is running,
	  *{@code isRunning()} will return true. Otherwise, the method will return false.
	  *@return true if thread is running, false if thread is suspended*/
	private boolean isRunning()
	{
		return this.isRunning;
	}

	/**Close the dialog and running thread in an ordered manner.
	  *@param event the WindowEvent fired by the frame*/
	@Override
	public void windowClosed(WindowEvent event)
	{
		this.isRunning = false;
		super.setVisible(false);
		super.dispose();
	}

	/**Close the dialog and running thread in an ordered manner.
	  *@param event the WindowEvent fired by the frame*/
	@Override
	public void windowClosing(WindowEvent event)
	{
		this.isRunning = false;
		super.setVisible(false);
		super.dispose();
	}

	@Override
	public void windowActivated(WindowEvent event){}
	@Override
	public void windowDeactivated(WindowEvent event){}
	@Override
	public void windowDeiconified(WindowEvent event){}
	@Override
	public void windowIconified(WindowEvent event){}
	@Override
	public void windowOpened(WindowEvent event){}
}
