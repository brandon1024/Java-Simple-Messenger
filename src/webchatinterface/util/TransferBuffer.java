 package webchatinterface.util;

import java.io.Serializable;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
 *<p>
 *Serializable Byte Array object that is transmitted by TCP between client 
 *applications through a dedicated server interface.
 *<p>
 *A {@code TransferBuffer} object contains a {@code byte} array representing the file, a 
 *timestamp, a sender, and a local message identification number.
 */

public class TransferBuffer extends TransportEntity implements Serializable
{
	private static final long serialVersionUID = 731185342360505610L;

	private final byte[] BYTE_ARRAY;
	private final String TRANSFER_ID;
	
	public TransferBuffer(byte[] byteArray, String transferID, String sender, String senderID)
	{
		super(sender, senderID);
		this.BYTE_ARRAY = byteArray;
		this.TRANSFER_ID = transferID;
	}
	
	public byte[] getByteArray()
	{
		return this.BYTE_ARRAY;
	}
	
	public String getTransferID()
	{
		return this.TRANSFER_ID;
	}
}
