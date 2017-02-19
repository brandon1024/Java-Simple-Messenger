package webchatinterface.client.communication;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The class {@code ConnectionDeniedException} is a throwable exception that describes a situation in 
  *which an a connection was denied by the host.*/

public class ConnectionDeniedException extends CannotEstablishConnectionException
{
	/**Serial Version UID is used as a version control for the class that implements
	 *the serializable interface.*/
	private static final long serialVersionUID = 7425679860650348725L;

	/**Constructs a new ConnectionDeniedException with the specified detail message.
	  *@param message a String containing a message that describes the cause of the exception*/
	public ConnectionDeniedException(String message)
	{
		super(message);
	}
	
	/**Constructs a new ConnectionDeniedException.*/
	public ConnectionDeniedException()
	{
		super();
	}
}
