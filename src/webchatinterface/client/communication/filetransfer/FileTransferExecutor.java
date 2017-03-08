package webchatinterface.client.communication.filetransfer;

import util.DynamicQueue;
import util.KeyGenerator;
import webchatinterface.AbstractIRC;
import webchatinterface.client.AbstractClient;
import webchatinterface.client.communication.WebChatClient;
import webchatinterface.client.ui.WebChatClientGUI;
import webchatinterface.client.ui.dialog.TransferProgressDialog;
import webchatinterface.util.ClientUser;
import webchatinterface.util.Command;
import webchatinterface.util.TransferBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
  *<p>
  *The {@code FileTransferExecutor} class is used to handle orderly file transfer between
  *the client and the server applications.
  *<p>
  *Files are read sequentially and split into small byte buffers. Each buffer is sent
  *to the server wrapped in a {@code TransferBuffer} object.
  *<p>
  *Each transfer is marked with a unique transfer identification key, used to distinguish 
  *concurrent transfers and prevent buffer scrambling. This key, along with various information regarding
  *the transfer, is sent to the server and clients in a file transfer manifest command. This notifies
  *the server of a transfer, and allows clients to initiate a FileTransferExecutor in MODE_RECEIVE.
  *<p>
  *The FileTransferExecutor class has two standard modes. When {@code start(File file)} is invoked, 
  *the FileTransferExecutor thread establishes the transfer and handles splitting the file into buffers to
  *send to the server. If {@code start(Command transferManifest)} is invoked, the FileTransferExecutor thread
  *handles receiving the buffers, and reassembling the file to be displayed in the client UI.
  */

public class FileTransferExecutor implements Runnable
{
	private TransferProgressDialog dialog;
	private WebChatClientGUI userInterface;
	private WebChatClient client;
	private ClientUser clientUser;
	private File file;
	private String transferID;
	private DynamicQueue<TransferBuffer> queuedBuffers = new DynamicQueue<TransferBuffer>();
	private volatile boolean transferRunning;
	private int mode;
	private Command transferManifest;
	
	public FileTransferExecutor(WebChatClientGUI userInterface, WebChatClient client) throws ExecutorInitializationException
	{
		if(userInterface == null)
			throw new ExecutorInitializationException("parameter must not be null");

		if(client == null)
			throw new ExecutorInitializationException("parameter must not be null");

		this.userInterface = userInterface;
		this.dialog = null;
		this.client = client;
		this.clientUser = AbstractClient.getClientUser();
		this.mode = -1;
		this.file = null;
		this.transferRunning = false;
		this.transferID = null;
		this.transferManifest = null;
	}
	
	public void start(File file) throws FileTransferException
	{
		this.start(file, null);
	}
	
	public void start(Command transferManifest) throws FileTransferException
	{
		this.start(null, transferManifest);
	}

	private void start(File file, Command transferManifest) throws FileTransferException
	{
		if(this.transferRunning)
			throw new ConcurrentFileTransferException("Concurrent file transfers are not supported on a single instance of FileTransferExecutor");

		this.transferRunning = true;
		this.file = file;
		this.transferManifest = transferManifest;
		this.mode = file != null ? TransferUtilities.MODE_SEND : TransferUtilities.MODE_RECEIVE;
		this.transferID = file != null ? KeyGenerator.generateKey64(KeyGenerator.ALPHANUMERIC_MIXED_CASE) : (String)((Object[]) this.transferManifest.getMessage())[3];
		this.dialog = file != null ? new TransferProgressDialog() : null;

		(new Thread(this)).start();
	}
	
	public void run() throws FileTransferException
	{
		if(this.mode == TransferUtilities.MODE_SEND)
			this.send();
		else if(this.mode == TransferUtilities.MODE_RECEIVE)
			this.receive();
		else
			throw new ExecutorInitializationException("FileTransferExecutor thread was not initialized properly; use start(file, mode) to start thread");
	}
	
	private void send()
	{
		try(FileInputStream fis = new FileInputStream(file))
		{
			//Define Variables
			final long bufferSize = 4096L;
			final long bytesTotal = this.file.length();
			long bytesRead = 0L;
			long bytesRemaining = bytesTotal;
			long numberOfPackets = bytesTotal / bufferSize + (bytesTotal % bufferSize > 0 ? 1 : 0);
			
			//Update Dialog
			this.dialog.updateTransferDialog(0, 0, bytesTotal, 0, this.file.getName());
			
			//Initiate File Transfer with Transfer Manifest Command
			Object[] transferData = {numberOfPackets, bytesTotal, bufferSize, this.transferID, this.file.getName()};
			this.transferManifest = new Command(Command.FILE_TRANSFER, transferData, this.clientUser.getUsername(), this.clientUser.getUserID());
			this.client.send(this.transferManifest);
			
			//Send Buffers
			while(bytesRemaining > 0)
			{
				long time = System.currentTimeMillis();
				byte[] array = (bytesRemaining < bufferSize) ? new byte[(int)bytesRemaining] : new byte[(int)bufferSize];
				
				fis.read(array);
				bytesRead += array.length;
				bytesRemaining -= array.length;
				
				//Create TransferBuffer
				TransferBuffer message = new TransferBuffer(array, this.file.getName(), this.transferID, this.clientUser.getUsername(), this.clientUser.getUserID());
				
				//Send Message and Close Streams
				this.client.send(message);
				
				long timeElapsedMillis = (System.currentTimeMillis() - time) / 1000;
				this.dialog.updateTransferDialog(bytesRead, array.length, bytesTotal, timeElapsedMillis, this.file.getName());
			}
			
			//Update Dialog
			this.dialog.updateTransferDialogComplete();
		}
		catch(IOException e)
		{
			AbstractClient.logException(e);
			this.dialog.updateTransferDialogError();
		}
		
		this.transferRunning = false;
	}
	
	private void receive()
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
