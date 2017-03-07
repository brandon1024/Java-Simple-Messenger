package webchatinterface.server.ui.dialog;

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

import webchatinterface.server.WebChatServer;
import webchatinterface.server.communication.WebChatServerInstance;
import webchatinterface.server.util.ChatRoom;
import webchatinterface.util.ClientUser;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The ConnectedUsersDialog class is designed to display a list of users connected that
  *auto-refreshes to display the most current information. The dialog runs on a seperate thread, and 
  *polls {@code ChatRoom.getGlobalMembers()} periodically for client connection data. The thread runs
  *until the user closes the dialog or the server closes.
  *<p>
  *The list contains the client username, availablility, chatroom, user ID, instance ID and IP address.
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
	
	/**Object representing an icon for users with appear offline status*/
	private static Object APPEAR_OFFLINE_ICON;
	
	/**Object representing an icon for users with offline status*/
	private static Object OFFLINE_ICON;
	
	/**A reference to the server thread. Used to poll {@code isRunning()}*/
	private WebChatServer server;
	
	/**Content pane for the dialog frame.*/
	private Container masterPane;
	
	/**Control variable used to exit the thread when the server closes or the user selected to
	  *exit the frame.*/
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
	
	/**Constructor for a new ConnectedUsersDialog. Constructs the frame
	  *and sets objects fields.
	  *@param server A reference to the server. Used to poll {@code isRunning()}*/
	public ConnectedUsersDialog(WebChatServer server)
	{
		super("Connected Clients");
		super.setSize(600,150);
		super.setVisible(true);
		super.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		super.addWindowListener(this);
		
		try
		{
			super.setIconImage(ImageIO.read(ConnectedUsersDialog.class.getResource("/webchatinterface/server/resources/SERVERICON.png")));
		}
		catch(IOException | IllegalArgumentException e){}
		
		this.server = server;
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
	
	/**Executed when the thread starts. Periodically polls the list of connected users
	  *and displays the information in a table. The table will automatically refresh every second.
	  *The thread will run until the control variable isRunning is false, or the server thread
	  *terminates.*/
	@Override
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
		tableModel.addColumn("Chatroom");
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
			data = ChatRoom.getGlobalMembers();
			
			if(previousData == null)
				previousData = data;
			else if(Arrays.equals(data, previousData))
			{
				try
				{
					Thread.sleep(1000);
				}
				catch(InterruptedException e){}
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
			
			for(WebChatServerInstance member : data)
			{
				Object[] row = new Object[7];
				
				//Switch Client Availability
				switch(member.getAvailability())
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
				row[2] = Integer.toString(member.getID());
				row[3] = member.getUsername();
				row[4] = member.getIP();
				row[5] = member.getRoom().toString();
				row[6] = member.getUserID();
				
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
			catch(InterruptedException e){}
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
