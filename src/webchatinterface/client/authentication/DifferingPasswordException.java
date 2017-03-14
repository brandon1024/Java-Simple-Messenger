package webchatinterface.client.authentication;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
 *<p>
 *The class {@code DifferingPasswordException} is a throwable exception that describes a
 *situation in which the entered password and entered confirmation password do not match.
 */

public class DifferingPasswordException extends AuthenticationException
{
	public DifferingPasswordException(String message)
	{
		super(message);
	}

	public DifferingPasswordException()
	{
		super();
	}
}
