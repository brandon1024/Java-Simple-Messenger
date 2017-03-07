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
	private static final long serialVersionUID = -6687592140931873577L;
	private String username;
	private byte[] password;
	private String hostAddress;
	private Integer port;
	
	public Preset(String username, byte[] password, String hostAddress, Integer port)
	{
		this.username = username;
		this.password = password;
		this.hostAddress = hostAddress;
		this.port = port;
	}

	public String getUsername()
	{
		return this.username;
	}
	
	public byte[] getPassword()
	{
		return this.password;
	}

	public String getHostAddress()
	{
		return this.hostAddress;
	}

	public Integer getPort()
	{
		return this.port;
	}
	
	public String toString()
	{
		return "[" + this.username + "]" + this.hostAddress + ":" + this.port.toString();
	}
}