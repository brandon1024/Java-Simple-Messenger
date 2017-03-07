package webchatinterface.client.authentication;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
  *<p>
  *The class {@code AuthenticationAbortedException} is a throwable exception that describes a
  *situation in which the authentication processes is aborted.
  */

public class AuthenticationAbortedException extends AuthenticationException
{
	/**Serial Version UID is used as a version control for the class that implements
	 *the serializable interface.*/
	private static final long serialVersionUID = -5292957812858347281L;

	/**Constructs a new AuthenticationAbortedException with the specified detail message.
	  *@param message a String containing a message that describes the cause of the exception
	  */
	public AuthenticationAbortedException(String message)
	{
		super(message);
	}
	
	/**Constructs a new AuthenticationAbortedException.
	  */
	public AuthenticationAbortedException()
	{
		super();
	}
}
