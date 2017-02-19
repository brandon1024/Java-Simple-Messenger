package webchatinterface.client.util.authentication;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The class {@code AuthenticationException} is a throwable exception that describes a situation in 
  *which an error occured during the authentication process.*/
  
public class AuthenticationException extends Exception
{
	/**Serial Version UID is used as a version control for the class that implements
	 *the serializable interface.*/
	private static final long serialVersionUID = 7098273236121859270L;

	/**Constructs a new AuthenticationException with the specified detail message.
	  *@param message a String containing a message that describes the cause of the exception*/
	public AuthenticationException(String message)
	{
		super("Authentication Failed: " + message);
	}
	
	/**Constructs a new AuthenticationException.*/
	public AuthenticationException()
	{
		super();
	}
}