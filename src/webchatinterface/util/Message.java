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
	private static final long serialVersionUID = -6703287725930678616L;
	
	private final String MESSAGE;
	
	public Message(String message, String sender, String senderID)
	{
		super(sender, senderID);
		this.MESSAGE = message;
	}
	
	public String getFullMessage()
	{
		return super.toString() + this.MESSAGE;
	}
	
	public String getMessage()
	{
		return this.MESSAGE;
	}
}