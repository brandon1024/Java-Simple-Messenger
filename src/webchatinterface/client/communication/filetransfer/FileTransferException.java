package webchatinterface.client.communication.filetransfer;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
 *<p>
 *The class {@code FileTransferException} is a throwable exception that describes any error involving
 *a file transfer.*/

public class FileTransferException extends RuntimeException
{
	public FileTransferException(String message)
	{
		super(message);
	}

	public FileTransferException()
	{
		super();
	}
}
