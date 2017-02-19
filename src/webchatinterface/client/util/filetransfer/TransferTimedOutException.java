package webchatinterface.client.util.filetransfer;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The class {@code TransferTimedOutException} is a throwable exception that describes a situation in 
  *which an a transfer timed out due to inactivity.*/

public class TransferTimedOutException extends Exception
{
	/**Serial Version UID is used as a version control for the class that implements
	  *the serializable interface.*/
	private static final long serialVersionUID = 4366614249263282332L;

	/**Constructs a new TransferTimedOutException with the specified detail message.
	  *@param message a String containing a message that describes the cause of the exception*/
	public TransferTimedOutException(String message)
	{
		super("Authentication Failed: " + message);
	}
	
	/**Constructs a new TransferTimedOutException.*/
	public TransferTimedOutException()
	{
		super();
	}
}
