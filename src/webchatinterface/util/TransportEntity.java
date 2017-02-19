package webchatinterface.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
 *<p>
 *The TransportEntity class defines an object that has the necessary fields to
 *be used as a way of communication between the client and server applications.
 *Each object communicated through the client and server must extend this class.
 *Objects that do not extend this class will be ignored, and could result in
 *the connection being aborted.
 *<p>
 *Each TransportEntity must have a sender identifier. This identifier can be any string
 *that identifies the issuer of the object to other clients connected to the server. A simple
 *username will suffice.
 *<p>
 *Each TransportEntity must have a sender identification key. This key must be unique
 *among all clients connected to the server, with the exception of the client and server
 *who have a sender ID of 0. Typically, the sender ID is a 256-bit alphanumeric key.
 *<p>
 *Each TransportEntity must have a timestamp identifying when the object was created.
 *This is handled by the TransportEntity constructor.
 *<p>
 *Finally, each TransportEntity must have an identification number. This number defines
 *how many TransportEntity objects have been created by the client or server during its
 *lifetime. This is handled by the TransportEntity constructor
 */

public abstract class TransportEntity implements Serializable
{
	/**Serial Version UID is used as a version control for the class that implements 
	 *the serializable interface.*/
	private static final long serialVersionUID = 1L;

	/**Local class variable containing the number of instantiated TransportEntity 
	  *objects. Used by {@code TransportEntity()} constructor to assign unique 
	  *message identification number to the object.*/
	private static int messageIdentification = 0;
	
	/**A timestamp that represents the time at which the TransportEntity object
	  *was created.*/
	private final String TIMESTAMP;
	
	/**A string containing the name of the client who created the TransportEntity object*/
	private final String SENDER;
	
	/**A string containing the randomly generated user identification key*/
	private final String SENDER_ID;
	
	/**Unique local message identification number*/
	private final int ID;
	
	/**Constructor for a new instance of the TransportEntity class.
	  *@param sender the sender identifier or username
	  *@param senderID the unique sender identification key*/
	public TransportEntity(String sender, String senderID)
	{
		this.SENDER = sender;
		this.SENDER_ID = senderID;
		this.TIMESTAMP = this.getSystemTimestamp();
		this.ID = messageIdentification++;
	}
	
	/**Accessor method for the {@code TIMESTAMP} field of this TransportEntity object
	  *@return the timestamp of this TransportEntity object, i.e. the time and date at which
	  *the object was created*/
	public String getTimeStamp()
	{
		return this.TIMESTAMP;
	}
	
	/**Accessor method for the {@code SENDER} field of this TransportEntity object
	  *@return the username of the sender of this TransportEntity object*/
	public String getSender()
	{
		return this.SENDER;
	}
	
	/**Accessor method for the {@code SENDER_ID} field of this TransportEntity object
      *@return the unique identification key for the user that instantiated this object*/
	public String getSenderID()
	{
		return this.SENDER_ID;
	}
	
	/**Accessor method for the {@code ID} field of this TransportEntity object
	  *@return the unique local message identification number of this TransportEntity object*/
	public int getMessageID()
	{
		return this.ID;
	}
	
	/**Build and return a string containing the local system time expressed according
	  *to ISO 8601 with UTC timezone offset.
	  *<p>
	  *Format:
	  *<ul>
	  *<li>{@code YYYY-MM-DDThh:mm:ss+00:00}</li>
	  *</ul>
	  *@return the current system time expressed according to ISO 8601 with UTC timezone offset*/
	private String getSystemTimestamp()
	{
		Calendar cal = Calendar.getInstance();
		TimeZone tz = TimeZone.getDefault();
		Date date = cal.getTime();
		int UTC_Offset = tz.getOffset(cal.getTimeInMillis());
				
		String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date);
		
		if(UTC_Offset / 1000 / 60 / 60 >= 0)
		{
			timestamp += "+";
		}
		else
		{
			timestamp += "-";
		}
		
		if(Math.abs(UTC_Offset / 1000 / 60 / 60) < 10)
		{
			timestamp += "0";
		}

		timestamp += Math.abs(UTC_Offset / 1000 / 60 / 60) + ":";
		timestamp += UTC_Offset % (1000 * 60 * 60) + "0";
		
		return timestamp;
	}
	
	/**Build and return a textual representation of this transport entity object.
	  *<p>
	  *Format:
	  *<ul>
	  *<li>{@code [ID] YYYY-MM-DDThh:mm:ss+00:00 SENDER>}</li>
	  *</ul>
	  *@return a textual representation of this transport entity object.*/
	@Override
	public String toString()
	{
		return "[" + this.ID + "] " + this.TIMESTAMP + " " + this.SENDER + "> ";
	}
}
