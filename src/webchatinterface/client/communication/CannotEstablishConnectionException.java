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
	public CannotEstablishConnectionException(String message)
	{
		super(message);
	}
	
	public CannotEstablishConnectionException()
	{
		super();
	}
}
