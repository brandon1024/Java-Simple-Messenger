package webchatinterface.server.ui;

import webchatinterface.AbstractIRC;
import webchatinterface.server.network.ChannelManager;
import webchatinterface.server.ui.components.ResourceMonitor;
import webchatinterface.server.util.ResourceLoader;
import webchatinterface.server.AbstractServer;
import webchatinterface.server.communication.WebChatServer;
import webchatinterface.server.communication.WebChatServerInstance;
import webchatinterface.server.network.Channel;
import webchatinterface.server.ui.components.ConsoleManager;
import webchatinterface.server.ui.dialog.AccountListDialog;
import webchatinterface.server.ui.dialog.PreferencesDialog;
import webchatinterface.server.util.ResourceMonitorManager;
import webchatinterface.util.Command;

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
	private Container masterPane;
	private JMenuItem runServer;
	private JMenuItem suspendServer;
	private JMenuItem kickUser;
	private JMenuItem showConnectedUsers;
	private JMenuItem broadcastMessage;
	private JMenuItem exit;
	private JMenuItem showAccountManager;
	private JMenuItem showPreferencesDialog;
	private JMenuItem aboutApp;
	private JMenuItem getHelp;
	private ConsoleManager consoleMng;
	private ResourceMonitorManager resourceMonitorManager;
	private boolean running = false;
	private WebChatServer server;
	
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
	         	ResourceLoader.getInstance().loadResources();
	         	WebChatServerGUI userInterface = new WebChatServerGUI();
	         }
	      });
	}
	
	private WebChatServerGUI()
	{
		super.setTitle("Web Chat Server Interface - Suspended");
		super.setSize(800,600);  
		super.setResizable(false);
		super.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		super.setIconImage(ResourceLoader.getInstance().getFrameIcon());
		this.buildUI();
		this.validateSettings();
		super.setVisible(true);
		super.setState((AbstractServer.openMinimized) ? Frame.ICONIFIED : Frame.NORMAL);

		if(AbstractServer.startServerWhenApplicationStarts)
			this.runServer();
	}
	
	private void buildUI()
	{
		this.consoleMng = ConsoleManager.getInstance();
		this.resourceMonitorManager = ResourceMonitorManager.getInstance();
		this.resourceMonitorManager.start();
		(new Thread(consoleMng)).start();
		
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
	
	private void suspendServer()
	{
		//if the server is running
		if(this.running)
		{
			this.running = false;
			this.server.suspend();
			this.resourceMonitorManager.stop();
			this.server = null;
			
			super.setTitle("Web Chat Server Interface - Suspended");
		}
	}
	
	private void runServer()
	{
		if(!this.running)
		{
			//run server on specified port
			this.running = true;
			this.server = new WebChatServer();
			this.resourceMonitorManager.start(server);
			this.server.start();
			
			super.setTitle("Web Chat Server Interface - Running");
		}
	}
	
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
			Channel[] channels = ChannelManager.getInstance().getGlobalChannels();
			for (Channel channel : channels)
			{
				WebChatServerInstance[] clients = channel.getChannelMembers();
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
	
	private void showConnectedUsersDialog()
	{
		//if server running
		if(this.running)
			(new ConnectedUsersWindow(this.server)).start();
		else
			this.consoleMng.printConsole("No Connected Users; Server is Suspended", true);
	}

	private void showBroadcastMessageDialog()
	{
		//if server running
		if(this.running)
			this.server.showBroadcastMessageDialog(this);
		else
			consoleMng.printConsole("Cannot Broadcast Message; Server is Suspended", true);
	}
	
	private void showUsageMonitor(boolean show)
	{
		//If Usage Monitor is Visible
		if(show)
		{
			this.masterPane.add(ResourceMonitor.getInstance(), BorderLayout.PAGE_END);
			this.masterPane.validate();
		}
		else
		{
			this.masterPane.remove(ResourceMonitor.getInstance());
			this.masterPane.validate();
		}
	}
	
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
	
	public void windowClosing(WindowEvent event)
	{
		this.exit();
	}

	public void windowClosed(WindowEvent event)
	{
		this.exit();
	}

	public void windowActivated(WindowEvent event){}
	public void windowDeactivated(WindowEvent event){}
	public void windowDeiconified(WindowEvent event){}
	public void windowIconified(WindowEvent event){}
	public void windowOpened(WindowEvent event){}
}
