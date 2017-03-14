package webchatinterface.client.communication;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The class {@code ConnectionDeniedException} is a throwable exception that describes a situation in 
  *which an a connection was denied by the host.*/

public class ConnectionDeniedException extends CannotEstablishConnectionException
{
	private static final long serialVersionUID = 7425679860650348725L;

	public ConnectionDeniedException(String message)
	{
		super(message);
	}
	
	public ConnectionDeniedException()
	{
		super();
	}
}
