package webchatinterface.util;

import webchatinterface.helpers.TimeHelper;

import java.io.Serializable;
import java.util.Calendar;

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
	private static final long serialVersionUID = 1L;

	private static int messageIdentification = 0;
	private final String TIMESTAMP;
	private final String SENDER;
	private final String SENDER_ID;
	private final int ID;
	
	protected TransportEntity(String sender, String senderID)
	{
		this.SENDER = sender;
		this.SENDER_ID = senderID;
		this.TIMESTAMP = TimeHelper.formatTimestampUTC(Calendar.getInstance());
		this.ID = messageIdentification++;
	}
	
	public String getTimeStamp()
	{
		return this.TIMESTAMP;
	}
	
	public String getSender()
	{
		return this.SENDER;
	}
	
	public String getSenderID()
	{
		return this.SENDER_ID;
	}
	
	public int getMessageID()
	{
		return this.ID;
	}
	
	public String toString()
	{
		return "[" + this.ID + "] " + this.TIMESTAMP + " " + this.SENDER + "> ";
	}
}
