package webchatinterface.client.ui;

import webchatinterface.AbstractIRC;
import webchatinterface.client.AbstractClient;
import webchatinterface.client.authentication.AuthenticationAbortedException;
import webchatinterface.client.authentication.AuthenticationException;
import webchatinterface.client.authentication.Authenticator;
import webchatinterface.client.communication.WebChatClient;
import webchatinterface.client.ui.components.ConsoleManager;
import webchatinterface.client.ui.components.StatusBar;
import webchatinterface.client.ui.dialog.AboutApplicationDialog;
import webchatinterface.client.ui.dialog.HelpDialog;
import webchatinterface.client.util.ResourceLoader;
import webchatinterface.util.ClientUser;
import webchatinterface.util.Command;
import webchatinterface.util.Message;
import webchatinterface.util.TransferBuffer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
  *<p>
  *The {@code WebChatClientGUI} class outlines the user interface with which the user communicates 
  *with the {@code WebChatClient}.
  */

public class WebChatClientGUI extends JFrame implements ActionListener, WindowListener, KeyListener
{
	private JMenuItem signIn;
	private JMenuItem signUp;
	private JMenuItem quickSignIn;
	private JMenuItem signOut;
	private JMenuItem setAvailability;
	private JMenuItem availabilityAway;
	private JMenuItem availabilityAvailable;
	private JMenuItem availabilityBusy;
	private JMenuItem availabilityAppearOffline;
	private JMenuItem showConnectedUsers;
	private JMenuItem exit;
	private JMenuItem enterPrivateChannel;
	private JMenuItem exitPrivateChannel;
	private JMenuItem clearChat;
	private JMenuItem sendImage;
	private JMenuItem sendFile;
	private JMenuItem setWindowColors;
	private JMenuItem setSimpleView;
	private JMenuItem aboutApp;
	private JMenuItem checkVersion;
	private JMenuItem getHelp;
	private JButton send;
	private StatusBar statusBar;
	private JTextArea messageInput;
	private ConsoleManager chatArea;
	private WebChatClient client;
	private ClientUser clientUser;
	private boolean hasTyped = false;
	private int styleID;
	
	public static void main(String[] args)
	{
		//Set Look and Feel
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			AbstractClient.logException(e);
		}
		
		//Construct Frame
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				ResourceLoader.getInstance().loadResources();
				WebChatClientGUI userInterface = new WebChatClientGUI();
				userInterface.setVisible(true);
			}
		});
	}
	
	private WebChatClientGUI()
	{
		//Set Window Properties
		super.setTitle("Web Chat Interface");
		super.setSize(700,300);  
		super.setVisible(false);
		super.setResizable(true);
		super.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		super.setIconImage(ResourceLoader.getInstance().getFrameIcon());
		
		//Build Window UI Components
		this.buildUI();
	}
	
	private void buildUI()
	{
		//Construct Logger, ClientUser, ConsoleManager, and StatusBar
		this.clientUser = AbstractClient.getClientUser();
		this.chatArea = ConsoleManager.getInstance();
		this.chatArea.start();
		this.chatArea.setOpaque(false);
		this.statusBar = new StatusBar();
		
		//---BUILD MENU BAR---//
		Container masterPane;
		Container chatPane;
		JMenuBar menuBar;
		JMenu file;
		JMenu edit;
		JMenu help;
		menuBar = new JMenuBar();
		
		file = new JMenu("File");
		this.setAvailability = new JMenu("Set Availability");
		edit = new JMenu("Edit");
		help = new JMenu("Help");
		
		menuBar.add(file);
		menuBar.add(edit);
		menuBar.add(help);
		
		this.quickSignIn = new JMenuItem("Quick Sign In");
		this.signIn = new JMenuItem("Sign In...");
		this.signUp = new JMenuItem("Sign Up...");
		this.signOut = new JMenuItem("Log Out");
		this.availabilityAvailable = new JMenuItem("Available");
		this.availabilityBusy = new JMenuItem("Busy");
		this.availabilityAway= new JMenuItem("Away");
		this.availabilityAppearOffline = new JMenuItem("Appear Offline");
		this.showConnectedUsers = new JMenuItem("Show Connected Users");
		this.exit = new JMenuItem("Close");
		this.enterPrivateChannel = new JMenuItem("Enter Private Channel...");
		this.exitPrivateChannel = new JMenuItem("Exit Private Channel");
		this.clearChat = new JMenuItem("Clear Chat Window");
		this.sendImage = new JMenuItem("Send Image...");
		this.sendFile = new JMenuItem("Send File...");
		this.setWindowColors = new JMenuItem("Change Colors");
		this.setSimpleView = new JMenuItem("Toggle Simple Console");
		this.aboutApp = new JMenuItem("About Web Chat Interface");
		this.checkVersion = new JMenuItem("Check for Updates");
		this.getHelp = new JMenuItem("Get Help");
		
		this.quickSignIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
		this.signIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		this.signUp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
		this.signOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.ALT_MASK));
		this.availabilityAvailable.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
		this.availabilityBusy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.ALT_MASK));
		this.availabilityAway.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.ALT_MASK));
		this.showConnectedUsers.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		this.exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
		this.enterPrivateChannel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
		this.exitPrivateChannel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK));
		this.clearChat.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.ALT_MASK));
		this.sendImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK));
		this.sendFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.ALT_MASK));
		this.setWindowColors.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
		this.setSimpleView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
		this.checkVersion.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
		this.getHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
		this.aboutApp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.ALT_MASK));

		file.add(this.quickSignIn);
		file.add(this.signIn);
		file.add(this.signUp);
		file.add(this.signOut);
		file.addSeparator();
		this.setAvailability.add(this.availabilityAvailable);
		this.setAvailability.add(this.availabilityBusy);
		this.setAvailability.add(this.availabilityAway);
		this.setAvailability.add(this.availabilityAppearOffline);
		file.add(this.setAvailability);
		file.add(this.showConnectedUsers);
		file.addSeparator();
		file.add(this.exit);
		edit.add(this.enterPrivateChannel);
		edit.add(this.exitPrivateChannel);
		edit.addSeparator();
		edit.add(this.sendImage);
		edit.add(this.sendFile);
		edit.addSeparator();
		edit.add(this.clearChat);
		edit.add(this.setWindowColors);
		edit.add(this.setSimpleView);
		help.add(this.checkVersion);
		help.addSeparator();
		help.add(this.aboutApp);
		help.add(this.getHelp);
		
		this.quickSignIn.addActionListener(this);
		this.signIn.addActionListener(this);
		this.signUp.addActionListener(this);
		this.signOut.addActionListener(this);
		this.availabilityAvailable.addActionListener(this);
		this.availabilityBusy.addActionListener(this);
		this.availabilityAway.addActionListener(this);
		this.availabilityAppearOffline.addActionListener(this);
		this.showConnectedUsers.addActionListener(this);
		this.exit.addActionListener(this);
		this.enterPrivateChannel.addActionListener(this);
		this.exitPrivateChannel.addActionListener(this);
		this.clearChat.addActionListener(this);
		this.sendImage.addActionListener(this);
		this.sendFile.addActionListener(this);
		this.setWindowColors.addActionListener(this);
		this.setSimpleView.addActionListener(this);
		this.aboutApp.addActionListener(this);
		this.checkVersion.addActionListener(this);
		this.getHelp.addActionListener(this);
		
		this.setJMenuBar(menuBar);
		
		//---BUILD WINDOW---//
		masterPane = super.getContentPane();
		masterPane.setLayout(new BorderLayout());
		
		chatPane = new Container();
		chatPane.setLayout(new BorderLayout(0,0));
		chatArea.addKeyListener(this);
		
		//Scrollable Chat Panel
		DefaultCaret caret = (DefaultCaret)this.chatArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JScrollPane chatScroll = new JScrollPane(chatArea);
		chatScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		//Scrollable Message Input Panel
		this.messageInput = new JTextArea();
		this.messageInput.setRows(2);
		this.messageInput.addKeyListener(this);
		this.messageInput.setLineWrap(true);
		this.messageInput.setWrapStyleWord(true);
		JScrollPane messageInScroll = new JScrollPane(messageInput);
		messageInScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		//Set Send Button Image
		try
		{
			ImageIcon image = new ImageIcon(ImageIO.read(WebChatClientGUI.class.getResource("/webchatinterface/client/resources/SEND.png")));
			this.send = new JButton(image);
		}
		catch(IOException | IllegalArgumentException e)
		{
			AbstractClient.logException(e);
			this.send = new JButton("SEND");
		}

		this.send.setFocusPainted(false);
		this.send.setRolloverEnabled(false);
		this.send.addActionListener(this);
		
		chatPane.add(chatScroll, BorderLayout.CENTER);
		chatPane.add(messageInScroll, BorderLayout.SOUTH);
		
		masterPane.add(this.statusBar, BorderLayout.NORTH);
		masterPane.add(chatPane);
		masterPane.add(this.send, BorderLayout.EAST);
		
		//---ADD WINDOW LISTENER---//
		this.addWindowListener(this);
		
		//---SET INITIAL STATE---//
		this.setColors(ConsoleManager.STYLE_INITIALIZED);
		this.chatArea.setText("Please Sign In");
		this.disableClientMenus();
	}
	
	private void setColors(int color)
	{
		this.styleID = color;
		
		switch(color)
		{
			//Initialization Style
			case 0:
				this.chatArea.setConsoleStyle(ConsoleManager.STYLE_INITIALIZED);
				this.messageInput.setForeground(Color.BLACK);
				this.messageInput.setBackground(Color.WHITE);
				break;
			//Authenticated Style Grey
			case 1:
				this.chatArea.setConsoleStyle(ConsoleManager.STYLE_AUTHENTICATED);
				this.messageInput.setForeground(Color.BLACK);
				this.messageInput.setBackground(Color.WHITE);
				this.styleID++;
				break;
			//Hacker Style
			case 2:
				this.chatArea.setConsoleStyle(ConsoleManager.STYLE_AUTHENTICATED_HACKER);
				this.messageInput.setForeground(Color.GREEN);
				this.messageInput.setBackground(Color.BLACK);
				this.styleID++;
				break;
			//Colourful Style Red
			case 3:
				this.chatArea.setConsoleStyle(ConsoleManager.STYLE_AUTHENTICATED_RED);
				this.messageInput.setForeground(Color.BLACK);
				this.messageInput.setBackground(Color.WHITE);
				this.styleID++;
				break;
			//Colourful Style Orange
			case 4:
				this.chatArea.setConsoleStyle(ConsoleManager.STYLE_AUTHENTICATED_ORANGE);
				this.messageInput.setForeground(Color.BLACK);
				this.messageInput.setBackground(Color.WHITE);
				this.styleID++;
				break;
			//Default Authenticated Style
			default:
				this.styleID = 1;
				this.setColors(styleID);
				break;
		}
	}
	
	private void enableClientMenus()
	{
		this.signOut.setEnabled(true);
		this.setAvailability.setEnabled(true);
		this.availabilityAvailable.setEnabled(true);
		this.availabilityBusy.setEnabled(true);
		this.availabilityAway.setEnabled(true);
		this.availabilityAppearOffline.setEnabled(true);
		this.showConnectedUsers.setEnabled(true);
		this.exit.setEnabled(true);
		this.enterPrivateChannel.setEnabled(true);
		this.exitPrivateChannel.setEnabled(true);
		this.clearChat.setEnabled(true);
		this.sendImage.setEnabled(true);
		this.sendFile.setEnabled(true);
		this.setWindowColors.setEnabled(true);
		this.setSimpleView.setEnabled(true);
		this.aboutApp.setEnabled(true);
		this.checkVersion.setEnabled(true);
		this.getHelp.setEnabled(true);

		this.quickSignIn.setEnabled(false);
		this.signIn.setEnabled(false);
		this.signUp.setEnabled(false);
	}
	
	private void disableClientMenus()
	{
		this.quickSignIn.setEnabled(true);
		this.signIn.setEnabled(true);
		this.signUp.setEnabled(true);
		this.exit.setEnabled(true);
		this.aboutApp.setEnabled(true);
		this.getHelp.setEnabled(true);

		this.signOut.setEnabled(false);
		this.setAvailability.setEnabled(false);
		this.availabilityAvailable.setEnabled(false);
		this.availabilityBusy.setEnabled(false);
		this.availabilityAway.setEnabled(false);
		this.availabilityAppearOffline.setEnabled(false);
		this.showConnectedUsers.setEnabled(false);
		this.enterPrivateChannel.setEnabled(false);
		this.exitPrivateChannel.setEnabled(false);
		this.clearChat.setEnabled(false);
		this.sendImage.setEnabled(false);
		this.sendFile.setEnabled(false);
		this.setWindowColors.setEnabled(false);
		this.setSimpleView.setEnabled(false);
		this.checkVersion.setEnabled(false);
	}
	
	private void setCursorStyle(int cursorStyle)
	{
		this.setCursor(Cursor.getPredefinedCursor(cursorStyle));
	}
	
	private void authenticate(boolean tryQuick)
	{
		try
		{
			if(this.clientUser.isSignedIn())
			{
				this.chatArea.printConsole(new Message("You Are Already Signed In", "CLIENT", "0"));
				return;
			}

			//Construct Authenticator Object and Show Input Dialog
			Authenticator auth = new Authenticator(this);

			if(tryQuick)
				auth.quickAuthenticate();
			else
				auth.showAuthenticationDialog();
			
			//Start WebChatClient Thread
			this.client = new WebChatClient(this, auth.getSession());
			this.client.start();
		}
		catch(UnknownHostException e)
		{
			this.disconnect("IP Address of Host Could Not Be Determined");
			AbstractClient.logException(e);
		}
		catch(IOException e)
		{
			this.disconnect("Unable to Establish Connection to Host");
			AbstractClient.logException(e);
		}
		catch(SecurityException e)
		{
			this.disconnect("Security Manager: Operation not allowed");
			AbstractClient.logException(e);
		}
		catch(IllegalArgumentException e)
		{
			this.disconnect("Port Parameter Outside Specified Range of Valid Port Values" +
				"\nPlease Enter Port Between 0 and 65535 inclusive.");
			AbstractClient.logException(e);
		}
		catch(AuthenticationAbortedException e)
		{
			this.disconnect("Please Authenticate");
			AbstractClient.logException(e);
		}
		catch (AuthenticationException e)
		{
			this.disconnect(e.getMessage());
			AbstractClient.logException(e);
		}
	}

	private void createNewAccount()
	{
		try
		{
			if(this.clientUser.isSignedIn())
			{
				this.chatArea.printConsole(new Message("You Are Already Signed In", "CLIENT", "0"));
				return;
			}

			//Construct Authenticator Object and Show Input Dialog
			Authenticator auth = new Authenticator(this);
			auth.showNewAccountDialog();

			//Start WebChatClient Thread
			this.client = new WebChatClient(this, auth.getSession());
			this.client.start();
		}
		catch(UnknownHostException e)
		{
			this.disconnect("IP Address of Host Could Not Be Determined");
			AbstractClient.logException(e);
		}
		catch(IOException e)
		{
			this.disconnect("Unable to Establish Connection to Host");
			AbstractClient.logException(e);
		}
		catch(SecurityException e)
		{
			this.disconnect("Security Manager: Operation not allowed");
			AbstractClient.logException(e);
		}
		catch(IllegalArgumentException e)
		{
			this.disconnect("Port Parameter Outside Specified Range of Valid Port Values" +
					"\nPlease Enter Port Between 0 and 65535 inclusive.");
			AbstractClient.logException(e);
		}
		catch(AuthenticationAbortedException e)
		{
			this.disconnect("Please Authenticate");
			AbstractClient.logException(e);
		}
		catch (AuthenticationException e)
		{
			this.disconnect(e.getMessage());
			AbstractClient.logException(e);
		}
	}
	
	private void signOut()
	{
		//Disconnect Client from Server
		if(this.clientUser.isSignedIn())
		{
			//Execute Orderly Connection Release Procedure
			Command com = new Command(Command.CONNECTION_SUSPENDED, this.clientUser.getUsername(), this.clientUser.getUserID());
			this.send(com);
			this.disconnect("Please Sign In");
		}
		else
			this.chatArea.setText("You are already logged out");
	}
	
	public void disconnect(String message)
	{
		//Update User Instance Variables
		if(this.client != null)
		{
			this.client.disconnect();
			this.client = null;
		}
		this.clientUser.signOut();
		
		//Update Window Style
		this.setColors(0);
		this.disableClientMenus();
		this.statusBar.setAvailability(ClientUser.OFFLINE);
		this.statusBar.setChannel("Public Channel");
		
		//Update Window Properties
		super.setTitle("Web Chat Interface");
		this.chatArea.setText(message);
	}
	
	public void connectionAuthorized()
	{
		this.setColors(ConsoleManager.STYLE_AUTHENTICATED);
		this.chatArea.clearConsole();
		super.setTitle("Web Chat Interface - " + this.clientUser.getUsername());
		this.enableClientMenus();
		this.statusBar.setAvailability(ClientUser.AVAILABLE);
		this.statusBar.setChannel("Public Channel");
	}
	
	private void updateAvailable(Command com)
	{
		//If Client Version Does Not Match Most Recent Version from Server
		if (!com.getMessage().equals(AbstractIRC.CLIENT_VERSION))
		{
			//Construct Message Dialog with Appropriate Information
			JPanel dialogPanel = new JPanel();
			String message = "Update Available!\nYour Version: " + AbstractIRC.CLIENT_VERSION + 
				"\nNewest Version: " + com.getMessage();

			JOptionPane.showMessageDialog(dialogPanel, message,
					"Get Help", JOptionPane.INFORMATION_MESSAGE);
		}
		else
			this.displayMessage(new Message("You Are Running The Most Recent Version :)", "CLIENT", "0"));
	}
	
	public void processCommand(Command com)
	{
		switch(com.getCommand())
		{
			//If Server Responded to Version Request
			case Command.CLIENT_VERSION:
				this.updateAvailable(com);
				break;
			//If a Client Connected to Server Began to Type Message
			case Command.MESSAGE_TYPED:
				(new Thread()
				{
					@Override
					public void run()
					{
						WebChatClientGUI.this.send.setBackground(Color.BLACK);
						try
						{
							Thread.sleep(500);
						}
						catch (InterruptedException e)
						{
							AbstractClient.logException(e);
						}
						WebChatClientGUI.this.send.setBackground(Color.WHITE);
					}
				}).start();
				break;
			//If Server Closes
			case Command.CONNECTION_SUSPENDED:
				if(com.getReason() == Command.REASON_BLACKLISTED)
					this.disconnect("Disconnected; You are Blacklisted");
				else if(com.getReason() == Command.REASON_INCONSISTENT_USER_ID)
					this.disconnect("Disconnected; Inconsistent User Information");
				else if(com.getReason() == Command.REASON_KICKED)
					this.disconnect("Disconnected; You Were Kicked");
				else if(com.getReason() == Command.REASON_CHANNEL_CLOSED)
					this.disconnect("Disconnected; Channel Closed");
				else if(com.getReason() == Command.REASON_SERVER_CLOSED)
					this.disconnect("Disconnected; Server Closed");
				else if(com.getReason() == Command.REASON_SERVER_FULL)
					this.disconnect("Disconnected; Server Full");
				else
					this.disconnect("Disconnected;");
				break;
			//If Denied Connection to Server
			case Command.CONNECTION_DENIED:
				this.disconnect("Disconnected; " + com.getMessage());
				break;
			//If a User Requested a Private Channel
			case Command.PRIVATE_CHANNEL_REQUEST:
				this.privateChannelRequest(com);
				break;
			//If a User Authorized a Private Channel
			case Command.PRIVATE_CHANNEL_AUTHORIZED:
				this.enterPrivateChannel(com);
				break;
				//If a User Authorized a Private Channel
			case Command.PRIVATE_CHANNEL_DENIED:
				this.chatArea.printConsole(new Message("PrivateChannel Request Denied.", "CLIENT", "0"));
				break;
			case Command.PRIVATE_CHANNEL_EXIT:
				this.exitPrivateChannel();
				break;
		}
	}
	
	private void send(Object object)
	{
		try
		{
			if(!this.clientUser.isSignedIn())
				this.chatArea.setText("Please Authenticate");
			//Send MultimediaMessage Object
			else if(object instanceof TransferBuffer)
				this.client.send((TransferBuffer) object);
			//Send Message Object
			else if(object instanceof Message)
				this.client.send((Message) object);
			//Send Command Object
			else if(object instanceof Command)
				this.client.send((Command) object);
			else
				throw new IOException();
		}
		catch(IOException e)
		{
			AbstractClient.logException(e);
			this.displayMessage(new Message("Unable to Send Message", "CLIENT", "0"));
		}
	}
	
	public void displayMessage(Message message)
	{
		this.chatArea.printConsole(message);
	}
	
	public void displayFile(File file, Command transferManifest)
	{
		this.chatArea.printFile(file, transferManifest);
	}
	
	private void privateChannelRequest(Command com)
	{
		JPanel dialogPanel = new JPanel();
		dialogPanel.add(new JLabel(com.getSender() + " requested a private channel. Confirm?"));
		
		int returnValue = JOptionPane.showConfirmDialog(null, dialogPanel, 
				"Private Channel Request", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		if(returnValue == 0)
		{
			String[] recipient = {com.getSender(), com.getSenderID()};
			this.send(new Command(Command.PRIVATE_CHANNEL_AUTHORIZED, recipient, this.clientUser.getUsername(), this.clientUser.getUserID()));
			this.enterPrivateChannel(com);
		}
		else
		{
			String[] recipient = {com.getSender(), com.getSenderID()};
			this.send(new Command(Command.PRIVATE_CHANNEL_DENIED, recipient, this.clientUser.getUsername(), this.clientUser.getUserID()));
		}
	}
	
	private void enterPrivateChannel(Command com)
	{
		this.setColors(ConsoleManager.STYLE_AUTHENTICATED_RED);
		this.chatArea.clearConsole();
		this.chatArea.printConsole(new Message("Entered Private Channel", "CLIENT", "0"));
		this.statusBar.setChannel("Private Channel: " + com.getSender());
	}
	
	private void exitPrivateChannel()
	{
		if(this.clientUser.isSignedIn())
		{
			this.setColors(ConsoleManager.STYLE_AUTHENTICATED);
			this.chatArea.clearConsole();
			this.chatArea.printConsole(new Message("Private Channel Closed. Entered Public Channel", "CLIENT", "0"));
			this.statusBar.setChannel("Public Channel");
		}
	}
	
	private void setAvailability(int availability)
	{
		if(this.clientUser.isSignedIn())
		{
			this.clientUser.setAvailability(availability);
			this.statusBar.setAvailability(availability);

			if(availability == ClientUser.AVAILABLE)
				this.send(new Command(Command.CLIENT_AVAILABILITY_AVAILABLE, this.clientUser.getUsername(), this.clientUser.getUserID()));
			else if(availability == ClientUser.BUSY)
				this.send(new Command(Command.CLIENT_AVAILABILITY_BUSY, this.clientUser.getUsername(), this.clientUser.getUserID()));
			else if(availability == ClientUser.AWAY)
				this.send(new Command(Command.CLIENT_AVAILABILITY_AWAY, this.clientUser.getUsername(), this.clientUser.getUserID()));
			else if(availability == ClientUser.APPEAR_OFFLINE)
				this.send(new Command(Command.CLIENT_AVAILABILITY_APPEAR_OFFLINE, this.clientUser.getUsername(), this.clientUser.getUserID()));
		}
		else
			this.chatArea.setText("Please Authenticate");
	}

	private void exit()
	{
		if(this.clientUser.isSignedIn())
		{
			//Execute Orderly Connection Release Procedure
			Command com = new Command(Command.CONNECTION_SUSPENDED, this.clientUser.getUsername(), this.clientUser.getUserID()); 
			this.send(com);
			this.disconnect("Please Sign In");
			
		}
		
		System.exit(0);
	}

	private void checkVersion()
	{
		if(this.clientUser.isSignedIn())
		{
			//ping command to server with client version
			Command com = new Command(Command.CLIENT_VERSION_REQUEST, this.clientUser.getUsername(), this.clientUser.getUserID());
			this.send(com);
		}
		else
			this.chatArea.setText("Please Authenticate");
	}
	
	private void sendFile()
	{
		if(this.clientUser.isSignedIn())
		{
			//Show File Chooser
			FileDialog fd = new FileDialog(this, "Attach:", FileDialog.LOAD);
			fd.setVisible(true);
			String filename = fd.getFile();
			String fileLocation = fd.getDirectory();
			
			if(filename != null)
			{
				//Retrieve Selected File Path
				File file = new File(fileLocation + "//" + filename);
				
				//Do Not Allow File Size > 100MB
				if((int)file.length() > 104857600)
				{
					this.chatArea.printConsole(new Message("Cannot send file. Maximum file size is 100MB", "CLIENT", "0"));
					return;
				}
				
				//Send File
				this.client.send(file);
			}
		}
		else
			this.chatArea.setText("Please Authenticate");
	}
	
	private void sendMessage()
	{
		if(this.clientUser.isSignedIn())
		{
			//Get Text From Text Input Area
			String messageText = messageInput.getText().trim();
			
			if(!messageText.isEmpty())
			{
				//Create and Send Message Object, Clear TextArea
				Message message = new Message(messageText, this.clientUser.getUsername(), this.clientUser.getUserID());
				this.send(message);
				this.messageInput.setText("");
				this.messageInput.requestFocus();
				hasTyped = false;
			}
		}
		else
			this.chatArea.setText("Please Authenticate");
	}
	
	private void showConnectedUsersDialog()
	{
		if(this.clientUser.isSignedIn())
			(new ConnectedUsersWindow(this.client, this.clientUser)).start();
	}
	
	private void showPrivateChannelSelectionDialog()
	{
		if(this.clientUser.isSignedIn())
		{
			//Set Dialog Window Properties
			JDialog dialogPanel = new JDialog();
			JPanel componentPanel = new JPanel();
			componentPanel.setLayout(new BoxLayout(componentPanel, BoxLayout.PAGE_AXIS));
			dialogPanel.setTitle("Connected Users");
			dialogPanel.setIconImage(ResourceLoader.getInstance().getFrameIcon());
			
			//Declare Availability ImageIcons
			Object availableIcon;
			Object busyIcon;
			Object awayIcon;
			
			//Attempt to Set ImageIcons
			try
			{
				availableIcon = new ImageIcon(ImageIO.read(WebChatClientGUI.class.getResource("/webchatinterface/client/resources/AVAILABLE.png")));
				busyIcon = new ImageIcon(ImageIO.read(WebChatClientGUI.class.getResource("/webchatinterface/client/resources/BUSY.png")));
				awayIcon = new ImageIcon(ImageIO.read(WebChatClientGUI.class.getResource("/webchatinterface/client/resources/AWAY.png")));
			}
			catch(IOException | IllegalArgumentException e)
			{
				AbstractClient.logException(e);
				availableIcon = "";
				busyIcon = "";
				awayIcon = "";
			}
			
			//Set Column Labels
			String[] columnLabel = new String[3];
			columnLabel[0] = "";
			columnLabel[1] = "Username";
			columnLabel[2] = "Status";
			
			//Construct Table Array
			Object[][] connectedUsers = this.client.getConnectedUsers();
			
			int size = connectedUsers.length;
			for(Object[] user : connectedUsers)
			{
				if((Integer)user[3] == ClientUser.APPEAR_OFFLINE || (Integer)user[3] == ClientUser.OFFLINE)
					size--;
			}
			
			Object[][] table = new Object[size][columnLabel.length];
			
			for (int i = 0, o = 0; i < size; i++, o++)
			{
				//Switch Client Availability
				switch((Integer)connectedUsers[o][3])
				{
					case ClientUser.AVAILABLE:
						table[i][0] = availableIcon;
						table[i][2] = "AVAILABLE";
						break;
					case ClientUser.BUSY:
						table[i][0] = busyIcon;
						table[i][2] = "BUSY";
						break;
					case ClientUser.AWAY:
						table[i][0] = awayIcon;
						table[i][2] = "AWAY";
						break;
					case ClientUser.APPEAR_OFFLINE:
					case ClientUser.OFFLINE:
						i--;
						continue;
				}
				
				//Assign Client Information to Object[][]
				if(connectedUsers[o][1].equals(this.clientUser.getUserID()))
					table[i][1] = "Me";
				else
					table[i][1] = connectedUsers[o][0];
			}
			
			//Declare and Initialize JTable with Data and Column Labels
			//Override getColumnClass() method to allow ImageIcons to be rendered
			JTable clientConnections = new JTable(table, columnLabel)
			{
	            private static final long serialVersionUID = -986803268686380681L;

				//  Returning the Class of each column will allow different
	            //  renderer to be used based on Class
	            @Override
				public Class<?> getColumnClass(int column)
	            {
	                return getValueAt(0, column).getClass();
	            }
	        };
	        clientConnections.setColumnSelectionAllowed(false);
	        clientConnections.setRowSelectionAllowed(true);
	        
	        //Set Static Column Widths
	        clientConnections.getColumnModel().getColumn(0).setPreferredWidth(16);
	        clientConnections.getColumnModel().getColumn(0).setMinWidth(16);
	        clientConnections.getColumnModel().getColumn(0).setMaxWidth(16);
	        clientConnections.getColumnModel().getColumn(2).setPreferredWidth(100);
	        clientConnections.getColumnModel().getColumn(2).setMinWidth(100);
	        clientConnections.getColumnModel().getColumn(2).setMaxWidth(100);
	        
	        //Make Scrollable
			JScrollPane scrollPane = new JScrollPane(clientConnections);
			clientConnections.setPreferredScrollableViewportSize(new Dimension(250,400));
			componentPanel.add(scrollPane);
			
			JButton okButton = new JButton("Enter Private Channel");
			okButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					if(clientConnections.getSelectedRow() == -1)
						dialogPanel.dispose();
					else
					{
						try
						{
							Object[] recipient = connectedUsers[clientConnections.getSelectedRow()];
							if(recipient[1].equals(WebChatClientGUI.this.clientUser.getUserID()))
								WebChatClientGUI.this.chatArea.printConsole(new Message("Unable to Enter Private Channel with Yourself", "CLIENT", "0"));
							else
							{
								WebChatClientGUI.this.client.send(new Command(Command.PRIVATE_CHANNEL_REQUEST, recipient, WebChatClientGUI.this.clientUser.getUsername(), WebChatClientGUI.this.clientUser.getUserID()));
								WebChatClientGUI.this.chatArea.printConsole(new Message("Private Channel Request Sent to " + recipient[0], "CLIENT", "0"));
							}
							
						}
						catch (IOException e)
						{
							AbstractClient.logException(e);
							WebChatClientGUI.this.chatArea.printConsole(new Message("Unable to Enter Private Channel", "CLIENT", "0"));
						}
						finally
						{
							dialogPanel.dispose();
						}
					}
				}
			});
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					dialogPanel.dispose();
				}
			});
			
			JPanel buttons = new JPanel();
			buttons.add(okButton);
			buttons.add(cancelButton);
			componentPanel.add(buttons);
			
			dialogPanel.setContentPane(componentPanel);
			dialogPanel.pack();
			dialogPanel.setVisible(true);
			dialogPanel.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		}
		else
		{
			this.chatArea.setText("Please Authenticate");
		}
	}
	
	private void showAboutDialog()
	{
		AboutApplicationDialog.showAboutDialog();
	}
	
	private void showHelpDialog()
	{
		HelpDialog.showHelpDialog();
	}
	
	public void actionPerformed(ActionEvent event)
	{
		//If User Selected to Sign In
		if(event.getSource() == this.quickSignIn)
		{
			//Attempt to sign in with accelerated authentication.
			this.setCursorStyle(Cursor.WAIT_CURSOR);
			this.authenticate(true);
			this.setCursorStyle(Cursor.DEFAULT_CURSOR);
		}
		//If User Selected to Sign In
		else if(event.getSource() == this.signIn)
		{
			//Attempt to sign in with normal authentication.
			this.setCursorStyle(Cursor.WAIT_CURSOR);
			this.authenticate(false);
			this.setCursorStyle(Cursor.DEFAULT_CURSOR);
		}
		//If User Selected to Sign Up
		else if(event.getSource() == this.signUp)
		{
			this.createNewAccount();
		}
		//If User Selected to Disconnect
		else if(event.getSource() == this.signOut)
		{
			this.signOut();
		}
		//If User Selected to Set Availability to Available
		else if(event.getSource() == this.availabilityAvailable)
		{
			this.setAvailability(ClientUser.AVAILABLE);
		}
		//If User Selected to Set Availability to Busy
		else if(event.getSource() == this.availabilityBusy)
		{
			this.setAvailability(ClientUser.BUSY);
		}
		//If User Selected to Set Availability to Away
		else if(event.getSource() == this.availabilityAway)
		{
			this.setAvailability(ClientUser.AWAY);
		}
		//If User Selected to Set Availability to Appear Offline
		else if(event.getSource() == this.availabilityAppearOffline)
		{
			this.setAvailability(ClientUser.APPEAR_OFFLINE);
		}
		//If user selected to display connected users
		else if(event.getSource() == this.showConnectedUsers)
		{
			this.showConnectedUsersDialog();
		}
		//If User Selected to Close Client Application
		else if(event.getSource() == this.exit)
		{
			this.setCursorStyle(Cursor.WAIT_CURSOR);
			this.exit();
			this.setCursorStyle(Cursor.DEFAULT_CURSOR);
		}
		else if(event.getSource() == this.enterPrivateChannel)
		{
			this.showPrivateChannelSelectionDialog();
		}
		else if(event.getSource() == this.exitPrivateChannel)
		{
			this.send(new Command(Command.PRIVATE_CHANNEL_EXIT, this.clientUser.getUsername(), this.clientUser.getUserID()));
		}
		//If User Selected to Clear Chat Window
		else if(event.getSource() == this.clearChat && this.clientUser.isSignedIn())
		{
			this.chatArea.clearConsole();
		}
		//If User Selected to Show About Dialog
		else if(event.getSource() == this.aboutApp)
		{
			this.showAboutDialog();
		}
		//If User Selected to Check Client Version
		else if(event.getSource() == this.checkVersion)
		{
			this.checkVersion();
		}
		//If User Selected to Send Image
		else if(event.getSource() == this.sendImage)
		{
			this.sendFile();
		}
		else if(event.getSource() == this.sendFile)
		{
			this.sendFile();
		}
		//If User Selected to Change Window Colors
		else if(event.getSource() == this.setWindowColors)
		{
			this.setColors(styleID);
		}
		else if(event.getSource() == this.setSimpleView)
		{
			this.chatArea.setSimpleView(!this.chatArea.isSimpleView());
		}
		//If User Selected to Show Help Dialog
		else if(event.getSource() == this.getHelp)
		{
			this.showHelpDialog();
		}
		//If User Selected to Send Message
		else if(event.getSource() == this.send)
		{
			this.sendMessage();
		}
	}
	
	public void keyPressed(KeyEvent event)
	{
		//If User Signed In and Pressed Enter Key
		if(event.getKeyCode() == KeyEvent.VK_ENTER && this.clientUser.isSignedIn())
		{
			event.consume();
			this.sendMessage();
		}
		//If User Signed In and Pressed Any Key Except Enter
		else if(event.getKeyCode() != KeyEvent.VK_ENTER && this.clientUser.isSignedIn())
		{
			//If User Has Not Already Typed
			if(!hasTyped)
			{
				//Change Variable
				hasTyped = true;
				
				//Broadcast Message
				this.send(new Command(Command.MESSAGE_TYPED, this.clientUser.getUsername(), this.clientUser.getUserID()));
			}
		}
	}
	
	public void keyTyped(KeyEvent event) {}
	public void keyReleased(KeyEvent event) {}
	
	public void windowClosing(WindowEvent event)
	{
		this.exit();
	}

	public void windowClosed(WindowEvent event)
	{
		this.exit();
	}

	public void windowActivated(WindowEvent event){	}
	public void windowDeactivated(WindowEvent event){ }
	public void windowDeiconified(WindowEvent event){ }
	public void windowIconified(WindowEvent event){ }
	public void windowOpened(WindowEvent event){ }
}