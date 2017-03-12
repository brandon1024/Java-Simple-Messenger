package webchatinterface.server.communication;

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
	private static final long serialVersionUID = -5624158322875482839L;
	public final String message;
	public final int everyMinutes;
	public final int dailyHour;
	public final int dailyMinute;
	public final boolean repeatDaily;
	
	public ScheduledServerMessage(String message, int everyMinutes)
	{
		this.message = message;
		this.everyMinutes = everyMinutes;
		this.dailyHour = 0;
		this.dailyMinute = 0;
		this.repeatDaily = false;
	}
	
	public ScheduledServerMessage(String message, int dailyHour, int dailyMinute)
	{
		this.message = message;
		this.dailyHour = dailyHour;
		this.dailyMinute = dailyMinute;
		this.everyMinutes = 0;
		this.repeatDaily = true;
	}
	
	public String toString()
	{
		if(this.repeatDaily)
		{
			String hour = this.dailyHour < 10 ? "0" + this.dailyHour : String.valueOf(this.dailyHour);
			String minute = this.dailyMinute < 10 ? "0" + this.dailyMinute : String.valueOf(this.dailyMinute);
			return "Repeat [Daily " + hour + ":" + minute + "] " + (this.message.length() > 50 ? this.message.substring(0, 50) + "..." : this.message);
		}
		else
			return "Repeat [Every " + this.everyMinutes + " minutes" + "] " + (this.message.length() > 50 ? this.message.substring(0, 50) + "..." : this.message);
	}
}