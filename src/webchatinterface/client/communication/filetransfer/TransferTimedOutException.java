package webchatinterface.client.communication.filetransfer;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The class {@code TransferTimedOutException} is a throwable exception that describes a situation in 
  *which an a transfer timed out due to inactivity.*/

public class TransferTimedOutException extends Exception
{
	public TransferTimedOutException(String message)
	{
		super("Authentication Failed: " + message);
	}
	
	public TransferTimedOutException()
	{
		super();
	}
}
