package webchatinterface.client.util;

import java.io.Serializable;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
  *<p>
  *Serializable Preset Object that represents a saved connection preset for the client application.
  *The preset contains the Username String, the server host address, and the server port number.
  */

public class Preset implements Serializable
{
	/**Serial Version UID is used as a version control for the class that implements
	 *the serializable interface.*/
	private static final long serialVersionUID = -6687592140931873577L;

	/**The saved username field*/
	private String username;
	
	/***/
	private byte[] password;
	
	/**The saved server host address field*/
	private String hostAddress;
	
	/**The saved server port number field*/
	private Integer port;
	
	/**Constructs a serializable preset object, with the given fields for the username, server host 
	  *address, and server port number.
	  *@param username the preset username
	  *@param hostAddress the preset host address
	  *@param port the preset port number*/
	public Preset(String username, byte[] password, String hostAddress, Integer port)
	{
		this.username = username;
		this.password = password;
		this.hostAddress = hostAddress;
		this.port = port;
	}

	/**Accessor method for the username field of this preset object.
	  *@return the username field of this preset object*/
	public String getUsername()
	{
		return this.username;
	}
	
	/***/
	public byte[] getPassword()
	{
		return this.password;
	}

	/**Accessor method for the host address field of this preset object.
	  *@return the host address field of this preset object*/
	public String getHostAddress()
	{
		return this.hostAddress;
	}

	/**Accessor method for the port number field of this preset object.
	  *@return the port number field of this preset object*/
	public Integer getPort()
	{
		return this.port;
	}
	
	/**Overridden {@code java.lang.object.toString()} method.
	  *<p>
	  *Format: {@code [username]hostaddress:port}
	  *@return textual representation of this preset object*/
	@Override
	public String toString()
	{
		return "[" + this.username + "]" + this.hostAddress + ":" + this.port.toString();
	}
}