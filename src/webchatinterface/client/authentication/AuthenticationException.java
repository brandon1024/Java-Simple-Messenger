package webchatinterface.client.authentication;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The class {@code AuthenticationException} is a throwable exception that describes a situation in 
  *which an error occured during the authentication process.*/
  
public class AuthenticationException extends Exception
{
	public AuthenticationException(String message)
	{
		super("Authentication Failed: " + message);
	}
	
	public AuthenticationException()
	{
		super();
	}
}