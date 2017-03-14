package webchatinterface.client.communication.filetransfer;

import util.KeyGenerator;
import webchatinterface.client.AbstractClient;
import webchatinterface.client.communication.WebChatClient;
import webchatinterface.client.ui.dialog.TransferProgressDialog;
import webchatinterface.util.ClientUser;
import webchatinterface.util.Command;
import webchatinterface.util.TransferBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class OutboundFileTransferExecutor implements Runnable
{
	private WebChatClient client;
	private ClientUser clientUser;
	private File file;
	private String transferID;
	private volatile boolean transferRunning;

	public OutboundFileTransferExecutor(WebChatClient client) throws ExecutorInitializationException
	{
		this.client = client;
		this.clientUser = AbstractClient.getClientUser();
		this.file = null;
		this.transferID = null;
		this.transferRunning = false;
	}

	public void start(File file) throws FileTransferException
	{
		if(this.transferRunning)
			throw new ConcurrentFileTransferException("Concurrent file transfers are not supported on a single instance of FileTransferExecutor");

		this.file = file;
		this.transferID = KeyGenerator.generateKey64(KeyGenerator.ALPHANUMERIC_MIXED_CASE);
		this.transferRunning = true;
		(new Thread(this)).start();
	}

	public void run() throws FileTransferException
	{
		TransferProgressDialog dialog = new TransferProgressDialog();

		try(FileInputStream fis = new FileInputStream(this.file))
		{
			//Define Variables
			final long bufferSize = 4096L;
			final long bytesTotal = this.file.length();
			long bytesRead = 0L;
			long bytesRemaining = bytesTotal;
			long numberOfPackets = bytesTotal / bufferSize + (bytesTotal % bufferSize > 0 ? 1 : 0);

			//Update Dialog
			dialog.updateTransferDialog(0, 0, bytesTotal, 0, this.file.getName());

			//Initiate File Transfer with Transfer Manifest Command
			Object[] transferData = {numberOfPackets, bytesTotal, bufferSize, this.transferID, this.file.getName()};
			Command transferManifest = new Command(Command.FILE_TRANSFER, transferData, this.clientUser.getUsername(), this.clientUser.getUserID());
			this.client.send(transferManifest);

			//Send Buffers
			while(bytesRemaining > 0)
			{
				long time = System.currentTimeMillis();
				byte[] array = (bytesRemaining < bufferSize) ? new byte[(int)bytesRemaining] : new byte[(int)bufferSize];

				fis.read(array);
				bytesRead += array.length;
				bytesRemaining -= array.length;

				//Create TransferBuffer
				TransferBuffer message = new TransferBuffer(array, this.transferID, this.clientUser.getUsername(), this.clientUser.getUserID());

				//Send Message and Close Streams
				this.client.send(message);

				long timeElapsedMillis = (System.currentTimeMillis() - time) / 1000;
				dialog.updateTransferDialog(bytesRead, array.length, bytesTotal, timeElapsedMillis, this.file.getName());
			}

			//Update Dialog
			dialog.updateTransferDialogComplete();
		}
		catch(IOException e)
		{
			AbstractClient.logException(e);
			dialog.updateTransferDialogError();
		}

		this.transferRunning = false;
	}

	public String getTransferID()
	{
		return this.transferID;
	}

	public boolean isRunning()
	{
		return this.transferRunning;
	}
}
