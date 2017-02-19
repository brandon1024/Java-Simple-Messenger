package webchatinterface.server.util;

import java.io.Serializable;

public class UserFile implements Serializable
{
	private static final long serialVersionUID = -6370351879299501807L;
	private byte[] emailAddress;
	private byte[] username;
	private byte[] salt;
	private byte[] saltedHashPassword;
	
	public UserFile(byte[] emailAddress, byte[] username, byte[] salt, byte[] saltedHashPassword)
	{
		this.setEmailAddress(emailAddress);
		this.setUsername(username);
		this.setSalt(salt);
		this.setSaltedHashPassword(saltedHashPassword);
	}

	public byte[] getUsername()
	{
		return username;
	}

	public void setUsername(byte[] username)
	{
		this.username = username;
	}

	public byte[] getSalt()
	{
		return salt;
	}

	public void setSalt(byte[] salt)
	{
		this.salt = salt;
	}

	public byte[] getSaltedHashPassword()
	{
		return saltedHashPassword;
	}

	public void setSaltedHashPassword(byte[] saltedHashPassword)
	{
		this.saltedHashPassword = saltedHashPassword;
	}

	public byte[] getEmailAddress()
	{
		return emailAddress;
	}

	public void setEmailAddress(byte[] emailAddress)
	{
		this.emailAddress = emailAddress;
	}
}