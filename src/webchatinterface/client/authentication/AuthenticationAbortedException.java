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
	public AuthenticationAbortedException(String message)
	{
		super(message);
	}

	public AuthenticationAbortedException()
	{
		super();
	}
}
