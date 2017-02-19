package webchatinterface.server.util;

import java.io.Serializable;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The {@code ScheduledServerMessage} class represents a scheduled server message.
  *A message may be scheduled to broadcast during fixed minute intervals, or
  *at a specific time daily.*/

public class ScheduledServerMessage implements Serializable
{
	/**Serial Version UID is used as a version control for the class that implements
	 *the serializable interface.*/
	private static final long serialVersionUID = -5624158322875482839L;

	/**The Message object to be broadcasted*/
	public final String message;
	
	/**Field representing the time interval between each broadcast of this message*/
	public final int everyMinutes;
	
	/**Field representing the daily hour of broadcast*/
	public final int dailyHour;
	
	/**Field representing the daily minute of broadcast*/
	public final int dailyMinute;
	
	/**Defines whether the message is broadcasted daily or on specific time intervals*/
	public final boolean repeatDaily;
	
	/**Constructs a new ScheduledMessage given a Message object and a broadcast time interval
	  *@param message The Message object to be broadcasted
	  *@param everyMinutes The fixed time interval between broadcasts*/
	public ScheduledServerMessage(String message, int everyMinutes)
	{
		this.message = message;
		this.everyMinutes = everyMinutes;
		this.dailyHour = 0;
		this.dailyMinute = 0;
		this.repeatDaily = false;
	}
	
	/**Constructs a new ScheduledMessage given a Message object, and daily hour and minute
	  *of broadcast
	  *@param message The Message object to be broadcasted
	  *@param dailyHour The hour at which the broadcast will occur
	  *@param dailyMinute The minute at which the broadcast will occur*/
	public ScheduledServerMessage(String message, int dailyHour, int dailyMinute)
	{
		this.message = message;
		this.dailyHour = dailyHour;
		this.dailyMinute = dailyMinute;
		this.everyMinutes = 0;
		this.repeatDaily = true;
	}
	
	/**Overridden toString() from java.lang.Object.
	  *@return a String representation of this object*/
	@Override
	public String toString()
	{
		if(this.message.length() > 50)
		{
			if(repeatDaily)
			{
				String hour = this.dailyHour < 10 ? "0" + this.dailyHour : String.valueOf(this.dailyHour);
				String minute = this.dailyMinute < 10 ? "0" + this.dailyMinute : String.valueOf(this.dailyMinute);
				return "Repeat [Daily " + hour + ":" + minute + "] " + this.message.substring(0, 50) + "...";
			}
			else
			{
				return "Repeat [Every " + this.everyMinutes + " minutes" + "] " + this.message.substring(0, 50) + "...";
			}
		}
		else
		{
			if(repeatDaily)
			{
				String hour = this.dailyHour < 10 ? "0" + this.dailyHour : String.valueOf(this.dailyHour);
				String minute = this.dailyMinute < 10 ? "0" + this.dailyMinute : String.valueOf(this.dailyMinute);
				return "Repeat [Daily " + hour + ":" + minute + "] " + this.message;
			}
			else
			{
				return "Repeat [Every " + this.everyMinutes + " minutes" + "] " + this.message;
			}
		}
	}
}