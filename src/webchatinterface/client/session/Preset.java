package webchatinterface.client.session;

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

	private String emailAddress;
	private String username;
	private byte[] password;
	private String hostAddress;
	private Integer portNumber;
	private boolean guest;

	public Preset(String emailAddress, String username, byte[] password, String hostAddress, Integer portNumber, boolean guest)
	{
		this.emailAddress = emailAddress;
		this.username = username;
		this.password = password;
		this.hostAddress = hostAddress;
		this.portNumber = portNumber;
		this.guest = guest;
	}

	public String getEmailAddress()
	{
		return emailAddress;
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
		return this.portNumber;
	}

	public boolean isGuest()
	{
		return guest;
	}

	public String toString()
	{
		return "[" + this.username + "]" + this.hostAddress + ":" + this.portNumber.toString();
	}
}