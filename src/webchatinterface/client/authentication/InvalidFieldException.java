package webchatinterface.client.authentication;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
 *<p>
 *The class {@code InvalidFieldException} is a throwable exception that describes a
 *situation in which a field entered during the authentication process or account
 *creation process was invalid.
 */

public class InvalidFieldException extends AuthenticationException
{
	public InvalidFieldException(String message)
	{
		super(message);
	}

	public InvalidFieldException()
	{
		super();
	}
}
