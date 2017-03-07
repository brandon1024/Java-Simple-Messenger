package webchatinterface.server.ui;

import webchatinterface.AbstractIRC;
import webchatinterface.server.AbstractServer;
import webchatinterface.server.WebChatServer;
import webchatinterface.server.WebChatServerInstance;
import webchatinterface.server.ui.preferences.PreferencesDialog;
import webchatinterface.server.util.ChatRoom;
import webchatinterface.util.Command;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
  *<p>
  *The {@code WebChatsServerGUI} class outlines the user interface with which the user communicates 
  *with the {@code WebChatServer}. The class handles building a graphical user interface, and 
  *displaying the server status to the console area.
  */

public class WebChatServerGUI extends JFrame implements ActionListener, WindowListener
{
	/**Serial Version UID is used as a version control for the class that implements
	 *the serializable interface.*/
	private static final long serialVersionUID = 3471342435146329783L;

	//GRAPHICAL USER INTERFACE COMPONENTS
	/**The master container for all sub-containers within the
	  *user interface*/
	private Container masterPane;
	
	/**The menu option for running the {@code WebChatServer} with the 
	  *current settings*/
	private JMenuItem runServer;
	
	/**The menu option for suspending the {@code WebChatServer}*/
	private JMenuItem suspendServer;
	
	/**The menu option for displaying the dialog for kicking a user from 
	  *the server*/
	private JMenuItem kickUser;
	
	/**The menu option for displaying the dialog with a list of current 
	  *connected users*/
	private JMenuItem showConnectedUsers;
	
	/**The menu option for displaying the dialog for broadcasting a message 
	  *to all connected users*/
	private JMenuItem broadcastMessage;
	
	/**The menu option for executing orderly termination procedures*/
	private JMenuItem exit;
	
	/***/
	private JMenuItem showAccountManager;
	
	/***/
	private JMenuItem showPreferencesDialog;
	
	/**The menu option for displaying the 'about' dialog*/
	private JMenuItem aboutApp;
	
	/**The menu option for displaying the 'help' dialog*/
	private JMenuItem getHelp;
	
	/**The server console manager. 
	  *@see ConsoleManager*/
	private ConsoleManager consoleMng;
	
	/**The server usage monitor. 
	  *@see UsageMonitor*/
	private UsageMonitor usageMnt;
	
	/**Variable that describes the server state, i.e. whether the 
	  *server is running or suspended. Used to control server actions 
	  *and prevent unexpected server issues.*/
	private boolean running = false;
	
	/**The underlying server with which this interface communicates.
	  *@see WebChatServer*/
	private WebChatServer server;
	
	
	/**Entry point of the WebChatServer application. The {@code main()} 
	  *method coordinates the initialization of the graphical user interface.
	  *By default, the window size is set to 800x600, visibility enabled,
	  *resizable disabled, and default close operation to {@code DO_NOTHING_ON_CLOSE}.
	  *@param args command line arguments*/
	public static void main(String[] args)
	{
		//Set OS Window Look and Feel
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			AbstractServer.logException(e);
		}
		
		//Build Server Temp Folder if Does Not Exist
		if(!(new File(AbstractIRC.SERVER_APPLCATION_DIRECTORY)).isDirectory())
			(new File(AbstractIRC.SERVER_APPLCATION_DIRECTORY)).mkdir();
		
		//Build Blacklist File if Does Not Exist
		if(!(new File(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "BLACKLIST.dat").exists()))
		{
			try
			{
				(new File(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "BLACKLIST.dat")).createNewFile();
			}
			catch(IOException e)
			{
				AbstractServer.logException(e);
			}
		}
		
		SwingUtilities.invokeLater(new Runnable()
		{
	         @Override
	         public void run()
	         {
	        	 WebChatServerGUI userInterface = new WebChatServerGUI();
	         }
	      });
	}
	
	/**Constructs the user interface for the server. Initializes and
	  *displays all window components and their action listeners.*/
	private WebChatServerGUI()
	{
		super.setTitle("Web Chat Server Interface - Suspended");
		super.setSize(800,600);  
		super.setResizable(false);
		super.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.buildUI();
		this.validateSettings();
		super.setVisible(true);
		super.setState((AbstractServer.openMinimized) ? Frame.ICONIFIED : Frame.NORMAL);

		if(AbstractServer.startServerWhenApplicationStarts)
			this.runServer();
	}
	
	/**Initializes and displays all window components and their
	  *action listeners*/
	private void buildUI()
	{
		this.consoleMng = new ConsoleManager();
		this.usageMnt = UsageMonitor.getInstance();
		(new Thread(consoleMng)).start();
		(new Thread(usageMnt)).start();
		
		//---SET WINDOW ICON---//
		try
		{
			this.setIconImage(ImageIO.read(WebChatServerGUI.class.getResource("/webchatinterface/server/resources/SERVERICON.png")));
		}
		catch(IOException | IllegalArgumentException e)
		{
			AbstractServer.logException(e);
		}
		
		//---BUILD MENU BAR---//
		JMenuBar menuBar;
		JMenu serverMenu;
		JMenu edit;
		JMenu help;
		menuBar = new JMenuBar();
		
		serverMenu = new JMenu("Server");
		edit = new JMenu("Edit");
		help = new JMenu("Help");
		
		menuBar.add(serverMenu);
		menuBar.add(edit);
		menuBar.add(help);
		
		this.runServer = new JMenuItem("Run Server");
		this.suspendServer = new JMenuItem("Suspend Server");
		this.kickUser = new JMenuItem("Kick Client Connection");
		this.showConnectedUsers = new JMenuItem("Show Client Connections");
		this.broadcastMessage = new JMenuItem("Broadcast Message...");
		this.exit = new JMenuItem("Close Server");
		
		this.showAccountManager = new JMenuItem("Account Manager...");
		this.showPreferencesDialog = new JMenuItem("Preferences...");
		
		this.aboutApp = new JMenuItem("About Web Chat Server Interface");
		this.getHelp = new JMenuItem("Get Help");
		
		serverMenu.add(runServer);
		serverMenu.add(suspendServer);
		serverMenu.addSeparator();
		serverMenu.add(kickUser);
		serverMenu.add(showConnectedUsers);
		serverMenu.add(broadcastMessage);
		serverMenu.addSeparator();
		serverMenu.add(exit);
		
		edit.add(showAccountManager);
		edit.add(showPreferencesDialog);
		
		help.add(aboutApp);
		help.add(getHelp);
		
		this.runServer.addActionListener(this);
		this.suspendServer.addActionListener(this);
		this.kickUser.addActionListener(this);
		this.showConnectedUsers.addActionListener(this);
		this.broadcastMessage.addActionListener(this);
		this.exit.addActionListener(this);
		this.showAccountManager.addActionListener(this);
		this.showPreferencesDialog.addActionListener(this);
		this.aboutApp.addActionListener(this);
		this.getHelp.addActionListener(this);
		
		this.addWindowListener(this);
		
		this.setJMenuBar(menuBar);
		
		//---BUILD WINDOW---//
		this.masterPane = super.getContentPane();
		
		DefaultCaret caret = (DefaultCaret)consoleMng.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JScrollPane scroll = new JScrollPane (consoleMng);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		this.masterPane.add(scroll, BorderLayout.CENTER);
		
		this.consoleMng.printConsole("WEB CHAT SERVER INTERFACE " + AbstractIRC.SERVER_VERSION, false);
		this.consoleMng.printConsole("Copyright 2016 by Brandon Richardson (brandon1024.br@gmail.com)", false);
		
		this.consoleMng.printConsole("Current Status: Server Suspended", false);
		this.consoleMng.printConsole("Current Settings: Server Port " + AbstractServer.serverPortNumber, false);
		this.consoleMng.printConsole("Current Settings: Server IP " + AbstractServer.serverBindIPAddress, false);
		this.consoleMng.printConsole();
	}
	
	public void validateSettings()
	{
		this.showUsageMonitor(AbstractServer.showResourceMonitor);
		this.consoleMng.validateSettings();
	}
	
	/**Executes orderly server suspend procedures. Invoking the {@code suspend()} 
	  *method will close the server thread and all server instance descendants, and 
	  *the {@code WebChatServer} object will be recycled and a new object will 
	  *be instantiated when {@code runServer()} is run. 
	  *<p>
	  *Suspending the server will disconnect all connected users, and will update 
	  *the usage monitor. If the server is not running, the call will be ignored.
	  *@see WebChatServer#suspend()
	  *@see WebChatServerInstance*/
	private void suspendServer()
	{
		//if the server is running
		if(this.running)
		{
			this.running = false;
			this.server.suspend();
			this.usageMnt.suspendServer();
			this.server = null;
			
			super.setTitle("Web Chat Server Interface - Suspended");
		}
	}
	
	/**Executes the server with the current state settings. Invoking the {@code runServer()} 
	  *method will instantiate a new {@code WebChatServer} object with the current settings, 
	  *and run the threaded object.
	  *<p>
	  *The usage monitor is updated to display server analytics. If the server is already running 
	  *the call will be ignored.*/
	private void runServer()
	{
		if(!this.running)
		{
			//run server on specified port
			this.running = true;
			this.server = new WebChatServer(this.consoleMng);
			this.usageMnt.runServer(server);
			this.server.start();
			
			super.setTitle("Web Chat Server Interface - Running");
		}
	}
	
	/***/
	public void restartServer()
	{
		this.suspendServer();
		
		try
		{
			AbstractServer.saveState();
		}
		catch(IOException e){}
		
		this.validateSettings();
		this.runServer();
	}
	
	/**Display the kick user dialog, and allow the server user to kick and/or blacklist a 
	  *client connection.*/
	private void showKickUserDialog()
	{
		//If the server is running
		if(this.running)
		{
			//Create Dialog Components
			JPanel dialogPanel = new JPanel();
			JComboBox<WebChatServerInstance> connectedUsers = new JComboBox<WebChatServerInstance>();
			JCheckBox blacklist = new JCheckBox("Blacklist User");
			
			//Populate JComboBox with WebChatServerInstances
			ChatRoom[] rooms = ChatRoom.getGlobalRooms();
			for (ChatRoom room : rooms)
			{
				WebChatServerInstance[] clients = room.getConnectedClients();
				for(WebChatServerInstance member : clients)
					connectedUsers.addItem(member);
			}
			
			//Add Components to Dialog
			dialogPanel.add(connectedUsers);
			dialogPanel.add(blacklist);
			
			//Show Dialog
			int result = JOptionPane.showConfirmDialog(this, dialogPanel, "Kick User",
					JOptionPane.OK_CANCEL_OPTION);
			
			//If User Selected OK Option
			if(result == JOptionPane.OK_OPTION)
			{
				//Get the Selected WebChatServerInstance
				WebChatServerInstance connection = (WebChatServerInstance)connectedUsers.getSelectedItem();
				
				//Disconnect User, and Blacklist if JCheckBox Selected
				if(blacklist.isSelected())
				{
					this.server.disconnectUser(connection, Command.REASON_BLACKLISTED);
					this.server.blacklistUser(connection);
					this.consoleMng.printConsole("Successfully Blacklisted User: " + connection.toString(), false);
				}
				else
				{
					this.server.disconnectUser(connection, Command.REASON_KICKED);
					this.consoleMng.printConsole("Successfully Kicked User: " + connection.toString(), false);
				}
			} 
		}
		//if server suspended
		else
			this.consoleMng.printConsole("No Connected Users; Server is Suspended", true);
	}
	
	/**Display the connected users dialog. Displays a list of all users connected to the server in
	  *all private and public chatrooms, along with their information.
	  *<p>
	  *Format:
	  *{@code [AVAIL ICON][AVAILABILITY][INSTANCE ID][USERNAME][IP ADDRESS][ROOM][USER ID]}*/
	private void showConnectedUsersDialog()
	{
		//if server running
		if(this.running)
			(new ConnectedUsersDialog(this.server)).start();
		else
			this.consoleMng.printConsole("No Connected Users; Server is Suspended", true);
	}

	/**Display the Message broadcast dialog. Allows the user to broadcast a message, or specifify
	  *an automated server message with specific broadcast frequency.
	  *@see webchatinterface.server.util.BroadcastHelper#showBroadcastMessageDialog()*/
	private void showBroadcastMessageDialog()
	{
		//if server running
		if(this.running)
			this.server.showBroadcastMessageDialog();
		else
			consoleMng.printConsole("Cannot Broadcast Message; Server is Suspended", true);
	}
	
	/**Toggle the Usage Monitor lower pane.*/
	private void showUsageMonitor(boolean show)
	{
		//If Usage Monitor is Visible
		if(show)
		{
			this.masterPane.add(usageMnt, BorderLayout.PAGE_END);
			this.masterPane.validate();
		}
		else
		{
			this.masterPane.remove(usageMnt);
			this.masterPane.validate();
		}
	}
	
	/**Gracefully exit the server application.*/
	private void exit()
	{
		if(this.running)
		{
			JPanel warningDialog = new JPanel();
			warningDialog.add(new JLabel("Server is running. Are you sure you want to exit?"));

			int result = JOptionPane.showConfirmDialog(this, warningDialog, 
				"Exit", JOptionPane.OK_CANCEL_OPTION);

			if(result == JOptionPane.OK_OPTION)
				this.suspendServer();
			else
				return;
		}
		
		try
		{
			AbstractServer.saveState();
		}
		catch (IOException e){}
		System.exit(0);
	}
	
	/**Display the 'About Application' dialog.*/
	private void showAboutDialog()
	{
		//display About dialog
		String about = "Web Chat Interface"
			+ "\nDeveloper: Brandon Richardson"
			+ "\nBuild Version: " + AbstractIRC.SERVER_VERSION
			+ "\nRelease Date: " + AbstractIRC.RELEASE_DATE + "\n\n" + 
			"Web Chat Interface is an application that allows users to chat"
			+ "\nover the web. Once you sign in and connect to a server, multiple"
			+ "\nusers can communicate";

		JOptionPane.showMessageDialog(this, about,
			"About Web Chat Interface", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**Display the 'Help' dialog.*/
	private void showHelpDialog()
	{
		//display Help dialog
		String help = "Need help?"
  		+ "\nContact the developer:"
  		+ "\n\tBrandon Richardson"
  		+ "\n\tEmail: brandon1024.br@gmail.com";

		JOptionPane.showMessageDialog(this, help,
			"Get Help", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**Respond to an {@code ActionEvent} occured by the user communicating with
	  *a component in the user interface.
	  *@param event the {@code ActionEvent} that occured as a result of the
	  *user communicating with a component in the user interface
	  *@see java.awt.event.ActionEvent
	  *@see java.awt.event.ActionListener*/
	@Override
	public void actionPerformed(ActionEvent event)
	{
		//Run Server
		if(event.getSource() == this.runServer)
			this.runServer();
		//Suspend Server
		else if(event.getSource() == this.suspendServer)
			this.suspendServer();
		//Kick Connected User
		else if(event.getSource() == this.kickUser)
			this.showKickUserDialog();
		//Show Connected Users
		else if(event.getSource() == this.showConnectedUsers)
			this.showConnectedUsersDialog();
		//Broadcast Message to Connected Users
		else if(event.getSource() == this.broadcastMessage)
			this.showBroadcastMessageDialog();
		//Exit Server Application Gracefully
		else if(event.getSource() == this.exit)
			this.exit();
		else if(event.getSource() == this.showAccountManager)
			AccountListDialog.displayAccountList();
		//Exit Server Application Gracefully
		else if(event.getSource() == this.showPreferencesDialog)
			(new PreferencesDialog(this)).showDialog();
		//About Application
		else if(event.getSource() == this.aboutApp)
			this.showAboutDialog();
		//Get Help
		else if(event.getSource() == this.getHelp)
			this.showHelpDialog();
	}
	
	/**Execute orderly termination procedures. If the server is running, the server
	  *is suspended. The current settings are saved to the configuration file.
	  *@param event the {@code WindowEvent} that occured as a result of the
	  *user communicating with the window
	  *@see java.awt.event.WindowEvent
	  *@see java.awt.event.WindowListener*/
	@Override
	public void windowClosing(WindowEvent event)
	{
		this.exit();
	}

	/**Execute orderly termination procedures. If the server is running, the server
	  *is suspended. The current settings are saved to the configuration file.
	  *@param event the {@code WindowEvent} that occured as a result of the
	  *user communicating with the window
	  *@see java.awt.event.WindowEvent
	  *@see java.awt.event.WindowListener*/
	@Override
	public void windowClosed(WindowEvent event)
	{
		this.exit();
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
