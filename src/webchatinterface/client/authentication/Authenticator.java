package webchatinterface.client.authentication;

import webchatinterface.client.AbstractClient;
import webchatinterface.client.session.PresetLoader;
import webchatinterface.client.ui.WebChatClientGUI;
import webchatinterface.client.ui.dialog.AuthenticationDialog;
import webchatinterface.client.ui.dialog.NewAccountDialog;
import webchatinterface.client.util.Preset;
import webchatinterface.helpers.EmailHelper;
import webchatinterface.helpers.UsernameHelper;

import java.util.Arrays;

/**@author Brandon Richardson
 *@version 1.4.3
 *@since 06/05/2016
  *<p>
  *The {@code Authenticator} class represents an object that is able to gather information from the
  *user to authenticate for a client-server socket connection. The class is designed to do so by
  *displaying a dialog with fields for gathering from the user the desired username, server host 
  *address and port number.
  *<p>
  *The {@code Authenticator} also handles loading and saving previously saved authentication presets.
  */

public class Authenticator
{
	private WebChatClientGUI parent;

	private String emailAddress;
	private String username;
	private byte[] password;
	private String hostAddress;
	private Integer portNumber;
	private boolean guest;
	private boolean newAccount;

	public Authenticator(WebChatClientGUI parent)
	{
		this.parent = parent;
		this.emailAddress = null;
		this.username = null;
		this.password = null;
		this.hostAddress = null;
		this.portNumber = null;
		this.guest = false;
		this.newAccount = false;
	}
	
	public void quickAuthenticate() throws AuthenticationException
	{
		if(!this.loadPreset())
			this.showAuthenticationDialog();
	}
	
	public void showNewAccountDialog() throws AuthenticationException
	{
		this.guest = false;
		this.newAccount = true;

		//---REQUEST SETTINGS FROM USER---//
		NewAccountDialog nad = new NewAccountDialog(parent);

		//---RETRIEVE INPUT FROM DIALOG FIELDS---//
		if(nad.showDialog() == 1)
		{
			try
			{
				this.hostAddress = nad.getHostAddress();
				this.portNumber = Integer.valueOf(nad.getPortNumber());
			}
			catch(NumberFormatException e)
			{
				AbstractClient.logException(e);
				throw new InvalidFieldException("Invalid Port Number: " + nad.getPortNumber());
			}

			this.emailAddress = nad.getEmailAddress();
			if(!EmailHelper.isValidEmailAddress(this.emailAddress))
				throw new InvalidFieldException("Invalid Email Address: " + nad.getEmailAddress());

			this.username = nad.getUsername();
			if(!UsernameHelper.isValidUsername(this.username))
				throw new InvalidFieldException("Invalid Username: " + nad.getUsername());

			char[] pass1 = nad.getPassword();
			char[] pass2 = nad.getConfirmPassword();

			if(!Arrays.equals(pass1, pass2))
				throw new InvalidFieldException("Password Does Not Match Confirmation");

			if(pass1.length < 6)
				throw new InvalidFieldException("Invalid Password; Password must be a minimum of 6 characters.");

			for(char character : pass1)
			{
				if(character > Byte.MAX_VALUE)
					throw new InvalidFieldException("Invalid Password; ASCII characters only: a-z, A-Z, 0-9, or any !\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~");
			}

			this.password = new byte[pass1.length];

			for(int i = 0; i < this.password.length; i++)
				this.password[i] = (byte) pass1[i];

			this.removeSensitiveInformation(pass1);
			this.removeSensitiveInformation(pass2);
		}
		else
			throw new AuthenticationAbortedException("Authentication Aborted by User");

		//---SAVE NEW ACCOUNT SETTINGS TO PRESET STORE---//
		this.savePreset();
	}

	public void showAuthenticationDialog() throws AuthenticationException
	{
		//---ATTEMPT TO LOAD SAVED SETTINGS---//
		this.loadPreset();

		//---REQUEST SETTINGS FROM USER---//
		AuthenticationDialog auth = new AuthenticationDialog(this.parent, this.username, this.password, this.hostAddress, this.portNumber);

		//---RETRIEVE INPUT FROM DIALOG FIELDS---//
		if(auth.showDialog() == 1)
		{
			try
			{
				this.hostAddress = auth.getHostAddress();
				this.portNumber = Integer.valueOf(auth.getPortNumber());
			}
			catch(NumberFormatException e)
			{
				AbstractClient.logException(e);
				throw new InvalidFieldException("Invalid Port Number: " + auth.getPortNumber());
			}

			if(auth.getIsGuest())
			{
				this.guest = true;
				this.newAccount = false;
				this.username = null;
				this.password = null;
			}
			else
			{
				this.guest = false;
				this.newAccount = false;
				this.username = auth.getUsername();

				char[] pass = auth.getPassword();

				if(pass.length < 6)
					throw new InvalidFieldException("Invalid Password; Password must be a minimum of 6 characters.");

				for(char character : pass)
				{
					if(character > Byte.MAX_VALUE)
						throw new InvalidFieldException("Invalid Password; ASCII characters only: a-z, A-Z, 0-9, or any !\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~");
				}

				this.password = new byte[pass.length];

				for(int i = 0; i < this.password.length; i++)
					this.password[i] = (byte) pass[i];

				this.removeSensitiveInformation(pass);
			}
		}
		else
			throw new AuthenticationAbortedException("Authentication Aborted by User");

		//---SAVE NEW ACCOUNT SETTINGS TO PRESET STORE---//
		if(auth.getSavePreset())
			this.savePreset();
	}
	
	private boolean loadPreset()
	{
		Preset savedPreset = PresetLoader.loadPreset();

		if(savedPreset == null)
			return false;

		this.username = savedPreset.getUsername();
		this.password = savedPreset.getPassword();
		this.hostAddress = savedPreset.getHostAddress();
		this.portNumber = savedPreset.getPort();

		return true;
	}
	
	private boolean savePreset()
	{
		Preset savedPreset = new Preset(this.username, this.password, this.hostAddress, this.portNumber);
		return PresetLoader.savePreset(savedPreset);
	}
	
	public boolean isGuest()
	{
		return this.guest;
	}
	
	public boolean isNewMember()
	{
		return this.newAccount;
	}
	
	public String getEmailAddress()
	{
		return this.emailAddress;
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
	
	public int getPortNumber()
	{
		return this.portNumber;
	}

	private void removeSensitiveInformation(char[] password)
	{
		Arrays.fill(password, Character.MIN_VALUE);
	}

	public void removeSensitiveInformation(byte[] password)
	{
		Arrays.fill(password, Byte.MIN_VALUE);
	}
}