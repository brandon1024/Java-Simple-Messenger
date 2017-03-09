package webchatinterface.client.communication.filetransfer;

import webchatinterface.client.communication.WebChatClient;
import webchatinterface.client.ui.WebChatClientGUI;
import webchatinterface.util.Command;
import webchatinterface.util.TransferBuffer;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class FileTransferManager
{
	private WebChatClientGUI graphicalUserInterface;
	private ArrayList<InboundFileTransferExecutor> ongoingInboundTransfers;

	public FileTransferManager(WebChatClientGUI graphicalUserInterface)
	{
		this.graphicalUserInterface = graphicalUserInterface;
		this.ongoingInboundTransfers = new ArrayList<InboundFileTransferExecutor>();
	}

	public void executeInboundTransfer(Command transferManifest)
	{
		InboundFileTransferExecutor FileTransferExecutor = new InboundFileTransferExecutor(this.graphicalUserInterface);
		FileTransferExecutor.start(transferManifest);
		this.ongoingInboundTransfers.add(FileTransferExecutor);
	}

	public void executeOutboundTransfer(WebChatClient client, File file)
	{
		(new OutboundFileTransferExecutor(client)).start(file);
	}

	public void processTransferBuffer(TransferBuffer buffer)
	{
		//Get TransferBuffer ID
		String messageTransferID = buffer.getTransferID();
		Iterator<InboundFileTransferExecutor> iterator = this.ongoingInboundTransfers.iterator();

		//Iterate All Ongoing File Transfers
		while(iterator.hasNext())
		{
			InboundFileTransferExecutor next = iterator.next();

			//If Transfer is Running, Pass on TransferBuffer
			if(next.isRunning())
			{
				if(next.getTransferID().equals(messageTransferID))
					next.bufferReceived(buffer);
			}
			//Else Remove From List of Ongoing Transfers
			else
				iterator.remove();
		}
	}
}
