package webchatinterface.util;

import java.io.Serializable;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
 *<p>
 *Serializable Message object that is transmitted by TCP between client 
 *applications through a dedicated server interface.
 *<p>
 *A Message object contains a String object representing the message, a 
 *timestamp, a sender, and a local message identification number.
 */

public class Message extends TransportEntity implements Serializable
{
	/**Serial Version UID is used as a version control for the class that implements 
	 *the serializable interface.*/
	private static final long serialVersionUID = -6703287725930678616L;
	
	/**A string containing a message represented by this Message object.*/
	private final String MESSAGE;
	
	/**Builds a new Message object with a given message and client name.
	  *Assigns a unique local message identification number to {@code ID}
	  *and assigns the timestamp to {@code TIMESTAMP}.
	  *
	  *@param message the message represented by this Message object
	  *@param sender the name of the client who created the message
	  *@param senderID the unique 256-bit alphanumeric key associated with each client*/
	public Message(String message, String sender, String senderID)
	{
		super(sender, senderID);
		this.MESSAGE = message;
	}
	
	/**Builds and returns a new string containing the full message, including
	  *the message identification number, timestamp, sender, and message.
	  *<p>
	  *Format:
	  *<ul>
	  *<li>{@code [ID] YYYYMMDD_HHMMSS Sender > Message...}
	  </ul>
	  *@return the full message, with message identification number, timestamp,
	  *sender and message*/
	public String getFullMessage()
	{
		return super.toString() + this.MESSAGE;
	}
	
	/**Accessor method for the {@code MESSAGE} field of this Message object
	  *@return the message that this Message object represents*/
	public String getMessage()
	{
		return this.MESSAGE;
	}
}