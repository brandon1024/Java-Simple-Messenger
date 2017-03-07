package webchatinterface.server.ui.components;

import util.DynamicQueue;
import webchatinterface.AbstractIRC;
import webchatinterface.helpers.TimeHelper;
import webchatinterface.server.AbstractServer;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The {@code ConsoleManager} class manages the display of messages to the console and exception 
  *logging. The purpose of the Console Manager is to prevent messages from displaying incorrectly 
  *in high volume reception situations. It also allows for the user or developer to view a log of 
  *exceptions thrown throughout the execution of the server application, which can aid in 
  *application troubleshooting.
  *<p>
  *Messages and warnings are distinguished by color in the console. The Console Manager class
  *displays warning messages in red.
  *<p>
  *The console manager extends JTextPane, so it may be used as a components in the GUI. However, the
  *Console Manager uses a print queue to correctly print messages to the console. Therefore, using 
  *methods defined  in JTextPane are strongly discouraged.
  */

public class ConsoleManager extends JTextPane implements Runnable, AbstractIRC
{
	private DynamicQueue<ConsoleMessage> printQueue;
	private ArrayList<ConsoleMessage> printCache;
	private StyledDocument doc;
	private MutableAttributeSet messageAttribute;
	private MutableAttributeSet warningAttribute;
	
	public ConsoleManager()
	{
		//Build JTextPane
		super();
		super.setEditable(false);
		
		//Provide reference to JTextPane StyledDocument
		this.doc = this.getStyledDocument();
		
		this.setBackground(AbstractServer.backgroundColor);
		
		//Style Attribute Set for Generic Messages
		this.messageAttribute = new SimpleAttributeSet();
		StyleConstants.setForeground(this.messageAttribute, AbstractServer.foregroundColor);
		StyleConstants.setFontSize(this.messageAttribute, AbstractServer.textFont.getSize());
		StyleConstants.setFontFamily(this.messageAttribute, AbstractServer.textFont.getFontName());
		
		this.warningAttribute = new SimpleAttributeSet();
		StyleConstants.setForeground(this.warningAttribute, Color.red);
		StyleConstants.setFontSize(this.warningAttribute, AbstractServer.textFont.getSize());
		StyleConstants.setFontFamily(this.warningAttribute, AbstractServer.textFont.getFontName());
		
		//Build Console Print Queue
		this.printQueue = new DynamicQueue<ConsoleMessage>();
		this.printCache = new ArrayList<ConsoleMessage>();
	}
	
	public void run()
	{
		while(true)
		{
			while(!this.printQueue.isEmpty())
			{
				ConsoleMessage message = this.printQueue.dequeue();
				AbstractServer.logString(message.getMessage() + "\n");
				
				try
				{
					if(message.isWarning())
					{
						this.doc.setParagraphAttributes(doc.getLength(), 1, this.warningAttribute, false);
						this.doc.insertString(this.doc.getLength(), message.getMessage() + "\n", this.warningAttribute);
					}
					else
					{
						this.doc.setParagraphAttributes(doc.getLength(), 1, this.messageAttribute, false);
						this.doc.insertString(this.doc.getLength(), message.getMessage() + "\n", this.messageAttribute);
					}
				}
				catch(BadLocationException e)
				{
					AbstractServer.logException(e);
				}
			}
			
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				AbstractServer.logException(e);
			}
		}
	}
	
	public void printConsole(String message, boolean warning)
	{
		synchronized(this)
		{
			ConsoleMessage consoleMessage = new ConsoleMessage(TimeHelper.formatTimestamp(Calendar.getInstance(), "yyyyMMdd_HHmmss") + ": " + message, warning);
			this.printQueue.enqueue(consoleMessage);
			this.printCache.add(consoleMessage);
		}
	}
	
	public void printConsole()
	{
		synchronized(this)
		{
			ConsoleMessage consoleMessage = new ConsoleMessage("\n", false);
			this.printQueue.enqueue(consoleMessage);
			this.printCache.add(consoleMessage);
		}
	}
	
	private void clearConsole(boolean clearCache)
	{
		synchronized(this)
		{
			this.printQueue.removeAll();
			if(clearCache)
				this.printCache.clear();
		}
		
		super.setText("");
	}
	
	public void validateSettings()
	{
		this.setBackground(AbstractServer.backgroundColor);
		
		this.messageAttribute = new SimpleAttributeSet();
		StyleConstants.setForeground(this.messageAttribute, AbstractServer.foregroundColor);
		StyleConstants.setFontSize(this.messageAttribute, AbstractServer.textFont.getSize());
		StyleConstants.setFontFamily(this.messageAttribute, AbstractServer.textFont.getFontName());
		
		this.warningAttribute = new SimpleAttributeSet();
		StyleConstants.setForeground(this.warningAttribute, Color.red);
		StyleConstants.setFontSize(this.warningAttribute, AbstractServer.textFont.getSize());
		StyleConstants.setFontFamily(this.warningAttribute, AbstractServer.textFont.getFontName());
		
		this.clearConsole(false);
		for(ConsoleMessage message : this.printCache)
			this.printQueue.enqueue(message);
	}
	
	private class ConsoleMessage
	{
		private String message;
		private boolean warning;

		public ConsoleMessage(String message, boolean warning)
		{
			this.message = message;
			this.warning = warning;
		}

		public String getMessage()
		{
			return message;
		}

		boolean isWarning()
		{
			return warning;
		}
	}
}