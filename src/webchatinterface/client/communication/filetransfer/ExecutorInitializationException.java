package webchatinterface.client.communication.filetransfer;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
 *<p>
 *The class {@code ExecutorInitializationException} is a throwable exception that describes a situation involving
 *an improperly initialized file transfer executor.*/

public class ExecutorInitializationException extends FileTransferException
{
	public ExecutorInitializationException(String message)
	{
		super(message);
	}

	public ExecutorInitializationException()
	{
		super();
	}
}
