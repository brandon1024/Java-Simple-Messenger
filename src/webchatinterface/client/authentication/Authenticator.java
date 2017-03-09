package webchatinterface.client.authentication;

import webchatinterface.client.AbstractClient;
import webchatinterface.client.session.PresetLoader;
import webchatinterface.client.session.Session;
import webchatinterface.client.ui.WebChatClientGUI;
import webchatinterface.client.ui.dialog.AuthenticationDialog;
import webchatinterface.client.ui.dialog.NewAccountDialog;
import webchatinterface.client.session.Preset;
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
	private Session session;

	public Authenticator(WebChatClientGUI parent)
	{
		this.parent = parent;
		this.session = new Session();
	}
	
	public void quickAuthenticate() throws AuthenticationException
	{
		if(!this.loadPreset())
			this.showAuthenticationDialog();
	}
	
	public void showNewAccountDialog() throws AuthenticationException
	{
		this.session.guest = false;
		this.session.newAccount = true;

		//---REQUEST SETTINGS FROM USER---//
		NewAccountDialog nad = new NewAccountDialog(parent);

		//---RETRIEVE INPUT FROM DIALOG FIELDS---//
		if(nad.showDialog() == 1)
		{
			try
			{
				this.session.hostAddress = nad.getHostAddress();
				this.session.portNumber = Integer.valueOf(nad.getPortNumber());
			}
			catch(NumberFormatException e)
			{
				AbstractClient.logException(e);
				throw new InvalidFieldException("Invalid Port Number: " + nad.getPortNumber());
			}

			this.session.emailAddress = nad.getEmailAddress();
			if(!EmailHelper.isValidEmailAddress(this.session.emailAddress))
				throw new InvalidFieldException("Invalid Email Address: " + nad.getEmailAddress());

			this.session.username = nad.getUsername();
			if(!UsernameHelper.isValidUsername(this.session.username))
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

			this.session.password = new byte[pass1.length];

			for(int i = 0; i < this.session.password.length; i++)
				this.session.password[i] = (byte) pass1[i];

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
		AuthenticationDialog auth = new AuthenticationDialog(this.parent, this.session.username, this.session.password, this.session.hostAddress, this.session.portNumber);

		//---RETRIEVE INPUT FROM DIALOG FIELDS---//
		if(auth.showDialog() == 1)
		{
			try
			{
				this.session.hostAddress = auth.getHostAddress();
				this.session.portNumber = Integer.valueOf(auth.getPortNumber());
			}
			catch(NumberFormatException e)
			{
				AbstractClient.logException(e);
				throw new InvalidFieldException("Invalid Port Number: " + auth.getPortNumber());
			}

			if(auth.getIsGuest())
			{
				this.session.guest = true;
				this.session.newAccount = false;
				this.session.username = null;
				this.session.password = null;
			}
			else
			{
				this.session.guest = false;
				this.session.newAccount = false;
				this.session.username = auth.getUsername();

				char[] pass = auth.getPassword();

				if(pass.length < 6)
					throw new InvalidFieldException("Invalid Password; Password must be a minimum of 6 characters.");

				for(char character : pass)
				{
					if(character > Byte.MAX_VALUE)
						throw new InvalidFieldException("Invalid Password; ASCII characters only: a-z, A-Z, 0-9, or any !\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~");
				}

				this.session.password = new byte[pass.length];

				for(int i = 0; i < this.session.password.length; i++)
					this.session.password[i] = (byte) pass[i];

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

		this.session = PresetLoader.renderSession(savedPreset);

		return true;
	}
	
	private boolean savePreset()
	{
		Preset preset = PresetLoader.renderPreset(this.session);
		return PresetLoader.savePreset(preset);
	}
	
	public Session getSession()
	{
		return this.session;
	}

	private void removeSensitiveInformation(char[] password)
	{
		Arrays.fill(password, Character.MIN_VALUE);
	}

	public static void removeSensitiveInformation(byte[] password)
	{
		Arrays.fill(password, Byte.MIN_VALUE);
	}
}