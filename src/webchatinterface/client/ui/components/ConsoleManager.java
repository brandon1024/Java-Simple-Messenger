package webchatinterface.client.ui.components;

import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import util.DynamicQueue;
import webchatinterface.client.AbstractClient;
import webchatinterface.helpers.TimeHelper;
import webchatinterface.util.ClientUser;
import webchatinterface.util.Command;
import webchatinterface.util.Message;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The {@code ConsoleManager} class manages the display of messages to the console. The purpose of the 
  *Console Manager is to prevent messages from displaying incorrectly in high volume reception situations.
  *The Console Manager implements a queued message system, in which messages are displayed in the order
  *in which they were received.
  *<p>
  *Messages are displayed in such a way to distinguish the curent user's messages from other user's 
  *messages. Users are distingued by the SENDER_ID field of the Message objects. 
  *If the SENDER_ID field matches the userID defined in WebChatClientGUI, the message is right
  *justified in the JTextPane. Otherwise, the message is left justified. Above the message, the 
  *username of the sender is displayed in a smaller font size. Similarily, below the message, the
  *time sent is displayed in a smaller font size.
  *<p>
  *Images are also displayed in the JTextPane, justified similar to simple text messages. These
  *images are part of the private inner ImageButton class, allowing them to be clicked to display
  *the images enlarged in a JDialog.
  *<p>
  *Files may also be displayed in the JTextPane, justified similar to simple text messages. Once received,
  *a file is represented by a FileButton object, allowing the user to save the file to the desired
  *directory.
  *<p>
  *The console manager extends JTextPane, so it may be used as a component in the GUI. However, the 
  *Console Manager uses a print queue to correctly print messages to the console. Therefore, using 
  *methods defined  in JTextPane are strongly discouraged.
  */

public class ConsoleManager extends JTextPane implements Runnable
{
	/**Serial Version UID is used as a version control for the class that implements
	 *the serializable interface.*/
	private static final long serialVersionUID = 2619250731419205387L;

	/**Default console style; black foreground with white background*/
	public static final int STYLE_INITIALIZED = 0;
	
	/**Stylized console; white foreground with charcoal background*/
	public static final int STYLE_AUTHENTICATED = 1;
	
	/**Stylized console; green foreground with black background*/
	public static final int STYLE_AUTHENTICATED_HACKER = 2;
	
	/**Stylized console; green foreground with black background*/
	public static final int STYLE_AUTHENTICATED_RED = 3;
	
	/**Stylized console; black foreground with purple background*/
	public static final int STYLE_AUTHENTICATED_ORANGE = 4;
	
	/**Queue of ConsoleMessage objects waiting to be appended to the underlying JTextPane*/
	private DynamicQueue<ConsoleMessage> consoleQueue;
	
	/**A list of cached ConsoleMessages. This ArrayList is used to repopulate the
	  *console conversation when the user selects to change the console style, which
	  *requires clearing the console*/
	private ArrayList<ConsoleMessage> cachedMessages;
	
	/**The StyledDocument for the underlying JTextPane*/
	private StyledDocument doc;
	
	/**Attribute Set for current user message header*/
	private MutableAttributeSet currentUserStyleHeader;
	
	/**Attribute Set for current user message body*/
	private MutableAttributeSet currentUserStyleText;
	
	/**Attribute Set for other users message header*/
	private MutableAttributeSet otherUsersStyleHeader;
	
	/**Attribute Set for other users message body*/
	private MutableAttributeSet otherUsersStyleText;
	
	/**Current style ID*/
	private int style;
	
	/**Allows messages to be displayed in a simple top down view, as opposed to the
	  *more complex justified view.*/
	private boolean simpleView = false;

	/**Control variable for the ConsoleManager thread.*/
	private volatile boolean isRunning = false;
	
	/**The ClientUser object representing a model of the user, the user status, and parameters.
	  *@see webchatinterface.util.ClientUser*/
	private ClientUser client;
	
	/**Builds a {@code ConsoleManager} object. Constructs the underlying JTextPane framework, print 
	  *queue, and style attributes.*/
	private ConsoleManager()
	{
		//Build JTextPane
		super();
		super.setEditable(false);
		super.setFocusable(true);
		super.setFont(new Font("Courier New", Font.PLAIN, 12));
		this.style = ConsoleManager.STYLE_INITIALIZED;
		
		this.client = AbstractClient.getClientUser();
		
		//Provide reference to JTextPane StyledDocument
		this.doc = super.getStyledDocument();
		
		//Style Attribute Set for Current User Message Header
		this.currentUserStyleHeader = new SimpleAttributeSet();
		StyleConstants.setForeground(this.currentUserStyleHeader, Color.white);
		StyleConstants.setFontSize(this.currentUserStyleHeader, 10);
		StyleConstants.setAlignment(this.currentUserStyleHeader, StyleConstants.ALIGN_RIGHT);
		
		//Style Attribute Set for Current User Message Body
		this.currentUserStyleText = new SimpleAttributeSet();
		StyleConstants.setForeground(this.currentUserStyleText, Color.white);
		StyleConstants.setFontSize(this.currentUserStyleText, 14);
		StyleConstants.setAlignment(this.currentUserStyleText, StyleConstants.ALIGN_RIGHT);
		
		//Style Attribute Set for Other User Message Header
		this.otherUsersStyleHeader = new SimpleAttributeSet();
		StyleConstants.setForeground(this.otherUsersStyleHeader, Color.white);
		StyleConstants.setFontSize(this.otherUsersStyleHeader, 10);
		StyleConstants.setAlignment(this.otherUsersStyleHeader, StyleConstants.ALIGN_LEFT);
		
		//Style Attribute Set for Other User Message Body
		this.otherUsersStyleText = new SimpleAttributeSet();
		StyleConstants.setForeground(this.otherUsersStyleText, Color.white);
		StyleConstants.setFontSize(this.otherUsersStyleText, 14);
		StyleConstants.setAlignment(this.otherUsersStyleText, StyleConstants.ALIGN_LEFT);
		
		//Build ConsoleQueue and Message Cache
		this.consoleQueue = new DynamicQueue<ConsoleMessage>();
		this.cachedMessages = new ArrayList<ConsoleMessage>();
	}
	
	/**Start the ConsoleManager thread.*/
	public void start()
	{
		if(this.isRunning())
			return;
		
		this.isRunning = true;
		(new Thread(this)).start();
	}
	
	/**Accessor method for the state of the ConsoleManager thread.
	  *@return true of the ConsoleManager is running, false otherwise.*/
	private boolean isRunning()
	{
		return this.isRunning;
	}
	
	/**Executed when the {@code ConsoleManager} thread starts. Periodically polls the console queue, 
	  *appending any stored messages, images or files to the underlying JTextPane in a sequential 
	  *manner. Sets the caret to the end of the document, such that it remains scolled and new text 
	  *is easily visible.
	  *<p>
	  *Poll time: 100ms*/
	@Override
	public void run()
	{
		while(this.isRunning)
		{
			//If Queues Empty, Sleep 100ms
			if(this.consoleQueue.isEmpty())
			{
				try
				{
					Thread.sleep(100);
				}
				catch(InterruptedException e)
				{
					AbstractClient.logException(e);
				}
			}
			//If Print Queue Not Empty
			if(!this.consoleQueue.isEmpty())
			{
				//Dequeue Message From Queue
				ConsoleMessage message = this.consoleQueue.dequeue();
				
				if(message == null)
					continue;
				
				String messageBody = message.getMessage();
				String sender = message.getSender();
				String senderID = message.getSenderID();
				String timestamp = message.getTimestamp();
				FileButton file = message.getFile();
				ImageButton image = message.getImage();
				int type = message.getType();
				
				if(type == ConsoleMessage.MESSAGE)
				{
					if(this.simpleView)
					{
						try
						{
							this.doc.setParagraphAttributes(doc.getLength(), 1, this.otherUsersStyleHeader, false);
							this.doc.insertString(this.doc.getLength(), "[" + sender + "] " + messageBody + "\n", this.otherUsersStyleText);
						}
						catch(BadLocationException e)
						{
							AbstractClient.logException(e);
						}
					}
					//If Sender ID Matches Client User ID
					else if(senderID.equals(this.client.getUserID()))
					{
						try
						{
							//Display Message
							this.doc.setParagraphAttributes(doc.getLength(), 1, this.currentUserStyleHeader, false);
							this.doc.insertString(this.doc.getLength(), sender + ":\n", this.currentUserStyleHeader);
							this.doc.insertString(this.doc.getLength(), messageBody + "\n", this.currentUserStyleText);
							this.doc.insertString(this.doc.getLength(), "Sent: " + timestamp + "\n\n", this.currentUserStyleHeader);
						}
						catch(BadLocationException e)
						{
							AbstractClient.logException(e);
						}
					}
					//If Sender ID Does Not Match Client User ID
					else
					{
						try
						{
							//Display Message
							this.doc.setParagraphAttributes(doc.getLength(), 1, this.otherUsersStyleHeader, false);
							this.doc.insertString(this.doc.getLength(), sender + ":\n", this.otherUsersStyleHeader);
							this.doc.insertString(this.doc.getLength(), messageBody + "\n", this.otherUsersStyleText);
							this.doc.insertString(this.doc.getLength(), "Sent: " + timestamp + "\n\n", this.otherUsersStyleHeader);
						}
						catch(BadLocationException e)
						{
							AbstractClient.logException(e);
						}
					}
				}
				else if(type == ConsoleMessage.IMAGE)
				{
					if(this.simpleView)
					{
						try
						{
							this.doc.setParagraphAttributes(doc.getLength(), 1, this.otherUsersStyleHeader, false);
							this.doc.insertString(this.doc.getLength(), "[" + sender + "] " + "\n", this.otherUsersStyleText);
							super.insertComponent(image);
							this.doc.insertString(this.doc.getLength(), "\n", this.otherUsersStyleText);
						}
						catch(BadLocationException e)
						{
							AbstractClient.logException(e);
						}
					}
					//If Sender ID Matches Client User ID
					else if(senderID.equals(this.client.getUserID()))
					{
						try
						{
							//Display Message
							this.doc.setParagraphAttributes(doc.getLength(), 1, this.currentUserStyleHeader, false);
							this.doc.insertString(this.doc.getLength(), sender + ":\n", this.currentUserStyleHeader);
							super.insertComponent(image);
							this.doc.insertString(this.doc.getLength(), "\nSent: " + timestamp + "\n\n", this.currentUserStyleHeader);
						}
						catch(BadLocationException e)
						{
							AbstractClient.logException(e);
						}
					}
					//If Sender ID Does Not Matche Client User ID
					else
					{
						try
						{
							//Display Message
							this.doc.setParagraphAttributes(doc.getLength(), 1, this.otherUsersStyleHeader, false);
							this.doc.insertString(this.doc.getLength(), sender + ":\n", this.otherUsersStyleHeader);
							super.insertComponent(image);
							this.doc.insertString(this.doc.getLength(), "\nSent: " + timestamp + "\n\n", this.currentUserStyleHeader);
						}
						catch(BadLocationException e)
						{
							AbstractClient.logException(e);
						}
					}
				}
				else if(type == ConsoleMessage.FILE)
				{
					if(this.simpleView)
					{
						try
						{
							this.doc.setParagraphAttributes(doc.getLength(), 1, this.otherUsersStyleHeader, false);
							this.doc.insertString(this.doc.getLength(), "[" + sender + "] " + "\n", this.otherUsersStyleText);
							super.insertComponent(file);
							this.doc.insertString(this.doc.getLength(), "\nSize: " + messageBody, this.otherUsersStyleHeader);
							this.doc.insertString(this.doc.getLength(), "\n", this.otherUsersStyleText);
						}
						catch(BadLocationException e)
						{
							AbstractClient.logException(e);
						}
					}
					//If Sender ID Matches Client User ID
					else if(senderID.equals(this.client.getUserID()))
					{
						try
						{
							//Display Message
							this.doc.setParagraphAttributes(doc.getLength(), 1, this.currentUserStyleHeader, false);
							this.doc.insertString(this.doc.getLength(), sender + ":\n", this.currentUserStyleHeader);
							
							//Insert FileButton Component
							super.insertComponent(file);
							
							this.doc.insertString(this.doc.getLength(), "\nSize: " + messageBody, this.currentUserStyleHeader);
							this.doc.insertString(this.doc.getLength(), "\nSent: " + timestamp + "\n\n", this.currentUserStyleHeader);
						}
						catch(BadLocationException e)
						{
							AbstractClient.logException(e);
						}
					}
					//If Sender ID Does Not Matche Client User ID
					else
					{
						try
						{
							//Display Message
							this.doc.setParagraphAttributes(doc.getLength(), 1, this.otherUsersStyleHeader, false);
							this.doc.insertString(this.doc.getLength(), sender + ":\n\n", this.otherUsersStyleHeader);
							
							//Insert FileButton Component
							super.insertComponent(file);
							
							this.doc.insertString(this.doc.getLength(), "\nSize: " + messageBody, this.otherUsersStyleHeader);
							this.doc.insertString(this.doc.getLength(), "\nSent: " + timestamp + "\n\n", this.otherUsersStyleHeader);
						}
						catch(BadLocationException e)
						{
							AbstractClient.logException(e);
						}
					}
					
					if(this.cachedMessages.size() >= 100)
						this.cachedMessages.clear();
				}
			}
			
			super.setCaretPosition(this.doc.getLength());
		}
	}
	
	/**Private method used by run() to format a message timestamp. Message timestamps are
	  *expressed according to standard ISO 8601 format with UTC offset. {@code formatTimestamp()}
	  *converts the message timestamp to civil time of this machine, and formats the time
	  *to a 12-hour time format.
	  *<p>
	  *If the parameter format is incorrect, the method will return null;
	  *@param messageTimestamp the unformatted timestamp of format {@code YYYY-MM-DDThh:mm:ss+00:00} to
	  *be formatted to 12-hour time format
	  *@return the formatted timestamp in 12-hour time format, or null if format is incorrect*/
	private String formatTimestamp(String messageTimestamp)
	{
		try
		{
			String systemTimestamp = TimeHelper.formatTimestampUTC(Calendar.getInstance());
			int[] systemTime = new int[8];
			systemTime[0] = Integer.parseInt(systemTimestamp.substring(0, 4)); //year
			systemTime[1] = Integer.parseInt(systemTimestamp.substring(5, 7)); //month
			systemTime[2] = Integer.parseInt(systemTimestamp.substring(8, 10)); //day
			systemTime[3] = Integer.parseInt(systemTimestamp.substring(11, 13)); //hour
			systemTime[4] = Integer.parseInt(systemTimestamp.substring(14, 16)); //minute
			systemTime[5] = Integer.parseInt(systemTimestamp.substring(17, 19)); //sec
			systemTime[6] = Integer.parseInt(systemTimestamp.substring(19, 22)); //UTC hour offset
			systemTime[7] = Integer.parseInt(systemTimestamp.substring(23, 25)); //UTC min offset
			
			int[] messageTime = new int[8];
			messageTime[0] = Integer.parseInt(messageTimestamp.substring(0, 4)); //year
			messageTime[1] = Integer.parseInt(messageTimestamp.substring(5, 7)); //month
			messageTime[2] = Integer.parseInt(messageTimestamp.substring(8, 10)); //day
			messageTime[3] = Integer.parseInt(messageTimestamp.substring(11, 13)); //hour
			messageTime[4] = Integer.parseInt(messageTimestamp.substring(14, 16)); //minute
			messageTime[5] = Integer.parseInt(messageTimestamp.substring(17, 19)); //sec
			messageTime[6] = Integer.parseInt(messageTimestamp.substring(19, 22)); //UTC hour offset
			messageTime[7] = Integer.parseInt(messageTimestamp.substring(23, 25)); //UTC min offset
			
			//Convert Message Time to UTC
			messageTime[3] += (-1 * messageTime[6]);
			messageTime[4] += messageTime[7];
			messageTime[6] = 0;
			messageTime[7] = 0;
			
			if(messageTime[4] >= 60)
			{
				messageTime[3]++;
				messageTime[4] -= 60;
			}
			else if(messageTime[4] < 0)
			{
				messageTime[3]--;
				messageTime[4] += 60;
			}
			
			if(messageTime[3] >= 24)
			{
				messageTime[2]++;
				messageTime[3] -= 24;
			}
			else if(messageTime[3] < 0)
			{
				messageTime[2]--;
				messageTime[3] += 24;
			}
			
			//Add Local UTC Offset to Message Time
			messageTime[3] += systemTime[6];
			messageTime[4] += systemTime[7];
			
			if(messageTime[4] >= 60)
			{
				messageTime[3]++;
				messageTime[4] -= 60;
			}
			else if(messageTime[4] < 0)
			{
				messageTime[3]--;
				messageTime[4] += 60;
			}
			
			if(messageTime[3] >= 24)
			{
				messageTime[2]++;
				messageTime[3] -= 24;
			}
			else if(messageTime[3] < 0)
			{
				messageTime[2]--;
				messageTime[3] += 24;
			}
			
			//Format Timestamp to 12-hour Time
			String formattedTimestamp;
			
			if(messageTime[3] > 12)
			{
				formattedTimestamp = (messageTime[3] - 12) + ":";
				if(messageTime[4] < 10)
					formattedTimestamp += "0";
				formattedTimestamp += messageTime[4] + "pm";
			}
			else
			{
				formattedTimestamp = messageTime[3] + ":";
				if(messageTime[4] < 10)
					formattedTimestamp += "0";
				formattedTimestamp += messageTime[4] + "am";
			}
			
			return formattedTimestamp;
		}
		catch(IndexOutOfBoundsException e)
		{
			AbstractClient.logException(e);
			return null;
		}
	}
	
	/**Enqueue a new {@code Message} object.
	  *@param message the {@code Message} object to enqueue to the printQueue.
	  *@see ConsoleManager#run()*/
	public void printConsole(Message message)
	{
		String messageBody = message.getMessage();
		String sender = message.getSender();
		String senderID = message.getSenderID();
		String timestamp = this.formatTimestamp(message.getTimeStamp());
		ConsoleMessage component = new ConsoleMessage(messageBody, sender, timestamp, senderID);
		
		synchronized(this)
		{
			this.consoleQueue.enqueue(component);
			this.cachedMessages.add(component);
		}
	}
	
	/**Enqueue a new file object to be displayed in the console.
	  *@param file the file that the new FileButton object will represent
	  *@param transferManifest the file transfer manifest command.*/
	public void printFile(File file, Command transferManifest)
	{
		String filename = file.getName();
		String fileExtension = filename.substring(filename.lastIndexOf('.'));
		
		if(fileExtension.equalsIgnoreCase(".jpg") || fileExtension.equalsIgnoreCase(".jpeg") || fileExtension.equalsIgnoreCase(".gif" ) || fileExtension.equalsIgnoreCase(".png"))
		{
			ImageButton image = new ImageButton(file, transferManifest);
			ConsoleMessage component = new ConsoleMessage(image, this.formatTimestamp(transferManifest.getTimeStamp()), transferManifest);
			
			synchronized(this)
			{
				this.consoleQueue.enqueue(component);
				this.cachedMessages.add(component);
			}
		}
		else
		{
			String size;
			if(file.length() / 1024 / 1024 > 1)
				size = file.length() / 1024 / 1024 + "MB";
			else if(file.length() / 1024 > 1)
				size = file.length() / 1024 + "kB";
			else
				size = file.length() + "B";
			
			FileButton fileButton = new FileButton(file);
			ConsoleMessage component = new ConsoleMessage(fileButton, size, this.formatTimestamp(transferManifest.getTimeStamp()), transferManifest);
			
			synchronized(this)
			{
				this.consoleQueue.enqueue(component);
				this.cachedMessages.add(component);
			}
		}
	}
	
	/**Removes all stored objects in the print queue, clears all the text in the JTextPane, and
	  *appends a message to the text pane.
	  *@param message a string representing the text to display in the empty console.*/
	public void setText(String message)
	{
		this.clearConsole();
		
		try
		{
			this.doc.setParagraphAttributes(doc.getLength(), 1, this.otherUsersStyleText, false);
			this.doc.insertString(this.doc.getLength(), message + "\n", this.otherUsersStyleText);
		}
		catch(BadLocationException e)
		{
			AbstractClient.logException(e);
		}
	}
	
	/**Changes the way message objects are appended to the console.
	  *<p>
	  *The format for simple view is as follows:
	  *Format: {@code [USERNAME] message...}
	  *@param simpleView if true, messages are displayed in a simple view. If false, messages
	  *are displayed in standard view.*/
	public void setSimpleView(boolean simpleView)
	{
		this.simpleView = simpleView;

		super.setText("");
		
		synchronized(this)
		{
			this.consoleQueue.removeAll();
		
			for(ConsoleMessage message : this.cachedMessages)
				this.consoleQueue.enqueue(message);
		}
	}
	
	/**Accessor method for the message display style. If true, messages are displayed in a simple view. 
	  *If false, messages are displayed in standard view.
	  *@return true if the console message display style is simple*/
	public boolean isSimpleView()
	{
		return this.simpleView;
	}
	
	/**Removes all stored objects in the print queue, and clears all the text in the JTextPane.*/
	public void clearConsole()
	{
		synchronized(this)
		{
			this.consoleQueue.removeAll();
			this.cachedMessages.clear();
		}
		
		super.setText("");
	}
	
	/**Set the style of the console.
	  *@param styleID the style code
	  *@see ConsoleManager#STYLE_INITIALIZED
	  *@see ConsoleManager#STYLE_AUTHENTICATED
	  *@see ConsoleManager#STYLE_AUTHENTICATED_HACKER
	  *@see ConsoleManager#STYLE_AUTHENTICATED_ORANGE*/
	public void setConsoleStyle(int styleID)
	{
		this.style = styleID;
		
		//change console style
		switch(this.style)
		{
			case ConsoleManager.STYLE_INITIALIZED:
				super.repaint();
				StyleConstants.setForeground(this.currentUserStyleHeader, Color.black);
				StyleConstants.setForeground(this.currentUserStyleText, Color.black);
				StyleConstants.setForeground(this.otherUsersStyleHeader, Color.black);
				StyleConstants.setForeground(this.otherUsersStyleText, Color.black);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.currentUserStyleHeader, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.currentUserStyleText, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.otherUsersStyleHeader, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.otherUsersStyleText, false);
				break;
			case ConsoleManager.STYLE_AUTHENTICATED:
				super.repaint();
				StyleConstants.setForeground(this.currentUserStyleHeader, Color.white);
				StyleConstants.setForeground(this.currentUserStyleText, Color.white);
				StyleConstants.setForeground(this.otherUsersStyleHeader, Color.white);
				StyleConstants.setForeground(this.otherUsersStyleText, Color.white);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.currentUserStyleHeader, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.currentUserStyleText, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.otherUsersStyleHeader, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.otherUsersStyleText, false);
				break;
			case ConsoleManager.STYLE_AUTHENTICATED_HACKER:
				super.repaint();
				StyleConstants.setForeground(this.currentUserStyleHeader, Color.green);
				StyleConstants.setForeground(this.currentUserStyleText, Color.green);
				StyleConstants.setForeground(this.otherUsersStyleHeader, Color.green);
				StyleConstants.setForeground(this.otherUsersStyleText, Color.green);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.currentUserStyleHeader, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.currentUserStyleText, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.otherUsersStyleHeader, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.otherUsersStyleText, false);
				break;
			case ConsoleManager.STYLE_AUTHENTICATED_RED:
				super.repaint();
				StyleConstants.setForeground(this.currentUserStyleHeader, Color.white);
				StyleConstants.setForeground(this.currentUserStyleText, Color.white);
				StyleConstants.setForeground(this.otherUsersStyleHeader, Color.white);
				StyleConstants.setForeground(this.otherUsersStyleText, Color.white);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.currentUserStyleHeader, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.currentUserStyleText, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.otherUsersStyleHeader, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.otherUsersStyleText, false);
				break;
			case ConsoleManager.STYLE_AUTHENTICATED_ORANGE:
				super.repaint();
				StyleConstants.setForeground(this.currentUserStyleHeader, Color.black);
				StyleConstants.setForeground(this.currentUserStyleText, Color.black);
				StyleConstants.setForeground(this.otherUsersStyleHeader, Color.black);
				StyleConstants.setForeground(this.otherUsersStyleText, Color.black);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.currentUserStyleHeader, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.currentUserStyleText, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.otherUsersStyleHeader, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.otherUsersStyleText, false);
				break;
			default:
				super.repaint();
				StyleConstants.setForeground(this.currentUserStyleHeader, Color.black);
				StyleConstants.setForeground(this.currentUserStyleText, Color.black);
				StyleConstants.setForeground(this.otherUsersStyleHeader, Color.black);
				StyleConstants.setForeground(this.otherUsersStyleText, Color.black);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.currentUserStyleHeader, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.currentUserStyleText, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.otherUsersStyleHeader, false);
				this.doc.setCharacterAttributes(0, this.doc.getLength() + 1, this.otherUsersStyleText, false);
				break;
		}
		
		super.setText("");
		
		synchronized(this)
		{
			this.consoleQueue.removeAll();
		
			for(ConsoleMessage message : this.cachedMessages)
				this.consoleQueue.enqueue(message);
		}
	}
	
	/**Overridden {@code paintComponent()} method. Relies on private {@code style} field to paint
	  *the component according to the desired style.
	  *@param g The {@code Graphics} object used to paint the component*/
	@Override
	public void paintComponent(Graphics g)
	{
		super.setOpaque(false);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int[] x = {this.getWidth() * 2 / 3, this.getWidth(), this.getWidth()};
		int[] y = {0, 400, 0};
		
		switch(this.style)
		{
			case ConsoleManager.STYLE_INITIALIZED:
				g2d.setPaint(new GradientPaint(0,0, new Color(150,150,150), this.getWidth(), this.getHeight(), new Color(250,250,250)));
				g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
				g2d.setPaint(new GradientPaint(0,0, new Color(255, 255, 255), x[1]-x[0], y[1], new Color(100, 100, 100)));
				g2d.fillPolygon(x, y, x.length);
				break;
			case ConsoleManager.STYLE_AUTHENTICATED:
				g2d.setPaint(new GradientPaint(0,0, new Color(38, 38, 38), this.getWidth(), this.getHeight(), new Color(75,75,75)));
				g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
				g2d.setPaint(new GradientPaint(0,0, new Color(75, 75, 75), x[1]-x[0], y[1], new Color(38, 38, 38)));
				g2d.fillPolygon(x, y, x.length);
				break;
			case ConsoleManager.STYLE_AUTHENTICATED_HACKER:
				g2d.setPaint(new GradientPaint(0,0, new Color(0, 0, 0), this.getWidth(), this.getHeight(), new Color(50,50,50)));
				g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
				g2d.setPaint(new GradientPaint(0,0, new Color(75, 75, 75), x[1]-x[0], y[1], new Color(30, 30, 30)));
				g2d.fillPolygon(x, y, x.length);
				break;
			case ConsoleManager.STYLE_AUTHENTICATED_RED:
				g2d.setPaint(new GradientPaint(0,0, new Color(200, 0, 0), this.getWidth(), this.getHeight(), new Color(100,0,0)));
				g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
				g2d.setPaint(new GradientPaint(0,0, new Color(100, 0, 0), x[1]-x[0], y[1], new Color(200, 0, 0)));
				g2d.fillPolygon(x, y, x.length);
				break;
			case ConsoleManager.STYLE_AUTHENTICATED_ORANGE:
				g2d.setPaint(new GradientPaint(0,0, new Color(255, 153, 0), this.getWidth(), this.getHeight(), new Color(175, 75, 0)));
				g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
				g2d.setPaint(new GradientPaint(0,0, new Color(175, 75, 0), x[1]-x[0], y[1], new Color(255, 153, 0)));
				g2d.fillPolygon(x, y, x.length);
				break;
		}
		
		super.paintComponent(g2d);
	}
	
	/**The {@code ImageButton} class represents a clickable image component that may be appeneded
	  *to the console JTextPane.*/
	private class ImageButton extends JButton
	{
		/**Serial Version UID is used as a version control for the class that implements
		  *the serializable interface.*/
		private static final long serialVersionUID = 6023399895605940567L;

		/**Constructs a new ImageButton object from a file reference and transfer manifest. An image button
		  *is fundamentally a transparent JButton with an icon. The icon is the image represented
		  *by the TransferBuffer object. The image is scalled such that the width does not exceed
		  *600 pixels and the height does not exceed 250 pixels.
		  *<p>
		  *The ImageButton also has a custom action listener, allowing it to open the enlarged image
		  *in a JFrame for easy viewing.*/
		public ImageButton(File file, Command transferManifest)
		{
			//Construct JButton
			super();
			
			//Set JButton Properties
			super.setOpaque(false);
			super.setContentAreaFilled(false);
			super.setFocusable(false);
			super.setBorder(BorderFactory.createRaisedBevelBorder());
			
			//Get Image and Dimensions
			ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());
			int width =  imageIcon.getIconWidth();
			int height =  imageIcon.getIconHeight();

			//Scale Image Dimensions
			while(width > 600 || height > 250)
			{
				width /= 2;
				height /= 2;
			}

			//Scale Image
			Image image =  imageIcon.getImage();
			image = image.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH);
			
			//Set JButton Icon as Image
			super.setIcon(new ImageIcon(image));
			
			//Add Custom ActionListener
			super.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent event)
				{
					JFrame dialogFrame = new JFrame(transferManifest.getSender() + " - " + file.getName());
					Container masterPane = dialogFrame.getContentPane();
					
					int width = imageIcon.getIconWidth();
					int height = imageIcon.getIconHeight();

					while(width > 1200 || height > 1000)
					{
						width *= 3;
						width /= 4;
						height *= 3;
						height /= 4;
					} 
					
					Image image = imageIcon.getImage();
					image = image.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH);
					
					JLabel imageLabel = new JLabel(new ImageIcon(image));
					imageLabel.addMouseListener(new MouseAdapter()
					{
						@Override
						public void mouseClicked(MouseEvent event)
						{
							JDialog dialog = new JDialog();
							
							//Show File Chooser
							FileDialog fd = new FileDialog(dialog, "Save Image:", FileDialog.SAVE);
							fd.setFile(file.getName());
							fd.setVisible(true);
							String filename = fd.getFile();
							String fileLocation = fd.getDirectory();
							
							if(filename != null)
							{
								try
								{
									File destination = new File(fileLocation + "//" + filename);

									//copy file from source to destination
									FileInputStream fis = new FileInputStream(file);
									FileOutputStream fos = new FileOutputStream(destination);
									
									byte[] buffer = new byte[1024];
						            int len;
						            
						            while ((len = fis.read(buffer)) > 0)
						            	fos.write(buffer, 0, len);
						            
						            fis.close();
						            fos.close();
									
									Desktop.getDesktop().open(destination);
								}
								catch (Exception e){}
							}
						}
					});
					
					masterPane.add(imageLabel);
					dialogFrame.pack();
					
					try
					{
						dialogFrame.setIconImage(ImageIO.read(ConsoleManager.class.getResource("/webchatinterface/client/resources/CLIENTICON.png")));
					}
					catch(Exception e){}
					
					dialogFrame.setResizable(true);
					dialogFrame.setVisible(true);
					dialogFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				}
			});
		}
	}
	
	/**The {@code FileButton} class represents a clickable component that may be appeneded
	  *to the console JTextPane. The {@code FileButton}, once clicked and a directory is chosen by
	  *the user, the file is copied from the temporary directory to the desired directory.*/
	private class FileButton extends JButton
	{
		/**Serial Version UID is used as a version control for the class that implements
		  *the serializable interface.*/
		private static final long serialVersionUID = 6023399895605940567L;

		/**Constructs a new FileButton object from a file reference and transfer manifest. A file button
		  *is fundamentally a JButton with a filename as text. 
		  *<p>
		  *The FileButton also has a custom action listener, allowing the file to be saved to a directory
		  *as given by the client through a FileDialog.
		  *@param file The {@code file} object that this FileButton object will wrap*/
		public FileButton(File file)
		{
			//Construct JButton
			super();
			
			//Set JButton Properties
			super.setText(file.getName());
			super.setFont(new Font("Courier New", Font.PLAIN, 12));
			super.setForeground(Color.BLUE);
			super.setOpaque(false);
			
			//Add Custom ActionListener
			super.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent event)
				{
					//Show File Chooser
					FileDialog fd = new FileDialog(new JFrame(), "Save File:", FileDialog.SAVE);
					fd.setFile(file.getName());
					fd.setVisible(true);
					String filename = fd.getFile();
					String fileLocation = fd.getDirectory();
					
					if(filename != null)
					{
						try
						{
							File destination = new File(fileLocation + "//" + filename);

							//copy file from source to destination
							FileInputStream fis = new FileInputStream(file);
							FileOutputStream fos = new FileOutputStream(destination);
							
							byte[] buffer = new byte[1024];
				            int len;
				            
				            while ((len = fis.read(buffer)) > 0)
				            	fos.write(buffer, 0, len);
				            
				            fis.close();
				            fos.close();
							
							Desktop.getDesktop().open(destination);
						}
						catch (IOException e)
						{
							AbstractClient.logException(e);
						}
					}
				}
			});
		}
	}
	
	/**The {@code ConsoleMessage} class represents a single text, image or file message that the
	  *ConsoleManager class will use to append to the console. The ConsoleMessage is able to
	  *represent three types of messages, and depending on the type of message, the object fields
	  *will have the information necessary to append to the console.*/
	private class ConsoleMessage
	{
		/**Static class member used to describe a ConsoleMessage that represents a String message.*/
		private static final int MESSAGE = 0;
		
		/**Static class member used to describe a ConsoleMessage that represents a MultimediaMessage message.*/
		private static final int IMAGE = 1;
		
		/**Static class member used to describe a ConsoleMessage that represents a TransferBuffer message.*/
		private static final int FILE = 2;
		
		/**Field representing the message body of the message*/
		private final String message;
		
		/**Field representing the username of the client who issued the message*/
		private final String sender;
		
		/**Field representing the user ID of the client who issued the message*/
		private final String senderID;
		
		/**Field representing a timestamp of when the message was issued*/
		private final String timestamp;
		
		/**Field representing the FileButton component, a clickable component that represents a 
		  *TransferBuffer object.*/
		private final FileButton file;
		
		/**Field representing the ImageButton component, a clickable component that represents a 
		  *MultimediaMessage object.*/
		private final ImageButton image;
		
		/**The type of message this ConsoleMessage object represents, as defined by the static
		  *class members MESSAGE, IMAGE, and FILE.*/
		private final int type;
		
		/**Constructs a new ConsoleMessage that represents a textual Message object.
		  *@param message The message body
		  *@param sender The username of the client who issued the message
		  *@param timestamp The timestamp of when the message was issued
		  *@param senderID The userID associated with the client who issued the message*/
		public ConsoleMessage(String message, String sender, String timestamp, String senderID)
		{
			this.message = message;
			this.sender = sender;
			this.senderID = senderID;
			this.timestamp = timestamp;
			this.file = null;
			this.image = null;
			this.type = ConsoleMessage.MESSAGE;
		}
		
		/**Constructs a new ConsoleMessage that represents a file message object.
		  *@param file The FileButton component that represents the file message
		  *@param size The size of the file. Note, the message field of this ConsoleMessage object
		  *obtains this parameter
		  *param timestamp The timestamp of when the message was issued*/
		public ConsoleMessage(FileButton file, String size, String timestamp, Command transferManifest)
		{
			this.message = size;
			this.sender = transferManifest.getSender();
			this.senderID = transferManifest.getSenderID();
			this.timestamp = timestamp;
			this.file = file;
			this.image = null;
			this.type = ConsoleMessage.FILE;
		}
		
		/**Constructs a new ConsoleMessage that represents an image message object.
		  *@param image The ImageButton component that represents the image message
		  *@param timestamp The timestamp of when the message was issued*/
		public ConsoleMessage(ImageButton image, String timestamp, Command transferManifest)
		{
			this.message = null;
			this.sender = transferManifest.getSender();
			this.senderID = transferManifest.getSenderID();
			this.timestamp = timestamp;
			this.file = null;
			this.image = image;
			this.type = ConsoleMessage.IMAGE;	
		}
		
		/**Return the message field of this ConsoleMessage object. If this instance represents a
		  *String message, this method returns the String message. If this instance represents
		  *a file message, this method returns the size of the file. If this instance represents
		  *an image message, this method returns null.
		  *@return the message field associated with the message*/
		public String getMessage()
		{
			return this.message;
		}
		
		/**Return the sender field of this ConsoleMessage object.
		  *@return the sender field associated with the message*/
		public String getSender()
		{
			return this.sender;
		}
		
		/**Return the sender ID field of this ConsoleMessage object.
		  *@return the sender ID field associated with the message*/
		public String getSenderID()
		{
			return this.senderID;
		}
		
		/**Return the message timestamp of this ConsoleMessage object.
		  *@return the timestamp associated with the message*/
		public String getTimestamp()
		{
			return this.timestamp;
		}
		
		/**Return the FileButton object if this ConsoleMessage represents
		  *a file. If this instance represents a String or image message,
		  *this method returns null.
		  *@return the FileButton object associated with the message, or null if
		  *this object does not represent a file message*/
		public FileButton getFile()
		{
			return this.file;
		}
		
		/**Return the ImageButton object if this ConsoleMessage represents
		  *an image message. If this instance represents a String or file message,
		  *this method returns null.
		  *@return the ImageButton object associated with the message, or null if
		  *this object does not represent an image message*/
		public ImageButton getImage()
		{
			return this.image;
		}
		
		/**Return the type of message that this instance of ConsoleMessage represents,
		  *as defined by the static class members.
		  *<p>
		  *Message Types:
		  *<ul>
		  *<li>MESSAGE: 0
		  *<li>IMAGE: 1
		  *<li>FILE: 2
		  *</ul>
		  *@return the sender field of this instance*/
		public int getType()
		{
			return this.type;
		}
	}
	
	/**Private class that holds the single instance of ConsoleManager. This implementation of the
	  *singleton pattern, known as the initialization-on-demands holder idiom, takes advantage of
	  *language guarentees about class initialization.*/
	private static class InstanceHolder
	{
		/**The single instance of ConsoleManager.*/
		private static final ConsoleManager INSTANCE = new ConsoleManager();
	}
	
	/**Accessor method for the instance of ConsoleManager. InstanceHolder is loaded only on the first
	  *execution of getInstance().
	  *@return the single instance of ConsoleManager.*/
	public static ConsoleManager getInstance()
	{
		return InstanceHolder.INSTANCE;
	}
}