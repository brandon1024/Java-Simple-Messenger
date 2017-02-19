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
	/**Serial Version UID is used as a version control for the class that implements 
	 *the serializable interface.*/
	private static final long serialVersionUID = 731185342360505610L;

	/**The byte array represented by this {@code TransferBuffer} object.*/
	private final byte[] BYTE_ARRAY;
	
	/**The filename associated with the byte array*/
	private final String FILE_NAME;
	
	/**The unique transfer identification number associated with the file transfer instance*/
	private final String TRANSFER_ID;
	
	/**Builds a new {@code TransferBuffer} object with a given byte array, filename,
	  *message, sender and sender ID.
	  *@param byteArray the byteArray represented by this TransferBuffer object
	  *@param fileName the filename associated with the byte array
	  *@param transferID the unique transfer identification number associated with the file transfer instance
	  *@param sender the name of the client who created the message
	  *@param senderID the unique 256-bit alphanumeric key associated with each client*/
	public TransferBuffer(byte[] byteArray, String fileName, String transferID, String sender, String senderID)
	{
		super(sender, senderID);
		this.BYTE_ARRAY = byteArray;
		this.FILE_NAME = fileName;
		this.TRANSFER_ID = transferID;
	}
	
	/**Accessor method for the byteArray field of this TransferBuffer object
	  *@return the byte array represented by this {@code TransferBuffer} object.*/
	public byte[] getByteArray()
	{
		return this.BYTE_ARRAY;
	}
	
	/**Accessor method for the filename field of this TransferBuffer object
	  *@return the filename associated with the byte array*/
	public String getFileName()
	{
		return this.FILE_NAME;
	}
	
	/**Accessor method for the transfer ID field of this TransferBuffer object
	  *@return the unique transfer identification number associated with the file transfer instance*/
	public String getTransferID()
	{
		return this.TRANSFER_ID;
	}
}
