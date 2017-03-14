package webchatinterface.client.communication.filetransfer;

import util.DynamicQueue;
import webchatinterface.AbstractIRC;
import webchatinterface.client.AbstractClient;
import webchatinterface.client.ui.WebChatClientGUI;
import webchatinterface.util.Command;
import webchatinterface.util.TransferBuffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class InboundFileTransferExecutor implements Runnable
{
	private WebChatClientGUI userInterface;
	private DynamicQueue<TransferBuffer> queuedBuffers;
	private Command transferManifest;
	private String transferID;
	private volatile boolean transferRunning;

	public InboundFileTransferExecutor(WebChatClientGUI userInterface) throws ExecutorInitializationException
	{
		this.userInterface = userInterface;
		this.queuedBuffers = null;
		this.transferManifest = null;
		this.transferID = null;
		this.transferRunning = false;
	}

	public void start(Command transferManifest) throws FileTransferException
	{
		if(this.transferRunning)
			throw new ConcurrentFileTransferException("Concurrent file transfers are not supported on a single instance of InboundFileTransferExecutor");

		this.queuedBuffers = new DynamicQueue<TransferBuffer>();
		this.transferManifest = transferManifest;
		this.transferID = (String)((Object[]) this.transferManifest.getMessage())[3];
		this.transferRunning = true;
		(new Thread(this)).start();
	}

	public void run() throws FileTransferException
	{
		Object[] transferData = (Object[]) this.transferManifest.getMessage();
		long bytesTotal = (Long) transferData[1];
		long bytesRemaining = bytesTotal;
		String fileName = (String) transferData[4];
		File file = new File(AbstractIRC.CLIENT_APPLCATION_DIRECTORY + fileName);

		try(FileOutputStream fos = new FileOutputStream(file))
		{
			int timeoutCounter = 0;

			while(bytesRemaining > 0)
			{
				if(this.queuedBuffers.size() != 0)
				{
					TransferBuffer buffer = this.queuedBuffers.dequeue();
					byte[] byteArray = buffer.getByteArray();
					bytesRemaining -= byteArray.length;
					fos.write(byteArray);
					timeoutCounter = 0;
				}
				else
				{
					try
					{
						timeoutCounter++;
						Thread.sleep(100);
					}
					catch(InterruptedException e)
					{
						AbstractClient.logException(e);
					}
				}

				if(timeoutCounter == 120)
					throw new TransferTimedOutException("File Transfer Timed Out (120 seconds); bytes remaining " + bytesRemaining + "B of total " + bytesTotal + "B");
			}

			this.userInterface.displayFile(file, this.transferManifest);
		}
		catch(TransferTimedOutException | IOException e)
		{
			AbstractClient.logException(e);
			file.delete();
		}

		this.transferRunning = false;
	}

	public synchronized void bufferReceived(TransferBuffer buffer)
	{
		this.queuedBuffers.enqueue(buffer);
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
