package webchatinterface.server.communication;

import webchatinterface.AbstractIRC;
import webchatinterface.helpers.TimeHelper;
import webchatinterface.server.AbstractServer;
import webchatinterface.server.network.ChannelManager;
import webchatinterface.server.ui.WebChatServerGUI;
import webchatinterface.server.ui.components.ConsoleManager;
import webchatinterface.server.ui.dialog.BroadcastMessageDialog;
import webchatinterface.util.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;

public class BroadcastScheduler implements Runnable
{
	private ConsoleManager console;
	private ChannelManager channelManager;
	private BroadcastHelper broadcastHelper;
	private ArrayList<ScheduledServerMessage> scheduledServerMessages;
	private volatile boolean RUN;

	private BroadcastScheduler()
	{
		this.console = ConsoleManager.getInstance();
		this.broadcastHelper = BroadcastHelper.getInstance();
		this.channelManager = ChannelManager.getInstance();
		this.scheduledServerMessages = new ArrayList<ScheduledServerMessage>();
		this.RUN = false;
	}

	public void start()
	{
		if(this.RUN)
			return;

		this.scheduledServerMessages.clear();
		this.loadScheduledMessages();
		(new Thread(this)).start();
	}

	public void stop()
	{
		this.RUN = false;
		this.saveScheduledMessages();
		this.clearScheduledMessages();
	}

	public void run()
	{
		int minutes = 0;

		while(this.RUN)
		{
			for(ScheduledServerMessage message : this.scheduledServerMessages)
			{
				if(message.repeatDaily)
				{
					String time = TimeHelper.formatTimestamp(Calendar.getInstance(), "HHmm");
					int hour = Integer.parseInt(time.substring(0,2));
					int minute = Integer.parseInt(time.substring(2));

					if(message.dailyHour == hour && message.dailyMinute == minute)
					{
						this.broadcastHelper.broadcastMessage(new Message(message.message, "SERVER", "0"), this.channelManager.publicChannel);
						this.console.printConsole("SCHEDULED SERVER MESSAGE: " + (message.message.length() > 30 ? message.message.substring(0,30) : message.message), false);
					}
				}
				else
				{
					if(minutes % message.everyMinutes == 0)
					{
						this.broadcastHelper.broadcastMessage(new Message(message.message, "SERVER", "0"), this.channelManager.publicChannel);
						this.console.printConsole("SCHEDULED SERVER MESSAGE: " + (message.message.length() > 30 ? message.message.substring(0,30) : message.message), false);
					}
				}
			}

			try
			{
				Thread.sleep(1000*60);
			}
			catch(InterruptedException e)
			{
				AbstractServer.logException(e);
			}

			if(minutes++ == 60*24)
				minutes = 0;
		}
	}

	public void showEditScheduledMessagesDialog(WebChatServerGUI parent)
	{
		BroadcastMessageDialog bmd = new BroadcastMessageDialog(parent, scheduledServerMessages.toArray(new ScheduledServerMessage[0]));
		int exitCode = bmd.showDialog();

		if(exitCode == 1)
			this.broadcastHelper.broadcastMessage(new Message(bmd.getScheduledMessage().message, "SERVER", "0"), this.channelManager.publicChannel);
		else if(exitCode == 2)
			this.removeScheduledMessage(bmd.getSelectedScheduledMessage());
		else if(exitCode == 3)
			this.clearScheduledMessages();
	}

	private void loadScheduledMessages()
	{
		try(ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "SCHEDULED_MESSAGES.dat")))
		{
			Object messageIn;
			while((messageIn = objectIn.readObject()) != null)
				this.scheduledServerMessages.add((ScheduledServerMessage)messageIn);
		}
		catch(EOFException e){}
		catch (IOException | ClassNotFoundException e)
		{
			AbstractServer.logException(e);
		}
	}

	private void saveScheduledMessages()
	{
		try(ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream(AbstractIRC.SERVER_APPLCATION_DIRECTORY + "SCHEDULED_MESSAGES.dat", false)))
		{
			for(ScheduledServerMessage message : this.scheduledServerMessages)
			{
				try
				{
					objectOut.writeObject(message);
				}
				catch (IOException e)
				{
					AbstractServer.logException(e);
				}
			}
		}
		catch (IOException e)
		{
			AbstractServer.logException(e);
		}
	}

	private synchronized void scheduleMessage(ScheduledServerMessage message)
	{
		this.scheduledServerMessages.add(message);
		this.console.printConsole("Successfully scheduled server message", false);
	}

	private synchronized void removeScheduledMessage(ScheduledServerMessage message)
	{
		if(message == null)
			return;

		for(ScheduledServerMessage m : this.scheduledServerMessages)
		{
			if(m.equals(message))
			{
				this.scheduledServerMessages.remove(m);
				this.console.printConsole("Successfully deleted scheduled server message", false);
				return;
			}
		}

		this.console.printConsole("Scheduled server message not found", true);
	}

	private synchronized void clearScheduledMessages()
	{
		this.scheduledServerMessages.clear();
		this.console.printConsole("Successfully cleared scheduled server messages", false);
	}

	public static BroadcastScheduler getInstance()
	{
		return InstanceHolder.INSTANCE;
	}

	private static class InstanceHolder
	{
		private static final BroadcastScheduler INSTANCE = new BroadcastScheduler();
	}
}
