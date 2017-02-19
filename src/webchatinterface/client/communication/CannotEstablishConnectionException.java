package webchatinterface.client.communication;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The class {@code CannotEstablishConnectionException} is a throwable exception that describes a situation in 
  *which an a connection between a client and server cannot be established for some reason. Typically, 
  *a CannotEstablishConnectionException is thrown if the streams are corrupted and data cannot be exchanged
  *in a regular manner.*/

public class CannotEstablishConnectionException extends Exception
{
	/**Serial Version UID is used as a version control for the class that implements
	 *the serializable interface.*/
	private static final long serialVersionUID = -4586609475355563661L;

	/**Constructs a new ConnectionDeniedException with the specified detail message.
	  *@param message a String containing a message that describes the cause of the exception*/
	public CannotEstablishConnectionException(String message)
	{
		super(message);
	}
	
	/**Constructs a new ConnectionDeniedException.*/
	public CannotEstablishConnectionException()
	{
		super();
	}
}
