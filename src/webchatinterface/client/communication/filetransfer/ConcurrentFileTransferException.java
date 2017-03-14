package webchatinterface.client.communication.filetransfer;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
 *<p>
 *The class {@code ConcurrentFileTransferException} is a throwable exception that describes a situation in
 *which an attempted file transfer is executed on a FileTransferExecutor that is currently running.*/

public class ConcurrentFileTransferException extends FileTransferException
{
	public ConcurrentFileTransferException(String message)
	{
		super(message);
	}

	public ConcurrentFileTransferException()
	{
		super();
	}
}
